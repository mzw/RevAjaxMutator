package jp.mzw.revajaxmutator.command;

import java.io.File;

import org.owasp.webscarab.model.Preferences;
import org.owasp.webscarab.model.StoreException;
import org.owasp.webscarab.plugin.Framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.mzw.revajaxmutator.proxy.FilterPlugin;
import jp.mzw.revajaxmutator.proxy.RecorderPlugin;
import jp.mzw.revajaxmutator.proxy.RewriterPlugin;

public class Proxy extends Command {
	protected static Logger LOG = LoggerFactory.getLogger(Proxy.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUsageContent() {
		StringBuilder builder = new StringBuilder();

		builder.append("Command: ").append("proxy -record").append("\n");
		builder.append("For: ").append("Lanuch proxy server recording http messages").append("\n");

		builder.append("Command: ").append("proxy -rewrite").append("\n");
		builder.append("For: ").append("Lanuch proxy server rewriting http messages").append("\n");
		
		builder.append("Command: ").append("proxy -filter").append("\n");
		builder.append("For: ").append("Lanuch proxy server filtering http messages").append("\n");

		return builder.toString();
	}

	/**
	 * Launch proxy server with several plugin applications
	 * 
	 * -record: records HTTP messages
	 * -rewrite: rewrites HTTP messages
	 * -filter: filters HTTP messages
	 * 
	 * @param args
	 */
	public void launch(String[] args) {
		try {
			Framework framework = new Framework();
			Preferences.setPreference("Proxy.listeners", "127.0.0.1:8080");
			framework.setSession("FileSystem", new File(".conversation"), "");
			org.owasp.webscarab.plugin.proxy.Proxy proxy = new org.owasp.webscarab.plugin.proxy.Proxy(framework);
			for (int i = 0; i < args.length; i++) {
				if ("-record".equals(args[i])) {
					LOG.info("adding RecorderPlugin: path = {}", args[i + 1]);
					proxy.addPlugin(new RecorderPlugin(args[i + 1]));
				}
				if ("-rewrite".equals(args[i])) {
					LOG.info("adding RewriterPlugin: path = {}", args[i + 1]);
					proxy.addPlugin(new RewriterPlugin(args[i + 1]));
				}
				if ("-filter".equals(args[i])) {
					LOG.info("adding FilterPlugin: url = {}, method = {}", args[i + 1], args[i + 2]);
					proxy.addPlugin(new FilterPlugin(args[i + 1], args[i + 2]));
				}
			}
			proxy.run();
			while (proxy.isRunning()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.exit(1);
				}
			}
		} catch (StoreException e) {
			LOG.error(e.getMessage());
		}
	}

}
