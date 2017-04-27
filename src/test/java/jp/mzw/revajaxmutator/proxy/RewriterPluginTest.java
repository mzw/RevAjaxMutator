package jp.mzw.revajaxmutator.proxy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.owasp.webscarab.model.HttpUrl;
import org.owasp.webscarab.model.Request;
import org.owasp.webscarab.model.Response;

public class RewriterPluginTest {

	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();

	@Test
	public void testRewriteJSFileWithMutant() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IOException {
		// Use reflection to allow access to the private method
		final Method method = RewriterPlugin.class.getDeclaredMethod("rewriteResponseContent", Request.class,
				Response.class);
		method.setAccessible(true);

		final Request request = new Request();
		final Response response = new Response();

		// Create mutant file
		final String mutantId = "1";
		final String jsFilename = "salt.min.js";
		final String dummyMutantContent = "window.$=function(e,t,l){var n={\"#\":\"getElementById\",\".\":\"getElementsByClassName\",\"@\":\"getElementsByName\",\"=\":\"getElementsByTagName\",\"*\":\"querySelectorAll\"}[e[0]],m=(t===l?document:t)[n](e.slice(1));return m.length<2?m[0]:m};\n";
		this.tmpDir.create();
		final File tmpFile = this.tmpDir.newFile(jsFilename + "." + mutantId);
		try (PrintWriter out = new PrintWriter(tmpFile)) {
			out.print(dummyMutantContent);
		}

		// Fetch file from some website
		final String getUrl = "http://example.org:80/" + jsFilename;
		request.setURL(new HttpUrl(getUrl));
		request.setHeader("Cookie", mutantId);

		final RewriterPlugin plugin = new RewriterPlugin(this.tmpDir.getRoot().getAbsolutePath());
		plugin.setRewriteFile(jsFilename);

		method.invoke(plugin, request, response);

		final String responseFileContent = new String(response.getContent(), "UTF-8");

		Assert.assertEquals(responseFileContent, dummyMutantContent);
	}
}
