package jp.mzw.revajaxmutator.test.result;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Provides utility functionalities for parsing coverage results reported by JSCover
 * 
 * @author Yuta Maezawa
 *
 */
public class Coverage {
	protected static Logger LOGGER = LoggerFactory.getLogger(Coverage.class);

	/**
	 * Get coverage results of all test cases
	 * 
	 * @param jscoverReportdir Where JSCover reports coverage results
	 * @return List of files containing coverage results of all test cases
	 * @throws IOException Causes when parsing test results
	 */
	public static List<File> getCoverageResults(File jscoverReportdir) throws IOException {
		List<File> ret = Lists.newArrayList();
		List<TestResult> results = TestResult.parseTestResults(jscoverReportdir);
		for (TestResult result : results) {
			File dir = new File(jscoverReportdir, result.getClassName() + "#" + result.getMethodName());
			File file = new File(dir, "jscoverage.json");
			ret.add(file);
		}
		return ret;
	}

	/**
	 * Get coverage results of failing test cases
	 * 
	 * @param jscoverReportdir Where JSCover reports coverage results
	 * @return List of files containing coverage results of failing test cases
	 * @throws IOException Causes when parsing test results
	 */
	public static List<File> getFailureCoverageResults(File jscoverReportdir) throws IOException {
		List<File> ret = Lists.newArrayList();
		List<TestResult> results = TestResult.parseTestResults(jscoverReportdir);
		for (TestResult result : results) {
			if (0 < result.getFailureCount()) {
				File dir = new File(jscoverReportdir, result.getClassName() + "#" + result.getMethodName());
				File file = new File(dir, "jscoverage.json");
				ret.add(file);
			}
		}
		return ret;
	}
	
	/**
	 * Parse coverage results
	 * 
	 * @param file Contains coverage results
	 * @return Coverage results
	 * @throws IOException Causes if given file does not exist
	 * @throws JSONException Causes if given file is broken
	 */
	public static JSONObject parse(File file) throws IOException, JSONException {
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		StringBuilder builder = new StringBuilder();
		String line;
		String delim = "";
		while ((line = br.readLine()) != null) {
			builder.append(delim).append(line);
			delim = "\n";
		}
		fr.close();
		return new JSONObject(builder.toString());
	}

	/**
	 * Get coverage results of target JavaScript file
	 * 
	 * @param files Contains coverage results
	 * @param recordedJsFile Represents target JavaScript file
	 * @return Map whose keys are coverage-report files and values contain coverage results of target JavaScript file
	 * @throws JSONException
	 * @throws IOException
	 */
	public static Map<File, boolean[]> getTargetCoverageResults(final List<File> files, final File recordedJsFile, final File recordDir) throws JSONException, IOException {
		Map<File, boolean[]> ret = Maps.newHashMap();
		for (final File file : files) {
			JSONObject json = Coverage.parse(file);
			
			String encoded_url = getNameRepresentingUrl(recordedJsFile, recordDir);
			String decoded_url = URLDecoder.decode(encoded_url, "utf-8");
			URL url = new URL(decoded_url);
			String path_to_js_file = URLDecoder.decode(url.getPath(), "utf-8");
			
			JSONArray array = Coverage.getCoverageResults(json, path_to_js_file);
			if (array == null) {
				return ret;
			}
			
			boolean[] coverages = new boolean[array.length()];
			for (int i = 0; i < array.length(); i++) {
				Object line = array.get(i);
				int freq = Coverage.getCoverFreq(line);
				if (0 < freq) {
					coverages[i] = true; // covered
				} else {
					coverages[i] = false; // not covered
				}
			}

			ret.put(file, coverages);
		}
		return ret;
	}

	public static String getNameRepresentingUrl(final File recordedJsFile, final File recordDir) {
		if (recordedJsFile.getParentFile().equals(recordDir)) {
			return recordedJsFile.getName();
		}
		// For too-long URL problem
		StringBuilder url = new StringBuilder();
		File focus = recordedJsFile;
		while (!focus.equals(recordDir)) {
			url.insert(0, focus.getName());
			focus = focus.getParentFile();
		}
		return url.toString();
	}

	/**
	 * Get coverage frequency at given line.
	 * Positive integer represent that the line is covered, otherwise not covered.
	 * 
	 * @param line Contains coverage frequency of line
	 * @return Coverage frequency
	 */
	public static int getCoverFreq(Object line) {
		if (line instanceof Integer) {
			return (Integer) line;
		} else {
			return 0;
		}
	}
	
	/**
	 * Get coverage results of target JavaScript file
	 * 
	 * @param coverage
	 * @param path_to_js_file
	 * @return
	 * @throws JSONException
	 */
	public static JSONArray getCoverageResults(JSONObject coverage, String path_to_js_file) throws JSONException {
		for (@SuppressWarnings("unchecked") Iterator<Object> i = coverage.keys(); i.hasNext();) {
			String filename = i.next().toString();
			if (filename.equals(path_to_js_file)) {
				JSONObject _coverage = coverage.getJSONObject(filename);
				for (@SuppressWarnings("unchecked") Iterator<Object> j = _coverage.keys(); j.hasNext();) {
					Object type = j.next();
					if ("lineData".equals(type.toString())) {
						return _coverage.getJSONArray(type.toString());
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Determine whether coverage results represents 'covered' between given start and end line numbers.
	 * 
	 * @param results Contains coverage results
	 * @param startLineNum
	 * @param endLineNum
	 * @return true if covered, otherwise false
	 */
	public static boolean isCovered(final Map<File, boolean[]> results, int startLineNum, int endLineNum) {
		for (Map.Entry<File, boolean[]> entry : results.entrySet()) {
			boolean[] testsCoverage = entry.getValue();
			for (int line = startLineNum; line <= endLineNum; line++) {
				if (testsCoverage[line] == true) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Precondition: 
	 * 
	 * Determine whether coverage results represents 'covered' between given start and end line numbers.
	 * 
	 * @param results
	 * @param startLineNum
	 * @param endLineNum
	 * @param methodName
	 * @return
	 */
	public static boolean isCovered(final Map<File, boolean[]> results, int startLineNum, int endLineNum, String methodName) {
		for (File file : results.keySet()) {
			String name = Coverage.getTestMethodName(file);
			if (name.equals(methodName)) {
				boolean[] coverages = results.get(file);
				for (int line = startLineNum; line <= endLineNum; line++) {
					if (coverages[line]) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Note: We currently design RevAjaxMutator to put files containing coverage results at the following paths.
	 * 
	 * {@code path/to/jscover/app/test-class#method/jscoverage.json}
	 * 
	 * Get {@code test-class#method} from the path above.
	 * 
	 * @param file
	 * @return
	 */
	public static String getTestMethodName(File file) {
		return new File(file.getParent()).getName();
	}
}
