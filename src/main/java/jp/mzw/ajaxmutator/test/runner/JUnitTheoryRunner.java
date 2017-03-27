package jp.mzw.ajaxmutator.test.runner;

import org.junit.Ignore;
import org.junit.experimental.theories.Theories;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

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

	public JUnitTheoryRunner(Class<?> testClass, boolean shouldRunAllTest) throws InitializationError {
		super(testClass);
		this.shouldRunAllTest = shouldRunAllTest;
	}

    @Override
    protected void runChild(final FrameworkMethod method, RunNotifier notifier) {
        Description description= describeChild(method);
        if (method.getAnnotation(Ignore.class) != null || skipNextExecution) {
            notifier.fireTestIgnored(description);
        } else {
            if (!shouldRunAllTest) {
                if (reporter == null || !lastNotifier.equals(notifier)) {
                    reporter = new RunNotifierFailureReporter(notifier);
                    lastNotifier = notifier;
                }
            } else {
            	if(reporter == null) {
                    reporter = new RunNotifierFailureReporter(notifier);
            	}
            }
            runLeaf(methodBlock(method), description, reporter);
        }
    }

    /**
     * Wrapper of RunNotifier. Only task for this class is set flag on when test fails.
     */
    private class RunNotifierFailureReporter extends RunNotifier {
        private final RunNotifier notifier;

        private RunNotifierFailureReporter(RunNotifier notifier) {
            this.notifier = notifier;
        }

        @Override
        public void addListener(RunListener listener) {
            notifier.addListener(listener);
        }

        @Override
        public void removeListener(RunListener listener) {
            notifier.removeListener(listener);
        }

        @Override
        public void fireTestRunStarted(Description description) {
            notifier.fireTestRunStarted(description);
        }

        @Override
        public void fireTestRunFinished(Result result) {
            notifier.fireTestRunFinished(result);
        }

        @Override
        public void fireTestStarted(Description description) throws StoppedByUserException {
            notifier.fireTestStarted(description);
        }

        @Override
        public void fireTestAssumptionFailed(Failure failure) {
            notifier.fireTestAssumptionFailed(failure);
        }

        @Override
        public void fireTestIgnored(Description description) {
            notifier.fireTestIgnored(description);
        }

        @Override
        public void fireTestFinished(Description description) {
            notifier.fireTestFinished(description);
        }

        @Override
        public void pleaseStop() {
            notifier.pleaseStop();
        }

        @Override
        public void addFirstListener(RunListener listener) {
            notifier.addFirstListener(listener);
        }

        @Override
        public void fireTestFailure(Failure failure) {
            notifier.fireTestFailure(failure);
            skipNextExecution = true;
        }
    }
}
