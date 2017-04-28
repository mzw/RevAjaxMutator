package jp.mzw.ajaxmutator.test.runner;

import java.util.List;

import org.junit.Assume;
import org.junit.Ignore;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.internal.Assignments;
import org.junit.experimental.theories.internal.ParameterizedAssertionError;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import jp.mzw.revajaxmutator.test.WebAppTestBase;

/**
 *
 * @author Yuta Maezawa
 *
 */
public class JUnitTheoryRunner extends Theories {
	private final boolean shouldRunAllTest;
	private boolean skipNextExecution = false;
	private RunNotifierFailureReporter reporter;
	private RunNotifier lastNotifier;
	private final String mutationId;

	public JUnitTheoryRunner(Class<?> testClass, boolean shouldRunAllTest, String mutationId)
			throws InitializationError {
		super(testClass);
		this.shouldRunAllTest = shouldRunAllTest;
		this.mutationId = mutationId;
	}

	public JUnitTheoryRunner(Class<?> testClass, boolean shouldRunAllTest) throws InitializationError {
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
			this.runLeaf(this.methodBlock(method), description, this.reporter);
		}
	}

	// Overridden from the Theories class in order to pass the "mutationId" to
	// the test class
	@Override
	public Statement methodBlock(FrameworkMethod method) {
		return new TheoryAnchor(method, this.getTestClass()) {

			@Override
			protected void runWithCompleteAssignment(final Assignments complete) throws Throwable {
				new BlockJUnit4ClassRunner(JUnitTheoryRunner.this.getTestClass().getJavaClass()) {
					@Override
					protected void collectInitializationErrors(List<Throwable> errors) {
						// do nothing
					}

					@Override
					public Statement methodBlock(FrameworkMethod method) {
						final Statement statement = super.methodBlock(method);
						return new Statement() {
							@Override
							public void evaluate() throws Throwable {
								try {
									statement.evaluate();
									handleDataPointSuccess();
								} catch (final AssumptionViolatedException e) {
									handleAssumptionViolation(e);
								} catch (final Throwable e) {
									reportParameterizedError(e, complete.getArgumentStrings(nullsOk()));
								}
							}

						};
					}

					@Override
					protected Statement methodInvoker(FrameworkMethod method, Object test) {
						return methodCompletesWithParameters(method, complete, test);
					}

					@Override
					public Object createTest() throws Exception {
						final Object[] params = complete.getConstructorArguments();

						if (!nullsOk()) {
							Assume.assumeNotNull(params);
						}

						final Object instance = this.getTestClass().getOnlyConstructor().newInstance(params);

						// Here we finally set the parameter for the test class
						final WebAppTestBase webAppTest = (WebAppTestBase) instance;
						webAppTest.setMutationFileId(JUnitTheoryRunner.this.mutationId);

						return instance;
					}
				}.methodBlock(method).evaluate();
			}

			public Statement methodCompletesWithParameters(final FrameworkMethod method, final Assignments complete,
					final Object freshInstance) {
				return new Statement() {
					@Override
					public void evaluate() throws Throwable {
						final Object[] values = complete.getMethodArguments();

						if (!nullsOk()) {
							Assume.assumeNotNull(values);
						}

						method.invokeExplosively(freshInstance, values);
					}
				};
			}

			@Override
			protected void reportParameterizedError(Throwable e, Object... params) throws Throwable {
				if (params.length == 0) {
					throw e;
				}
				throw new ParameterizedAssertionError(e, method.getName(), params);
			}

			private boolean nullsOk() {
				final Theory annotation = method.getMethod().getAnnotation(Theory.class);
				if (annotation == null) {
					return false;
				}
				return annotation.nullsAccepted();
			}
		};
	}

	// @Override
	// protected Statement methodBlock(FrameworkMethod method) {
	// Object test;
	// try {
	// test = new ReflectiveCallable() {
	// @Override
	// protected Object runReflectiveCall() throws Throwable {
	// return JUnitTheoryRunner.this.createTest();
	// }
	// }.run();
	// } catch (final Throwable e) {
	// return new Fail(e);
	// }
	//
	// Statement statement = this.methodInvoker(method, test);
	// statement = this.possiblyExpectingExceptions(method, test, statement);
	// statement = this.withPotentialTimeout(method, test, statement);
	// statement = this.withBefores(method, test, statement);
	// statement = this.withAfters(method, test, statement);
	// statement = this.withRules(method, test, statement);
	// return statement;
	// }
	//
	// @Override
	// protected Object createTest() throws Exception {
	// final Object instance =
	// this.getTestClass().getOnlyConstructor().newInstance();
	//
	// final WebAppTestBase webAppTest = (WebAppTestBase) instance;
	// webAppTest.setMutationFileId(this.mutationId);
	//
	// return instance;
	// }

	/**
	 * Wrapper of RunNotifier. Only task for this class is set flag on when test
	 * fails.
	 */
	private class RunNotifierFailureReporter extends RunNotifier {
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
			JUnitTheoryRunner.this.skipNextExecution = true;
		}
	}
}
