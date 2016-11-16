package jp.mzw.revajaxmutator.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import jp.mzw.ajaxmutator.JUnitExecutor;
import jp.mzw.revajaxmutator.FilterPlugin;
import jp.mzw.revajaxmutator.RecorderPlugin;
import jp.mzw.revajaxmutator.RewriterPlugin;
import jp.mzw.revajaxmutator.config.AppConfigBase;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.owasp.webscarab.model.StoreException;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;

public class WebAppTestBase{

	protected static String URL;
	protected static String ADMIN_URL;
	
    protected static WebDriver driver;
    protected static WebDriverWait wait;
    protected static ThreadLocal<WebDriver> currentDriver = new ThreadLocal<WebDriver>();
    protected static ThreadLocal<WebDriverWait> currentWait = new ThreadLocal<WebDriverWait>();

    private static String CONFIG_FILENAME = "localenv.properties";
    
    protected static Properties CONFIG;
    protected static String FIREFOX_BIN;
    protected static String PHANTOMJS_BIN;
    protected static String PROXY;
    protected static String PROXY_IP;
    protected static String PROXY_PORT;
    protected static String SELENIUMGRID_HUB_URL;
    protected static String XPI_FILE_PATH;
    protected static int TIMEOUT;
    
    public static void beforeTestBaseClass(String config) throws IOException, StoreException, InterruptedException {
    	readTestBaseConfig();
    	launchBrowser("");
    	beforeTestClass(config);
    }
    
    public static void beforeTestBaseClass(String config,String mutantname) throws IOException, StoreException, InterruptedException {
    	readTestBaseConfig();
    	launchBrowser(mutantname);
    	beforeTestClass(config);
    }
    
    private static org.openqa.selenium.Proxy getProxy(){
    	org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
        proxy.setHttpProxy(PROXY);
        proxy.setFtpProxy(PROXY);
        proxy.setSslProxy(PROXY);
    	return proxy;
    }
    
    private static FirefoxProfile getFirefoxProfile(String mutantname){
    	FirefoxProfile profile = new FirefoxProfile();
    	File modifyHeaders = new File(XPI_FILE_PATH);
    	profile.setEnableNativeEvents(false); 
    	try {
    		profile.addExtension(modifyHeaders); 
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	profile.setPreference("modifyheaders.headers.count", 1);
    	profile.setPreference("modifyheaders.headers.action0", "Add");
    	profile.setPreference("modifyheaders.headers.name0", "mutant");
    	profile.setPreference("modifyheaders.headers.value0", mutantname);
    	profile.setPreference("modifyheaders.headers.enabled0", true);
    	profile.setPreference("modifyheaders.config.active", true);
    	profile.setPreference("modifyheaders.config.alwaysOn", true);
    	return profile;
    }
    
	/**
     * Launch given Firefox browser with given proxy configuration
     */
    private static void launchBrowser(String mutantname) {
        DesiredCapabilities cap = new DesiredCapabilities();
        
        if(FIREFOX_BIN != null) {
            cap.setCapability(CapabilityType.PROXY, getProxy());
        	FirefoxBinary binary = new FirefoxBinary(new File(FIREFOX_BIN));
        	cap.setCapability(FirefoxDriver.BINARY, binary);
        	driver = new FirefoxDriver(cap);
        	wait = new WebDriverWait(driver, TIMEOUT);
        } else if(PHANTOMJS_BIN != null) {
        	ArrayList<String> cliArgsCap = new ArrayList<String>();
        	cliArgsCap.add("--proxy="+PROXY);
        	cliArgsCap.add("--proxy-type=http");
        	cliArgsCap.add("--local-to-remote-url-access=true");
        	cliArgsCap.add("--web-security=false");
        	cliArgsCap.add("--webdriver-loglevel=NONE");
        	cap.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
        	
        	cap.setJavascriptEnabled(true);
        	cap.setCapability("phantomjs.page.settings.XSSAuditingEnabled", true);
        	cap.setCapability("phantomjs.page.settings.localToRemoteUrlAccessEnabled", true);
        	cap.setCapability("phantomjs.page.settings.resourceTimeout","20000");
            cap.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, PHANTOMJS_BIN);
            driver = new PhantomJSDriver(cap);
            wait = new WebDriverWait(driver, TIMEOUT);
        }
        //concurrent
        else{
        	final DesiredCapabilities firefox = DesiredCapabilities.firefox();
        	firefox.setCapability(FirefoxDriver.PROFILE, getFirefoxProfile(mutantname));
            firefox.setCapability(CapabilityType.PROXY, getProxy());
            WebDriver driver = null;
     		try {
     			driver = new RemoteWebDriver(new URL(SELENIUMGRID_HUB_URL), firefox);
     		} catch (MalformedURLException e) {
     			e.printStackTrace();
     		}
     		currentDriver.set(driver);
     		currentWait.set(new WebDriverWait(driver, TIMEOUT));
        }
    }
    
