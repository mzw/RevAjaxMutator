package jp.mzw.revajaxmutator.proxy.owaspzed;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import org.parosproxy.paros.core.proxy.ProxyListener;
import org.parosproxy.paros.network.HttpMessage;

public class RecorderListener implements ProxyListener {

    private String directory;

    RecorderListener(String directory) {
        this.directory = directory;
    }

    @Override
    public boolean onHttpRequestSend(HttpMessage httpMessage) {
        return true;
    }

    @Override
    public boolean onHttpResponseReceive(HttpMessage httpMessage) {
        try {
            File dir = new File(directory);
            String filename = URLEncoder.encode(httpMessage.getRequestHeader().getURI().toString(), "utf-8");
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
            out.write(httpMessage.getResponseBody().toString().getBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public int getArrangeableListenerOrder() {
        return 0;
    }
}
