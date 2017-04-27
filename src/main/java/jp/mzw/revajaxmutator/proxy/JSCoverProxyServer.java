package jp.mzw.revajaxmutator.proxy;

import java.io.File;
import java.util.ArrayList;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class JSCoverProxyServer {
	private static Thread server;

	static String JSCOVER_REPORT_DIR;
	static String JSCOVER_REPORT_FILE = "jscoverage.json";

	public static void launch(final String dir, final String port) throws InterruptedException {
		final File cov_result = new File(dir, JSCOVER_REPORT_FILE);
		if (cov_result.exists()) {
			cov_result.delete();
		}

		if (server == null) {
			final ArrayList<String> _args = new ArrayList<String>();
			_args.add("-ws");
			_args.add("--port=" + port);
			_args.add("--proxy");
			_args.add("--local-storage");
			_args.add("--report-dir=" + dir);
			_args.add("--no-instrument-reg=.*jquery.*");
			_args.add("--no-instrument-reg=.*bootstrap.*");
			server = new Thread(() -> jscover.Main.main(_args.toArray(new String[_args.size()])));
			server.start();
			Thread.sleep(300); // wait for launching proxy server
		}
	}

	public static void launch(final String dir, final String port, String[] insr, String[] no_instr)
			throws InterruptedException {
		final File cov_result = new File(dir, JSCOVER_REPORT_FILE);
		if (cov_result.exists()) {
			cov_result.delete();
		}

		final ArrayList<String> _args = new ArrayList<String>();
		_args.add("-ws");
		_args.add("--port=" + port);
		_args.add("--proxy");
		_args.add("--local-storage");
		_args.add("--report-dir=" + dir);
		_args.add("--log=SEVERE");
		for (final String regx : insr) {
			if ("".equals(regx)) {
				continue;
			}
			_args.add("--only-instrument-reg=" + regx);
		}
		for (final String regx : no_instr) {
			if ("".equals(regx)) {
				continue;
			}
			_args.add("--no-instrument-reg=" + regx);
		}
		final String[] args = _args.toArray(new String[0]);

		if (server == null) {
			server = new Thread(() -> jscover.Main.main(args));
			server.start();
			Thread.sleep(300); // wait for launching proxy server
		}
	}

	/**
	 * Interrupt proxy server for JSCover
	 */
	public static void interrupt() {
		if (server == null) {
			return;
		}
		server.interrupt();
	}

	public static void reportCoverageResults(WebDriver driver, File reportDir) {
		if (server == null) {
			return;
		}
		final File file = new File(reportDir, "jscoverage.json");
		if (file.exists()) {
			file.delete();
		}
		((JavascriptExecutor) driver).executeScript("jscoverage_report();");
	}
}
