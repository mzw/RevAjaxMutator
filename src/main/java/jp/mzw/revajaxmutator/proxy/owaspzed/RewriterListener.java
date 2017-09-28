package jp.mzw.revajaxmutator.proxy.owaspzed;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jp.mzw.revajaxmutator.config.app.AppConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.parosproxy.paros.core.proxy.ProxyListener;
import org.parosproxy.paros.network.HttpMessage;

public class RewriterListener implements ProxyListener {

    final private String mDirname;
    final private List<String> mRewriteFiles;

    RewriterListener(String directory, String file) {
        mDirname = directory;
        mRewriteFiles = new ArrayList<>();
        mRewriteFiles.add(file);
    }

    @Override
    public int getArrangeableListenerOrder() {
        return 0;
    }

    @Override
    public boolean onHttpRequestSend(HttpMessage httpMessage) {
        if(checkIfRequestIsForTargetJsFile(httpMessage) != null) {
            httpMessage.getRequestHeader().setHeader("If-Modified-Since", null);
            httpMessage.getRequestHeader().setHeader("If-None-Match", null);
        }
        return true;
    }

    @Override
    public boolean onHttpResponseReceive(HttpMessage httpMessage) {

        try {
            final String filename = this.checkIfRequestIsForTargetJsFile(httpMessage);
            if (filename == null || filename.equals("")) {
                return true;
            }

            String mutantId = this.getMutantFileIdentifier(httpMessage);
            if (!mutantId.equals("")) {
                mutantId = "." + mutantId;
            }

            // Get the mutated file to replace in the response message
            try (BufferedInputStream in = this.findMutantFile(filename, mutantId);
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                // Replace incoming server .js file with local, mutated .js file
                final byte[] buf = new byte[1024 * 8];
                int len;
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                final String contents = out.toString(); // TODO check encoding
                httpMessage.setResponseBody(contents);
                httpMessage.getResponseHeader()
                    .setContentLength(httpMessage.getResponseBody().length());
            }
        } catch (final FileNotFoundException e) {
            // ignore non-recorded urls
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private String checkIfRequestIsForTargetJsFile(final HttpMessage request) {
        String url;
        try {
            url = URLEncoder
                .encode(request.getRequestHeader().getURI().toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        for (final String _filename : this.mRewriteFiles) {
            final Pattern pattern = Pattern.compile(_filename);
            final Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                return _filename;
            }
        }
        return null;
    }

    private String getMutantFileIdentifier(HttpMessage request) {
        for (HttpCookie httpCookie : request.getRequestHeader().getHttpCookies()) {
            if ("jsMutantId".equals(httpCookie.getName())) {
                return httpCookie.getValue();
            }
        }
        return "";
    }

    private BufferedInputStream findMutantFile(String jsFilename, String mutantExt)
        throws FileNotFoundException {
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
                for (File candidate : FileUtils
                    .listFiles(file, FileFilterUtils.fileFileFilter(),
                        FileFilterUtils.trueFileFilter())) {
                    String name = AppConfig.getTooLongUrlName(new File(this.mDirname), candidate);
                    if (pattern.matcher(name).find() && name.endsWith(mutantExt)) {
                        return new BufferedInputStream(
                            new FileInputStream(candidate.getAbsolutePath()));
                    }
                }
            }
        }
        return null;
    }
}
