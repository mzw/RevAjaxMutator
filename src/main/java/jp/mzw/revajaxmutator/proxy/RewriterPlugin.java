package jp.mzw.revajaxmutator.proxy;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.owasp.webscarab.httpclient.HTTPClient;
import org.owasp.webscarab.model.Request;
import org.owasp.webscarab.model.Response;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.mzw.revajaxmutator.config.app.AppConfig;

public class RewriterPlugin extends ProxyPlugin {
	protected static final Logger LOGGER = LoggerFactory.getLogger(RewriterPlugin.class);
	
	protected final String mDirname;
	protected final List<String> mRewriteFiles;

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
	 *
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	protected synchronized void rewriteResponseContent(Request request, Response response) {
		try {
			final String filename = this.checkIfRequestIsForTargetJsFile(request);
			if (filename == null || filename == "") {
				return;
			}

			String mutantId = this.getMutantFileIdentifier(request);
			if (mutantId != "") {
				mutantId = "." + mutantId;
			}

			// Get the mutated file to replace in the response message
			try (BufferedInputStream in = this.findMutantFile(request, filename, mutantId);
					ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				// Replace incoming server .js file with local, mutated .js file
				final byte[] buf = new byte[1024 * 8];
				int len = 0;
				while ((len = in.read(buf)) != -1) {
					out.write(buf, 0, len);
				}
				response.setContent(out.toByteArray());
			}
		} catch (final FileNotFoundException e) {
			// ignore non-recorded urls
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	protected String checkIfRequestIsForTargetJsFile(final Request request) throws UnsupportedEncodingException {
		final String url = URLEncoder.encode(request.getURL().toString(), "utf-8");
		for (final String _filename : this.mRewriteFiles) {
			final Pattern pattern = Pattern.compile(_filename);
			final Matcher matcher = pattern.matcher(url);
			if (matcher.find()) {
				return _filename;
			}
		}
		return null;
	}

	private String getMutantFileIdentifier(Request request) {
		final String[] cookies = request.getHeaders("Cookie");
		final String jsMutantRegex = "jsMutantId=[^;]*";
		final Pattern jsMutantPattern = Pattern.compile(jsMutantRegex);
		final Matcher jsMutantMatcher = jsMutantPattern.matcher(cookies[0]);
		if (jsMutantMatcher.find()) {
			final String[] splits = jsMutantMatcher.group().split("=");
			if (splits.length > 1) {
				return splits[1];
			}
			return "";
		}
		return "";
	}

	protected BufferedInputStream findMutantFile(Request request, String jsFilename, String mutantExt)
			throws FileNotFoundException {
		if (request.getHeader(MUTANT_HEADER_NAME) == null) {
			// Search for .js file
			final Pattern pattern = Pattern.compile(jsFilename);
			for (File file : new File(this.mDirname).listFiles()) {
				if (file.isFile()) {
					final Matcher matcher = pattern.matcher(file.getName());
					if (matcher.find() && file.getName().endsWith(mutantExt)) {
						return new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
					}
					// If file name is too big, it was split into a
					// hierarchy of directories
				} else if (file.isDirectory()) {
					for (File candidate : FileUtils.listFiles(file, FileFilterUtils.fileFileFilter(), FileFilterUtils.trueFileFilter())) {
						String name = AppConfig.getTooLongUrlName(new File(mDirname), candidate);
						if (pattern.matcher(name).find() && name.endsWith(mutantExt)) {
							return new BufferedInputStream(new FileInputStream(candidate.getAbsolutePath()));
						}
					}
				}
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
			return new BufferedInputStream(new FileInputStream(this.mDirname + "/" + "tested" + "/" + testedFilename));
		}
		System.out.println("FILE NOT FOUND!!!!");
		throw new FileNotFoundException();
	}

	private class Plugin implements HTTPClient {
		private final HTTPClient mClient;

		public Plugin(HTTPClient client) {
			this.mClient = client;
		}

		@Override
		public Response fetchResponse(Request request) throws IOException {
			// remove if-modified-since and if-none-match to avoid 304
			request.deleteHeader("If-Modified-Since");
			request.deleteHeader("If-None-Match");

			if (request.getURL() != null) {
				final Response response = this.mClient.fetchResponse(request);

				if ("200".equals(response.getStatus())) {
					if (request.getURL().getPath().endsWith(".png")) {
						// skip
					} else {
						RewriterPlugin.this.rewriteResponseContent(request, response);
					}
				}
				return response;
			}
			return null;
		}

	}

}
