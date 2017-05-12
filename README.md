# RevAjaxMutator
Written by [Yuta Maezawa](http://mzw.jp) and greatest contributors

## Description
A tool for "mutation testing" and "automated program repair" of Ajax Web Applications

## Let's Get Started

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
Then, RevAjaxMutator is available in a project under test.
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
chrome_bin=/path/to/chromedriver
firefox_bin=/path/to/firefox
geckodriver_bin=/path/to/geckodriver-0.15.0/geckodriver
phantomjs_bin=/path/to/phantomjs-2.1.1-linux-x86_64/bin/phantomjs

#selenium_hub_ip=http://localhost:5000

proxy_ip = 127.0.0.1
proxy_port = 8083

timeout = 5
``` 
RevAjaxMutator will use the driver in the order above.

In addition, deploy another property file for an application under test.
```
url 					= http://mzw.jp:80/yuta/research/ram/example/after/faulty/quizzy/main.php
path_to_js_file 		= quizzy/quizzy.js
path_to_html_file 		= main.php
path_to_test_case_file 	= src/main/java/jp/mzw/revajaxmutator/test/quizzy/QuizzyTest.java
ram_record_dir			= record/quizzy
failure_cov_file 		= jscover/quizzy/jscoverage.failure.json
proxy					= ram
#proxy					= ram record
#proxy					= ram rewrite
```

### Setting up Selenium-grid (optional)
To use Selenium-grid (distributed processing), uncomment ``selenium_hub_ip`` from ``localenv.properties`` and change the value as appropriate.
Selenium-grid also requires a "hub" machine and the "worker" machines properly setup and configured.
To setup the hub, input the following into an available machine:
```
$ docker pull selenium/hub
$ docker run -d -p 5000:4444 --name selenium-hub -P selenium/hub
```
For each worker machine:
```
$ docker run -d --link selenium-hub:hub -P --name chrome selenium/node-chrome
$ docker start chrome

copy RevAjaxMutatorProxy into worker (TODO UPDATE THESE VALUES WITH REAL REPOSITORY):
$ wget "https://mvn-repo.mzw.jp/service/local/artifact/maven/redirect?r=snapshots&g=jp.mzw.revajaxmutator&a=RevAjaxMutator&v=LATEST"
$ sudo docker cp /RevAjaxMutator-LATEST.jar chrome:/home/seluser/

attach bash to running container:
$ sudo docker exec -i -t chrome /bin/bash
container$ sudo java -cp /home/seluser/RevAjaxMutator-LATEST.jar jp.mzw.revajaxmutator.proxy.ProxyServer
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
config-class	:= jp.mzw.revajaxmutator.test.quizzy.QuizzyConfig
config-file 	:= target/classes/quizzy.properties

compile:
	mvn clean compile test-compile dependency:copy-dependencies
	
rec:
	echo "proxy=ram record" >> ${config-file}
	${ram} test ${test-class}
	
mutation:
	${ram} mutate ${config-class}\$$MutateConfiguration
	
testing:
	echo "proxy=ram rewrite" >> ${config-file}
	${ram} analysis ${config-class}\$$MutateConfiguration ${test-class}
	
automated:
	${ram} mutate ${config-class}\$$RepairConfiguration
	
program:
	${ram} search ${config-class}

repair:
	echo "proxy=ram rewrite" >> ${config-file}
	${ram} analysis ${config-class}\$$RepairConfiguration ${test-class}
```

Enjoy RevAjaxMutator!
```
$ make mutation testing
$ make automated program repair
```

## Contributors
- Kazuki Nishiura (AjaxMutator)
- Kohsuke Yatoh (Proxy functionality)
- Junto Nakaoka (Space-search functionality)
- Shumpei Itho (Do-fewer, do-smarter, and do-faster approaches)
- Filipe Guerreiro (OnelineAjaxFixMiner)
- Many graduate students in computer science (Test case implementation)

## License
[Apache 2.0 License](blob/master/LICENSE)
