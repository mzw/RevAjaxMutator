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
chromedriver_bin=/path/to/chromedriver
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
(If not running in the same machine as the hub, remove --link option. Also remove the --add-host option if you are not using the examples from "ram-test".)
$ docker run -d --link selenium-hub:hub -P --name selenium-worker \
  --add-host ram-test.mzw.jp:172.17.0.1 \
  --env "NODE_MAX_OBJECTS=4"   \
  --env "NODE_MAX_INSTANCES=4" \
  --env "NODE_MAX_SESSIONS=4"  \
  filipeguerreiro/selenium-worker:latest
$ docker exec -i -t selenium-worker /bin/bash
seluser$ /home/seluser/start-proxy.sh
CTRL+P, CTRL+Q
```

### Test Case
Test cases need to be extended from ``jp.mzw.revajaxmutator.test.WebAppTestBase``.
Otherwise, you need to instantiate our record/rewrite proxy server manually.
```
import jp.mzw.revajaxmutator.test.WebAppTestBase;
public class YourAppTest extends WebAppTestBase {...
```

Example test cases can be found at: <https://gitlab.mzw.jp/yuta/ram-test-2>

### Run
To run RevAjaxMutator against a particular project (in this case, Roundcubemail from ram-test-2) :
```
records webpage files locally -- needed for mutating files and testing the results:
$ java -cp ${class-path} jp.mzw.revajaxmutator.CLI \
    record \
    jp.mzw.revajaxmutator.test.roundcubemail.RoundcubemailConfig \
    jp.mzw.revajaxmutator.test.roundcubemail.RoundcubemailTest

generate test coverage -- speeds up mutation:
$ java -cp ${class-path} jp.mzw.revajaxmutator.CLI \
    test-each \
    jp.mzw.revajaxmutator.test.roundcubemail.RoundcubemailConfig \
    jp.mzw.revajaxmutator.test.roundcubemail.RoundcubemailTest
    
try to automatically find the correct mutation that fixes the failing test:
$ java -cp ${class-path} jp.mzw.revajaxmutator.CLI \
    validate-concurrently \
    jp.mzw.revajaxmutator.test.roundcubemail.RoundcubemailConfig \
    jp.mzw.revajaxmutator.test.roundcubemail.RoundcubemailTest
```

Enjoy RevAjaxMutator!

## Contributors
- Kazuki Nishiura (AjaxMutator)
- Kohsuke Yatoh (Proxy functionality)
- Junto Nakaoka (Space-search functionality)
- Shumpei Itho (Do-fewer, do-smarter, and do-faster approaches)
- Filipe Guerreiro (OnelineAjaxFixMiner)
- Many graduate students in computer science (Test case implementation)

## License
[Apache 2.0 License](blob/master/LICENSE)
