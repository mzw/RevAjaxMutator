package jp.mzw.revajaxmutator.test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import jp.mzw.revajaxmutator.config.LocalEnv;
import jp.mzw.revajaxmutator.config.app.AppConfig;
import jp.mzw.revajaxmutator.proxy.JSCoverProxyServer;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;

abstract public class WebAppTestBase {

	/** Possess configuration related to local environment */
	protected static LocalEnv localenv;

	/** Possess configuration related to an application under test */
	protected static AppConfig config;

	/**
	 * [Important] a test suite needs to inherit {@code WebAppTestBase} and call
	 * this method at its {@code BeforeClass}-annotated method
	 * 
	 * @param clazz
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static void setUpBeforeClass(Class<? extends AppConfig> clazz) throws IOException, InstantiationException, IllegalAccessException {
		// Load configurations
		localenv = new LocalEnv(LocalEnv.FILENAME);
		config = clazz.newInstance();
		// Thread locals
		drivers = new ThreadLocal<WebDriver>();
		waits = new ThreadLocal<WebDriverWait>();
		// Launch
		launchBrowser(localenv, config);
	}

	/** Possess Web browser in thread-local manner */
	protected static ThreadLocal<WebDriver> drivers;

	/** Possess {@code wait} instance in thread-local manner */
	protected static ThreadLocal<WebDriverWait> waits;

	/**
	 * Provides Web browser in thread-local manner
	 * 
	 * @return
	 */
	public static WebDriver getDriver() {
		return drivers.get();
	}

	/**
	 * 
	 * @return
	 */
	public static WebDriverWait getWait() {
		return waits.get();
	}

	/**
	 * 
	 * @param localenv
	 * @param config
	 * @throws IOException
	 */
	protected static void launchBrowser(LocalEnv localenv, AppConfig config) throws IOException {
		if (localenv.getFirefoxBin() != null) {

			com.google.gson.JsonObject json = new com.google.gson.JsonObject();
			json.addProperty("proxyType", "MANUAL");
			json.addProperty("httpProxy", localenv.getProxyIp());
			json.addProperty("httpProxyPort", localenv.getProxyPort());
			json.addProperty("sslProxy", localenv.getProxyIp());
			json.addProperty("sslProxyPort", localenv.getProxyPort());

			DesiredCapabilities cap = new DesiredCapabilities();
			cap.setCapability("proxy", json);

			@SuppressWarnings("deprecation")
			GeckoDriverService service = new GeckoDriverService.Builder(new FirefoxBinary(new File(localenv.getFirefoxBin())))
					.usingDriverExecutable(new File(localenv.getGeckodriverBin())).usingAnyFreePort().usingAnyFreePort().build();
			service.start();

			@SuppressWarnings("deprecation")
			WebDriver driver = new FirefoxDriver(service, cap, cap);
			WebDriverWait wait = new WebDriverWait(driver, localenv.getTimeout());

			drivers.set(driver);
			waits.set(wait);
		} else if (localenv.getPhantomjsBin() != null) {
			DesiredCapabilities cap = new DesiredCapabilities();

			ArrayList<String> cliArgsCap = new ArrayList<String>();
			cliArgsCap.add("--proxy=" + localenv.getProxyAddress());
			cliArgsCap.add("--proxy-type=http");
			cliArgsCap.add("--local-to-remote-url-access=true");
			cliArgsCap.add("--web-security=false");
			cliArgsCap.add("--webdriver-loglevel=NONE");
			cap.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);

			cap.setJavascriptEnabled(true);
			cap.setCapability("phantomjs.page.settings.XSSAuditingEnabled", true);
			cap.setCapability("phantomjs.page.settings.localToRemoteUrlAccessEnabled", true);
			cap.setCapability("phantomjs.page.settings.resourceTimeout", "20000");
			cap.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, localenv.getPhantomjsBin());

			PhantomJSDriver driver = new PhantomJSDriver(cap);
			WebDriverWait wait = new WebDriverWait(driver, localenv.getTimeout());
			drivers.set(driver);
			waits.set(wait);
		}
	}

	/**
	 * Report coverage results if available
	 * 
	 * Quit Web browser if instantiated
	 */
	@AfterClass
	public static void tearDownAfterClass() {
		JSCoverProxyServer.reportCoverageResults(getDriver(), config.getJscoverReportDir());
		getDriver().quit();
	}

	/**
	 * Open given URL
	 * 
	 * Wait for showing all widgets
	 * 
	 * @throws MalformedURLException
	 */
	@Before
	public void setUp() throws MalformedURLException {
		getDriver().get(config.getUrl().toString());
		waitUntilShowWidgets();
	}

	/**
	 * Wait for showing all widgets
	 */
	protected void waitUntilShowWidgets() {
		getWait().until(new Function<WebDriver, Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
			}
		});
	}

	/*--------------------------------------------------
		Utilities
	 --------------------------------------------------*/
	public static void takeScreenshot(String filename) {
		try {
			File screenshot = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(screenshot, new File(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
