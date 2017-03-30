package jp.mzw.revajaxmutator.config;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class LocalEnvTest {

	protected static LocalEnv localenv;

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		localenv = new LocalEnv("localenv-test.properties");
	}

	@Test
	public void testGetFirefoxBin() {
		String actual = localenv.getFirefoxBin();
		Assert.assertArrayEquals("path/to/firefox-bin".toCharArray(), actual.toCharArray());
	}
	
	@Test
	public void testGetGeckodriverBin() {
		String actual = localenv.getGeckodriverBin();
		Assert.assertArrayEquals("path/to/geckodriver".toCharArray(), actual.toCharArray());
	}

	@Test
	public void testGetPhantomjsBin() {
		String actual = localenv.getPhantomjsBin();
		Assert.assertArrayEquals("path/to/bin/phantomjs".toCharArray(), actual.toCharArray());
	}

	@Test
	public void testGetProxyIp() {
		String actual = localenv.getProxyIp();
		Assert.assertArrayEquals("127.0.0.1".toCharArray(), actual.toCharArray());
	}

	@Test
	public void testGetProxyPort() {
		int actual = localenv.getProxyPort();
		Assert.assertEquals(8080, actual);
	}

	@Test
	public void testGetProxyAddress() {
		String actual = localenv.getProxyAddress();
		Assert.assertArrayEquals("127.0.0.1:8080".toCharArray(), actual.toCharArray());
	}

	@Test
	public void testGetTimeout() {
		long actual = localenv.getTimeout();
		Assert.assertEquals(3, actual);
	}
	
	@Test
	public void testGetThreadNum() {
		int expected = Runtime.getRuntime().availableProcessors();
		int actual = localenv.getThreadNum();
		Assert.assertEquals(expected, actual);
	}
}
