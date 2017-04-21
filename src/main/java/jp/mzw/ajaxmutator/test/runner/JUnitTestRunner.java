package jp.mzw.ajaxmutator.test.runner;

import static org.junit.internal.runners.rules.RuleMemberValidator.RULE_VALIDATOR;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Test.None;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.internal.runners.statements.Fail;
import org.junit.internal.runners.statements.FailOnTimeout;
import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.rules.RunRules;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.mzw.revajaxmutator.test.WebAppTestBase;

/**
 * Basically same as {@link BlockJUnit4ClassRunner}, but this class can skip
 * test execution when any test fails.
 */
public class JUnitTestRunner extends ParentRunner<FrameworkMethod> {
	private static final Logger LOGGER = LoggerFactory.getLogger(JUnitTestRunner.class);

	protected final boolean shouldRunAllTest;
	protected boolean skipNextExecution = false;
	protected RunNotifierFailureReporter reporter;
	protected RunNotifier lastNotifier;
	protected final String mutationFileId;

	public JUnitTestRunner(Class<?> testClass, boolean shouldRunAllTest, String mutationId) throws InitializationError {
		super(testClass);
		this.shouldRunAllTest = shouldRunAllTest;
		this.mutationFileId = mutationId;
	}

	public JUnitTestRunner(Class<?> testClass, boolean shouldRunAllTest) throws InitializationError {
		this(testClass, shouldRunAllTest, "");
	}

	@Override
	protected void runChild(final FrameworkMethod method, RunNotifier notifier) {

		final Description description = this.describeChild(method);
		if (method.getAnnotation(Ignore.class) != null || this.skipNextExecution) {
			notifier.fireTestIgnored(description);
		} else {
			if (!this.shouldRunAllTest) {
				if (this.reporter == null || !this.lastNotifier.equals(notifier)) {
					this.reporter = new RunNotifierFailureReporter(notifier);
					this.lastNotifier = notifier;
				}
			} else {
				if (this.reporter == null) {
					this.reporter = new RunNotifierFailureReporter(notifier);
				}
			}
			LOGGER.info("<Thread:{}> run test {} : {}", Thread.currentThread().getId(), this.getTestClass().getName(),
					method.getName());
			this.runLeaf(this.methodBlock(method), description, this.reporter);
		}
	}

	@Override
	protected Description describeChild(FrameworkMethod method) {
		return Description.createTestDescription(this.getTestClass().getJavaClass(), this.testName(method),
				method.getAnnotations());
	}

	@Override
	protected List<FrameworkMethod> getChildren() {
		return this.computeTestMethods();
	}

	protected List<FrameworkMethod> computeTestMethods() {
		return this.getTestClass().getAnnotatedMethods(Test.class);
	}

	@Override
	protected void collectInitializationErrors(List<Throwable> errors) {
		super.collectInitializationErrors(errors);

		this.validateNoNonStaticInnerClass(errors);
		this.validateConstructor(errors);
		this.validateInstanceMethods(errors);
		this.validateFields(errors);

	}

	protected void validateNoNonStaticInnerClass(List<Throwable> errors) {
		if (this.getTestClass().isANonStaticInnerClass()) {
			final String gripe = "The inner class " + this.getTestClass().getName() + " is not static.";
			errors.add(new Exception(gripe));
		}
	}

	/**
	 * Adds to {@code errors} if the test class has more than one constructor,
	 * or if the constructor takes parameters. Override if a subclass requires
	 * different validation rules.
	 */
	protected void validateConstructor(List<Throwable> errors) {
		this.validateOnlyOneConstructor(errors);
		this.validateZeroArgConstructor(errors);
	}

	/**
	 * Adds to {@code errors} if the test class has more than one constructor
	 * (do not override)
	 */
	protected void validateOnlyOneConstructor(List<Throwable> errors) {
		if (!this.hasOneConstructor()) {
			final String gripe = "Test class should have exactly one public constructor";
			errors.add(new Exception(gripe));
		}
	}

	/**
	 * Adds to {@code errors} if the test class's single constructor takes
	 * parameters (do not override)
	 */
	protected void validateZeroArgConstructor(List<Throwable> errors) {
		if (!this.getTestClass().isANonStaticInnerClass() && this.hasOneConstructor()
				&& (this.getTestClass().getOnlyConstructor().getParameterTypes().length != 0)) {
			final String gripe = "Test class should have exactly one public zero-argument constructor";
			errors.add(new Exception(gripe));
		}
	}

	protected boolean hasOneConstructor() {
		return this.getTestClass().getJavaClass().getConstructors().length == 1;
	}

	/**
	 * Adds to {@code errors} for each method annotated with {@code @Test},
	 * {@code @Before}, or {@code @After} that is not a public, void instance
	 * method with no arguments.
	 *
	 * @deprecated unused API, will go away in future version
	 */
	@Deprecated
	protected void validateInstanceMethods(List<Throwable> errors) {
		this.validatePublicVoidNoArgMethods(After.class, false, errors);
		this.validatePublicVoidNoArgMethods(Before.class, false, errors);
		this.validateTestMethods(errors);

		if (this.computeTestMethods().size() == 0) {
			errors.add(new Exception("No runnable methods"));
		}
	}

