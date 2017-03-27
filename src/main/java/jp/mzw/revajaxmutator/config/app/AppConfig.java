package jp.mzw.revajaxmutator.config.app;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.mzw.revajaxmutator.config.mutation.MutateConfiguration;

public abstract class AppConfig implements IAppConfig {
	protected Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

	abstract public MutateConfiguration getMutationAnalysisConfig() throws InstantiationException, IllegalAccessException, IOException;

	abstract public MutateConfiguration getProgramRepairConfig() throws IOException;

	/** Contain configuration parameters */
	protected Properties config;

	/**
	 * Constructor
	 * 
	 * @param filename
	 * @throws IOException
	 */
	protected AppConfig(String filename) throws IOException {
		config = new Properties();
		config.load(AppConfig.class.getClassLoader().getResourceAsStream(filename));
	}

	/**
	 * Constructor
	 * 
	 * @param config
	 */
	public AppConfig(Properties config) {
		this.config = config;
	}

	/**
	 * 
	 * @return
	 */
	protected Properties getConfig() {
		return this.config;
	}

	/**
	 * Parameters and default values
	 * 
	 * @author Yuta Maezawa
	 *
	 */
	public static enum Param {
		URL, RAM_RECORD_DIR, JSCOVER_REPORT_DIR, PATH_TO_JS_FILE, PATH_TO_HTML_FILE, PATH_TO_TESTCASE_FILE;
		public static String getDefault(Param param) {
			switch (param) {
			case URL:
				return "http://127.0.0.1:80/index.php?query=string";
			case RAM_RECORD_DIR:
				return "record/app";
			case JSCOVER_REPORT_DIR:
				return "jscover/app";
			case PATH_TO_JS_FILE:
				return "js/foo.js";
			case PATH_TO_HTML_FILE:
				return "index.php";
			case PATH_TO_TESTCASE_FILE:
				return "src/test/java/package/MyTest.java";
			}
			return "";
		}
	}

	/**
	 * Get parameter values written in application configuration file
	 * 
	 * @param param
	 * @return
	 */
	public String getParam(Param param) {
		String p = param.name().toLowerCase();
		return config.getProperty(p) != null ? config.getProperty(p) : Param.getDefault(param);
	}

	/**
	 * 
	 */
	public File getRecordDir() {
		String param = getParam(Param.RAM_RECORD_DIR);
		return new File(param);
	}

	public File getJscoverReportDir() {
		String param = getParam(Param.JSCOVER_REPORT_DIR);
		return new File(param);
	}

	/**
	 * 
	 */
	public URL getUrl() throws MalformedURLException {
		String param = getParam(Param.URL);
		URL url = new URL(param);
		if (url.getPort() == -1) {
			LOGGER.warn("Not specified port number: " + url.getPath());
		}
		return url;
	}

	/**
	 * 
	 */
	public String pathToJsFile() {
		return getParam(Param.PATH_TO_JS_FILE);
	}

	/**
	 * 
	 * @return
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 */
	public File getRecordedJsFile() throws MalformedURLException, UnsupportedEncodingException {
		URL url = new URL(getUrl(), pathToJsFile());
		String filename = URLEncoder.encode(url.toString(), "utf-8");
		return new File(getRecordDir(), filename);
	}

	/**
	 * 
	 * @return
	 */
	public String pathToHtmlFile() {
		return getParam(Param.PATH_TO_HTML_FILE);
	}

	/**
	 * 
	 * @return
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 */
	public File getRecordedHtmlFile() throws MalformedURLException, UnsupportedEncodingException {
		URL url = new URL(getUrl(), pathToHtmlFile());
		String filename = URLEncoder.encode(url.toString(), "utf-8");
		return new File(getRecordDir(), filename);
	}

	/**
	 * 
	 * @return
	 */
	public String pathToTestcaseFile() {
		return getParam(Param.PATH_TO_TESTCASE_FILE);
	}

	/**
	 * 
	 * @return
	 */
	public File getTestcaseFile() {
		return new File(pathToTestcaseFile());
	}

}