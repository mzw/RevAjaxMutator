package jp.mzw.revajaxmutator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.mzw.ajaxmutator.JUnitExecutor;
import jp.mzw.ajaxmutator.JUnitTestEachMethodRunner;
import jp.mzw.ajaxmutator.JUnitTestRunner;
import jp.mzw.ajaxmutator.JUnitTheoryRunner;
import jp.mzw.ajaxmutator.MutationTestConductor;
import jp.mzw.ajaxmutator.TestExecutor;
import jp.mzw.ajaxmutator.generator.MutationFileWriter;
import jp.mzw.ajaxmutator.util.Util;
import jp.mzw.revajaxmutator.config.AppConfigBase;
import jp.mzw.revajaxmutator.genprog.GenProgConductor;
import jp.mzw.revajaxmutator.search.Searcher;
import jp.mzw.revajaxmutator.test.WebAppTestBase;
import junit.framework.Test;

import org.json.JSONException;
import org.junit.experimental.theories.Theories;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.owasp.webscarab.model.Preferences;
import org.owasp.webscarab.model.StoreException;
import org.owasp.webscarab.plugin.Framework;
import org.owasp.webscarab.plugin.proxy.Proxy;

public class Main {

	private static final String MUTANT = "mutant";

	public static void main(String[] args) {
		if (args.length == 0) {
			usage();
			System.exit(1);
		}
		String cmd = args[0];
		String[] rargs = Arrays.copyOfRange(args, 1, args.length);
		try {
			if ("test".equals(cmd)) {
				test(rargs);
				System.exit(0);
			}
			if ("each_method_test".equals(cmd)) {
				each_method_test(rargs);
				System.exit(0);
			}
			if ("mutate".equals(cmd)) {
				mutate(rargs);
				System.exit(0);
			}
			if ("analysis".equals(cmd)) {
				analysis(rargs);
				System.exit(0);
			}
			if ("concurrent_analysis".equals(cmd)) {
				concurrent_analysis(rargs);
				System.exit(0);
			}
			if ("createfile".equals(cmd)) {
				createfile(rargs);
				System.exit(0);
			}
			if ("proxy".equals(cmd)) {
				proxy(rargs);
				System.exit(0);
			}
			if ("search".equals(cmd)) {
				search(rargs);
				System.exit(0);
			}
			if ("genprog".equals(cmd)) {
				genprog(rargs);
				System.exit(0);
			}
			usage();
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void test(String[] args) throws ClassNotFoundException, InitializationError {
		String className = args[0];
		Class<?> testClass = Class.forName(className);

		Runner runner = null;
		RunWith runWith = testClass.getAnnotation(RunWith.class);
		if (runWith == null) {
			runner = new JUnitTestRunner(testClass, true);
		} else if (Theories.class.equals(runWith.value())) {
			runner = new JUnitTheoryRunner(testClass, true);
		} else {
			runner = new BlockJUnit4ClassRunner(Class.forName(className));
		}

		Result result = (new JUnitCore()).run(runner);
		outputTestResult(result);
	}

	private static boolean isTestMethod(Method method) {
		for (Annotation annotation : method.getAnnotations()) {
			if (annotation.annotationType().equals(org.junit.Test.class)) {
				return true;
			}
		}
		return false;
	}

	private static void outputTestResult(Result result) {
		System.out.println(String.format("%d tests, %d fail", result.getRunCount(), result.getFailureCount()));
		for (Failure f : result.getFailures()) {
			System.out.println(f.getDescription());
			System.out.println(" exception: " + f.getException());
		}
	}

	public static void each_method_test(String[] args)
			throws ClassNotFoundException, InitializationError, IOException, InterruptedException {
		String className = args[0];
		String configFileName = args[1];

		Class<?> testClass = Class.forName(className);

		String jscoverFolderName = getPropertyValue(configFileName, "jscover_report_dir");

		for (Method method : testClass.getMethods()) {
			if (isTestMethod(method)) {
				Result result = (new JUnitCore()).run(new JUnitTestEachMethodRunner(testClass, true, method));
				outputTestResult(result);
				Thread.sleep(3000); // wait for outputting JSCover file
				File folder = new File(jscoverFolderName + File.separator + method.getName());
				if (!folder.exists())
					folder.mkdir();
				File jscoverFolder = new File(jscoverFolderName);
				for (File file : jscoverFolder.listFiles()) {
					if (file.getName().contains("jscoverage") || file.getName().contains("original-src")) {
						file.renameTo(new File(folder.getPath() + File.separator + file.getName()));
					}
				}
			}
		}
	}

	public static void mutate(String[] args)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (args.length == 0) {
			System.err.println("please specify configuration classname");
			System.exit(1);
		}
		String className = args[0];
		Class<?> clazz = getClass(className);

		MutateConfiguration config = (MutateConfiguration) clazz.newInstance();
		MutationTestConductor conductor = config.mutationTestConductor();
		conductor.generateMutations(config.mutators());
	}

	public static void analysis(String[] args)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String className = args[0];
		String testClassName = args[1];

		MutateConfiguration config = (MutateConfiguration) Class.forName(className).newInstance();

		MutationTestConductor conductor = config.mutationTestConductor();

		conductor.mutationAnalysisUsingExistingMutations(new JUnitExecutor(false, Class.forName(testClassName)));
	}

