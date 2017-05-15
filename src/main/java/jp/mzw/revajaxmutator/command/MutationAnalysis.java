package jp.mzw.revajaxmutator.command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.experimental.theories.Theories;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.owasp.webscarab.model.StoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import jp.mzw.ajaxmutator.mutator.Mutator;
import jp.mzw.ajaxmutator.prioritizer.Prioritizer;
import jp.mzw.ajaxmutator.sampling.Sampling;
import jp.mzw.ajaxmutator.test.conductor.MutationTestConductor;
import jp.mzw.ajaxmutator.test.conductor.RichMutationTestConductor;
import jp.mzw.ajaxmutator.test.executor.JUnitExecutor;
import jp.mzw.ajaxmutator.test.executor.TestExecutor;
import jp.mzw.ajaxmutator.test.runner.JUnitTestRunner;
import jp.mzw.ajaxmutator.test.runner.JUnitTheoryRunner;
import jp.mzw.revajaxmutator.config.LocalEnv;
import jp.mzw.revajaxmutator.config.app.AppConfig;
import jp.mzw.revajaxmutator.config.mutation.MutateConfiguration;
import jp.mzw.revajaxmutator.proxy.ProxyServer;
import jp.mzw.revajaxmutator.proxy.RecorderPlugin;
import jp.mzw.revajaxmutator.proxy.RewriterPlugin;
import jp.mzw.revajaxmutator.test.result.Coverage;

public class MutationAnalysis extends Command {
	protected static Logger LOG = LoggerFactory.getLogger(MutationAnalysis.class);

	@Override
	public String getUsageContent() {
		final StringBuilder builder = new StringBuilder();

		builder.append(Command.getCommandDescription("mutate ${ConfigClassName}", "Generate patches as mutants"));
		builder.append(Command.getCommandDescription("analyze ${ConfigClassName} ${TestClassName}...",
				"Run test cases on mutants"));
		builder.append(Command.getCommandDescription("analyze-concurrently ${ConfigClassName} ${TestClassName}...",
				"Run test cases on mutants concurrently"));

		return builder.toString();
	}

	/**
	 * Run all test cases and record source code files
	 *
	 * @param args
	 */
	public void record(String[] args) {
		if (args.length < 2) {
			this.showUsage();
			return;
		}

		try {
			// Configuration setup
			final LocalEnv localenv = new LocalEnv(LocalEnv.FILENAME);
			final String configClassName = args[0];
			final Class<?> configClass = getClass(configClassName);
			final AppConfig config = (AppConfig) configClass.newInstance();

			// Start WebScarab proxy with the RecorderPlugin to store the .html
			// and .js file in the local filesystem for later mutation
			final File recordDir = config.getRecordDir();
			recordDir.mkdirs();
			final RecorderPlugin plugin = new RecorderPlugin(recordDir.getAbsolutePath());
			ProxyServer.launch(Arrays.asList(plugin), localenv.getProxyAddress());
			System.out.println("RECORDING SOURCE CODE FILES");
			this.runTests(args);

			// Start JSCover proxy to record coverage results
			// JSCoverProxyServer.launch(localenv.getJsCoverageDir(),
			// localenv.getJsCoveragePort());
			// localenv.setShouldRunJSCoverProxy(true);
			// System.out.println("RUNNING COVERAGE REPORT");
			// this.runTests(args);
			// localenv.setShouldRunJSCoverProxy(false);

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InitializationError
				| InterruptedException | IOException | StoreException e) {
			LOG.error(e.getMessage());
		} finally {
			ProxyServer.interrupt();
		}

	}

	private void runTests(String[] args) throws ClassNotFoundException, InitializationError {
		for (int i = 1; i < args.length; i++) {
			final String testClassName = args[i];
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
			new JUnitCore().run(runner);
		}
	}

	/**
	 * Generate patches as mutants
	 *
	 * @param args
	 */
	public void mutate(String[] args) {
		if (args.length < 2) {
			this.showUsage();
			return;
		}

		try {
			final String configClassName = args[0];
			final Class<?> configClass = getClass(configClassName);
			final AppConfig config = (AppConfig) configClass.newInstance();

			this.mutate(config);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e) {
			LOG.error(e.getMessage());
		}
	}

