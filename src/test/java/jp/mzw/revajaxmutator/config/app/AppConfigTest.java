package jp.mzw.revajaxmutator.config.app;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.mzw.revajaxmutator.config.app.AppConfig;
import jp.mzw.revajaxmutator.config.mutation.MutateConfiguration;

public class AppConfigTest {

	protected static AppConfig config;

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		config = new AppTestConfig();
	}
	
	@Test
	public void testGetRecordDir() {
		File actual = config.getRecordDir();
		Assert.assertArrayEquals("record/app-test".toCharArray(), actual.getPath().toCharArray());
	}
	
	@Test
	public void testGetJscoverReportDir() {
		File actual = config.getJscoverReportDir();
		Assert.assertArrayEquals("jscover/app-test".toCharArray(), actual.getPath().toCharArray());
	}
	
	@Test
	public void testGetUrl() throws MalformedURLException {
		URL url = config.getUrl();
		Assert.assertArrayEquals("http".toCharArray(), url.getProtocol().toCharArray());
		Assert.assertArrayEquals("www.example.org".toCharArray(), url.getHost().toCharArray());
		Assert.assertEquals(8080, url.getPort());
		Assert.assertArrayEquals("/path/to/index.php".toCharArray(), url.getPath().toCharArray());
		Assert.assertArrayEquals("query=string".toCharArray(), url.getQuery().toCharArray());
	}
	
	@Test
	public void testPathToJsFile() {
		String actual = config.pathToJsFile();
		Assert.assertArrayEquals("path/to/app-test.js".toCharArray(), actual.toCharArray());
	}
	
	@Test
	public void testGetRecordedJsFile() throws MalformedURLException, UnsupportedEncodingException {
		File actual = config.getRecordedJsFile();
		Assert.assertArrayEquals("record/app-test/http%3A%2F%2Fwww.example.org%3A8080%2Fpath%2Fto%2Fpath%2Fto%2Fapp-test.js".toCharArray(), actual.getPath().toCharArray());
	}
	
	@Test
	public void testPathToHtmlFile() {
		String actual = config.pathToHtmlFile();
		Assert.assertArrayEquals("path/to/app-test.php".toCharArray(), actual.toCharArray());
	}
	
	@Test
	public void testGetRecordedHtmlFile() throws MalformedURLException, UnsupportedEncodingException {
		File actual = config.getRecordedHtmlFile();
		Assert.assertArrayEquals("record/app-test/http%3A%2F%2Fwww.example.org%3A8080%2Fpath%2Fto%2Fpath%2Fto%2Fapp-test.php".toCharArray(), actual.getPath().toCharArray());
	}
	
	@Test
	public void testPathToTestcaseFile() {
		String actual = config.pathToTestcaseFile();
		Assert.assertArrayEquals("src/test/java/jp/mzw/revajaxmutator/test/app_test/AppTestTest.java".toCharArray(), actual.toCharArray());
	}
	
	@Test
	public void testGetTestcaseFile() {
		File actual = config.getTestcaseFile();
		Assert.assertArrayEquals("src/test/java/jp/mzw/revajaxmutator/test/app_test/AppTestTest.java".toCharArray(), actual.getPath().toCharArray());
	}

	private static class AppTestConfig extends AppConfig {
		private AppTestConfig() throws IOException {
			super("app-test.properties");
		}

		@Override
		public MutateConfiguration getMutationAnalysisConfig() throws InstantiationException, IllegalAccessException, IOException {
			return null;
		}

		@Override
		public MutateConfiguration getProgramRepairConfig() throws IOException {
			return null;
		}
	}

}
