package jp.mzw.revajaxmutator.proxy;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.owasp.webscarab.model.Preferences;
import org.owasp.webscarab.model.StoreException;
import org.owasp.webscarab.plugin.Framework;
import org.owasp.webscarab.plugin.proxy.Proxy;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;

public class ProxyServer {
	protected static Proxy mProxy;
	protected static final File conversationDir = new File(".conversation");

	public static void launch(List<ProxyPlugin> plugins, String proxy) throws StoreException, InterruptedException {
		final Framework framework = new Framework();
		Preferences.setPreference("Proxy.listeners", proxy);
		framework.setSession("FileSystem", conversationDir, "");
		mProxy = new Proxy(framework);
		if (plugins != null) {
			for (final ProxyPlugin plugin : plugins) {
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
			final FilterPlugin filter_plugin = (FilterPlugin) mProxy.getPlugin("FilterPlugin");
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

	public static void removeConversationDir() throws IOException {
		if (conversationDir.exists()) {
			FileUtils.forceDeleteOnExit(conversationDir);
		}
	}

	public static void main(String[] args) throws IOException, StoreException, InterruptedException {

		final RewriterPlugin plugin = new SeleniumGridRewriterPlugin();
		ProxyServer.launch(Arrays.asList(plugin), SeleniumGridRewriterPlugin.SEL_GRID_PROXY_ADDRESS);

		// Wait indefinitely
		Thread.currentThread().join();
	}
}