	/**
	 * Generate patches as mutants
	 *
	 * @param config
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public void mutate(AppConfig config) throws InstantiationException, IllegalAccessException, IOException {
		final MutateConfiguration mutateConfig = config.getMutationAnalysisConfig();
		final MutationTestConductor conductor = mutateConfig.mutationTestConductor();
		conductor.generateMutations(mutateConfig.mutators());
	}

	/**
	 * Run test cases on mutants
	 *
	 * @param args
	 */
	public void analyze(String[] args) {
		if (args.length < 2) {
			this.showUsage();
			return;
		}

		try {
			final LocalEnv localenv = new LocalEnv(LocalEnv.FILENAME);

			final String configClassName = args[0];
			final Class<?> configClass = getClass(configClassName);
			final AppConfig config = (AppConfig) configClass.newInstance();

			final File recordDir = config.getRecordDir();
			final RewriterPlugin plugin = new RewriterPlugin(recordDir.getAbsolutePath());
			final String recordedFilename = AppConfig.getRecordedFileName(config.getUrl(), config.pathToJsFile());
			plugin.setRewriteFile(recordedFilename);
			ProxyServer.launch(Arrays.asList(plugin), localenv.getProxyAddress());

			final List<Class<?>> testClasses = new ArrayList<>();
			for (int i = 1; i < args.length; i++) {
				final String testClassName = args[i];
				final Class<?> testClass = getClass(testClassName);
				testClasses.add(testClass);
			}

			this.analyze(config, testClasses.toArray(new Class<?>[testClasses.size()]));

			ProxyServer.interrupt();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | StoreException
				| InterruptedException | IOException e) {
			LOG.error(e.getMessage());
			ProxyServer.interrupt();
		}
	}

