package jp.mzw.revajaxmutator.command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.owasp.webscarab.model.StoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.mzw.ajaxmutator.test.conductor.MutationTestConductor;
import jp.mzw.ajaxmutator.test.executor.JUnitExecutor;
import jp.mzw.revajaxmutator.config.LocalEnv;
import jp.mzw.revajaxmutator.config.app.AppConfig;
import jp.mzw.revajaxmutator.config.mutation.MutateConfiguration;
import jp.mzw.revajaxmutator.genprog.GenProgConductor;
import jp.mzw.revajaxmutator.proxy.ProxyServer;
import jp.mzw.revajaxmutator.proxy.RewriterPlugin;
import jp.mzw.revajaxmutator.search.Searcher;

public class ProgramRepair extends Command {
	protected static Logger LOG = LoggerFactory.getLogger(ProgramRepair.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUsageContent() {
		StringBuilder builder = new StringBuilder();

		builder.append(Command.getCommandDescription("generate ${ConfigClassName}", "Generate patches as fix candidates"));
		builder.append(Command.getCommandDescription("validate ${ConfigClassName} ${TestClassName}...", "Validate patched programs"));

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
			showUsage();
			return;
		}

		try {
			String configClassName = args[0];
			Class<?> configClass = getClass(configClassName);
			AppConfig config = (AppConfig) configClass.newInstance();

			generate(config);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e) {
			LOG.error(e.getMessage());
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
	public void generate(AppConfig config) throws IOException, InstantiationException, IllegalAccessException, JSONException {
		MutateConfiguration mutateConfig = config.getProgramRepairConfig();
		MutationTestConductor conductor = mutateConfig.mutationTestConductor();
		conductor.generateMutations(mutateConfig.mutators());
		search(config.getClass());
	}

	/**
	 * Validate patched programs
	 * 
	 * @param args
	 */
	public void validate(String[] args) {

		try {
			LocalEnv localenv = new LocalEnv(LocalEnv.FILENAME);

			String configClassName = args[0];
			Class<?> configClass = getClass(configClassName);
			AppConfig config = (AppConfig) configClass.newInstance();

			File recordDir = config.getRecordDir();
			RewriterPlugin plugin = new RewriterPlugin(recordDir.getAbsolutePath());
			plugin.setRewriteFile(config.getRecordedJsFile().getName());
			ProxyServer.launch(Arrays.asList(plugin), localenv.getProxyAddress());

			List<Class<?>> testClasses = new ArrayList<>();
			for (int i = 1; i < args.length; i++) {
				String testClassName = args[i];
				Class<?> testClass = getClass(testClassName);
				testClasses.add(testClass);
			}

			validate(config, testClasses.toArray(new Class<?>[testClasses.size()]));

			ProxyServer.interrupt();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | StoreException | InterruptedException | IOException e) {
			LOG.error(e.getMessage());
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
	public void validate(AppConfig config, Class<?>[] testClasses) throws InstantiationException, IllegalAccessException, IOException {
		MutationTestConductor conductor = config.getMutationAnalysisConfig().mutationTestConductor();
		JUnitExecutor executor = new JUnitExecutor(false, testClasses);
		conductor.mutationAnalysisUsingExistingMutations(executor);
	}

	public void search(String[] args) {
		if (args.length != 1) {
			showUsage();
			return;
		}

		try {
			Class<?> clazz = getClass(args[0]);
			search(clazz);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | JSONException | IOException e) {
			LOG.error(e.getMessage());
		}
	}

	public void search(Class<?> clazz) throws InstantiationException, IllegalAccessException, JSONException, IOException {
		Searcher searcher = new Searcher(clazz);
		searcher.search();
	}

	public void genprog(String[] args)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, JSONException, InterruptedException {
		String className = args[0];
		String testClassName = args[1];

		GenProgConductor conductor = new GenProgConductor(Class.forName(className));
		conductor.search(Class.forName(testClassName));
	}
}
