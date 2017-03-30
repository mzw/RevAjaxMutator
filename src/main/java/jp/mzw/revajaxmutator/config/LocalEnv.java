package jp.mzw.revajaxmutator.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LocalEnv {

	public static final String FILENAME = "localenv.properties";

	protected Properties config;

	public LocalEnv(String filename) throws IOException {
		InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
		config = new Properties();
		config.load(is);
	}
	
	public static enum Param {
		FIREFOX_BIN, GECKODRIVER_BIN, PHANTOMJS_BIN, PROXY_IP, PROXY_PORT, TIMEOUT, THREAD_NUM;
		public static String getDefault(Param param) {
			switch(param) {
			case FIREFOX_BIN:
				return null;
			case GECKODRIVER_BIN:
				return null;
			case PHANTOMJS_BIN:
				return null;
			case PROXY_IP:
				return "127.0.0.1";
			case PROXY_PORT:
				return new Integer(8080).toString();
			case TIMEOUT:
				return new Long(3).toString();
			case THREAD_NUM:
				return new Integer(Runtime.getRuntime().availableProcessors()).toString();
			}
			return "";
		}
	}
	
	public String getParam(Param param) {
		String p = param.name().toLowerCase();
		return config.getProperty(p) != null ? config.getProperty(p) : Param.getDefault(param);
	}

	public String getFirefoxBin() {
		return getParam(Param.FIREFOX_BIN);
	}
	
	public String getGeckodriverBin() {
		return getParam(Param.GECKODRIVER_BIN);
	}

	public String getPhantomjsBin() {
		return getParam(Param.PHANTOMJS_BIN);
	}

	public String getProxyIp() {
		return getParam(Param.PROXY_IP);
	}

	public int getProxyPort() {
		String param = getParam(Param.PROXY_PORT);
		return Integer.parseInt(param);
	}

	public String getProxyAddress() {
		return this.getProxyIp() + ":" + this.getProxyPort();
	}

	public long getTimeout() {
		String param = getParam(Param.TIMEOUT);
		return Long.parseLong(param);
	}
	
	public int getThreadNum() {
		String param = getParam(Param.THREAD_NUM);
		return Integer.parseInt(param);
	}
}
