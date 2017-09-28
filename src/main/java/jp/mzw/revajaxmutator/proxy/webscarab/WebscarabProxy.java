package jp.mzw.revajaxmutator.proxy.webscarab;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jp.mzw.revajaxmutator.proxy.FilterPlugin;
import jp.mzw.revajaxmutator.proxy.RamProxy;
import jp.mzw.revajaxmutator.proxy.RecorderPlugin;
import jp.mzw.revajaxmutator.proxy.RewriterPlugin;
import jp.mzw.revajaxmutator.proxy.SeleniumGridRewriterPlugin;
import org.owasp.webscarab.model.Preferences;
import org.owasp.webscarab.model.StoreException;
import org.owasp.webscarab.plugin.Framework;
import org.owasp.webscarab.plugin.proxy.Proxy;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;

public class WebscarabProxy implements RamProxy {

    private static List<Proxy> mProxies = new ArrayList<>();
    private static final File conversationDir = new File(".conversation");

    public static void launch(List<ProxyPlugin> plugins, String proxy)
        throws StoreException, InterruptedException {
        final Framework framework = new Framework();
        Preferences.setPreference("Proxy.listeners", proxy);
        framework.setSession("FileSystem", conversationDir, "");
        Proxy mProxy = new Proxy(framework);
        mProxies.add(mProxy);
        if (plugins != null) {
            for (final ProxyPlugin plugin : plugins) {
                mProxy.addPlugin(plugin);
            }
        }
        mProxy.run();
        Thread.sleep(300); // wait for launching proxy server
    }

    public void stop() {
        for (Proxy mProxy : mProxies) {
            mProxy.stop();
        }
    }

    public void setup(String address) {
        try {
            final Framework framework = new Framework();
            Preferences.setPreference("Proxy.listeners", address);
            framework.setSession("FileSystem", conversationDir, "");
            Proxy proxy = new Proxy(framework);
            mProxies.add(proxy);
        } catch (StoreException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void launch() {
        for (Proxy mProxy : mProxies) {
            mProxy.run();
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                System.exit(1);
            }
        }
    }

    public void addRecorderPlugin(String directory) {
        for (Proxy mProxy : mProxies) {
            mProxy.addPlugin(new RecorderPlugin(directory));
        }
    }

    public void addFilterPlugin(String urlPrefix, String method) {
        for (Proxy mProxy : mProxies) {
            mProxy.addPlugin(new FilterPlugin(urlPrefix, method));
        }
    }

    public void addRewriterPlugin(String directory, String filename) {
        final RewriterPlugin rewriterPlugin = new RewriterPlugin(directory);
        if (filename != null) {
            rewriterPlugin.setRewriteFile(filename);
        }
        for (Proxy mProxy : mProxies) {
            mProxy.addPlugin(rewriterPlugin);
        }
    }

    public void restart() {
        for (Proxy mProxy : mProxies) {
            if (mProxy.stop()) {
                mProxy.run();
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args)
        throws IOException, StoreException, InterruptedException {

        final RewriterPlugin plugin = new SeleniumGridRewriterPlugin();
        WebscarabProxy.launch(Collections.singletonList(plugin),
            SeleniumGridRewriterPlugin.SEL_GRID_PROXY_ADDRESS);

        // Wait indefinitely
        Thread.currentThread().join();
    }
}
