# RevAjaxMutator
Written by [Yuta Maezawa](http://mzw.jp) and greatest contributors

## Get Started

### Maven Settings
Add our in-house Maven repository.
```
<repositories>
	<repository>
		<id>jp.mzw.mvn-repo</id>
		<url>http://mvn-repo.mzw.jp</url>
	</repository>
</repositories>
```
Then, RevAjaxMutator is available in your testing project.
```
<dependency>
	<groupId>jp.mzw.revajaxmutator</groupId>
	<artifactId>RevAjaxMutator</artifactId>
	<version>0.1</version>
</dependency>
```

### Properties
In your class-path, 

### Test Case Implementation
We recommend to use ``jp.mzw.revajaxmutator.test.WebAppTestBase``.
```
...
import jp.mzw.revajaxmutator.test.WebAppTestBase;
public class YourAppTest extends WebAppTestBase {
...
	@BeforeClass
	public static void beforeTestClass() throws StoreException, InterruptedException, IOException {
		WebAppTestBase.beforeTestClass(APP_CONFIG);
	}
	@AfterClass
	public static void afterTestClass() {
		try {
			Properties config = getConfig(APP_CONFIG);
			String jscover_report_dir = config.getProperty("jscover_report_dir") != null ? config.getProperty("jscover_report_dir") : null;
			if(jscover_report_dir != null) {
				File cov_result = new File(jscover_report_dir, "jscoverage.json");
		        if (cov_result.exists()) cov_result.delete();
		        ((JavascriptExecutor) driver).executeScript("jscoverage_report();");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		driver.quit();
	}
```

### Configuration
As a configuration for RevAjaxMutator,
we typically implement a class, as follows.
```
public class MyConfiguration extends jp.mzw.revajaxmutator.config.AppConfigBase {
	public static class MutateConfiguration extends jp.mzw.revajaxmutator.MutateConfigurationBase {
	}
}
```

### Implementing Test Cases

### Compile
> mvn clean compile assembly:single

### Test Case Implementation

### Create Configuration

### Run
```
$ ram.sh 
```

## Contributors
- Kazuki Nishiura (AjaxMutator)
- Kohsuke Yatoh (Proxy functionality)
- Junto Nakaoka (Repair space search)
- Many graduate students in computer science (Test case implementation)

----
(C) [Yuta Maezawa](http://mzw.jp) 2016
