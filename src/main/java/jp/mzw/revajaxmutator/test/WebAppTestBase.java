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
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.gson.JsonObject;

abstract public class WebAppTestBase {
	protected static final Logger LOGGER = LoggerFactory.getLogger(WebAppTestBase.class);

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
		drivers = new ThreadLocal<>();
		waits = new ThreadLocal<>();
		actions = new ThreadLocal<>();
		// Launch
		launchBrowser(localenv, config);
	}

	/** Possess Web browser in thread-local manner */
	protected static ThreadLocal<WebDriver> drivers;

	/** Possess {@code WebDriverWait} instances in thread-local manner */
	protected static ThreadLocal<WebDriverWait> waits;

	/** Possess {@code Actions} instances in thread-local manner */
	protected static ThreadLocal<Actions> actions;

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
	 * @return
	 */
	public static Actions getActions() {
		return actions.get();
	}

	/**
	 * 
	 * @param localenv
	 * @param config
	 * @throws IOException
	 */
	protected static void launchBrowser(LocalEnv localenv, AppConfig config) throws IOException {
		if (localenv.getFirefoxBin() != null) {
			DesiredCapabilities cap = new DesiredCapabilities();

			JsonObject json = new JsonObject();
			json.addProperty("proxyType", "MANUAL");
			json.addProperty("httpProxy", localenv.getProxyIp());
			json.addProperty("httpProxyPort", localenv.getProxyPort());
			json.addProperty("sslProxy", localenv.getProxyIp());
			json.addProperty("sslProxyPort", localenv.getProxyPort());
			cap.setCapability(CapabilityType.PROXY, json);

			JsonObject prefs = new JsonObject();
			prefs.addProperty("network.proxy.type", 1);
			prefs.addProperty("network.proxy.http", localenv.getProxyIp());
			prefs.addProperty("network.proxy.http_port", localenv.getProxyPort());
			prefs.addProperty("network.proxy.ssl", localenv.getProxyIp());
			prefs.addProperty("network.proxy.ssl_port", localenv.getProxyPort());
			prefs.addProperty("network.proxy.share_proxy_settings", Boolean.TRUE);
			prefs.addProperty("network.proxy.no_proxies_on", "");

			JsonObject options = new JsonObject();
			options.add("prefs", prefs);
			options.addProperty("binary", localenv.getFirefoxBin());
			cap.setCapability("moz:firefoxOptions", options);

			@SuppressWarnings("deprecation")
			GeckoDriverService service = new GeckoDriverService.Builder(new FirefoxBinary(new File(localenv.getFirefoxBin())))
					.usingDriverExecutable(new File(localenv.getGeckodriverBin())).usingAnyFreePort().usingAnyFreePort().build();
			service.start();

			@SuppressWarnings("deprecation")
			WebDriver driver = new FirefoxDriver(service, cap, cap);
			WebDriverWait wait = new WebDriverWait(driver, localenv.getTimeout(), 50);
			Actions action = new Actions(driver);

			drivers.set(driver);
			waits.set(wait);
			actions.set(action);
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

			cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

			PhantomJSDriver driver = new PhantomJSDriver(cap);
			WebDriverWait wait = new WebDriverWait(driver, localenv.getTimeout(), 50);
			Actions action = new Actions(driver);

			drivers.set(driver);
			waits.set(wait);
			actions.set(action);
		}
	}

	/*--------------------------------------------------
		Utilities
	 --------------------------------------------------*/

	/**
	 * Report coverage results if available
	 * 
	 * Quit Web browser if instantiated
	 */
	@AfterClass
	public static void tearDownAfterClassBase() {
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
	public void setUpBase() throws MalformedURLException {
		getDriver().get(config.getUrl().toString());
		waitUntilShowWidgets();
	}

	/**
	 * Wait for showing all widgets
	 */
	protected static void waitUntilShowWidgets() {
		getWait().until(new Function<WebDriver, Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
			}
		});
	}

	/*--------------------------------------------------
		Utilities
	 --------------------------------------------------*/
	public static WebElement until(final By locator) {
		getWait().until(new Function<WebDriver, Boolean>() {
			public Boolean apply(WebDriver driver) {
				return driver.findElement(locator) != null;
			}
		});
		return getDriver().findElement(locator);
	}

	public static WebElement clickable(final By locator) {
		getWait().until(new Function<WebDriver, Boolean>() {
			public Boolean apply(WebDriver driver) {
				WebElement element = driver.findElement(locator);
				return element != null && element.isDisplayed() && element.isEnabled();
			}
		});
		return getDriver().findElement(locator);
	}

	public static WebElement until(final By locator, final String text) {
		getWait().until(new Function<WebDriver, Boolean>() {
			public Boolean apply(WebDriver driver) {
				return driver.findElement(locator) != null;
			}
		});
		getWait().until(new Function<WebDriver, Boolean>() {
			public Boolean apply(WebDriver driver) {
				return driver.findElement(locator).getText().equals(text);
			}
		});
		return getDriver().findElement(locator);
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// NOP
		}
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