	/**
	 * Run test cases on mutants
	 *
	 * @param config
	 * @param testClasses
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public void analyze(AppConfig config, Class<?>... testClasses)
			throws InstantiationException, IllegalAccessException, IOException {
		final MutationTestConductor conductor = config.getMutationAnalysisConfig().mutationTestConductor();
		final JUnitExecutor executor = new JUnitExecutor(false, testClasses);
		conductor.mutationAnalysisUsingExistingMutations(executor);
	}

	/**
	 * Run test cases on mutants concurrently
	 *
	 * @param args
	 * @throws InterruptedException
	 * @throws StoreException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public void concurrently(String[] args) {
		if (args.length < 2) {
			this.showUsage();
			return;
		}

		try {
			final String configClassName = args[0];
			final Class<?> configClass = getClass(configClassName);
			final AppConfig config = (AppConfig) configClass.newInstance();

			final File recordDir = config.getRecordDir();
			final String recordedFilename = AppConfig.getRecordedFileName(config.getUrl(), config.pathToJsFile());

			// Build Web-scarab plugin that will modify the .js file with the
			// different mutations
			final LocalEnv localenv = new LocalEnv(LocalEnv.FILENAME);
			if (localenv.getSeleniumHubAddress() == null) {
				final RewriterPlugin plugin = new RewriterPlugin(recordDir.getAbsolutePath());
				plugin.setRewriteFile(recordedFilename);

				// Start Web-scarab proxy with the plugin
				ProxyServer.launch(Arrays.asList(plugin), localenv.getProxyAddress());
			}

			final List<Class<?>> testClasses = new ArrayList<>();
			for (int i = 1; i < args.length; i++) {
				final String testClassName = args[i];
				final Class<?> testClass = getClass(testClassName);
				testClasses.add(testClass);
			}

			this.concurrently(config, testClasses.toArray(new Class<?>[testClasses.size()]));
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IOException | StoreException
				| InterruptedException e) {
			LOG.error(e.getMessage());
		}
	}

	/**
	 * Run test cases on mutants in a multiple-threads manner
	 *
	 * @param config
	 * @param testClasses
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void concurrently(AppConfig config, Class<?>... testClasses)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		final LocalEnv localenv = new LocalEnv(LocalEnv.FILENAME);
		final MutateConfiguration mutateConfig = config.getMutationAnalysisConfig();

		final List<File> coverageFiles = Coverage.getCoverageResults(config.getJscoverReportDir());
		final Map<File, boolean[]> coverages = Coverage.getTargetCoverageResults(coverageFiles,
				config.getRecordedJsFile());

		final List<File> failureCoverageFiles = Coverage.getFailureCoverageResults(config.getJscoverReportDir());
		final Map<File, boolean[]> failureCoverages = Coverage.getTargetCoverageResults(failureCoverageFiles,
				config.getRecordedJsFile());

		final RichMutationTestConductor conductor = new RichMutationTestConductor();
		conductor.setup(config.getMutationAnalysisConfig().mutationTestConductor(), coverages);
		conductor.setThreadNum(localenv.getThreadNum());

		conductor.setSamplingStrategy(Sampling.getSampling(Sampling.Strategy.EventHandler));
		conductor.setPrioritizeStrategy(
				Prioritizer.getPrioritizer(Prioritizer.Strategy.Coverage).setParameters(failureCoverages));

		// TODO
		final List<TestExecutor> executors = Lists.newArrayList();
		for (final Mutator<?> mutator : mutateConfig.mutators()) {
			executors.add(new JUnitExecutor(false, testClasses));
		}

		conductor.mutationAnalysisUsingExistingMutations(executors);
	}

	//
	// for (Method method : testClass.getMethods()) {
	// if (isTestMethod(method)) {
	// File file = new File(
	// failureCoverageFile.getParentFile().getPath() + File.separator +
	// method.getName() + File.separator + failureCoverageFile.getName());
	// failureCoverageFiles.put(method.getName(), file);
	// }
	// }

	// MutationTestConductor conductor =
	// config.getMutationAnalysisConfig().mutationTestConductor();
	// conductor.mutationAnalysisUsingExistingMutations(executors,
	// failureCoverageFiles);

	// public static void createfile(String[] args) {
	// String configFileName = args[0];
	//
	// String testFilePath = getPropertyValue(configFileName,
	// "path_to_test_case_file");
	//
	// List<String> mutantNames = getMutantNames(configFileName);
	//
	// createMutantTestFile(new File(testFilePath), mutantNames);
	// }
	//
	// private static List<String> getMutantNames(String configFileName) {
	// Properties propaties = new Properties();
	// try {
	// propaties.load(AppConfigBase.class.getClassLoader().getResourceAsStream(new
	// File(configFileName).getName()));
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// File mutantsDir = new File(propaties.getProperty("ram_record_dir") +
	// File.separator + MutationFileWriter.DEFAULT_FOLDER_NAME);
	//
	// List<String> list = new ArrayList<String>();
	//
	// for (File file : mutantsDir.listFiles()) {
	// if
	// (file.getName().startsWith(MutationFileWriter.DEFAULT_FILE_NAME_PREFIX)
	// && file.getName().endsWith(MutationFileWriter.EXTENSION)) {
	// list.add(Util.getFileNameWithoutExtension(file.getName()));
	// }
	// }
	// return list;
	// }
	//
	// private static void createMutantTestFile(File testFile, List<String>
	// mutantNames) {
	//
	// String originalfilename = testFile.getName();
	//
	// List<String> original = Util.readFromFile(testFile.getPath());
	//
	// File testCodeDir = testFile.getParentFile();
	//
	// for (File file : testCodeDir.listFiles()) {
	// if
	// (file.getName().startsWith(MutationFileWriter.DEFAULT_FILE_NAME_PREFIX)
	// && file.getName().endsWith(MutationFileWriter.EXTENSION)) {
	// file.delete();
	// }
	// }
	//
	// for (String mutantname : mutantNames) {
	//
	// String content = applyMutantNameToTestCode(original,
	// Util.getFileNameWithoutExtension(originalfilename), mutantname);
	//
	// String newfilepath = testFile.getParent() + File.separator + mutantname +
	// originalfilename;
	//
	// Util.writeToFile(newfilepath, content);
	// }
	// }
	//
	// private static List<TestExecutor> createJUnitExecuterList(String
	// testClassName, List<String> mutantNames) throws ClassNotFoundException {
	// List<TestExecutor> executors = new ArrayList<TestExecutor>();
	// String[] splitedTestClassName = testClassName.split("\\.");
	// for (String mutantname : mutantNames) {
	// String[] name = splitedTestClassName.clone();
	// name[name.length - 1] = mutantname + name[name.length - 1];
	// String newTestClassName = Util.join(name, ".");
	// executors.add(new JUnitExecutor(false, Class.forName(newTestClassName)));
	// }
	// return executors;
	// }
	//
	// private static String applyMutantNameToTestCode(List<String> original,
	// String originalfilename, String mutantname) {
	//
	// List<String> clone = new ArrayList<String>(original);
	//
	// for (int i = 0; i < clone.size(); i++) {
	// if (clone.get(i).contains("class " + originalfilename)) {
	// clone.set(i, clone.get(i).replaceFirst(originalfilename, mutantname +
	// originalfilename));
	// }
	// if (clone.get(i).contains(WebAppTestBase.class.getSimpleName() + ".")) {
	// clone.set(i, clone.get(i).replaceFirst("\\);", ",\"" + mutantname +
	// "\");"));
	// }
	// }
	//
	// return Util.join(clone.toArray(new String[0]), System.lineSeparator());
	// }
}
