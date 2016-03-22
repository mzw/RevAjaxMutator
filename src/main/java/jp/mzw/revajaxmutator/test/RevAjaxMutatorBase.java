package jp.mzw.revajaxmutator.test;

import java.io.File;
import java.util.List;

import jp.mzw.revajaxmutator.FilterPlugin;

import org.owasp.webscarab.model.Preferences;
import org.owasp.webscarab.model.StoreException;
import org.owasp.webscarab.plugin.Framework;
import org.owasp.webscarab.plugin.proxy.Proxy;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;

public class RevAjaxMutatorBase {
	private static Proxy mProxy;
	
	public static void launchProxyServer(List<ProxyPlugin> plugins, String port) throws StoreException, InterruptedException {
        Framework framework = new Framework();
        Preferences.setPreference("Proxy.listeners", "127.0.0.1:" + port);
        framework.setSession("FileSystem", new File(".conversation"), "");
        
        mProxy = new Proxy(framework);
        for(ProxyPlugin plugin : plugins) {
        	mProxy.addPlugin(plugin);
        }
		
		mProxy.run();
    	Thread.sleep(300); // wait for launching proxy server
	}

    public static void relaunchProxyServerWith(FilterPlugin filter_plugin) throws InterruptedException, StoreException {
    	if(mProxy != null && mProxy.stop()) {
    		mProxy.addPlugin(filter_plugin);
    		mProxy.run();
    		Thread.sleep(300); // wait for launching proxy server
    	}
    }
    
    public static void disableFilterPlugin() throws InterruptedException {
    	if(mProxy != null && mProxy.stop()) {
    		FilterPlugin filter_plugin = (FilterPlugin) mProxy.getPlugin("FilterPlugin");
    		filter_plugin.setEnabled(false);
    		mProxy.run();
    		Thread.sleep(300); // wait for launching proxy server
    	}
    }
    
    public static void interruptProxyServer() {
    	if(mProxy != null) {
    		mProxy.stop();
    	}
    }
}
