package jp.mzw.revajaxmutator.test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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

import com.google.gson.JsonObject;

import jp.mzw.revajaxmutator.config.LocalEnv;
import jp.mzw.revajaxmutator.config.app.AppConfig;
import jp.mzw.revajaxmutator.proxy.JSCoverProxyServer;

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
	public static void setUpBeforeClass(Class<? extends AppConfig> clazz)
			throws IOException, InstantiationException, IllegalAccessException {
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
		final String firefoxBin = localenv.getFirefoxBin();
		final String chromeBin = localenv.getChromeBin();
		if (chromeBin != null) {
			// cap.setCapability("chrome.switches",
			// Arrays.asList("--proxy-server=" +
			// "http://user:password@proxy.com:8080"));

			System.setProperty("webdriver.chrome.driver", chromeBin);

			final ChromeOptions options = new ChromeOptions();
			// options.addArguments("--headless");
			options.addArguments("--proxy-server=" + "http://" + localenv.getProxyAddress());

			final DesiredCapabilities cap = DesiredCapabilities.chrome();
			cap.setCapability(ChromeOptions.CAPABILITY, options);

			final ChromeDriver driver = new ChromeDriver(cap);
			final WebDriverWait wait = new WebDriverWait(driver, localenv.getTimeout(), 50);
			final Actions action = new Actions(driver);

			drivers.set(driver);
			waits.set(wait);
			actions.set(action);
		} else if (firefoxBin != null) {
			final DesiredCapabilities cap = DesiredCapabilities.firefox();

			final JsonObject json = new JsonObject();
			json.addProperty("proxyType", "MANUAL");
			json.addProperty("httpProxy", localenv.getProxyIp());
			json.addProperty("httpProxyPort", localenv.getProxyPort());
			json.addProperty("sslProxy", localenv.getProxyIp());
			json.addProperty("sslProxyPort", localenv.getProxyPort());
			cap.setCapability(CapabilityType.PROXY, json);

			final JsonObject prefs = new JsonObject();
			prefs.addProperty("network.proxy.type", 1);
			prefs.addProperty("network.proxy.http", localenv.getProxyIp());
			prefs.addProperty("network.proxy.http_port", localenv.getProxyPort());
			prefs.addProperty("network.proxy.ssl", localenv.getProxyIp());
			prefs.addProperty("network.proxy.ssl_port", localenv.getProxyPort());
			prefs.addProperty("network.proxy.share_proxy_settings", Boolean.TRUE);
			prefs.addProperty("network.proxy.no_proxies_on", "");

			final JsonObject options = new JsonObject();
			options.add("prefs", prefs);
			options.addProperty("binary", firefoxBin);
			cap.setCapability("moz:firefoxOptions", options);
			cap.setCapability("marionette", true);

			@SuppressWarnings("deprecation")
			final GeckoDriverService service = new GeckoDriverService.Builder(new FirefoxBinary(new File(firefoxBin)))
					.usingDriverExecutable(new File(localenv.getGeckodriverBin())).usingAnyFreePort().usingAnyFreePort()
					.build();
			service.start();

			@SuppressWarnings("deprecation")
			final WebDriver driver = new FirefoxDriver(service, cap, cap);
			final WebDriverWait wait = new WebDriverWait(driver, localenv.getTimeout(), 50);
			final Actions action = new Actions(driver);

			drivers.set(driver);
			waits.set(wait);
			actions.set(action);
		} else if (localenv.getPhantomjsBin() != null) {
			final DesiredCapabilities cap = new DesiredCapabilities();

			final ArrayList<String> cliArgsCap = new ArrayList<String>();
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

			final PhantomJSDriver driver = new PhantomJSDriver(cap);
			final WebDriverWait wait = new WebDriverWait(driver, localenv.getTimeout(), 50);
			final Actions action = new Actions(driver);

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
		getWait().until(
				driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
	}

	/*--------------------------------------------------
		Utilities
	 --------------------------------------------------*/
	public static WebElement until(final By locator) {
		getWait().until(driver -> driver.findElement(locator) != null);
		return getDriver().findElement(locator);
	}

	public static WebElement until(final By locator, final String text) {
		getWait().until(driver -> driver.findElement(locator) != null);
		getWait().until(driver -> driver.findElement(locator).getText().equals(text));
		return getDriver().findElement(locator);
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (final InterruptedException e) {
			// NOP
		}
	}

	/*--------------------------------------------------
		Utilities
	 --------------------------------------------------*/
	public static void takeScreenshot(String filename) {
		try {
			final File screenshot = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(screenshot, new File(filename));
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
