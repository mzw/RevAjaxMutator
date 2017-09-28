package jp.mzw.revajaxmutator.proxy.owaspzed;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.httpclient.URI;
import org.parosproxy.paros.core.proxy.ProxyListener;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpResponseHeader;

public class FilterListener implements ProxyListener {

    private String urlPrefix;
    private String method;

    FilterListener(String urlPrefix, String method) {
        this.urlPrefix = urlPrefix;
        this.method = method.toUpperCase();
    }

    @Override
    public boolean onHttpRequestSend(HttpMessage httpMessage) {
        return true;
    }

    @Override
    public boolean onHttpResponseReceive(HttpMessage httpMessage) {
        if (shouldFilter(httpMessage)) {
            System.err.println("Filter request: " + httpMessage.getRequestHeader().getURI());
            makeErrorResponse(httpMessage);
        }
        return true;
    }

    @Override
    public int getArrangeableListenerOrder() {
        return 0;
    }

    private boolean shouldFilter(HttpMessage httpMessage) {
        return matchUrl(httpMessage.getRequestHeader().getURI()) && matchMethod(httpMessage.getRequestHeader().getMethod().toUpperCase());
    }

    private boolean matchUrl(URI url) {
        Pattern p = Pattern.compile(urlPrefix);
        Matcher m = p.matcher(url.toString());
        return m.find();
    }

    private boolean matchMethod(String method) {
        return "ALL".equals(this.method) || this.method != null && this.method.equals(method);
    }

    private void makeErrorResponse(HttpMessage message) {
        message.setResponseBody(new byte[]{});

        final HttpResponseHeader responseHeader = message.getResponseHeader();
        responseHeader.setContentLength(0);

        final String headerText = responseHeader.getHeadersAsString();
        StringBuilder resHeader = new StringBuilder("HTTP/1.1 404 Not Found");
        for(String line : headerText.split("\n")) {
            if(line.startsWith("Date")) {
                resHeader.append("\n").append("Date: Mon, 16 Mar 2015 12:07:45 GMT");
            } else if (line.startsWith("Server")) {
                resHeader.append("\n").append("Server: Apache/2.2.3 (CentOS)");
            } else if (line.startsWith("Connection")) {
                resHeader.append("\n").append("Connection: close");
            } else if (line.startsWith("Content-Type")) {
                resHeader.append("\n").append("Content-Type: text/html; charset=UTF-8");
            } else if (line.startsWith("HTTP/")) {
                // NOP
            } else {
                resHeader.append("\n").append(line);
            }
        }
        try {
            message.setResponseHeader(resHeader.toString());
        } catch (HttpMalformedHeaderException e) {
            e.printStackTrace();
        }
    }
}