	protected void validateFields(List<Throwable> errors) {
		RULE_VALIDATOR.validate(this.getTestClass(), errors);
	}

	/**
	 * Adds to {@code errors} for each method annotated with {@code @Test}that
	 * is not a public, void instance method with no arguments.
	 */
	protected void validateTestMethods(List<Throwable> errors) {
		this.validatePublicVoidNoArgMethods(Test.class, false, errors);
	}

	/**
	 * Returns a new fixture for running a test. Default implementation executes
	 * the test class's no-argument constructor (validation should have ensured
	 * one exists).
	 */
	protected Object createTest() throws Exception {
		final Object instance = this.getTestClass().getOnlyConstructor().newInstance();

		final WebAppTestBase webAppTest = (WebAppTestBase) instance;
		webAppTest.setMutationFileId(this.mutationFileId);

		return instance;
	}

	/**
	 * Returns the name that describes {@code method} for {@link Description}s.
	 * Default implementation is the method's name
	 */
	protected String testName(FrameworkMethod method) {
		return method.getName();
	}

	/**
	 * Returns a Statement that, when executed, either returns normally if
	 * {@code method} passes, or throws an exception if {@code method} fails.
	 *
	 * Here is an outline of the default implementation:
	 *
	 * <ul>
	 * <li>Invoke {@code method} on the result of {@code createTest()}, and
	 * throw any exceptions thrown by either operation.
	 * <li>HOWEVER, if {@code method}'s {@code @Test} annotation has the {@code
	 * expecting} attribute, return normally only if the previous step threw an
	 * exception of the correct type, and throw an exception otherwise.
	 * <li>HOWEVER, if {@code method}'s {@code @Test} annotation has the {@code
	 * timeout} attribute, throw an exception if the previous step takes more
	 * than the specified number of milliseconds.
	 * <li>ALWAYS run all non-overridden {@code @Before} methods on this class
	 * and superclasses before any of the previous steps; if any throws an
	 * Exception, stop execution and pass the exception on.
	 * <li>ALWAYS run all non-overridden {@code @After} methods on this class
	 * and superclasses after any of the previous steps; all After methods are
	 * always executed: exceptions thrown by previous steps are combined, if
	 * necessary, with exceptions from After methods into a
	 * {@link MultipleFailureException}.
	 * <li>ALWAYS allow {@code @Rule} fields to modify the execution of the
	 * above steps. A {@code Rule} may prevent all execution of the above steps,
	 * or add additional behavior before and after, or modify thrown exceptions.
	 * For more information, see {@link TestRule}
	 * </ul>
	 *
	 * This can be overridden in subclasses, either by overriding this method,
	 * or the implementations creating each sub-statement.
	 */
	protected Statement methodBlock(FrameworkMethod method) {
		Object test;
		try {
			test = new ReflectiveCallable() {
				@Override
				protected Object runReflectiveCall() throws Throwable {
					return JUnitTestRunner.this.createTest();
				}
			}.run();
		} catch (final Throwable e) {
			return new Fail(e);
		}

		Statement statement = this.methodInvoker(method, test);
		statement = this.possiblyExpectingExceptions(method, test, statement);
		statement = this.withPotentialTimeout(method, test, statement);
		statement = this.withBefores(method, test, statement);
		statement = this.withAfters(method, test, statement);
		statement = this.withRules(method, test, statement);
		return statement;
	}

	//
	// Statement builders
	//

	/**
	 * Returns a {@link Statement} that invokes {@code method} on {@code test}
	 */
	protected Statement methodInvoker(FrameworkMethod method, Object test) {
		return new InvokeMethod(method, test);
	}

	/**
	 * Returns a {@link Statement}: if {@code method}'s {@code @Test} annotation
	 * has the {@code expecting} attribute, return normally only if {@code next}
	 * throws an exception of the correct type, and throw an exception
	 * otherwise.
	 *
	 * @deprecated Will be private soon: use Rules instead
	 */
	@Deprecated
	protected Statement possiblyExpectingExceptions(FrameworkMethod method, Object test, Statement next) {
		final Test annotation = method.getAnnotation(Test.class);
		return this.expectsException(annotation) ? new ExpectException(next, this.getExpectedException(annotation))
				: next;
	}

	/**
	 * Returns a {@link Statement}: if {@code method}'s {@code @Test} annotation
	 * has the {@code timeout} attribute, throw an exception if {@code next}
	 * takes more than the specified number of milliseconds.
	 *
	 * @deprecated Will be private soon: use Rules instead
	 */
	@Deprecated
	protected Statement withPotentialTimeout(FrameworkMethod method, Object test, Statement next) {
		final long timeout = this.getTimeout(method.getAnnotation(Test.class));
		return timeout > 0 ? new FailOnTimeout(next, timeout) : next;
	}

