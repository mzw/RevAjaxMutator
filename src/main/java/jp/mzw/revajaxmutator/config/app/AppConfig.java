package jp.mzw.revajaxmutator.config.app;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import flex.messaging.util.URLDecoder;
import jp.mzw.revajaxmutator.config.mutation.MutateConfiguration;
import jp.mzw.revajaxmutator.search.Sorter;

public abstract class AppConfig implements IAppConfig {
	protected static Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

	abstract public MutateConfiguration getMutationAnalysisConfig()
			throws InstantiationException, IllegalAccessException, IOException;

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
		this.config = new Properties();
		this.config.load(AppConfig.class.getClassLoader().getResourceAsStream(filename));
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
		URL, RAM_RECORD_DIR, JSCOVER_REPORT_DIR, PATH_TO_JS_FILE, PATH_TO_HTML_FILE, PATH_TO_TESTCASE_FILE, SORT_TYPE;
		public static String getDefault(Param param) {
			switch (param) {
			case URL:
				return "http://127.0.0.1:80/index.php?query=string";
			case RAM_RECORD_DIR:
				return "record/app";
			case JSCOVER_REPORT_DIR:
				return "jscover";
			case PATH_TO_JS_FILE:
				return "js/foo.js";
			case PATH_TO_HTML_FILE:
				return "index.html";
			case PATH_TO_TESTCASE_FILE:
				return "src/test/java/package/MyTest.java";
			case SORT_TYPE:
				return Sorter.SortType.REPAIR_SOURCE_DFS.name();
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
		final String p = param.name().toLowerCase();
		return this.config.getProperty(p) != null ? this.config.getProperty(p) : Param.getDefault(param);
	}

	/**
	 * Get parameter values written in application configuration file
	 *
	 * @param param
	 * @return
	 */
	public String getParam(String param) {
		return this.config.getProperty(param.toLowerCase());
	}

	/**
	 *
	 */
	@Override
	public File getRecordDir() {
		final String param = this.getParam(Param.RAM_RECORD_DIR);
		return new File(param);
	}

	/**
	 *
	 */
	@Override
	public File getJscoverReportDir() {
		final String param = this.getParam(Param.JSCOVER_REPORT_DIR);
		return new File(param);
	}

	/**
	 *
	 */
	@Override
	public URL getUrl() throws MalformedURLException {
		final String param = this.getParam(Param.URL);
		final URL url = new URL(param);
		if (url.getPort() == -1) {
			LOGGER.warn("Not specified port number: " + url.getPath());
		}
		return url;
	}

	/**
	 *
	 */
	@Override
	public String pathToJsFile() {
		return this.getParam(Param.PATH_TO_JS_FILE);
	}

	/**
	 *
	 * @return
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 */
	public File getRecordedJsFile() throws MalformedURLException, UnsupportedEncodingException {
		final URL url = new URL(this.getUrl(), this.pathToJsFile());

		final String[] splits = url.toString().split("<regex>|</regex>");
		final StringBuilder regex = new StringBuilder();
		for (int i = 0; i < splits.length; i++) {
			if (i % 2 == 0) {
				regex.append(URLEncoder.encode(splits[i], "utf-8"));
			} else {
				regex.append(splits[i]);
			}
		}

		final Pattern pattern = Pattern.compile(regex.toString());
		for (File file : this.getRecordDir().listFiles()) {
			if (file.isFile() && !this.isMutantFile(file.getName())) {
				final Matcher matcher = pattern.matcher(file.getName());
				if (matcher.find()) {
					LOGGER.info("Found target JavaScript file: {}", file.getPath());
					return file;
				}
			} else if (file.isDirectory()) {
				String name = file.getName();
				while (0 < file.listFiles().length) {
					file = file.listFiles()[0];
					name += file.getName();
					if (file.isFile()) {
						break;
					}
				}
				final Matcher matcher = pattern.matcher(name);
				if (matcher.find()) {
					LOGGER.info("Found target JavaScript file: {}", file.getPath());
					return file;
				}
			}
		}

		LOGGER.warn("Not found target JavaScript file: {}", url.toString());
		return null;
	}

	/**
	 * Get recorded filename
	 *
	 * @param baseUrl
	 * @param pathToFile
	 * @return
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 */
	public static String getRecordedFileName(URL baseUrl, String pathToFile)
			throws MalformedURLException, UnsupportedEncodingException {
		final URL url = new URL(baseUrl, pathToFile);
		final String[] splits = url.toString().split("<regex>|</regex>");
		final StringBuilder regex = new StringBuilder();
		for (int i = 0; i < splits.length; i++) {
			if (i % 2 == 0) {
				regex.append(URLEncoder.encode(splits[i], "utf-8"));
			} else {
				regex.append(splits[i]);
			}
		}
		return regex.toString();
	}

	/**
	 *
	 * @return
	 */
	public String pathToHtmlFile() {
		return this.getParam(Param.PATH_TO_HTML_FILE);
	}

	/**
	 *
	 * @return
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 */
	public File getRecordedHtmlFile() throws MalformedURLException, UnsupportedEncodingException {
		final URL url = new URL(this.getUrl(), this.pathToHtmlFile());

		final String[] splits = url.toString().split("<regex>|</regex>");
		final StringBuilder regex = new StringBuilder();
		for (int i = 0; i < splits.length; i++) {
			if (i % 2 == 0) {
				regex.append(URLEncoder.encode(splits[i], "utf-8"));
			} else {
				regex.append(splits[i]);
			}
		}

		final Pattern pattern = Pattern.compile(regex.toString());
		for (File file : this.getRecordDir().listFiles()) {
			if (file.isFile()) {
				final Matcher matcher = pattern.matcher(file.getName());
				if (matcher.find()) {
					LOGGER.info("Found target HTML file: {}", file.getPath());
					return file;
				}
			} else if (file.isDirectory()) {
				String name = file.getName();
				while (0 < file.listFiles().length) {
					file = file.listFiles()[0];
					name += file.getName();
					if (file.isFile()) {
						break;
					}
				}
				final Matcher matcher = pattern.matcher(name);
				if (matcher.find()) {
					LOGGER.info("Found target HTML file: {}", file.getPath());
					return file;
				}
			}
		}

		LOGGER.warn("Not found target HTML file: {}", url.toString());
		return null;
	}

	/**
	 * 
	 * 
	 * @return
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 */
	public URL getJsUrl() throws MalformedURLException, UnsupportedEncodingException {
		final URL url = new URL(this.getUrl(), this.pathToJsFile());
		final String[] splits = url.toString().split("<regex>|</regex>");
		final StringBuilder regex = new StringBuilder();
		for (int i = 0; i < splits.length; i++) {
			if (i % 2 == 0) {
				regex.append(URLEncoder.encode(splits[i], "utf-8"));
			} else {
				regex.append(splits[i]);
			}
		}
		return new URL(URLDecoder.decode(regex.toString(), "utf-8"));
	}

	/**
	 *
	 * @return
	 */
	public String pathToTestcaseFile() {
		return this.getParam(Param.PATH_TO_TESTCASE_FILE);
	}

	/**
	 *
	 * @return
	 */
	public File getTestcaseFile() {
		return new File(this.pathToTestcaseFile());
	}

	private boolean isMutantFile(String filename) {
		final int i = filename.lastIndexOf('.');
		if (i > 0 && filename.substring(i + 1).matches("[0-9]+")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 *
	 * @return
	 */
	public Sorter.SortType getSortType() {
		final String param = this.getParam(Param.SORT_TYPE);
		return Sorter.getSortType(param);
	}

}
