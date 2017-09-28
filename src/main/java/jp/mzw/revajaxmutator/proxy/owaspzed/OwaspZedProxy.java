package jp.mzw.revajaxmutator.proxy.owaspzed;

import java.net.URL;
import jp.mzw.revajaxmutator.proxy.RamProxy;
import org.parosproxy.paros.core.proxy.ProxyServer;

public class OwaspZedProxy implements RamProxy {

    private ProxyServer proxyServer;

    private String address;
    private int port;

    @Override
    public void stop() {
        proxyServer.stopServer();
    }

    @Override
    public void setup(String address) {
        proxyServer = new ProxyServer();

        try {
            if (!address.startsWith("http")) {
                address = "http://" + address;
            }
            URL url = new URL(address);

            this.address = url.getHost();
            this.port = url.getPort();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void launch() {
        proxyServer.startServer(this.address, this.port, false);
    }

    @Override
    public void addRecorderPlugin(String directory) {
        proxyServer.addProxyListener(new RecorderListener(directory));
    }

    @Override
    public void addFilterPlugin(String urlPrefix, String method) {
        proxyServer.addProxyListener(new FilterListener(urlPrefix, method));
    }

    @Override
    public void addRewriterPlugin(String directory, String filename) {
        proxyServer.addProxyListener(new RewriterListener(directory, filename));
    }

    @Override
    public void restart() {
        proxyServer.stopServer();
        this.launch();
    }
}
