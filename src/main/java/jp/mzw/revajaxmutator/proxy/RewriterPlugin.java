package jp.mzw.revajaxmutator.proxy;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	private String mDirname;
	private List<String> mRewriteFiles;

	public RewriterPlugin(String dirname) {
		mDirname = dirname;
		mRewriteFiles = new ArrayList<String>();
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
		if (filename != null && !mRewriteFiles.contains(filename)) {
			mRewriteFiles.add(filename);
		}
	}

	/** */
	public static final String MUTANT_HEADER_NAME = "mutant";
	
	/**
	 * TODO Need to implement test cases
	 * 
	 * @param request
	 * @param response
	 */
	protected synchronized void rewriteResponseContent(Request request, Response response) {
		try {
			String filename = URLEncoder.encode(request.getURL().toString(), "utf-8");

			boolean matched = false;
			String regex = null;
			for (String _filename : mRewriteFiles) {
				Pattern pattern = Pattern.compile(_filename);
				Matcher matcher = pattern.matcher(filename);
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
				Pattern pattern = Pattern.compile(regex);
				for (File file : new File(mDirname).listFiles()) {
					if (file.isFile()) {
						Matcher matcher = pattern.matcher(file.getName());
						if (matcher.find()) {
							in = new BufferedInputStream(new FileInputStream(file));
							break;
						}
					} else if (file.isDirectory()) {
						String name = file.getName();
						while (0 < file.listFiles().length) {
							file = file.listFiles()[0];
							name += file.getName();
							if (file.isFile()) {
								break;
							}
						}
						Matcher matcher = pattern.matcher(name);
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
				String mutantname = request.getHeader(MUTANT_HEADER_NAME);

				String testedFilename = "";

				File dir = new File(mDirname + "/" + "tested");
				File[] files = dir.listFiles();
				for (File file : files) {
					if (file.getName().contains(mutantname)) {
						testedFilename = file.getName();
					}
				}
				in = new BufferedInputStream(new FileInputStream(mDirname + "/" + "tested" + "/" + testedFilename));
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024 * 8];
			int len = 0;
			while ((len = in.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
			in.close();
			response.setContent(out.toByteArray());
		} catch (FileNotFoundException e) {
			// ignore non-recorded urls
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class Plugin implements HTTPClient {
		private HTTPClient mClient;

		public Plugin(HTTPClient client) {
			mClient = client;
		}

		public Response fetchResponse(Request request) throws IOException {
			// remove if-modified-since and if-none-matche to avoid 304
			request.deleteHeader("If-Modified-Since");
			request.deleteHeader("If-None-Match");

			Response response = mClient.fetchResponse(request);

			if ("200".equals(response.getStatus())) {
				rewriteResponseContent(request, response);
			}
			return response;
		}

	}

}
