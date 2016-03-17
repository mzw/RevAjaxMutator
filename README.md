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
