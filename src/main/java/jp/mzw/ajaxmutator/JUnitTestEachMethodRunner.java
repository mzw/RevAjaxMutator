package jp.mzw.ajaxmutator;

import static org.junit.internal.runners.rules.RuleMemberValidator.RULE_VALIDATOR;

import org.junit.*;
import org.junit.Test.None;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.statements.*;
import org.junit.rules.RunRules;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.channels.ShutdownChannelGroupException;
import java.util.ArrayList;
import java.util.List;

public class JUnitTestEachMethodRunner extends JUnitTestRunner {
	private static Method testMethod;

	public JUnitTestEachMethodRunner(Class<?> testClass, boolean shouldRunAllTest,Method method) throws InitializationError {
		super(setMethod(testClass,method),shouldRunAllTest);
	}
	
	private static Class<?> setMethod(Class<?> testClass,Method method){
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