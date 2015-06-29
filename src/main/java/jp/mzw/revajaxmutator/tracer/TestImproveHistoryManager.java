package jp.mzw.revajaxmutator.tracer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.MutationFileInformation;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestImproveHistoryManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestImproveHistoryManager.class);

	String mMutantsDir;
	String mTestDir;
	String mMutationListFilename;
	ArrayList<String> mImproveMutationList;
	public TestImproveHistoryManager(String mutants_dir, String test_dir) {
		mMutantsDir = mutants_dir;
		mTestDir = test_dir;
	}

	public void saveMutationAnalysisResult() throws IOException {
		// This filename depends on AjaxMutator
		String result_filename = "mutation_list.csv";
		// Read current result of mutation analysis
		String content = FileUtils.readFileToString(new File(mMutantsDir, result_filename));
		// Get time-stamp
    	Date date = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
    	String timestamp = sdf.format(date);
    	// Write out as historical results
    	mMutationListFilename = "mutation_list." + timestamp + ".csv";
    	FileUtils.write(new File(mMutantsDir, mMutationListFilename), content);
	}
	
	public void analyzeTestImprovement() throws IOException {
		ArrayList<String> historicals = new ArrayList<String>();
		
		File dir = new File(mMutantsDir);
		for(File file : dir.listFiles()) { // to be sorted
			String filename = file.getName();
			if(filename.startsWith("mutation_list") && filename.endsWith(".csv") &&
					"mutation_list.csv".length() < filename.length()) { // to be modified
				historicals.add(filename);
			}
		}
		
		ArrayList<MutationResultParser> prevList = new ArrayList<MutationResultParser>();
		for(String filename : historicals) {
			
			MutationResultParser parser = new MutationResultParser(mMutantsDir, filename);
			parser.parse();
			
			mImproveMutationList = new ArrayList<String>();
			for(String mutname: parser.getListOfMutationName()) {
				for(MutationFileInformation result : parser.getMutationFileInformationList(mutname)) {
					if(result.getState().equals(MutationFileInformation.State.KILLED)) {
						String path = result.getAbsolutePath();
						
						boolean improved = true;
						
						MutationFileInformation.State prev_state = null;
						for(MutationResultParser prev : prevList) {
							for(MutationFileInformation prev_result : prev.getMutationFileInformationList(mutname)) {
								if(path.equals(prev_result.getAbsolutePath())) {
									prev_state = prev_result.getState();
									break;
								}
							}
							if(prev_state != null && prev_state.equals(MutationFileInformation.State.KILLED)) {
								improved = false;
								break;
							}
						}

						if(improved) {
							if(filename.equals(mMutationListFilename)) {
								if(!mImproveMutationList.contains(mutname)) {
									mImproveMutationList.add(mutname);
								}
							}
						}
					}
				}
			}
			
			prevList.add(parser);
		}
	}
	
	public void notifyImprovement() throws IOException, InterruptedException {
		if(mImproveMutationList == null || mImproveMutationList.size() == 0) {
			LOGGER.info("No test improvement");
			return;
		}
		
		boolean isYes;
		boolean isNo;
		while(true) {
			InputStreamReader isr = new InputStreamReader(System.in);
	        BufferedReader br = new BufferedReader(isr);
	        String line = br.readLine();
	        
	        isYes = isDefault(line) || isYes(line);
	        isNo = isNo(line);
	        if(isYes || isNo) {
	        	break;
	        } else {
	        	LOGGER.info("Y/n: ");
	        }
		}

		StringBuilder commit_msg = new StringBuilder();
		commit_msg.append("Improved: ");
		String delim = "";
		for(String op : mImproveMutationList) {
			commit_msg.append(delim).append(op);
			delim = ", ";
		}
		if(isYes) {
			String[] git_add = {"git", "add", "."};
			exec(mTestDir, git_add);
			String[] git_commit = {"git", "commit", "-m", commit_msg.toString()};
			exec(mTestDir, git_commit);
		} else {
			LOGGER.info("Commit by yourself");
			StringBuilder no_msg = new StringBuilder();
			no_msg.append("$ ").append("git").append(" add").append(" .").append("\n");
			no_msg.append("$ ").append("git").append(" commit -m").append("\""+commit_msg.toString()+"\"").append("\n");
			LOGGER.info(no_msg.toString());
		}
	}
	
	private boolean isDefault(String str) {
		if("".equals(str)) {
			return true;
		}
		return false;
	}
	private boolean isYes(String str) {
		if("y".equals(str) || "Y".equals(str) || "yes".equals(str) || "Yes".equals(str)) {
			return true;
		}
		return false;
	}
	private boolean isNo(String str) {
		if("n".equals(str) || "N".equals(str) || "no".equals(str) || "No".equals(str)) {
			return true;
		}
		return false;
	}
	
	private static int exec(String dir, String[] cmd) throws IOException, InterruptedException {
		Thread timeout = new TimeoutThread(Thread.currentThread());
		timeout.start();
		
		Process proc = null;
		int proc_result = 0;
		try {
			proc = Runtime.getRuntime().exec(cmd, null, new File(dir));
			
			try {
				proc_result = proc.waitFor();
				timeout.interrupt();
			} catch (InterruptedException e) {
				// NOP
			}
		} finally {
			if(proc != null) {
				LOGGER.info(readStdOut(proc));
				LOGGER.info(readStdErr(proc));
				
				proc.getErrorStream().close();
				proc.getInputStream().close();
				proc.getOutputStream().close();
				proc.destroy();
				
				return proc_result;
			}
		}
		return proc_result;
	}
	private static final long Timeout = 3000;
	private static class TimeoutThread extends Thread {
		private Thread mParent;
		public TimeoutThread(Thread parent) {
			mParent = parent;
		}
		public void run() {
			try {
				Thread.sleep(Timeout);
				mParent.interrupt();
			} catch (InterruptedException e) {
				// NOP
			}
		}
	}
	public static String readStdOut(Process process) {
		String ret = "";
		try {
			InputStream in = process.getInputStream();
			BufferedReader br_in = new BufferedReader(new InputStreamReader(in));
			String line_in = br_in.readLine();
			while(line_in != null) {
				ret += line_in + "\n";
				line_in = br_in.readLine();
			}
			br_in.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	public static String readStdErr(Process process) {
		String ret = "";
		try {
			InputStream err = process.getErrorStream();
			BufferedReader br_err = new BufferedReader(new InputStreamReader(err));
			String line_err = br_err.readLine();
			while(line_err != null) {
				ret += line_err + "\n";
				line_err = br_err.readLine();
			}
			br_err.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
}
