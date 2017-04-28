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
import jp.mzw.revajaxmutator.search.Sorter;

public class AppConfigTest {

	protected static AppConfig config;

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		config = new AppTestConfig();
	}

	@Test
	public void testGetRecordDir() {
		File actual = config.getRecordDir();
		Assert.assertArrayEquals("src/test/resources/record-test/app-test".toCharArray(), actual.getPath().toCharArray());
	}

	@Test
	public void testGetJscoverReportDir() {
		File actual = config.getJscoverReportDir();
		Assert.assertArrayEquals("src/test/resources/jscover-test/app-test".toCharArray(), actual.getPath().toCharArray());
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
		Assert.assertNotNull(actual);
	}

	@Test
	public void testPathToHtmlFile() {
		String actual = config.pathToHtmlFile();
		Assert.assertArrayEquals("path/to/app-test.php?query=string&id=<regex>[a-zA-Z0-9]*</regex>&foo=bar".toCharArray(), actual.toCharArray());
	}
	
	@Test
	public void testGetRecordedFileName() throws MalformedURLException, UnsupportedEncodingException {
		String actual = AppConfig.getRecordedFileName(config.getUrl(), config.pathToHtmlFile());
		Assert.assertArrayEquals("http%3A%2F%2Fwww.example.org%3A8080%2Fpath%2Fto%2Fpath%2Fto%2Fapp-test.php%3Fquery%3Dstring%26id%3D[a-zA-Z0-9]*%26foo%3Dbar".toCharArray(), actual.toCharArray());
	}

	@Test
	public void testGetRecordedHtmlFile() throws MalformedURLException, UnsupportedEncodingException {
		File actual = config.getRecordedHtmlFile();
		Assert.assertNotNull(actual);
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
	
	@Test
	public void testGetSortType() {
		Sorter.SortType actual = config.getSortType();
		Assert.assertEquals(Sorter.SortType.REPAIR_SOURCE_DFS, actual);
	}

	@Test
	public void testGetParamWithString() {
		String actual = config.getParam("path_to_js_file");
		Assert.assertArrayEquals("path/to/app-test.js".toCharArray(), actual.toCharArray());
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
