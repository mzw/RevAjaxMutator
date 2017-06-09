package jp.mzw.ajaxmutator.test.runner;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EachJUnitTestRunner extends JUnitTestRunner {
	private static Method testMethod;

	public EachJUnitTestRunner(Class<?> testClass, boolean shouldRunAllTest, Method method) throws InitializationError {
		super(setMethod(testClass, method), shouldRunAllTest);
	}

	private static Class<?> setMethod(Class<?> testClass, Method method) {
		testMethod = method;
		return testClass;
	}

	@Override
	protected List<FrameworkMethod> computeTestMethods() {
		List<FrameworkMethod> methods = new ArrayList<FrameworkMethod>();
		methods.add(new FrameworkMethod(testMethod));
		return methods;
	}
}
