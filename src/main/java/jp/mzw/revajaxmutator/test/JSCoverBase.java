package jp.mzw.revajaxmutator.test;

import java.io.File;

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
        new WebDriverWait(driver, timeout).until(
        		ExpectedConditions.elementToBeClickable(By.id("storeButton")));
        driver.findElement(By.id("storeButton")).click();
        new WebDriverWait(driver, timeout).until(
        		ExpectedConditions.textToBePresentInElement(By.id("storeDiv"), "Coverage data stored at"));
        
        server.interrupt();
    }
}
