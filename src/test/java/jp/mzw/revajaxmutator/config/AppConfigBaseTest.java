package jp.mzw.revajaxmutator.config;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class AppConfigBaseTest {

	@Test
	public void testGetPathToRecordedJsFile() throws MalformedURLException,
			UnsupportedEncodingException {
		AppConfig config = new AppConfig();
		assertEquals(
				"record/app/http%3A%2F%2Fexample.com%3A80%2Fpath%2Fto%2Fapp%2Fjs%2Ffoo.js",
				config.getRecordedJsFile().getPath());
	}

	private static class AppConfig extends AppConfigBase {

		@Override
		public File getRecordDir() {
			return new File("record/app");
		}

		@Override
		public URL getUrl() throws MalformedURLException {
			return new URL(
					"http://example.com:80/path/to/app/main.php?query=string");
		}

		@Override
		public String pathToJsFile() {
			return "js/foo.js";
		}

		@Override
		public File getSuccessCoverageFile() {
			return new File("jscover/app/jscoverage.success.json");
		}

		@Override
		public File getFailureCoverageFile() {
			return new File("jscover/app/jscoverage.failure.json");
		}

	}
	
}
