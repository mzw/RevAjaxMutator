package jp.mzw.revajaxmutator.proxy;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.owasp.webscarab.httpclient.HTTPClient;
import org.owasp.webscarab.model.Request;
import org.owasp.webscarab.model.Response;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;

public class RewriterPlugin extends ProxyPlugin {
	private final String mDirname;
	private final List<String> mRewriteFiles;

	public RewriterPlugin(String dirname) {
		this.mDirname = dirname;
		this.mRewriteFiles = new ArrayList<String>();
	}

	@Override
	public String getPluginName() {
		return "RewriterPlugin";
	}

	@Override
	public HTTPClient getProxyPlugin(HTTPClient client) {
		return new Plugin(client);
	}

	public void setRewriteFile(String filename) {
		if (filename != null && !this.mRewriteFiles.contains(filename)) {
			this.mRewriteFiles.add(filename);
		}
	}

	/** */
	public static final String MUTANT_HEADER_NAME = "mutant";

	/**
	 * TODO Need to implement test cases
	 *
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	protected synchronized void rewriteResponseContent(Request request, Response response) {
		try {
			final String filename = URLEncoder.encode(request.getURL().toString(), "utf-8");

			boolean matched = false;
			String regex = null;
			for (final String _filename : this.mRewriteFiles) {
				final Pattern pattern = Pattern.compile(_filename);
				final Matcher matcher = pattern.matcher(filename);
				if (matcher.find()) {
					matched = true;
					regex = _filename;
					break;
				}
			}
			if (!matched) {
				return;
			}

			BufferedInputStream in = null;
			if (request.getHeader(MUTANT_HEADER_NAME) == null) {
				// Search for .js file
				final Pattern pattern = Pattern.compile(regex);
				for (File file : new File(this.mDirname).listFiles()) {
					if (file.isFile()) {
						final Matcher matcher = pattern.matcher(file.getName());
						if (matcher.find()) {
							in = new BufferedInputStream(new FileInputStream(file));
							break;
						}
						// If file name is too big, it was split into a
						// hierarchy of directories
					} else if (file.isDirectory()) {
						String name = file.getName();
						while (0 < file.listFiles().length) {
							file = file.listFiles()[0];
							name += file.getName();
							if (file.isFile()) {
								break;
							}
						}
						final Matcher matcher = pattern.matcher(name);
						if (matcher.find()) {
							in = new BufferedInputStream(new FileInputStream(file));
							break;
						}
					}
				}
				if (in == null) {
					return;
				}
			} else {
				final String mutantname = request.getHeader(MUTANT_HEADER_NAME);

				String testedFilename = "";

				final File dir = new File(this.mDirname + "/" + "tested");
				final File[] files = dir.listFiles();
				for (final File file : files) {
					if (file.getName().contains(mutantname)) {
						testedFilename = file.getName();
					}
				}
				in = new BufferedInputStream(
						new FileInputStream(this.mDirname + "/" + "tested" + "/" + testedFilename));
			}

			// Get cookie indicating which mutation to apply
			try (final FileWriter fw = new FileWriter("/home/filipe/proxy_debug.txt", true)) {
				final String[] cookies = request.getHeaders("Cookie");
				// fw.write(String.join(", ", cookies) + "\n");
				final String jsMutantRegex = "jsMutantFile=[a-zA-Z0-9]*";
				final Pattern jsMutantPattern = Pattern.compile(jsMutantRegex);
				final Matcher jsMutantMatcher = jsMutantPattern.matcher(cookies[0]);
				if (jsMutantMatcher.find()) {
					final String jsMutant = jsMutantMatcher.group().split("=")[1];
					fw.write(jsMutant + "\n");
				}
			}

			// Replace incoming server .js file with local, mutated .js file
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			final byte[] buf = new byte[1024 * 8];
			int len = 0;
			while ((len = in.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
			in.close();
			response.setContent(out.toByteArray());
		} catch (final FileNotFoundException e) {
			// ignore non-recorded urls
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private class Plugin implements HTTPClient {
		private final HTTPClient mClient;

		public Plugin(HTTPClient client) {
			this.mClient = client;
		}

		@Override
		public Response fetchResponse(Request request) throws IOException {
			// remove if-modified-since and if-none-matche to avoid 304
			request.deleteHeader("If-Modified-Since");
			request.deleteHeader("If-None-Match");

			final Response response = this.mClient.fetchResponse(request);

			if ("200".equals(response.getStatus())) {
				RewriterPlugin.this.rewriteResponseContent(request, response);
			}
			return response;
		}

	}

}
