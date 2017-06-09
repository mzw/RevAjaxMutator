package jp.mzw.revajaxmutator.test.result;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestResult {
	protected static Logger LOGGER = LoggerFactory.getLogger(TestResult.class);
	
	public static final String FILENAME = "results.csv";

	private String className;
	private String methodName;
	
	private int runs;
	private int ignores;
	private int failures;

	public TestResult(String className, String methodName, Result result) {
		this.className = className;
		this.methodName = methodName;
		
		this.runs = result.getRunCount();
		this.ignores = result.getIgnoreCount();
		this.failures = result.getFailureCount();
	}

	public TestResult(String className, String methodName, int runs, int ignores, int failures) {
		this.className = className;
		this.methodName = methodName;
		
		this.runs = runs;
		this.ignores = ignores;
		this.failures = failures;
	}

	public String getClassName() {
		return this.className;
	}

	public String getMethodName() {
		return this.methodName;
	}

	public int getRunCount() {
		return this.runs;
	}
	
	public int getIgnoreCount() {
		return this.ignores;
	}
	
	public int getFailureCount() {
		return this.failures;
	}

	public static void store(File dir, List<TestResult> results) throws IOException {
		StringBuilder csv = new StringBuilder();
		csv.append("class").append(",");
		csv.append("method").append(",");
		csv.append("run").append(",");
		csv.append("ignore").append(",");
		csv.append("failure").append("\n");
		for (TestResult result : results) {
			csv.append(result.getClassName()).append(",");
			csv.append(result.getMethodName()).append(",");
			csv.append(result.getRunCount()).append(",");
			csv.append(result.getIgnoreCount()).append(",");
			csv.append(result.getFailureCount()).append("\n");
		}
		File file = new File(dir, FILENAME);
		FileUtils.writeStringToFile(file, csv.toString());
	}
	
	public static List<TestResult> parseTestResults(File jscoverReportdir) throws IOException {
		List<TestResult> ret = new ArrayList<>();
		
		File file = new File(jscoverReportdir, FILENAME);
		if (!file.exists()) {
			LOGGER.warn("Run <test-each> before");
			return ret;
		}
		
		String content = FileUtils.readFileToString(file);
		CSVParser parser = CSVParser.parse(content, CSVFormat.DEFAULT);
		List<CSVRecord> records = parser.getRecords();
		for (int i = 1; i < records.size(); i++) { // do not parse first
			CSVRecord record = records.get(i);
			String className = record.get(0);
			String methodName = record.get(1);
			int runs = Integer.parseInt(record.get(2));
			int ignores = Integer.parseInt(record.get(3));
			int failures = Integer.parseInt(record.get(4));
			TestResult result = new TestResult(className, methodName, runs, ignores, failures);
			ret.add(result);
		}
		
		return ret;
	}
}
