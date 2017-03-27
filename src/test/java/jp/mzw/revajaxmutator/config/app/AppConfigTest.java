package jp.mzw.revajaxmutator.config.app;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import jp.mzw.revajaxmutator.config.app.AppConfig;
import jp.mzw.revajaxmutator.config.mutation.MutateConfiguration;

public class AppConfigTest {

	@Test
	public void testGetPathToRecordedJsFile() throws IOException {
		AppConfig config = new TestAppConfig();
		assertEquals(
				"record/app/http%3A%2F%2Fexample.com%3A80%2Fpath%2Fto%2Fapp%2Fjs%2Ffoo.js",
				config.getRecordedJsFile().getPath());
	}

	private static class TestAppConfig extends AppConfig {

		protected TestAppConfig() throws IOException {
			super("");
		}

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
		public MutateConfiguration getMutationAnalysisConfig() throws InstantiationException, IllegalAccessException, IOException {
			return null;
		}

		@Override
		public MutateConfiguration getProgramRepairConfig() throws IOException {
			return null;
		}

	}
	
}
