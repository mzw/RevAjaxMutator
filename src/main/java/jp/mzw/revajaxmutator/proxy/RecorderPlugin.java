package jp.mzw.revajaxmutator.proxy;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

import org.owasp.webscarab.httpclient.HTTPClient;
import org.owasp.webscarab.model.Request;
import org.owasp.webscarab.model.Response;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;

public class RecorderPlugin extends ProxyPlugin {
	private String mDirname;

	public RecorderPlugin(String dirname) {
		mDirname = dirname;
	}

	@Override
	public String getPluginName() {
		return "RecorderPlugin";
	}

	@Override
	public HTTPClient getProxyPlugin(HTTPClient client) {
		return new Plugin(client);
	}

	private void recordResponseContent(Request request, Response response) {
		try {
			File dir = new File(mDirname);
			String filename = URLEncoder.encode(request.getURL().toString(), "utf-8");
			// file name too long
			int length = filename.length();
			while (128 < length) {
				String subdirname = filename.substring(0, 127);
				dir = new File(dir, subdirname);
				filename = filename.substring(127);
				length = filename.length();
			}
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(dir, filename);
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
			out.write(response.getContent());
			out.close();
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

			if (request.getURL() != null) {
				Response response = mClient.fetchResponse(request);
				if ("200".equals(response.getStatus())) {
					recordResponseContent(request, response);
				}
				return response;
			}
			return null;
		}

	}

}
