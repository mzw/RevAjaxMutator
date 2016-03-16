package jp.mzw.revajaxmutator.test;

import java.io.File;
import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class JSCoverBase {
	private static Thread server;
	static String JSCOVER_URL = "http://localhost/jscoverage.html";
	static String JSCOVER_REPORT_DIR;
	static String JSCOVER_REPORT_FILE = "jscoverage.json";

	public static void launchProxyServer(final String dir, final String port) throws InterruptedException {
		File cov_result = new File(dir, JSCOVER_REPORT_FILE);
        if (cov_result.exists()) cov_result.delete();
        
		if(server == null) {
			server = new Thread(new Runnable() {
				@Override
				public void run() {
					jscover.Main.main(new String[]{
							"-ws",
							"--port=" + port,
							"--proxy",
							"--local-storage",
							"--report-dir=" + dir,
							"--no-instrument-reg=.*jquery.*",
							"--no-instrument-reg=.*bootstrap.*",
					});
				}
			});
			server.start();
	    	Thread.sleep(300); // wait for launching proxy server
		}
	}
	

	public static void launchProxyServer(final String dir, final String port, String[] insr, String[] no_instr) throws InterruptedException {
		File cov_result = new File(dir, JSCOVER_REPORT_FILE);
        if (cov_result.exists()) cov_result.delete();
        
        ArrayList<String> _args = new ArrayList<String>();
        _args.add("-ws");
        _args.add("--port=" + port);
        _args.add("--proxy");
        _args.add("--local-storage");
        _args.add("--report-dir=" + dir);
        for(String regx : insr) {
        	if("".equals(regx)) continue;
        	_args.add("--only-instrument-reg=" + regx);
        }
        for(String regx : no_instr) {
        	if("".equals(regx)) continue;
        	_args.add("--no-instrument-reg=" + regx);
        }
        final String[] args = _args.toArray(new String[0]);
        
		if(server == null) {
			server = new Thread(new Runnable() {
				@Override
				public void run() {
					jscover.Main.main(args);
				}
			});
			server.start();
	    	Thread.sleep(300); // wait for launching proxy server
		}
	}
    
	/**
	 * Interrupt proxy server for JSCover
	 */
    @SuppressWarnings("deprecation")
	public static void interruptProxyServer(WebDriver driver, int timeout) {
    	if(server == null) {
    		return;
    	}
    	
        driver.get(JSCOVER_URL);
        new WebDriverWait(driver, timeout).until(
        		ExpectedConditions.elementToBeClickable(By.id("storeTab")));
        driver.findElement(By.id("storeTab")).click();
        try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        new WebDriverWait(driver, timeout).until(
        		ExpectedConditions.elementToBeClickable(By.id("storeButton")));
        driver.findElement(By.id("storeButton")).click();
        try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        new WebDriverWait(driver, timeout).until(
        		ExpectedConditions.textToBePresentInElement(By.id("storeDiv"), "Coverage data stored at"));
        
        server.interrupt();
    }
}
