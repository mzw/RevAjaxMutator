package jp.mzw.revajaxmutator.proxy;

import java.io.File;
import java.util.List;

import org.owasp.webscarab.model.Preferences;
import org.owasp.webscarab.model.StoreException;
import org.owasp.webscarab.plugin.Framework;
import org.owasp.webscarab.plugin.proxy.Proxy;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;

public class ProxyServer {
	protected static Proxy mProxy;

	public static void launch(List<ProxyPlugin> plugins, String proxy) throws StoreException, InterruptedException {
		Framework framework = new Framework();
		Preferences.setPreference("Proxy.listeners", proxy);
		framework.setSession("FileSystem", new File(".conversation"), "");
		mProxy = new Proxy(framework);
		if (plugins != null) {
			for (ProxyPlugin plugin : plugins) {
				mProxy.addPlugin(plugin);
			}
		}
		mProxy.run();
		Thread.sleep(300); // wait for launching proxy server
	}

	public static void relaunchWith(FilterPlugin filter_plugin) throws InterruptedException, StoreException {
		if (mProxy != null && mProxy.stop()) {
			mProxy.addPlugin(filter_plugin);
			mProxy.run();
			Thread.sleep(300); // wait for launching proxy server
		}
	}

	public static void disableFilterPlugin() throws InterruptedException {
		if (mProxy != null && mProxy.stop()) {
			FilterPlugin filter_plugin = (FilterPlugin) mProxy.getPlugin("FilterPlugin");
			filter_plugin.setEnabled(false);
			mProxy.run();
			Thread.sleep(300); // wait for launching proxy server
		}
	}

	public static void interrupt() {
		if (mProxy != null) {
			mProxy.stop();
		}
	}
}