	/**
	 * Returns a {@link Statement}: run all non-overridden {@code @Before}
	 * methods on this class and superclasses before running {@code next}; if
	 * any throws an Exception, stop execution and pass the exception on.
	 *
	 * @deprecated Will be private soon: use Rules instead
	 */
	@Deprecated
	protected Statement withBefores(FrameworkMethod method, Object target, Statement statement) {
		final List<FrameworkMethod> befores = this.getTestClass().getAnnotatedMethods(Before.class);
		return befores.isEmpty() ? statement : new RunBefores(statement, befores, target);
	}

	/**
	 * Returns a {@link Statement}: run all non-overridden {@code @After}
	 * methods on this class and superclasses before running {@code next}; all
	 * After methods are always executed: exceptions thrown by previous steps
	 * are combined, if necessary, with exceptions from After methods into a
	 * {@link MultipleFailureException}.
	 *
	 * @deprecated Will be private soon: use Rules instead
	 */
	@Deprecated
	protected Statement withAfters(FrameworkMethod method, Object target, Statement statement) {
		final List<FrameworkMethod> afters = this.getTestClass().getAnnotatedMethods(After.class);
		return afters.isEmpty() ? statement : new RunAfters(statement, afters, target);
	}

	protected Statement withRules(FrameworkMethod method, Object target, Statement statement) {
		Statement result = statement;
		result = this.withMethodRules(method, target, result);
		result = this.withTestRules(method, target, result);
		return result;
	}

	protected Statement withMethodRules(FrameworkMethod method, Object target, Statement result) {
		final List<TestRule> testRules = this.getTestRules(target);
		for (final org.junit.rules.MethodRule each : this.getMethodRules(target)) {
			if (!testRules.contains(each)) {
				result = each.apply(result, method, target);
			}
		}
		return result;
	}

	protected List<org.junit.rules.MethodRule> getMethodRules(Object target) {
		return this.rules(target);
	}

	/**
	 * @param target
	 *            the test case instance
	 * @return a list of MethodRules that should be applied when executing this
	 *         test
	 * @deprecated {@link org.junit.rules.MethodRule} is a deprecated interface.
	 *             Port to {@link TestRule} and
	 *             {@link BlockJUnit4ClassRunner#getTestRules(Object)}
	 */
	@Deprecated
	protected List<org.junit.rules.MethodRule> rules(Object target) {
		return this.getTestClass().getAnnotatedFieldValues(target, Rule.class, org.junit.rules.MethodRule.class);
	}

	/**
	 * Returns a {@link Statement}: apply all non-static {@link Value} fields
	 * annotated with {@link Rule}.
	 *
	 * @param statement
	 *            The base statement
	 * @return a RunRules statement if any class-level {@link Rule}s are found,
	 *         or the base statement
	 */
	protected Statement withTestRules(FrameworkMethod method, Object target, Statement statement) {
		final List<TestRule> testRules = this.getTestRules(target);
		return testRules.isEmpty() ? statement : new RunRules(statement, testRules, this.describeChild(method));
	}

	/**
	 * @param target
	 *            the test case instance
	 * @return a list of TestRules that should be applied when executing this
	 *         test
	 */
	protected List<TestRule> getTestRules(Object target) {
		return this.getTestClass().getAnnotatedFieldValues(target, Rule.class, TestRule.class);
	}

	protected Class<? extends Throwable> getExpectedException(Test annotation) {
		if (annotation == null || annotation.expected() == None.class) {
			return null;
		} else {
			return annotation.expected();
		}
	}

	protected boolean expectsException(Test annotation) {
		return this.getExpectedException(annotation) != null;
	}

	protected long getTimeout(Test annotation) {
		if (annotation == null) {
			return 0;
		}
		return annotation.timeout();
	}

	/**
	 * Wrapper of RunNotifier. Only task for this class is set flag on when test
	 * fails.
	 */
	protected class RunNotifierFailureReporter extends RunNotifier {
		private final RunNotifier notifier;

		private RunNotifierFailureReporter(RunNotifier notifier) {
			this.notifier = notifier;
		}

		@Override
		public void addListener(RunListener listener) {
			this.notifier.addListener(listener);
		}

		@Override
		public void removeListener(RunListener listener) {
			this.notifier.removeListener(listener);
		}

		@Override
		public void fireTestRunStarted(Description description) {
			this.notifier.fireTestRunStarted(description);
		}

		@Override
		public void fireTestRunFinished(Result result) {
			this.notifier.fireTestRunFinished(result);
		}

		@Override
		public void fireTestStarted(Description description) throws StoppedByUserException {
			this.notifier.fireTestStarted(description);
		}

		@Override
		public void fireTestAssumptionFailed(Failure failure) {
			this.notifier.fireTestAssumptionFailed(failure);
		}

		@Override
		public void fireTestIgnored(Description description) {
			this.notifier.fireTestIgnored(description);
		}

		@Override
		public void fireTestFinished(Description description) {
			this.notifier.fireTestFinished(description);
		}

		@Override
		public void pleaseStop() {
			this.notifier.pleaseStop();
		}

		@Override
		public void addFirstListener(RunListener listener) {
			this.notifier.addFirstListener(listener);
		}

		@Override
		public void fireTestFailure(Failure failure) {
			this.notifier.fireTestFailure(failure);
			JUnitTestRunner.this.skipNextExecution = true;
		}
	}
}