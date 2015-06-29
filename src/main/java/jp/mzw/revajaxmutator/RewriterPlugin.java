package jp.mzw.revajaxmutator;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;

import org.owasp.webscarab.httpclient.HTTPClient;
import org.owasp.webscarab.model.Request;
import org.owasp.webscarab.model.Response;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;

public class RewriterPlugin extends ProxyPlugin {
    private String mDirname;

    public RewriterPlugin(String dirname) {
        mDirname = dirname;
    }

    @Override
    public String getPluginName() {
        return "RewriterPlugin";
    }

    @Override
    public HTTPClient getProxyPlugin(HTTPClient client) {
        return new Plugin(client);
    }

    private void rewriteResponseContent(Request request, Response response) {
        try {
            String filename = URLEncoder.encode(request.getURL().toString(), "utf-8");
            BufferedInputStream in = new BufferedInputStream(
                    new FileInputStream(mDirname + "/" + filename));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024 * 8];
            int len = 0;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            in.close();
            response.setContent(out.toByteArray());
        }
        catch (FileNotFoundException e) {
            // ignore non-recorded urls
        }
        catch (IOException e) {
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
