package jp.mzw.revajaxmutator.proxy;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.owasp.webscarab.httpclient.HTTPClient;
import org.owasp.webscarab.model.HttpUrl;
import org.owasp.webscarab.model.Request;
import org.owasp.webscarab.model.Response;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;

public class FilterPlugin extends ProxyPlugin {

    private String mUrlPrefix;
    private String mMethod;
    private boolean mEnabled;

    public FilterPlugin(String urlPrefix, String method) {
        mUrlPrefix = urlPrefix;
        mMethod = method;
        mEnabled = true;
    }

    @Override
    public String getPluginName() {
        return "FilterPlugin";
    }

    @Override
    public HTTPClient getProxyPlugin(HTTPClient client) {
        return new Plugin(client);
    }

    private boolean shouldFilter(Request request) {
        return matchUrl(request.getURL()) && matchMethod(request.getMethod());
    }

    private boolean matchUrl(HttpUrl url) {
        Pattern p = Pattern.compile(mUrlPrefix);
        Matcher m = p.matcher(url.toString());
        return m.find();
    }

    private boolean matchMethod(String method) {
        return "ALL".equals(mMethod) || mMethod != null && mMethod.equals(method);
    }

    private Response makeErrorResponse() {
        Response response = new Response();
        response.setStatus("404 Not Found");
        response.setContent(new byte[]{});
        response.setHeader("Date", "Mon, 16 Mar 2015 12:07:45 GMT");
        response.setHeader("Server", "Apache/2.2.3 (CentOS)");
        response.setHeader("Connection", "close");
        response.setHeader("Content-Type", "text/html; charset=UTF-8");
        return response;
    }

    String getUrlPrefix() {
        return mUrlPrefix;
    }

    public String getMethod() {
        return mMethod;
    }

    private class Plugin implements HTTPClient {

        private HTTPClient mClient;

        Plugin(HTTPClient client) {
            mClient = client;
        }

        public Response fetchResponse(Request request) throws IOException {
            if (!mEnabled) {
                return mClient.fetchResponse(request);
            }
            if (shouldFilter(request)) {
                System.err.println("Filter request: " + request.getURL());
                return makeErrorResponse();
            }
            return mClient.fetchResponse(request);
        }
    }
}
