package jp.mzw.ajaxmutator.test.executor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
	private List<String> orderedMethodNames = null;
	private String executionMessage;
	// Used in concurrency to determine what kind of mutation tests this
	// Executor will run
	private String mutationFixAssignment;

	public JUnitExecutor(Class<?>... targetClasses) {
		this(true, targetClasses);
	}

	public JUnitExecutor(boolean shouldRunAllTest, Class<?>... targetClasses) {
		this.shouldRunAllTest = shouldRunAllTest;
		this.targetClasses = targetClasses;
	}

	public List<Result> run() {
		final ArrayList<Result> results = new ArrayList<Result>();
		for (final Class<?> testClass : this.targetClasses) {
			final Result result = this.runSingleTest(testClass);
			results.add(result);
		}
		return results;
	}

	private Result runSingleTest(Class<?> testClass) {
		if (this.orderedMethodNames == null) {
			Runner runner;
			try {
				final RunWith runWith = testClass.getAnnotation(RunWith.class);
				if (runWith == null) {
					runner = new JUnitTestRunner(testClass, this.shouldRunAllTest);
				} else if (Theories.class.equals(runWith.value())) {
					runner = new JUnitTheoryRunner(testClass, this.shouldRunAllTest);
				} else {
					runner = new JUnitTestRunner(testClass, this.shouldRunAllTest);
				}
			} catch (final InitializationError error) {
				throw new IllegalStateException(error);
			}
			final Result result = (new JUnitCore()).run(runner);
			return result;
		} else {
			Runner runner;
			try {
				runner = new OrderedJUnitTestRunner(testClass, this.shouldRunAllTest, this.orderedMethodNames);
			} catch (final InitializationError error) {
				throw new IllegalStateException(error);
			}
			final Result result = (new JUnitCore()).run(runner);
			return result;
		}
	}

	@Override
	public void setOrderedMethodNames(List<String> orderedMethodNames) {
		this.orderedMethodNames = orderedMethodNames;
	}

	@Override
	public boolean execute() {
		return this.execute("");
	}

	@Override
	public boolean execute(String mutationId) {
		this.testResults = new TreeMap<String, Boolean>();
		for (final Class<?> testClass : this.targetClasses) {
			if (!this.executeSingleTest(testClass, mutationId)) {
				this.updateMessage(false);
				return false;
			}
		}
		this.updateMessage(true);
		return true;
	}

	private boolean executeSingleTest(Class<?> testClass, String mutationId) {
		Runner runner;
		try {
			final RunWith runWith = testClass.getAnnotation(RunWith.class);
			if (runWith == null) {
				runner = new JUnitTestRunner(testClass, this.shouldRunAllTest, mutationId);
			} else if (Theories.class.equals(runWith.value())) {
				runner = new JUnitTheoryRunner(testClass, this.shouldRunAllTest, mutationId);
			} else {
				this.LOGGER.debug("Found unimplemented test-runner: {}", runWith.value());
				runner = new JUnitTestRunner(testClass, this.shouldRunAllTest, mutationId);
			}
		} catch (final InitializationError error) {
			throw new IllegalStateException(error);
		}

		System.out.println("(" + this.hashCode() + ") Before JUnitCore.run...");
		final Result result = new JUnitCore().run(runner);
		System.out.println("(" + this.hashCode() + ") After JUnitCore.run..." + result.wasSuccessful());
		System.out.println("(" + this.hashCode() + ") Run count " + result.getRunCount());
		System.out.println("(" + this.hashCode() + ") Failure count " + result.getFailureCount());
		System.out.println("(" + this.hashCode() + ") Ignore count " + result.getIgnoreCount());

		this.storeResult(result);
		return result.wasSuccessful();
	}

	private void storeResult(Result result) {
		final List<String> testMethods = new ArrayList<String>();
		for (final Class<?> clazz : this.targetClasses) {
			for (final Method method : clazz.getMethods()) {
				if (method.isAnnotationPresent(Test.class)) {
					testMethods.add(method.getName());
				}
			}
		}
		for (final String methodName : testMethods) {
			this.testResults.put(methodName, true);
		}
		if (!result.wasSuccessful()) {
			for (final Failure failure : result.getFailures()) {
				this.LOGGER.debug("Failure trace: {}", failure.getTrace());
				if (failure.getDescription().getMethodName() == null) {
					this.testResults.put("setup or teardown", false);
					continue;
				}
				this.testResults.put(failure.getDescription().getMethodName(), false);
			}
		}
	}

	private void updateMessage(boolean result) {
		final StringBuilder messageBuilder = new StringBuilder();
		if (result) {
			messageBuilder.append("Test succeed (failed to kill mutants), ").append(this.testResults.size())
					.append(" tests ran.\n");
		} else {
			messageBuilder.append("Mutant is killed; tests failed within ").append(this.testResults.size())
					.append('\n');
		}
		for (final Map.Entry<String, Boolean> entry : this.testResults.entrySet()) {
			messageBuilder.append(entry.getKey()).append(':').append(entry.getValue() ? 'x' : 'o').append(", ");
		}
		messageBuilder.append("result: " + (result ? 'x' : 'o'));
		this.executionMessage = messageBuilder.toString();
	}

	@Override
	public String getMessageOnLastExecution() {
		return this.executionMessage;
	}

	@Override
	public String getTargetClassName() {
		return this.targetClasses[0].getName();
	}

	@Override
	public String getMutationFixAssignment() {
		return this.mutationFixAssignment;
	}

	@Override
	public void setMutationFixAssignment(String mutationFixAssignment) {
		this.mutationFixAssignment = mutationFixAssignment;
	}

}
