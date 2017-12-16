package jp.mzw.revajaxmutator.test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import jp.mzw.revajaxmutator.config.LocalEnv;
import jp.mzw.revajaxmutator.config.app.AppConfig;
import jp.mzw.revajaxmutator.proxy.JSCoverProxyServer;
import jp.mzw.revajaxmutator.proxy.SeleniumGridRewriterPlugin;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class WebAppTestBase {
	protected static final Logger LOGGER = LoggerFactory.getLogger(WebAppTestBase.class);

	/** Possess configuration related to local environment */
	protected static LocalEnv localenv;

	/** Possess configuration related to an application under test */
	protected static AppConfig config;

	/** Identifier used to indicate to the proxy which mutation file to get */
	protected static ThreadLocal<String> mutationFileId;

	/** Possess Web browser in thread-local manner */
	protected static ThreadLocal<WebDriver> currentDriver;

	/** Possess {@code WebDriverWait} instances in thread-local manner */
	protected static ThreadLocal<WebDriverWait> waits;

	/** Possess {@code Actions} instances in thread-local manner */
	protected static ThreadLocal<Actions> actions;

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
		currentDriver = new ThreadLocal<>();
		waits = new ThreadLocal<>();
		actions = new ThreadLocal<>();
		mutationFileId = new ThreadLocal<>();

		// Launch
		launchBrowser(localenv, config);
	}

	/**
	 * Provides Web browser in thread-local manner
	 *
	 * @return
	 */
	public static WebDriver getDriver() {
		return currentDriver.get();
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

	public void setMutationFileId(String id) {
		mutationFileId.set(id);
	}

	/**
	 *
	 * @param localenv
	 * @param config
	 * @throws IOException
	 */
	protected static void launchBrowser(LocalEnv localenv, AppConfig config) throws IOException {
		if (localenv.useChrome()) {
			System.setProperty("chrome.binary", localenv.getChromeBin());
			System.setProperty("webdriver.chrome.driver", localenv.getChromedriverBin());
			System.setProperty("webdriver.chrome.silentOutput", "true");

			final ChromeOptions options = new ChromeOptions();
			if (localenv.getChromeHeadless()) {
				options.addArguments("--headless");
				options.addArguments("--no-sandbox");
				options.addArguments("--disable-gpu");
			}
			options.addArguments("--test-type");
            setIgnoreChromeDialogOption(options);
			options.setBinary(localenv.getChromeBin());

			final DesiredCapabilities cap = DesiredCapabilities.chrome();
			cap.setCapability(ChromeOptions.CAPABILITY, options);
			cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

			// Connect to Selenium grid if available
			WebDriver driver;
			if (localenv.useSeleniumGrid()) {
				options.addArguments("--proxy-server=" + SeleniumGridRewriterPlugin.SEL_GRID_PROXY_ADDRESS);
				driver = new RemoteWebDriver(new URL(localenv.getSeleniumHubAddress() + "/wd/hub"), cap);

				// Makes Selenium grid upload local files (i.e. mutant
				// files) to the worker nodes
				((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());
			} else {
				options.addArguments("--proxy-server=" + localenv.getProxyAddress());
				driver = new ChromeDriver(cap);
			}
			final WebDriverWait wait = new WebDriverWait(driver, localenv.getTimeout(), 50);
			final Actions action = new Actions(driver);

			currentDriver.set(driver);
			waits.set(wait);
			actions.set(action);
		} else if (localenv.useFirefox()) {
			System.setProperty("webdriver.gecko.driver", localenv.getGeckodriverBin());

			final String proxyIp = (localenv.getSeleniumHubAddress() == null) ? localenv.getProxyIp()
					: SeleniumGridRewriterPlugin.SEL_GRID_PROXY_IP;
			final String proxyPort = (localenv.getSeleniumHubAddress() == null)
					? new Integer(localenv.getProxyPort()).toString() : SeleniumGridRewriterPlugin.SEL_GRID_PROXY_PORT;

			final DesiredCapabilities cap = DesiredCapabilities.firefox();
			cap.setCapability("marionette", true);
			cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

			final FirefoxProfile profile = new FirefoxProfile();
			profile.setPreference("network.proxy.type", 1);
			profile.setPreference("network.proxy.http", proxyIp);
			profile.setPreference("network.proxy.http_port", Integer.parseInt(proxyPort));
			profile.setPreference("network.proxy.ssl", proxyIp);
			profile.setPreference("network.proxy.ssl_port", Integer.parseInt(proxyPort));
			profile.setPreference("network.proxy.share_proxy_settings", Boolean.TRUE);
			profile.setPreference("network.proxy.no_proxies_on", "");
			profile.setAcceptUntrustedCertificates(true);
			cap.setCapability(FirefoxDriver.PROFILE, profile);

			final FirefoxOptions options = new FirefoxOptions();
			options.setBinary(localenv.getFirefoxBin());
			options.addCapabilities(cap);

			WebDriver driver = null;
			if (localenv.useSeleniumGrid()) {
				driver = new RemoteWebDriver(new URL(localenv.getSeleniumHubAddress() + "/wd/hub"), cap);
				((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());
			} else {
				driver = new FirefoxDriver(options);
			}
			final WebDriverWait wait = new WebDriverWait(driver, localenv.getTimeout(), 50);
			final Actions action = new Actions(driver);

			currentDriver.set(driver);
			waits.set(wait);
			actions.set(action);
		} else if (localenv.usePhantomjs()) {
			final DesiredCapabilities cap = DesiredCapabilities.phantomjs();

			final ArrayList<String> cliArgsCap = new ArrayList<>();
			final String proxyAddr = (localenv.getSeleniumHubAddress() != null) ? localenv.getProxyAddress()
					: SeleniumGridRewriterPlugin.SEL_GRID_PROXY_ADDRESS;
			cliArgsCap.add("--proxy=" + proxyAddr);
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

			WebDriver driver = null;
			if (localenv.useSeleniumGrid()) {
				driver = new RemoteWebDriver(new URL(localenv.getSeleniumHubAddress() + "/wd/hub"), cap);
				((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());
			} else {
				driver = new PhantomJSDriver(cap);
			}
			final WebDriverWait wait = new WebDriverWait(driver, localenv.getTimeout(), 50);
			final Actions action = new Actions(driver);

			currentDriver.set(driver);
			waits.set(wait);
			actions.set(action);
		}
	}

    /**
     * Ignore "Changes you made may not be saved" dialog message from chrome
     *
     * @param options
     */
    private static void setIgnoreChromeDialogOption(ChromeOptions options) {
        Map<String, Object> prefs = new HashMap<>();
        Map<String, Object> profile = new HashMap<>();
        Map<String, Object> contentSettings = new HashMap<>();
        contentSettings.put("notifications", 2);
        profile.put("managed_default_content_settings", contentSettings);
        prefs.put("profile", profile);
        options.setExperimentalOption("prefs", prefs);
    }

	/*--------------------------------------------------
		Utilities
	 --------------------------------------------------*/

	/**
	 * Report coverage results if available
	 *
	 * Quit all drivers if instantiated
	 *
	 * @throws InterruptedException
	 */
	@AfterClass
	public static void tearDownAfterClassBase() throws InterruptedException {
		try  {
			JSCoverProxyServer.reportCoverageResults(getDriver(), config.getJscoverReportDir());
		} catch (org.openqa.selenium.WebDriverException e) {
			throw e;
		} finally {
//			getDriver().close();
			getDriver().quit();
		}
	}

	/**
	 * Open given URL
	 *
	 * Wait for showing all widgets
	 *
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 * @throws InterruptedException
	 * @throws UnsupportedEncodingException
	 */
	@Before
	public void setUpBase()
			throws MalformedURLException, URISyntaxException, InterruptedException, UnsupportedEncodingException {
		// Insert a cookies with information for the proxy to run the test
		if (!LocalEnv.shouldRunJSCoverProxy()) {
			this.setCookies();

			// If using selenium grid, upload the mutant file to the worker
			if (localenv.useSeleniumGrid()) {
				this.sendMutantFileToSeleniumWorker();
			}
		}

		getDriver().get(config.getUrl().toString());
		this.waitUntilShowWidgets();
	}

	private void sendMutantFileToSeleniumWorker() throws MalformedURLException, UnsupportedEncodingException {
		final WebElement upload = this.until(By.xpath("/html/body"));
		final String absolutePath = config.getRecordedJsFile().getAbsolutePath();
		final String mutantFilepath = (mutationFileId.get() == null || mutationFileId.get() == "") ? absolutePath
				: absolutePath + "." + mutationFileId.get();
		System.out.println("sending file: " + mutantFilepath);
		upload.sendKeys(mutantFilepath);
	}

	/**
	 * Adds cookies with enough information for the proxy to fetch the correct
	 * mutant .js file. To add a cookie to the session, we first need to
	 * navigate to the domain. Selenium does not allow setting cookies before
	 * going to any page.
	 *
	 * @throws UnsupportedEncodingException
	 *
	 * @see <a href=
	 *      "http://docs.seleniumhq.org/docs/03_webdriver.jsp#cookies">Selenium
	 *      docs</a>
	 *
	 */
	private void setCookies() throws MalformedURLException, UnsupportedEncodingException {
		if (mutationFileId.get() != null) {
			// When running in a multi-threaded environment, send the mutation
			// id so the proxy knows which file to replace
			final String dummyURL = "http://" + config.getUrl().getAuthority() + "/some404page";
			getDriver().get(dummyURL);
			getDriver().manage().addCookie(
					new Cookie("jsMutantId", mutationFileId.get(), config.getUrl().getAuthority(), "/", null));

			// If using selenium grid, we also need to send the file name, since
			// the testrunner and proxy are not in the same JVM
			if (localenv.useSeleniumGrid()) {
				final String jsMutantFilename = config.getRecordedJsFile().getName();
				getDriver().manage().addCookie(
						new Cookie("jsMutantFilename", jsMutantFilename, config.getUrl().getAuthority(), "/", null));
			}
		}
	}

	/**
	 * Wait for showing all widgets
	 */
	protected void waitUntilShowWidgets() {
		getWait().until(
				driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
	}

	/*--------------------------------------------------
		Utilities
	 --------------------------------------------------*/
	public WebElement until(final By locator) {
		getWait().until(driver -> driver.findElement(locator) != null);
		return getDriver().findElement(locator);
	}

	public WebElement until(final By locator, final String text) {
		getWait().until(driver -> driver.findElement(locator) != null);
		getWait().until(driver -> driver.findElement(locator).getText().equals(text));
		return getDriver().findElement(locator);
	}

	public WebElement clickable(final By locator) {
		getWait().until(driver -> {
			final WebElement element = driver.findElement(locator);
			return element != null && element.isDisplayed() && element.isEnabled();
		});
		return getDriver().findElement(locator);
	}

	// public static WebElement until(final By locator, final String text) {
	// getWait().until(driver -> driver.findElement(locator) != null);
	// getWait().until(driver ->
	// driver.findElement(locator).getText().equals(text));
	// return getDriver().findElement(locator);
	// }

	public void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (final InterruptedException e) {
			// NOP
		}
	}

	/*--------------------------------------------------
		Utilities
	 --------------------------------------------------*/
	public void takeScreenshot(String filename) {
		try {
			final File screenshot = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(screenshot, new File(filename));
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
