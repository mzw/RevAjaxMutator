package jp.mzw.revajaxmutator.test.quizzy;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.owasp.webscarab.model.StoreException;

import jp.mzw.revajaxmutator.test.WebAppTestBase;

public class QuizzyTest extends WebAppTestBase {

	@Test
	public void clickRadioButton0() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("quizzy_quiz_opt0")));
		driver.findElement(By.id("quizzy_quiz_opt0")).click();;
		Assert.assertEquals("Several quizzes are available; min/max score range is 0-100.",
				driver.findElement(By.id("quizzy_quiz_desc0")).getText().trim());
	}

	@Test
	public void clickRadioButton1() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("quizzy_quiz_opt1")));
		driver.findElement(By.id("quizzy_quiz_opt1")).click();;
		Assert.assertEquals("Under construction",
				driver.findElement(By.id("quizzy_quiz_desc1")).getText().trim());
	}
	
	@Test
	public void ckickRadioButtonLabel0() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("quizzy_quiz_lbl0")));
		driver.findElement(By.id("quizzy_quiz_lbl0")).click();
		Assert.assertEquals("Several quizzes are available; min/max score range is 0-100.",
				driver.findElement(By.id("quizzy_quiz_desc0")).getText().trim());
		Assert.assertTrue(driver.findElement(By.id("quizzy_quiz_opt0")).isSelected());
	}

	@Test
	public void ckickRadioButtonLabel1() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("quizzy_quiz_lbl1")));
		driver.findElement(By.id("quizzy_quiz_lbl1")).click();
		Assert.assertEquals("Under construction",
				driver.findElement(By.id("quizzy_quiz_desc1")).getText().trim());
		Assert.assertTrue(driver.findElement(By.id("quizzy_quiz_opt1")).isSelected());
	}
	
	//----------------------------------------------------------------------------------------------------
	protected static String APP_CONFIG = "quizzy.properties";

	@Before
	public void setup() {
		driver.get(URL);
		waitUntilShowWidgets();
	}
	
	public void waitUntilShowWidgets() {
		wait.until(new ExpectedCondition<Boolean>() {
	        public Boolean apply(WebDriver wdriver) {
	            return ((JavascriptExecutor) driver).executeScript(
	                "return document.readyState"
	            ).equals("complete");
	        }
	    });
	}
	
	@BeforeClass
	public static void beforeTestClass() throws StoreException, InterruptedException, IOException {
		WebAppTestBase.beforeTestClass(APP_CONFIG);
	}
	
	@AfterClass
	public static void afterTestClass() {
		try {
			Properties config = getConfig(APP_CONFIG);
			String proxy = config.getProperty("proxy") != null ? config.getProperty("proxy") : "not specified";
			String jscover_report_dir = config.getProperty("jscover_report_dir") != null ? config.getProperty("jscover_report_dir") : null;
			if(proxy.equals("jscover") && jscover_report_dir != null) {
				File cov_result = new File(jscover_report_dir, "jscoverage.json");
		        if (cov_result.exists()) cov_result.delete();
		        ((JavascriptExecutor) driver).executeScript("jscoverage_report();");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		driver.quit();
	}
}
