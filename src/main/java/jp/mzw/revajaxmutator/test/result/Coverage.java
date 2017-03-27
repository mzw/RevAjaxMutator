package jp.mzw.revajaxmutator.test.result;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class Coverage {
	protected static Logger LOGGER = LoggerFactory.getLogger(Coverage.class);

	public static List<File> getFailureCoverageResults(File jscoverReportdir) throws IOException {
		List<File> ret = new ArrayList<>();
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

	public static Map<String, boolean[]> getCoverageInfo(HashMap<String, File> files, String pathToJsFile) throws JSONException {
		Map<String, boolean[]> ret = Maps.newHashMap();
		for (Map.Entry<String, File> entry : files.entrySet()) {
			boolean[] coverageInfo;
			try {
				JSONObject failure_coverage_json = Coverage.parse(entry.getValue());

				String encoded_url = (new File(pathToJsFile)).getName();
				String decoded_url = URLDecoder.decode(encoded_url, "utf-8");

				URL url = new URL(decoded_url);
				String url_path_to_js_file = URLDecoder.decode(url.getPath(), "utf-8");

				JSONArray failure = Coverage.getCoverageData(failure_coverage_json, url_path_to_js_file);

				List<String> jsfile = FileUtils.readLines(new File(pathToJsFile));

				int line_num = failure.length();

				coverageInfo = new boolean[jsfile.size() + 1];
				for (int i = 1; i < line_num; i++) {
					Object failure_line = failure.get(i);
					int failure_cover_freq = Coverage.getCoverFreq(failure_line);
					if (0 < failure_cover_freq) {
						coverageInfo[i] = true; // covered
					} else {
						coverageInfo[i] = false; // no covered
					}
				}
			} catch (IOException e) {
				coverageInfo = new boolean[0];
				System.out.println("can't find Folder : " + entry.getKey());
			}
			ret.put(entry.getKey(), coverageInfo);
		}
		return ret;
	}

	/**
	 * 
	 * @param line
	 * @return
	 */
	public static int getCoverFreq(Object line) {
		if (line instanceof Integer) {
			return (Integer) line;
		} else {
			return 0;
		}
	}
	
	/**
	 * 
	 * 
	 * @param coverage
	 * @param path_to_js_file
	 * @return
	 * @throws JSONException
	 */
	public static JSONArray getCoverageData(JSONObject coverage, String path_to_js_file) throws JSONException {
		for (@SuppressWarnings("unchecked")
		Iterator<Object> i = coverage.keys(); i.hasNext();) {
			String filename = i.next().toString();
			if (filename.equals(path_to_js_file)) {
				JSONObject _coverage = coverage.getJSONObject(filename);
				for (@SuppressWarnings("unchecked")
				Iterator<Object> j = _coverage.keys(); j.hasNext();) {
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
	 * 
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws JSONException
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
}
