package jp.mzw.revajaxmutator.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

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
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.owasp.webscarab.model.StoreException;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;

public class WebAppTestBase {

	protected static String URL;
	protected static String ADMIN_URL;
    protected static WebDriver driver;
    protected static WebDriverWait wait;

    private static String CONFIG_FILENAME = "localenv.properties";
    
    protected static Properties CONFIG;
    protected static String FIREFOX_BIN;
    protected static String PHANTOMJS_BIN;
    protected static String PROXY;
    protected static String PROXY_PORT;
    protected static int TIMEOUT;
    
    /**
     * Before instantiating this class,
     * explicitly call methods in the following order:
     * 1. readConfig
     * 2. launchBrowser
     * although multiple "@BeforeClass" can be allowed.
     * @throws IOException indicates "localenv.properties" not found on the resource path
     */
    @BeforeClass
    public static void beforeTestBaseClass() throws IOException {
    	readTestBaseConfig();
    	launchBrowser();
    }
	
    /**
     * Launch given Firefox browser with given proxy configuration
     */
    private static void launchBrowser() {
        DesiredCapabilities cap = new DesiredCapabilities();
        
        if(FIREFOX_BIN != null) {
            org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
            proxy.setHttpProxy(PROXY);
            proxy.setFtpProxy(PROXY);
            proxy.setSslProxy(PROXY);
            cap.setCapability(CapabilityType.PROXY, proxy);
            
        	FirefoxBinary binary = new FirefoxBinary(new File(FIREFOX_BIN));
        	cap.setCapability(FirefoxDriver.BINARY, binary);
        	driver = new FirefoxDriver(cap);
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
        }
        
        wait = new WebDriverWait(driver, TIMEOUT);
    }
    
    /**
     * Read configuration to specify firefox and proxy
     * @throws IOException indicates "localenv.properties" not found on the resource path
     */
	private static void readTestBaseConfig() throws IOException {
		CONFIG = getConfig(CONFIG_FILENAME);

		FIREFOX_BIN = CONFIG.getProperty("firefox-bin");
		PHANTOMJS_BIN = CONFIG.getProperty("phantomjs-bin");
		PROXY_PORT = CONFIG.getProperty("proxy_port") != null ? CONFIG.getProperty("proxy_port") : "80";
		PROXY = "127.0.0.1:" + PROXY_PORT;
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
    	driver.quit();
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

			RevAjaxMutatorBase.launchProxyServer(plugins, PROXY_PORT);
		}
    }
    
    private static class AppConfig extends AppConfigBase {
    	private AppConfig(String config) throws IOException {
    		super(config);
    	}
    }
    
    public static void afterTestClass() {
    	JSCoverBase.interruptProxyServer(driver, TIMEOUT);
    	RevAjaxMutatorBase.interruptProxyServer();
    }
    

    /*--------------------------------------------------
		Utilities
     --------------------------------------------------*/
    public static void takeScreenshot(String filename) {
    	try {
    		File screenshot = ( (TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
    		FileUtils.copyFile(screenshot, new File(filename));
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
}
