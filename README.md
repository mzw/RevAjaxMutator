# RevAjaxMutator
Written by [Yuta Maezawa](http://mzw.jp) and greatest contributors

## Description
A tool for "mutation testing" and "automated program repair" of Ajax Web Applications

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

### Configuration
Deploy ``localenv.properties`` on the class path.
```
firefox-bin=path/to/firefox-bin
#phantomjs-bin=path/to/phantomjs-1.9.7-***/bin/phantomjs
proxy_port=8080
timeout=5
``` 

In addition, deploy another property file for an application under test.
```
url 					= http://mzw.jp:80/yuta/research/ram/example/after/faulty/quizzy/main.php
path_to_js_file 		= quizzy/quizzy.js
path_to_html_file 		= main.php
path_to_test_case_file 	= src/main/java/jp/mzw/revajaxmutator/test/quizzy/QuizzyTest.java
ram_record_dir			= record/quizzy
proxy					= ram
#proxy					= ram record
#proxy					= ram rewrite
success_cov_file 		= jscover/quizzy/jscoverage.success.json
failure_cov_file 		= jscover/quizzy/jscoverage.failure.json
```

### Test Case
Test cases need to be extended from ``jp.mzw.revajaxmutator.test.WebAppTestBase``.
Otherwise, you need to instantiate our record/rewrite proxy server manually.
```
import jp.mzw.revajaxmutator.test.WebAppTestBase;
public class YourAppTest extends WebAppTestBase {...
```

### Run
We provide a typical Makefile working on a project under test.
```
ram				:= java -cp target/classes:target/test-classes:target/dependency/* jp.mzw.revajaxmutator.Main
test-class		:= jp.mzw.revajaxmutator.test.quizzy.QuizzyTest
mutate-class	:= jp.mzw.revajaxmutator.test.quizzy.QuizzyConfig\$$MutateConfiguration
repair-class	:= jp.mzw.revajaxmutator.test.quizzy.QuizzyConfig\$$RepairConfiguration
config-file 	:= target/classes/quizzy.properties

compile:
	mvn clean compile test-compile dependency:copy-dependencies
	
rec:
	echo "proxy=ram record" >> ${config-file}
	${ram} test ${test-class}
	
mutate:
	${ram} mutate ${mutate-class}
	
analysis:
	echo "proxy=ram rewrite" >> ${config-file}
	${ram} analysis ${mutate-class} ${test-class}
	
repair:
	${ram} mutate ${repair-class}
	
search:
	${ram} search
	echo "proxy=ram rewrite" >> ${config-file}
	${ram} analysis ${repair-class} ${test-class}
```

For mutation testing:
```
$ make compile
$ make rec
$ make mutate
$ make analysis
```

For automated program repair:
```
$ make compile
$ make rec
$ make repair
$ make search
```

## License
[Apache 2.0 License](blob/master/LICENSE)

## Contributors
- Kazuki Nishiura (AjaxMutator)
- Kohsuke Yatoh (Proxy functionality)
- Junto Nakaoka (Space-search functionality)
- Many graduate students in computer science (Test case implementation)

----
(C) [Yuta Maezawa](http://mzw.jp) 2016
