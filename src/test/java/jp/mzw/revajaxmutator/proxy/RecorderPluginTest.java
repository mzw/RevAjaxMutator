package jp.mzw.revajaxmutator.proxy;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.owasp.webscarab.model.HttpUrl;
import org.owasp.webscarab.model.Request;
import org.owasp.webscarab.model.Response;

import jp.mzw.revajaxmutator.config.app.AppConfig;
import jp.mzw.revajaxmutator.config.mutation.MutateConfiguration;

public class RecorderPluginTest {

	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();

	@Test
	public void testFileNameTooLong()
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		Method method = RecorderPlugin.class.getDeclaredMethod("recordResponseContent", Request.class, Response.class);
		method.setAccessible(true);

		String dirname = tmpDir.getRoot().getPath();
		String filename = RandomStringUtils.randomAlphabetic(256);
		StringBuilder url = new StringBuilder("http://example.org:80/too/long/");
		url.append(filename);

		RecorderPlugin plugin = new RecorderPlugin(dirname);
		Request request = new Request();
		request.setURL(new HttpUrl(url.toString()));
		Response response = new Response();

		method.invoke(plugin, request, response);

		Properties params = new Properties();
		params.setProperty("url", url.toString());
		params.setProperty("path_to_js_file", filename);
		params.setProperty("ram_record_dir", dirname);
		AppConfig config = new AppTestConfig(params);
		File expect = config.getRecordedJsFile();

		Assert.assertTrue(expect.exists());
	}

	private static class AppTestConfig extends AppConfig {
		public AppTestConfig(Properties params) throws IOException {
			super(params);
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
