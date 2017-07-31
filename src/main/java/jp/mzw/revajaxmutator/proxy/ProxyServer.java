package jp.mzw.revajaxmutator.proxy;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import jp.mzw.revajaxmutator.proxy.owaspzed.OwaspZedProxy;
import org.apache.commons.io.FileUtils;
import org.owasp.webscarab.model.StoreException;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;

public class ProxyServer {
	//private static RamProxy mProxy = new WebscarabProxy();
	//private static RamProxy mProxy = new LittleProxy();
	private static RamProxy mProxy = new OwaspZedProxy();

	private static final File conversationDir = new File(".conversation");

	public static void launch(List<ProxyPlugin> plugins, String address) throws StoreException, InterruptedException {
		mProxy.setup(address);
		for(ProxyPlugin plugin : plugins) {
			switch (plugin.getPluginName()) {
				case "RewriterPlugin":
					RewriterPlugin r1 = (RewriterPlugin) plugin;
					mProxy.addRewriterPlugin(r1.getDirectoryName(), r1.getRewriteFiles().get(0));
					break;
				case "RecorderPlugin":
					RecorderPlugin r2 = (RecorderPlugin) plugin;
					mProxy.addRecorderPlugin(r2.getDirectoryName());
					break;
				case "FilterPlugin":
					FilterPlugin f = (FilterPlugin) plugin;
					mProxy.addFilterPlugin(f.getUrlPrefix(), f.getMethod());
					break;
			}
		}
		mProxy.launch();
	}

//	public static void relaunchWith(FilterPlugin filter_plugin) throws InterruptedException, StoreException {
//		if (mProxy != null && mProxy.stop()) {
//			mProxy.addPlugin(filter_plugin);
//			mProxy.run();
//			Thread.sleep(300); // wait for launching proxy server
//		}
//	}
//
//	public static void disableFilterPlugin() throws InterruptedException {
//		if (mProxy != null && mProxy.stop()) {
//			final FilterPlugin filter_plugin = (FilterPlugin) mProxy.getPlugin("FilterPlugin");
//			filter_plugin.setEnabled(false);
//			mProxy.run();
//			Thread.sleep(300); // wait for launching proxy server
//		}
//	}

	public static void interrupt() {
		if (mProxy != null) {
			mProxy.stop();
		}
	}

	public static void restart() throws InterruptedException {
		mProxy.restart();
	}

	public static void removeConversationDir() throws IOException {
		if (conversationDir.exists()) {
			FileUtils.deleteDirectory(conversationDir);
		}
	}

	public static void main(String[] args) throws IOException, StoreException, InterruptedException {

		final RewriterPlugin plugin = new SeleniumGridRewriterPlugin();
		ProxyServer.launch(Collections.singletonList(plugin), SeleniumGridRewriterPlugin.SEL_GRID_PROXY_ADDRESS);

		// Wait indefinitely
		Thread.currentThread().join();
	}
}