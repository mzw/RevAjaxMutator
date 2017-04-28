package jp.mzw.ajaxmutator.test.runner;

import java.util.List;

import org.junit.runners.model.InitializationError;

public class OrderedJUnitTestRunner extends JUnitTestRunner {

	public OrderedJUnitTestRunner(Class<?> testClass, boolean shouldRunAllTest, List<String> orderdMethodNames)
			throws InitializationError {
		super(testClass, shouldRunAllTest);
	}

}
