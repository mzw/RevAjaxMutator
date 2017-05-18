package jp.mzw.revajaxmutator.command;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.experimental.theories.Theories;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.owasp.webscarab.model.StoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.mzw.ajaxmutator.test.runner.EachJUnitTestRunner;
import jp.mzw.ajaxmutator.test.runner.JUnitTestRunner;
import jp.mzw.ajaxmutator.test.runner.JUnitTheoryRunner;
import jp.mzw.revajaxmutator.config.LocalEnv;
import jp.mzw.revajaxmutator.config.app.AppConfig;
import jp.mzw.revajaxmutator.proxy.JSCoverProxyServer;
import jp.mzw.revajaxmutator.proxy.ProxyServer;
import jp.mzw.revajaxmutator.test.result.TestResult;

public class Test extends Command {
	protected static Logger LOG = LoggerFactory.getLogger(Test.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUsageContent() {
		final StringBuilder builder = new StringBuilder();

		builder.append(Command.getCommandDescription("test ${TestClassName}", "Run test cases"));
		builder.append(
				Command.getCommandDescription("test-each ${ConfigClassName} ${TestClassName}", "Run each test case"));

		return builder.toString();
	}

	/**
	 * Run all test cases
	 *
	 * @param args
	 */
	public void all(String[] args) {
		if (args.length < 1) {
			this.showUsage();
			return;
		}

		try {
			final LocalEnv localenv = new LocalEnv(LocalEnv.FILENAME);
			ProxyServer.launch(null, localenv.getProxyAddress());

			for (final String testClassName : args) {
				final Class<?> testClass = getClass(testClassName);

				Runner runner = null;
				final RunWith runWith = testClass.getAnnotation(RunWith.class);
				if (runWith == null) {
					runner = new JUnitTestRunner(testClass, true);
				} else if (Theories.class.equals(runWith.value())) {
					runner = new JUnitTheoryRunner(testClass, true);
				} else {
					runner = new BlockJUnit4ClassRunner(testClass);
				}

				final Result result = (new JUnitCore()).run(runner);
				showTestResult(result);

				ProxyServer.interrupt();
			}
		} catch (ClassNotFoundException | InitializationError | StoreException | InterruptedException | IOException e) {
			LOG.error(e.getMessage());
			ProxyServer.interrupt();
		}

	}

	/**
	 * Run each test case and measure its coverage
	 *
	 * @param args
	 */
	public void each(String[] args) {
		if (args.length < 2) {
			this.showUsage();
			return;
		}

		try {
			final LocalEnv localenv = new LocalEnv(LocalEnv.FILENAME);

			final String configClassName = args[0];
			final Class<?> configClass = getClass(configClassName);
			final AppConfig config = (AppConfig) configClass.newInstance();

			final File dir = config.getJscoverReportDir();
			JSCoverProxyServer.launch(dir.getAbsolutePath(), localenv.getJsCoveragePort());
			// JSCoverProxyServer.launch(localenv.getJsCoverageDir(),
			// localenv.getJsCoveragePort());
			localenv.setShouldRunJSCoverProxy(true);

			final List<TestResult> results = new ArrayList<>();
			for (int i = 1; i < args.length; i++) {
				final String testClassName = args[i];
				final Class<?> testClass = getClass(testClassName);

				// TODO Remove or backup previous results

				for (final Method method : testClass.getMethods()) {
					if (isTestMethod(method) && !isTestMethodToIgnore(method)) {
						// TODO run with Theory?
						final Result result = (new JUnitCore()).run(new EachJUnitTestRunner(testClass, true, method));
						showTestResult(result);
						results.add(new TestResult(testClass.getName(), method.getName(), result));

						Thread.sleep(500); // wait for outputting JSCover file
						final String eachDirName = testClassName + "#" + method.getName();
						final File eachDir = new File(dir, eachDirName);
						if (eachDir.exists()) {
							eachDir.delete();
						}
						eachDir.mkdirs();
						for (final File file : dir.listFiles()) {
							if (file.getName().contains("jscoverage") || file.getName().contains("original-src")) {
								file.renameTo(new File(eachDir, file.getName()));
							}
						}
					}
				}
			}
			localenv.setShouldRunJSCoverProxy(false);
			TestResult.store(dir, results);

			JSCoverProxyServer.interrupt();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | InterruptedException
				| IOException | InitializationError e) {
			LOG.error(e.getMessage());
			JSCoverProxyServer.interrupt();
		}
	}

	/**
	 *
	 * @param result
	 */
	private static void showTestResult(Result result) {
		System.out.println(String.format("%d tests, %d ignore, %d fail", result.getRunCount(), result.getIgnoreCount(),
				result.getFailureCount()));
		for (final Failure f : result.getFailures()) {
			System.out.println(f.getDescription());
			System.out.println(" exception: " + f.getException());
			LOG.warn(f.getDescription().toString());
			LOG.warn(f.getException().toString());
			LOG.warn(f.getTrace());
		}
	}

}
