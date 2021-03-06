package jp.mzw.revajaxmutator.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LocalEnv {

	public static final String FILENAME = "localenv.properties";

	protected Properties config;

	private static boolean shouldRunJSCoverProxy;

	public LocalEnv(String filename) throws IOException {
		final InputStream is = this.getClass().getClassLoader().getResourceAsStream(filename);
		this.config = new Properties();
		this.config.load(is);
	}

	public static enum Param {
		FIREFOX_BIN, GECKODRIVER_BIN, PHANTOMJS_BIN, CHROME_BIN, CHROMEDRIVER_BIN, CHROME_HEADLESS, PROXY_IP, PROXY_PORT, TIMEOUT, THREAD_NUM, SELENIUM_HUB_IP, JSCOVER_REPORT_DIR, JSCOVER_IP, JSCOVER_PORT, LIMITED_TIME_MIN;
		public static String getDefault(Param param) {
			switch (param) {
			case FIREFOX_BIN:
				return null;
			case GECKODRIVER_BIN:
				return null;
			case PHANTOMJS_BIN:
				return null;
			case CHROME_BIN:
				return null;
			case CHROMEDRIVER_BIN:
				return null;
			case CHROME_HEADLESS:
				return Boolean.toString(false);
			case PROXY_IP:
				return "127.0.0.1";
			case PROXY_PORT:
				return Integer.toString(8081);
			case TIMEOUT:
				return Long.toString(3);
			case THREAD_NUM:
				return new Integer(Runtime.getRuntime().availableProcessors()).toString();
			case SELENIUM_HUB_IP:
				return null;
			case JSCOVER_REPORT_DIR:
				return "jscover";
			case JSCOVER_IP:
				return "127.0.0.1";
			case JSCOVER_PORT:
				return Integer.toString(3129);
			case LIMITED_TIME_MIN:
				return Long.toString(Long.MAX_VALUE); // i.e., not limit
			}
			return "";
		}
	}

	public String getParam(Param param) {
		final String p = param.name().toLowerCase();
		return this.config.getProperty(p) != null ? this.config.getProperty(p) : Param.getDefault(param);
	}

	public String getFirefoxBin() {
		return this.getParam(Param.FIREFOX_BIN);
	}

	public String getGeckodriverBin() {
		return this.getParam(Param.GECKODRIVER_BIN);
	}

	public boolean useFirefox() {
		return this.getFirefoxBin() != null && this.getGeckodriverBin() != null;
	}

	public String getPhantomjsBin() {
		return this.getParam(Param.PHANTOMJS_BIN);
	}

	public boolean usePhantomjs() {
		return this.getPhantomjsBin() != null;
	}

	public String getChromeBin() {
		return this.getParam(Param.CHROME_BIN);
	}

	public String getChromedriverBin() {
		return this.getParam(Param.CHROMEDRIVER_BIN);
	}

	public boolean getChromeHeadless() {
		final String flag = this.getParam(Param.CHROME_HEADLESS);
		return Boolean.parseBoolean(flag);
	}

	public boolean useChrome() {
		return this.getChromeBin() != null && this.getChromedriverBin() != null;
	}

	public String getProxyIp() {
		String param;
		if (LocalEnv.shouldRunJSCoverProxy) {
			param = this.getParam(Param.JSCOVER_IP);
		} else {
			param = this.getParam(Param.PROXY_IP);
		}
		return param;
	}

	public int getProxyPort() {
		String param;
		if (LocalEnv.shouldRunJSCoverProxy) {
			param = this.getParam(Param.JSCOVER_PORT);
		} else {
			param = this.getParam(Param.PROXY_PORT);
		}
		return Integer.parseInt(param);
	}

	public String getProxyAddress() {
		return this.getProxyIp() + ":" + this.getProxyPort();
	}

	public long getTimeout() {
		final String param = this.getParam(Param.TIMEOUT);
		return Long.parseLong(param);
	}

	public int getThreadNum() {
		final String param = this.getParam(Param.THREAD_NUM);
		return Integer.parseInt(param);
	}

	public String getSeleniumHubAddress() {
		return this.getParam(Param.SELENIUM_HUB_IP);
	}

	public boolean useSeleniumGrid() {
		return this.getSeleniumHubAddress() != null;
	}

	public String getJsCoverageDir() {
		return this.getParam(Param.JSCOVER_REPORT_DIR);
	}

	public String getJsCoveragePort() {
		return this.getParam(Param.JSCOVER_PORT);
	}

	public static boolean shouldRunJSCoverProxy() {
		return LocalEnv.shouldRunJSCoverProxy;
	}

	public void setShouldRunJSCoverProxy(boolean shouldRunJSCoverProxy) {
		LocalEnv.shouldRunJSCoverProxy = shouldRunJSCoverProxy;
	}

	public long getLimitedTimeMin() {
		String param = this.getParam(Param.LIMITED_TIME_MIN);
		return Long.parseLong(param);
	}
}
