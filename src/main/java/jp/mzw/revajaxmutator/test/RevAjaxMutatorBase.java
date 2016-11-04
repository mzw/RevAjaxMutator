package jp.mzw.revajaxmutator.test;

import java.io.File;
import java.util.List;

import jp.mzw.revajaxmutator.FilterPlugin;

import org.openqa.selenium.WebDriver;
import org.owasp.webscarab.model.Preferences;
import org.owasp.webscarab.model.StoreException;
import org.owasp.webscarab.plugin.Framework;
import org.owasp.webscarab.plugin.proxy.Proxy;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;

public class RevAjaxMutatorBase {
	protected static ThreadLocal<Proxy> mProxy = new ThreadLocal<Proxy>();
	
	public static void launchProxyServer(List<ProxyPlugin> plugins, String proxy) throws StoreException, InterruptedException {
        Framework framework = new Framework();
        
        Preferences.setPreference("Proxy.listeners", proxy);
        
        framework.setSession("FileSystem", new File(".conversation"), "");
        
        
        mProxy.set(new Proxy(framework));
        
        for(ProxyPlugin plugin : plugins) {
        	mProxy.get().addPlugin(plugin);
        }
		
        mProxy.get().run();
    	Thread.sleep(300); // wait for launching proxy server
	}
	

    public static void relaunchProxyServerWith(FilterPlugin filter_plugin) throws InterruptedException, StoreException {
    	if(mProxy.get() != null && mProxy.get().stop()) {
    		mProxy.get().addPlugin(filter_plugin);
    		mProxy.get().run();
    		Thread.sleep(300); // wait for launching proxy server
    	}
    }
    
    public static void disableFilterPlugin() throws InterruptedException {
    	if(mProxy.get() != null && mProxy.get().stop()) {
    		FilterPlugin filter_plugin = (FilterPlugin) mProxy.get().getPlugin("FilterPlugin");
    		filter_plugin.setEnabled(false);
    		mProxy.get().run();
    		Thread.sleep(300); // wait for launching proxy server
    	}
    }
    
    public static void interruptProxyServer() {
    	if(mProxy.get() != null) {
    		mProxy.get().stop();
    	}
    }
}
