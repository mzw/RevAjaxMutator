package jp.mzw.ajaxmutator.test.executor;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.mzw.ajaxmutator.test.runner.JUnitTestRunner;
import jp.mzw.ajaxmutator.test.runner.JUnitTheoryRunner;
import jp.mzw.ajaxmutator.test.runner.OrderedJUnitTestRunner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * TestExecutor for testclasses written in Junit4.
 *
 * @author Kazuki Nishiura
 */
public class JUnitExecutor implements TestExecutor {
	protected Logger LOGGER = LoggerFactory.getLogger(JUnitExecutor.class);

	private final boolean shouldRunAllTest;
	private final Class<?>[] targetClasses;
	private Map<String, Boolean> testResults;
	private List<String> orderdMethodNames = null;
	private String executionMessage;

	public JUnitExecutor(Class<?>... targetClasses) {
		this(true, targetClasses);
	}

	public JUnitExecutor(boolean shouldRunAllTest, Class<?>... targetClasses) {
		this.shouldRunAllTest = shouldRunAllTest;
		this.targetClasses = targetClasses;
	}

	public List<Result> run() {
		ArrayList<Result> results = new ArrayList<Result>();
		for (Class<?> testClass : targetClasses) {
			Result result = runSingleTest(testClass);
			results.add(result);
		}
		return results;
	}

	private Result runSingleTest(Class<?> testClass){
		if (orderdMethodNames == null) {
			Runner runner;
			try {
				RunWith runWith = testClass.getAnnotation(RunWith.class);
				if (runWith == null) {
					runner = new JUnitTestRunner(testClass, shouldRunAllTest);
				} else if (Theories.class.equals(runWith.value())) {
					runner = new JUnitTheoryRunner(testClass, shouldRunAllTest);
				} else {
					runner = new JUnitTestRunner(testClass, shouldRunAllTest);
				}
			} catch (InitializationError error) {
				throw new IllegalStateException(error);
			}
			Result result = (new JUnitCore()).run(runner);
			return result;
		} else {
			Runner runner;
			try {
				runner = new OrderedJUnitTestRunner(testClass, shouldRunAllTest, orderdMethodNames);
			} catch (InitializationError error) {
				throw new IllegalStateException(error);
			}
			Result result = (new JUnitCore()).run(runner);
			return result;
		}
	}
	
	@Override
	public void setOrderdMethodNames(List<String> orderdMethodNames) {
		this.orderdMethodNames = orderdMethodNames;
	}

	@Override
	public boolean execute() {
		testResults = new TreeMap<String, Boolean>();
		for (Class<?> testClass : targetClasses) {
			if (!executeSingleTest(testClass)) {
				updateMessage(false);
				return false;
			}
		}
		updateMessage(true);
		return true;
	}

	private boolean executeSingleTest(Class<?> testClass) {
		Runner runner;
		try {
			RunWith runWith = testClass.getAnnotation(RunWith.class);
			if (runWith == null) {
				runner = new JUnitTestRunner(testClass, shouldRunAllTest);
			} else if (Theories.class.equals(runWith.value())) {
				runner = new JUnitTheoryRunner(testClass, shouldRunAllTest);
			} else {
				LOGGER.debug("Found unimplemented test-runner: {}", runWith.value());
				runner = new JUnitTestRunner(testClass, shouldRunAllTest);
			}
		} catch (InitializationError error) {
			throw new IllegalStateException(error);
		}
		Result result = (new JUnitCore()).run(runner);
		storeResult(result);
		return result.wasSuccessful();
	}

	private void storeResult(Result result) {
		List<String> testMethods = new ArrayList<String>();
		for (Class<?> clazz : targetClasses) {
			for (Method method : clazz.getMethods()) {
				if (method.isAnnotationPresent(Test.class))
					testMethods.add(method.getName());
			}
		}
		for (String methodName : testMethods) {
			testResults.put(methodName, true);
		}
		if (!result.wasSuccessful()) {
			for (Failure failure : result.getFailures()) {
				LOGGER.debug("Failure trace: {}", failure.getTrace());
				if (failure.getDescription().getMethodName() == null) {
					testResults.put("setup or teardown", false);
					continue;
				}
				testResults.put(failure.getDescription().getMethodName(), false);
			}
		}
	}

	private void updateMessage(boolean result) {
		StringBuilder messageBuilder = new StringBuilder();
		if (result) {
			messageBuilder.append("Test succeed (failed to kill mutants), ").append(testResults.size())
					.append(" tests ran.\n");
		} else {
			messageBuilder.append("Mutant is killed; tests failed within ").append(testResults.size()).append('\n');
		}
		for (Map.Entry<String, Boolean> entry : testResults.entrySet()) {
			messageBuilder.append(entry.getKey()).append(':').append(entry.getValue() ? 'x' : 'o').append(", ");
		}
		messageBuilder.append("result: " + (result ? 'x' : 'o'));
		executionMessage = messageBuilder.toString();
	}

	@Override
	public String getMessageOnLastExecution() {
		return executionMessage;
	}

	@Override
	public String getTargetClassName() {
		return targetClasses[0].getName();
	}
}