	public static void concurrent_analysis(String[] args)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String className = args[0];
		String testClassName = args[1];
		String configFileName = args[2];

		MutateConfiguration config = (MutateConfiguration) Class.forName(className).newInstance();
		MutationTestConductor conductor = config.mutationTestConductor();

		List<String> mutantNames = getMutantNames(configFileName);

		List<TestExecutor> executors = createJUnitExecuterList(testClassName, mutantNames);

		HashMap<String, File> failureCoverageFiles = new HashMap<String, File>();

		File failureCoverageFile = new File(getPropertyValue(configFileName, "failure_cov_file"));

		failureCoverageFiles.put("", failureCoverageFile);

		Class<?> testClass = Class.forName(testClassName);

		for (Method method : testClass.getMethods()) {
			if (isTestMethod(method)) {
				File file = new File(failureCoverageFile.getParentFile().getPath() + File.separator + method.getName() + File.separator
						+ failureCoverageFile.getName());
				failureCoverageFiles.put(method.getName(), file);
			}
		}

		conductor.mutationAnalysisUsingExistingMutations(executors, failureCoverageFiles);
	}

	public static void createfile(String[] args) {
		String configFileName = args[0];

		String testFilePath = getPropertyValue(configFileName, "path_to_test_case_file");

		List<String> mutantNames = getMutantNames(configFileName);

		createMutantTestFile(new File(testFilePath), mutantNames);
	}

	private static String getPropertyValue(String configFileName, String propertyName) {

		Properties propaties = new Properties();
		try {
			propaties
					.load(AppConfigBase.class.getClassLoader().getResourceAsStream(new File(configFileName).getName()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return propaties.getProperty(propertyName);
	}

	private static List<String> getMutantNames(String configFileName) {
		Properties propaties = new Properties();
		try {
			propaties
					.load(AppConfigBase.class.getClassLoader().getResourceAsStream(new File(configFileName).getName()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		File mutantsDir = new File(
				propaties.getProperty("ram_record_dir") + File.separator + MutationFileWriter.DEFAULT_FOLDER_NAME);

		List<String> list = new ArrayList<String>();

		for (File file : mutantsDir.listFiles()) {
			if (file.getName().contains(MUTANT)) {
				list.add(Util.getFileNameWithoutExtension(file.getName()));
			}
		}
		return list;
	}

	private static void createMutantTestFile(File testFile, List<String> mutantNames) {

		String originalfilename = testFile.getName();

		List<String> original = Util.readFromFile(testFile.getPath());

		File testCodeDir = testFile.getParentFile();

		for (File file : testCodeDir.listFiles()) {
			if (file.getName().contains(MUTANT)) {
				file.delete();
			}
		}

		for (String mutantname : mutantNames) {

			String content = applyMutantNameToTestCode(original, Util.getFileNameWithoutExtension(originalfilename),
					mutantname);

			String newfilepath = testFile.getParent() + File.separator + mutantname + originalfilename;

			Util.writeToFile(newfilepath, content);
		}
	}

	private static List<TestExecutor> createJUnitExecuterList(String testClassName, List<String> mutantNames)
			throws ClassNotFoundException {

		List<TestExecutor> executors = new ArrayList<TestExecutor>();

		String[] splitedTestClassName = testClassName.split("\\.");

		for (String mutantname : mutantNames) {

			String[] name = splitedTestClassName.clone();

			name[name.length - 1] = mutantname + name[name.length - 1];

			String newTestClassName = Util.join(name, ".");

			executors.add(new JUnitExecutor(false, Class.forName(newTestClassName)));
		}

		return executors;
	}

	private static String applyMutantNameToTestCode(List<String> original, String originalfilename, String mutantname) {

		List<String> clone = new ArrayList<String>(original);

		for (int i = 0; i < clone.size(); i++) {
			if (clone.get(i).contains("class " + originalfilename)) {
				clone.set(i, clone.get(i).replaceFirst(originalfilename, mutantname + originalfilename));
			}
			if (clone.get(i).contains(WebAppTestBase.class.getSimpleName() + ".")) {
				clone.set(i, clone.get(i).replaceFirst("\\);", ",\"" + mutantname + "\");"));
			}
		}

		return Util.join(clone.toArray(new String[0]), System.lineSeparator());
	}

	public static void proxy(String[] args) throws StoreException {
		Framework framework = new Framework();
		Preferences.setPreference("Proxy.listeners", "127.0.0.1:8080");
		framework.setSession("FileSystem", new File(".conversation"), "");
		Proxy proxy = new Proxy(framework);
		for (int i = 0; i < args.length; i++) {
			if ("-record".equals(args[i])) {
				System.err.println("adding RecorderPlugin: path = " + args[i + 1]);
				proxy.addPlugin(new RecorderPlugin(args[i + 1]));
			}
			if ("-rewrite".equals(args[i])) {
				System.err.println("adding RewriterPlugin: path = " + args[i + 1]);
				proxy.addPlugin(new RewriterPlugin(args[i + 1]));
			}
			if ("-filter".equals(args[i])) {
				System.err.println("adding FilterPlugin: url = " + args[i + 1] + ", method = " + args[i + 2]);
				proxy.addPlugin(new FilterPlugin(args[i + 1], args[i + 2]));
			}
		}
		proxy.run();
		while (proxy.isRunning()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.exit(1);
			}
		}
	}

	public static void search(String[] args)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, JSONException, IOException {
		if (args.length == 0) {
			System.err.println("please specify configuration filename");
			System.exit(1);
		}
		Class<?> clazz = Class.forName(args[0]);
		Searcher searcher = new Searcher(clazz);
		searcher.search();
	}

	public static void genprog(String[] args) throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, IOException, JSONException, InterruptedException {
		String className = args[0];
		String testClassName = args[1];

		GenProgConductor conductor = new GenProgConductor(Class.forName(className));
		conductor.search(Class.forName(testClassName));
	}

	public static void usage() {
		System.err.println("please specify command (test, mutate, analysis, proxy, search, genprog)");
	}

	public static Class<?> getClass(String className) throws ClassNotFoundException {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			if (!className.contains("."))
				throw e;
		}
		int i = className.lastIndexOf('.');
		return Class.forName(className.substring(0, i) + '$' + className.substring(i + 1));
	}
}
