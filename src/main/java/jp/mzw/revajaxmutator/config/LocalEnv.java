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
		FIREFOX_BIN, GECKODRIVER_BIN, PHANTOMJS_BIN, CHROME_BIN, PROXY_IP, PROXY_PORT, TIMEOUT, THREAD_NUM, SELENIUM_HUB_IP, JSCOVER_REPORT_DIR, JSCOVER_IP, JSCOVER_PORT;
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
			case PROXY_IP:
				return "127.0.0.1";
			case PROXY_PORT:
				return new Integer(8081).toString();
			case TIMEOUT:
				return new Long(3).toString();
			case THREAD_NUM:
				return new Integer(Runtime.getRuntime().availableProcessors()).toString();
			case SELENIUM_HUB_IP:
				return null;
			case JSCOVER_REPORT_DIR:
				return "jscover";
			case JSCOVER_IP:
				return "127.0.0.1";
			case JSCOVER_PORT:
				return new Integer(3129).toString();
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

	public String getPhantomjsBin() {
		return this.getParam(Param.PHANTOMJS_BIN);
	}

	public String getChromeBin() {
		return this.getParam(Param.CHROME_BIN);
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
}