    protected static WebDriver getDriver() {
    	WebDriver rDriver = null;
    	if(driver != null){
    		rDriver = driver;
    	}else if(currentDriver.get() != null){
    		rDriver = currentDriver.get();
    	}
    	return rDriver;
	}
    
    protected static WebDriverWait getWait() {
    	WebDriverWait rWait = null;
    	if(wait != null){
    		rWait = wait;
    	}else if(currentWait.get() != null){
    		rWait = currentWait.get();
    	}
    	return rWait;
	}

    /**
     * Read configuration to specify firefox and proxy
     * @throws IOException indicates "localenv.properties" not found on the resource path
     */
	private static void readTestBaseConfig() throws IOException {
		CONFIG = getConfig(CONFIG_FILENAME);

		FIREFOX_BIN = CONFIG.getProperty("firefox-bin");
		PHANTOMJS_BIN = CONFIG.getProperty("phantomjs-bin");
		PROXY_IP = CONFIG.getProperty("proxy_ip") != null ? CONFIG.getProperty("proxy_ip") : "127.0.0.1";
		PROXY_PORT = CONFIG.getProperty("proxy_port") != null ? CONFIG.getProperty("proxy_port") : "80";
		PROXY = PROXY_IP + ":" + PROXY_PORT;
		SELENIUMGRID_HUB_URL = CONFIG.getProperty("seleniumgrid_hub_url") != null ? CONFIG.getProperty("seleniumgrid_hub_url") : "";
		XPI_FILE_PATH = CONFIG.getProperty("xpi_file_path") != null ? CONFIG.getProperty("xpi_file_path") : "";
		TIMEOUT = CONFIG.getProperty("timeout") != null ? Integer.parseInt(CONFIG.getProperty("timeout")) : 3;
		
	}

    @Before
	public void setup() throws InterruptedException {
    	// NOP
    }
    
    @After
    public void teardown() {
    	// NOP
    }
    
    @AfterClass
    public static void afterTestBaseClass() {
    	quitBrowser();
    }
    
    private static void quitBrowser() {
    	getDriver().quit();
    }
    
    public static Properties getConfig(String filename) throws IOException {
		InputStream is = WebAppTestBase.class.getClassLoader().getResourceAsStream(filename);
		Properties config = new Properties();
		config.load(is);
    	return config;
    }
    
    /*--------------------------------------------------
		For test classes
     --------------------------------------------------*/
    public static void beforeTestClass(String filename) throws IOException, StoreException, InterruptedException {
    	
    	Properties config = getConfig(filename);
    	AppConfig appConfig = new AppConfig(filename);
		
		URL = config.getProperty("url") != null ? config.getProperty("url") : "";
		ADMIN_URL = config.getProperty("admin_url") != null ? config.getProperty("admin_url") : "";
		
		String proxy = config.getProperty("proxy") != null ? config.getProperty("proxy") : "";
		// JSCover
		if("jscover".equals(proxy)) {
			String dir = config.getProperty("jscover_report_dir") != null ? config.getProperty("jscover_report_dir") : "jscover";
			String instr = config.getProperty("jscover_instr_regx") != null ? config.getProperty("jscover_instr_regx") : "";
			String no_instr = config.getProperty("jscover_no_instr_regx") != null ? config.getProperty("jscover_no_instr_regx") : "";
			
			JSCoverBase.launchProxyServer(dir, PROXY_PORT, instr.split(","), no_instr.split(","));
		}
		// RevAjaxMutator
		else if(proxy.startsWith("ram")) {
			String dir = config.getProperty("ram_record_dir") != null ? config.getProperty("ram_record_dir") : "record";
			
			ArrayList<ProxyPlugin> plugins = new ArrayList<ProxyPlugin>();
			if(proxy.contains("record")) {
				plugins.add(new RecorderPlugin(dir));
			}

			if(proxy.contains("rewrite")) {
				RewriterPlugin plugin = new RewriterPlugin(dir);
				plugin.setRewriteFile(appConfig.getRecordedJsFile().getName());
				plugins.add(plugin);
			}

			if(proxy.contains("filter")) {
				String filter_url_prefix = config.getProperty("ram_filter_url_prefix") != null ? config.getProperty("ram_filter_url_prefix") : "http://localhost:80";
				String filter_method = config.getProperty("ram_filter_method") != null ? config.getProperty("ram_filter_method") : "POST";
				plugins.add(new FilterPlugin(filter_url_prefix, filter_method));
			}

			RevAjaxMutatorBase.launchProxyServer(plugins, PROXY);
		}
    }
    
    
    private static class AppConfig extends AppConfigBase {
    	private AppConfig(String config) throws IOException {
    		super(config);
    	}
    }
    
    public static void afterTestClass() {
    	JSCoverBase.interruptProxyServer(getDriver(), TIMEOUT);
    	RevAjaxMutatorBase.interruptProxyServer();
    }
    

    /*--------------------------------------------------
		Utilities
     --------------------------------------------------*/
    public static void takeScreenshot(String filename) {
    	try {
    		File screenshot = ( (TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
    		FileUtils.copyFile(screenshot, new File(filename));
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
}