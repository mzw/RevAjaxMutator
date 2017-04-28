package jp.mzw.revajaxmutator.command;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.owasp.webscarab.model.StoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import jp.mzw.ajaxmutator.generator.MutationListManager;
import jp.mzw.ajaxmutator.mutator.Mutator;
import jp.mzw.ajaxmutator.prioritizer.Prioritizer;
import jp.mzw.ajaxmutator.sampling.Sampling;
import jp.mzw.ajaxmutator.test.conductor.MutationTestConductor;
import jp.mzw.ajaxmutator.test.conductor.RichMutationTestConductor;
import jp.mzw.ajaxmutator.test.executor.JUnitExecutor;
import jp.mzw.ajaxmutator.test.executor.TestExecutor;
import jp.mzw.revajaxmutator.config.LocalEnv;
import jp.mzw.revajaxmutator.config.app.AppConfig;
import jp.mzw.revajaxmutator.config.mutation.MutateConfiguration;
import jp.mzw.revajaxmutator.genprog.GenProgConductor;
import jp.mzw.revajaxmutator.proxy.ProxyServer;
import jp.mzw.revajaxmutator.proxy.RewriterPlugin;
import jp.mzw.revajaxmutator.search.Searcher;
import jp.mzw.revajaxmutator.search.Sorter;
import jp.mzw.revajaxmutator.test.result.Coverage;

public class ProgramRepair extends Command {
	protected static Logger LOG = LoggerFactory.getLogger(ProgramRepair.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUsageContent() {
		final StringBuilder builder = new StringBuilder();

		builder.append(
				Command.getCommandDescription("generate ${ConfigClassName}", "Generate patches as fix candidates"));
		builder.append(Command.getCommandDescription("validate ${ConfigClassName} ${TestClassName}...",
				"Validate patched programs"));

		return builder.toString();
	}

	/**
	 * Generate patches as fix candidates
	 *
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	public void generate(String[] args) {
		if (args.length < 1) {
			this.showUsage();
			return;
		}

		try {
			final String configClassName = args[0];
			final Class<?> configClass = getClass(configClassName);
			final AppConfig config = (AppConfig) configClass.newInstance();

			this.generate(config);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generate patches as fix candidates
	 *
	 * @param config
	 * @throws IOException
	 * @throws JSONException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public void generate(AppConfig config)
			throws IOException, InstantiationException, IllegalAccessException, JSONException {
		final MutateConfiguration mutateConfig = config.getProgramRepairConfig();
		final MutationTestConductor conductor = mutateConfig.mutationTestConductor();
		conductor.generateMutations(mutateConfig.mutators());
		this.search(config);
	}

	/**
	 * Validate patched programs
	 *
	 * @param args
	 */
	public void validate(String[] args) {

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

			this.validate(config, testClasses.toArray(new Class<?>[testClasses.size()]));

			ProxyServer.interrupt();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | StoreException
				| InterruptedException | IOException e) {
			e.printStackTrace();
			ProxyServer.interrupt();
		}

	}

