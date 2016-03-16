package jp.mzw.revajaxmutator.tracer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static Logger log = LoggerFactory.getLogger(Main.class);

	static String JUNIT_REPORT_FILE = "/Users/yuta/workspace/RevAjaxMutatorTest/junit/"
			+ "ImgSliderTest_20150626-170344.xml";
	static String TEST_GIT_REPOSITORY = "/Users/yuta/workspace/RevAjaxMutatorTest/";
	static String TEST_SRC_ROOT = "src/main/java/";
	
	public static void main(String[] args) throws IOException, NoHeadException, GitAPIException {
		JUnitParser parser = new JUnitParser(JUNIT_REPORT_FILE);
		ArrayList<TraceInfo> trace_info_list = parser.getErrorClassLineList();

		for(TraceInfo trace_info: trace_info_list) {
			/// Finds subject statement
			String test_class = trace_info.getTestCaseClassname();
			int test_line = trace_info.getErrorDetectedLine();
			
			String test_filename = test_class.replace(".", "/").concat(".java");
			String test_src_root = (new File(TEST_GIT_REPOSITORY, TEST_SRC_ROOT)).getAbsolutePath();
			String java_content = org.apache.commons.io.FileUtils.readFileToString(new File(test_src_root, test_filename));
			
			String[] _java_content = java_content.split("\n");
			String subject_statement = _java_content[test_line-1];
			trace_info.setSubjectStatement(subject_statement);
			
			/// Specifies mutation operator that helped to implement this test case statement
			GitTracer tracer = new GitTracer(TEST_GIT_REPOSITORY);

			String subject_testfile = (new File(TEST_SRC_ROOT, test_filename)).toString();
			RevCommit commit = tracer.trace(subject_testfile, subject_statement);
			if(commit == null) {
				log.debug("Could not find subject commit.");
				System.exit(0);
			}
			
			String mut_op = tracer.specifyMutationOpeator(commit);
			if(mut_op == null) {
				log.debug("Could not find subject mutation operator.");
				System.exit(0);
			}
			
			trace_info.setMutationOperator(mut_op);
		}
		

		for(TraceInfo trace_info: trace_info_list) {
			System.out.println("Test suite: " + trace_info.getTestSuiteName());
			System.out.println("Test class: " + trace_info.getTestCaseClassname());
			System.out.println("Test case: " + trace_info.getTestCaseName());
			System.out.println("Error detected line: " + trace_info.getErrorDetectedLine());
			System.out.println("Test statement: " + trace_info.getSubjectStatement());
			System.out.println("==Found:");
			System.out.println("Mutation opeartor id: " + trace_info.getMutationOperator());
			System.out.println("==========");
		}
	}

}
