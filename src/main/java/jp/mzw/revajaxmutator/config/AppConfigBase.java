package jp.mzw.revajaxmutator.config;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AppConfigBase implements IAppConfigBase {
	protected Logger LOGGER = LoggerFactory.getLogger(AppConfigBase.class);
	
	public AppConfigBase() {
		// for configure by override
	}
	
	Properties config;
	public AppConfigBase(String filename) throws IOException {
		config = new Properties();
		config.load(AppConfigBase.class.getClassLoader().getResourceAsStream(filename));
	}
	
	public File getRecordDir() {
		String record_dir = config.getProperty("ram_record_dir") != null ? config.getProperty("ram_record_dir") : "record/app";
		return new File(record_dir);
	}
	
	public URL getUrl() throws MalformedURLException {
		String _url = config.getProperty("url") != null ? config.getProperty("url") : "http://127.0.0.1:80/index.php?query=string";
		URL url = new URL(_url);
		if(url.getPort() == -1) {
			LOGGER.warn("Not specified port number: " + url.getPath());
		}
		return url;
	}
	
	public String pathToJsFile() {
		return config.getProperty("path_to_js_file") != null ? config.getProperty("path_to_js_file") : "js/foo.js";
	}

	public String pathToHtmlFile() {
		return config.getProperty("path_to_html_file") != null ? config.getProperty("path_to_html_file") : "index.php";
	}

	public String pathToTestCaseFile() {
		return config.getProperty("path_to_test_case_file") != null ? config.getProperty("path_to_test_case_file") : "src/test/java/MyTest.java";
	}
	
	public File getSuccessCoverageFile() {
		String path = config.getProperty("success_cov_file") != null ? config.getProperty("success_cov_file") : "jscover/app/jscoverage.success.json";
		return new File(path);
		
	}
	
	public File getFailureCoverageFile() {
		String path = config.getProperty("failure_cov_file") != null ? config.getProperty("failure_cov_file") : "jscover/app/jscoverage.failure.json";
		return new File(path);
	}

	public File getRecordedJsFile() throws MalformedURLException, UnsupportedEncodingException {
		URL url = new URL(getUrl(), pathToJsFile());
		String filename = URLEncoder.encode(url.toString(), "utf-8");
		return new File(getRecordDir(), filename);
	}

	public File getRecordedHtmlFile() throws MalformedURLException, UnsupportedEncodingException {
		URL url = new URL(getUrl(), pathToHtmlFile());
		String filename = URLEncoder.encode(url.toString(), "utf-8");
		return new File(getRecordDir(), filename);
	}
	
	public File getTestCase() {
		return new File(pathToTestCaseFile());
	}
	
}