	/**
	 * Validate patched programs
	 *
	 * @param config
	 * @param testClasses
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private void validate(AppConfig config, Class<?>[] testClasses)
			throws InstantiationException, IllegalAccessException, IOException {
		final MutationTestConductor conductor = config.getMutationAnalysisConfig().mutationTestConductor();
		final JUnitExecutor executor = new JUnitExecutor(false, testClasses);
		conductor.mutationAnalysisUsingExistingMutations(executor);
	}

	public void validateConcurrently(String[] args) {
		try {
			// Load test case target and its parameter configuration
			final String configClassName = args[0];
			final Class<?> configClass = getClass(configClassName);
			final AppConfig config = (AppConfig) configClass.newInstance();

			// Define location of .js file where patches will be applied to
			final File recordDir = config.getRecordDir();
			final String recordedFilename = AppConfig.getRecordedFileName(config.getUrl(), config.pathToJsFile());

			// Build Web-scarab plugin that will modify the .js file with the
			// different mutations
			final RewriterPlugin plugin = new RewriterPlugin(recordDir.getAbsolutePath());
			plugin.setRewriteFile(recordedFilename);

			// Start Web-scarab proxy with the plugin
			final LocalEnv localenv = new LocalEnv(LocalEnv.FILENAME);
			ProxyServer.launch(Arrays.asList(plugin), localenv.getProxyAddress());

			// Load the provided test-classes
			final List<Class<?>> testClasses = new ArrayList<>();
			for (int i = 1; i < args.length; i++) {
				final String testClassName = args[i];
				final Class<?> testClass = getClass(testClassName);
				testClasses.add(testClass);
			}

			this.validateConcurrently(config, testClasses.toArray(new Class<?>[testClasses.size()]));

			ProxyServer.interrupt();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | StoreException
				| InterruptedException | IOException e) {
			e.printStackTrace();
			ProxyServer.interrupt();
		}
	}

	private void validateConcurrently(AppConfig config, Class<?>[] testClasses)
			throws InstantiationException, IllegalAccessException, IOException {
		final MutateConfiguration mutateConfig = config.getMutationAnalysisConfig();

		final List<File> coverageFiles = Coverage.getCoverageResults(config.getJscoverReportDir());
		final Map<File, boolean[]> coverages = Coverage.getTargetCoverageResults(coverageFiles,
				config.getRecordedJsFile());

		final List<File> failureCoverageFiles = Coverage.getFailureCoverageResults(config.getJscoverReportDir());
		final Map<File, boolean[]> failureCoverages = Coverage.getTargetCoverageResults(failureCoverageFiles,
				config.getRecordedJsFile());

		final RichMutationTestConductor conductor = new RichMutationTestConductor();
		final MutationTestConductor configConductor = mutateConfig.mutationTestConductor();
		conductor.setup(configConductor, coverages);

		final LocalEnv localenv = new LocalEnv(LocalEnv.FILENAME);
		conductor.setThreadNum(localenv.getThreadNum());

		conductor.setSamplingStrategy(Sampling.getSampling(Sampling.Strategy.EventHandler));
		conductor.setPrioritizeStrategy(
				Prioritizer.getPrioritizer(Prioritizer.Strategy.Coverage).setParameters(failureCoverages));

		// TODO
		final List<TestExecutor> executors = Lists.newArrayList();
		for (final Mutator<?> mutator : mutateConfig.mutators()) {
			final TestExecutor e = new JUnitExecutor(true, testClasses);
			final String mutationName = mutator.mutationName();
			e.setMutationFixAssignment(mutationName);
			executors.add(e);
		}

		conductor.mutationAnalysisUsingExistingMutations(executors);
	}

	public void search(String[] args) {
		if (args.length < 1) {
			this.showUsage();
			return;
		}

		try {
			final String configClassName = args[0];
			final Class<?> configClass = getClass(configClassName);
			final AppConfig config = (AppConfig) configClass.newInstance();

			this.search(config);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | JSONException
				| IOException e) {
			e.printStackTrace();
		}
	}

	public void search(final AppConfig config)
			throws InstantiationException, IllegalAccessException, JSONException, IOException {
		final URL url = config.getUrl();
		final File recordedJsFile = config.getRecordedJsFile();
		final String pathToJsFile = config.pathToJsFile();
		final File jscoverReportDir = config.getJscoverReportDir();
		final Sorter.SortType sortType = config.getSortType();

		final MutationListManager manager = Searcher.getMutationListManager(recordedJsFile);
		final Collection<File> coverageFiles = Coverage.getCoverageResults(jscoverReportDir);
		final Searcher searcher = new Searcher(manager, sortType);
		searcher.setWeight(coverageFiles, url, pathToJsFile);
		searcher.search();
	}

	public void genprog(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException,
			IOException, JSONException, InterruptedException {
		final String className = args[0];
		final String testClassName = args[1];

		final GenProgConductor conductor = new GenProgConductor(Class.forName(className));
		conductor.search(Class.forName(testClassName));
	}
}
