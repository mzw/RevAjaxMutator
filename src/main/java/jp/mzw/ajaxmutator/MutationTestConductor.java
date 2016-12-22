package jp.mzw.ajaxmutator;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import difflib.DiffUtils;
import difflib.Patch;
import difflib.PatchFailedException;
import groovy.transform.Synchronized;
import jp.mzw.ajaxmutator.mutatable.Mutatable;
import jp.mzw.ajaxmutator.mutatable.genprog.Statement;
import jp.mzw.ajaxmutator.detector.genprog.StatementDetector;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.generator.MutationFileInformation;
import jp.mzw.ajaxmutator.generator.MutationFileWriter;
import jp.mzw.ajaxmutator.generator.MutationListManager;
import jp.mzw.ajaxmutator.generator.UnifiedDiffGenerator.DiffLine;
import jp.mzw.ajaxmutator.mutator.Mutator;
import jp.mzw.ajaxmutator.util.Randomizer;
import jp.mzw.ajaxmutator.util.Util;
import jp.mzw.revajaxmutator.search.Coverage;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.runner.Result;
import org.mozilla.javascript.ast.AstRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Executor to apply mutation testing to target applications. <br>
 * Note: Currently we assume that mutation target is single JavaScript file.
 *
 * @author Kazuki Nishiura
 */
public class MutationTestConductor {
	private static final Logger LOGGER = LoggerFactory.getLogger(MutationTestConductor.class);

	private MutationFileWriter mutationFileWriter;
	private MutationListManager mutationListManager;
	private Multimap<String, String> unkilledMutantsInfo;
	private Context context = Context.INSTANCE;
	private boolean setup = false;
	private int saveInformationInterval = Integer.MAX_VALUE;
	private ParserWithBrowser parser;
	private AstRoot astRoot;
	private boolean conducting;
	private boolean dryRun;
	private MutateVisitor visitor;
	private String pathToJsFile;

	private HashMap<String, boolean[]> coverageInfos;

	@SuppressWarnings("deprecation")
	private HashMap<String, boolean[]> getCoverageInfos(HashMap<String, File> failureCoverageFiles)
			throws JSONException {

		HashMap<String, boolean[]> map = new HashMap<String, boolean[]>();

		for (Map.Entry<String, File> entry : failureCoverageFiles.entrySet()) {

			boolean[] coverageInfo;

			try {
				JSONObject failure_coverage_json = Coverage.parse(entry.getValue());

				String encoded_url = (new File(pathToJsFile)).getName();
				String decoded_url = URLDecoder.decode(encoded_url);

				URL url = new URL(decoded_url);
				String url_path_to_js_file = URLDecoder.decode(url.getPath(), "utf-8");

				JSONArray failure = Coverage.getCoverageData(failure_coverage_json, url_path_to_js_file);

				List<String> jsfile = FileUtils.readLines(new File(pathToJsFile));

				int line_num = failure.length();

				coverageInfo = new boolean[jsfile.size() + 1];
				for (int i = 1; i < line_num; i++) {
					Object failure_line = failure.get(i);
					int failure_cover_freq = Coverage.getCoverFreq(failure_line);
					if (0 < failure_cover_freq) {
						coverageInfo[i] = true; // covered
					} else {
						coverageInfo[i] = false; // no covered
					}
				}
			} catch (IOException e) {
				coverageInfo = new boolean[0];
				System.out.println("can't find Folder : " + entry.getKey());
			}
			map.put(entry.getKey(), coverageInfo);
		}

		return map;
	}

	/**
	 * Setting information required for mutation testing. This method MUST be
	 * called before conducting mutation testing.
	 *
	 * @return if setup is successfully finished.
	 */
	public boolean setup(final String pathToJSFile, String targetURL, MutateVisitor visitor) {
		setup = false;
		this.pathToJsFile = pathToJSFile;
		context.registerJsPath(pathToJSFile);
		this.pathToJsFile = pathToJSFile;
		File jsFile = new File(pathToJSFile);
		Util.normalizeLineBreak(jsFile);
		mutationFileWriter = new MutationFileWriter(jsFile);
		Util.copyFile(pathToJSFile, pathToBackupFile());

		parser = ParserWithBrowser.getParser();
		try {
			FileReader fileReader = new FileReader(jsFile);
			astRoot = parser.parse(fileReader, targetURL, 1);
		} catch (IOException e) {
			LOGGER.error("IOException: cannot parse AST.");
			return false;
		}

		if (astRoot != null) {
			astRoot.visit(visitor);
			setup = true;
		} else {
			LOGGER.error("Cannot parse AST.");
		}
		this.visitor = visitor;
		return setup;
	}

	/**
	 * Check how many mutants are generated and so on.
	 *
	 * @return Mapping of mutator classes and how many times it should be
	 *         applied.
	 */
	public Map<Mutator<?>, Integer> dryRun(Set<Mutator<?>> mutators) {
		dryRun = true;
		checkIfSetuped();
		mutationListManager = new MutationListManager(mutationFileWriter.getDestinationDirectory());
		generateMutationFiles(visitor, mutators);
		return numOfMutation;
	}

	/**
	 * Generate mutation files corresponding to given {@link Mutator}.
	 */
	public void generateMutations(Set<Mutator<?>> mutators) {
		unkilledMutantsInfo = ArrayListMultimap.create();
		checkIfSetuped();
		mutationListManager = new MutationListManager(mutationFileWriter.getDestinationDirectory());
		generateMutationFiles(visitor, mutators);
		mutationListManager.generateMutationListFile();
	}

	/**
	 * Generate mutation files corresponding to given {@link Mutator}, and then
	 * running test.
	 */
	public void generateMutationsAndApplyTest(TestExecutor testExecutor, Set<Mutator<?>> mutators) {
		Stopwatch stopwatch = new Stopwatch().start();
		generateMutations(mutators);
		applyMutationAnalysis(testExecutor, stopwatch);
	}

	public void mutationAnalysisUsingExistingMutations(TestExecutor testExecutor) {
		mutationListManager = new MutationListManager(mutationFileWriter.getDestinationDirectory());
		mutationListManager.readExistingMutationListFile();
		unkilledMutantsInfo = ArrayListMultimap.create();

		checkIfSetuped();
		applyMutationAnalysis(testExecutor, new Stopwatch().start());
	}

	public void mutationAnalysisUsingExistingMutations(List<TestExecutor> testExecutors,
			HashMap<String, File> failureCoverageFiles) {
		mutationListManager = new MutationListManager(mutationFileWriter.getDestinationDirectory());
		mutationListManager.readExistingMutationListFile();
		unkilledMutantsInfo = ArrayListMultimap.create();

		coverageInfos = getCoverageInfos(failureCoverageFiles);

		for (Map.Entry<String, boolean[]> entry : coverageInfos.entrySet()) {
			System.out.println("------- " + entry.getKey() + " -------");
			for (int i = 1; i <entry.getValue().length ; i++) {
				System.out.println(i + ":" + entry.getValue()[i]);
			}
		}

		// for (int i = 1; i < coverageInfo.length ; i++) {
		// if(coverageInfo[i]){
		// System.out.println(i + ":" + "true");
		// }else{
		// System.out.println(i + ":" + "false");
		// }
		// }

		checkIfSetuped();
		// applyMutationAnalysis(testExecutors, new Stopwatch().start());
	}

	public void tryToKillSpecificMutant(String mutationFileName, TestExecutor testExecutor) {
		mutationListManager = new MutationListManager(mutationFileWriter.getDestinationDirectory());
		mutationListManager.readExistingMutationListFile();
		checkIfSetuped();

		for (String description : mutationListManager.getListOfMutationName()) {
			for (MutationFileInformation mutationFileInformation : mutationListManager
					.getMutationFileInformationList(description)) {
				if (mutationFileInformation.getFileName().equals(mutationFileName)) {
					List<String> original = Util.readFromFile(pathToJsFile);
					if (!applyMutationFile(original, mutationFileInformation)) {
						return;
					}
					if (testExecutor.execute()) { // This mutants cannot be
													// killed
						LOGGER.info("mutant {} is not be killed", description);
					} else {
						mutationFileInformation.setState(MutationFileInformation.State.KILLED);
						LOGGER.info("mutant {} is killed", description);
					}
					String message = testExecutor.getMessageOnLastExecution();
					if (message != null) {
						LOGGER.info(message);
					}

					mutationListManager.generateMutationListFile();

					LOGGER.info("restoring backup file...");
					Util.copyFile(pathToBackupFile(), context.getJsPath());
					return;
				}
			}
		}
		LOGGER.error("No mutant found for name " + mutationFileName);
	}

	public List<Result> testSpecificMutation(MutationFileInformation mutation, JUnitExecutor executor) {
		checkIfSetuped();

		List<String> original = Util.readFromFile(pathToJsFile);
		if (!applyMutationFile(original, mutation)) {
			return null; // fail to mutation
		}

		List<Result> testRestuls = executor.run();

		Util.copyFile(pathToBackupFile(), context.getJsPath());

		return testRestuls;
	}

	private Map<Mutator<?>, Integer> numOfMutation;

	private Map<Mutator<?>, Integer> generateMutationFiles(MutateVisitor visitor, Set<Mutator<?>> mutators) {
		numOfMutation = new HashMap<Mutator<?>, Integer>();

		// Events
		System.out.println("******************EventAttachements******************");
		generateMutationFiles(visitor.getEventAttachments(), mutators);
		System.out.println("******************TimerEventAttachementExpressions******************");
		generateMutationFiles(visitor.getTimerEventAttachmentExpressions(), mutators);
		// Asynchronous communications
		System.out.println("******************Requests******************");
		generateMutationFiles(visitor.getRequests(), mutators);
		// DOM manipulations
		System.out.println("******************DOMCreations******************");
		generateMutationFiles(visitor.getDomCreations(), mutators);
		System.out.println("******************DOMAppendings******************");
		generateMutationFiles(visitor.getDomAppendings(), mutators);
		System.out.println("******************DOMSelections******************");
		generateMutationFiles(visitor.getDomSelections(), mutators);
		System.out.println("******************DOMRemovals******************");
		generateMutationFiles(visitor.getDomRemovals(), mutators);
		System.out.println("******************AttributeModifications******************");
		generateMutationFiles(visitor.getAttributeModifications(), mutators);
		System.out.println("******************DOMClonings******************");
		generateMutationFiles(visitor.getDomClonings(), mutators);
		System.out.println("******************DOMNormalizations******************");
		generateMutationFiles(visitor.getDomNormalizations(), mutators);
		System.out.println("******************DOMReplacements******************");
		generateMutationFiles(visitor.getDomReplacements(), mutators);

		System.out.println("******************Statements******************");
		generateMutationFiles(visitor.getStatements(), mutators);

		// genelic
		System.out.println("******************AssignmentExpressions******************");
		generateMutationFiles(visitor.getAssignmentExpressions(), mutators);

		System.out.println("******************Breaks******************");
		generateMutationFiles(visitor.getBreaks(), mutators);

		System.out.println("******************Continues******************");
		generateMutationFiles(visitor.getContinues(), mutators);

		System.out.println("******************Fors******************");
		generateMutationFiles(visitor.getFors(), mutators);

		System.out.println("******************Funcnodes******************");
		generateMutationFiles(visitor.getFuncnodes(), mutators);

		System.out.println("******************Ifs******************");
		generateMutationFiles(visitor.getIfs(), mutators);

		System.out.println("******************Returns******************");
		generateMutationFiles(visitor.getReturns(), mutators);

		System.out.println("******************Switches******************");
		generateMutationFiles(visitor.getSwitches(), mutators);

		System.out.println("******************VariableDecss******************");
		generateMutationFiles(visitor.getVariableDecss(), mutators);

		System.out.println("******************Whiles******************");
		generateMutationFiles(visitor.getWhiles(), mutators);

		LOGGER.debug("Random values used for generating mutations: " + Arrays.toString(Randomizer.getReturnedValues()));
		return numOfMutation;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void generateMutationFiles(Set<? extends Mutatable> mutatables, Set<Mutator<?>> mutators) {
		if (mutatables.size() == 0) {
			System.out.println("mutable:0");
			return;
		}

		Set<Mutator<?>> applicableMutator = new HashSet<>();
		Mutatable aMutatable = Iterables.get(mutatables, 0);
		LOGGER.info("try to create mutations for {}. {} elements exist.", aMutatable.getClass().getSimpleName(),
				mutatables.size());
		System.out.println("just finished trying to create mutation");
		for (Mutator<?> mutator : mutators) {
			System.out.println("mutator:" + mutator.toString());
			if (mutator.isApplicable(aMutatable.getClass())) {
				System.out.println("--applicabable");
				applicableMutator.add(mutator);
			} else {
				System.out.println("--not applicabable");
			}
		}
		for (Mutator mutator : applicableMutator) {
			System.out.println("---mutator:" + mutator.toString());
			numOfMutation.put(mutator, 0);
			LOGGER.info("using {}", mutator.mutationName());
			for (Mutatable mutatable : mutatables) {
				List<Mutation> mutations = mutator.generateMutationList(mutatable);
				if (mutations == null) {
					continue;
				}
				for (Mutation mutation : mutations) {
					if (mutation == null) {
						LOGGER.info("Cannot create mutation for {} by using {}", mutatable, mutator.mutationName());
						continue;
					}
					numOfMutation.put(mutator, numOfMutation.get(mutator) + 1);
					if (dryRun) {
						System.out.println("dry run:true");
						continue;
					}
					File generatedFile = mutationFileWriter.writeToFile(mutation);
					if (generatedFile == null) {
						LOGGER.error("failed to generate mutation file");
						continue;
					}

					DiffLine diffLine = mutationFileWriter.getDiffLine(mutation);
					MutationFileInformation info = new MutationFileInformation(generatedFile.getName(),
							generatedFile.getAbsolutePath(), MutationFileInformation.State.NON_EQUIVALENT_LIVE,
							diffLine.getStartLine(), diffLine.getEndLine(), mutatable.getClass().getSimpleName(),
							mutator.mutationName(), mutation.getRepairValue().getValue(),
							mutation.getRepairSource().name());

					mutationListManager.addMutationFileInformation(mutator.mutationName(), info);
				}
			}
		}
	}

	private void applyMutationAnalysis(TestExecutor testExecutor, Stopwatch runningStopwatch) {
		conducting = true;
		addShutdownHookToRestoreBackup();
		// テスト実行
		int numberOfAppliedMutation = applyMutationAnalysis(testExecutor);

		runningStopwatch.stop();
		LOGGER.info("Updating mutation list file...");
		mutationListManager.generateMutationListFile();

		logExecutionDetail(numberOfAppliedMutation);
		LOGGER.info("restoring backup file...");
		Util.copyFile(pathToBackupFile(), context.getJsPath());
		LOGGER.info("finished! " + runningStopwatch.elapsedMillis() / 1000.0 + " sec.");
	}

	private void applyMutationAnalysis(List<TestExecutor> testExecutors, Stopwatch runningStopwatch) {
		conducting = true;
		addShutdownHookToRestoreBackup();
		int numberOfAppliedMutation = applyMutationAnalysis(testExecutors);

		runningStopwatch.stop();
		LOGGER.info("Updating mutation list file...");
		mutationListManager.generateMutationListFile();

		logExecutionDetail(numberOfAppliedMutation);
		LOGGER.info("restoring backup file...");
		Util.copyFile(pathToBackupFile(), context.getJsPath());
		LOGGER.info("finished! " + runningStopwatch.elapsedMillis() / 1000.0 + " sec.");
	}

	private int applyMutationAnalysis(TestExecutor testExecutor) {
		int numberOfAppliedMutation = 0;
		int numberOfMaxMutants = mutationListManager.getNumberOfUnkilledMutants();
		Thread commandReceiver = new Thread(new CommandReceiver());
		commandReceiver.start();
		List<String> original = Util.readFromFile(pathToJsFile);
		List<String> nameOfMutations = mutationListManager.getListOfMutationName();
		for (String description : nameOfMutations) {
			LOGGER.info("Start applying {}", description);
			for (MutationFileInformation mutationFileInformation : mutationListManager
					.getMutationFileInformationList(description)) {
				// execution can be canceled from outside.
				if (!conducting) {
					break;
				}
				if (mutationFileInformation.canBeSkipped() || !applyMutationFile(original, mutationFileInformation)) {
					continue;
				}
				numberOfAppliedMutation++;
				if (numberOfAppliedMutation >= saveInformationInterval
						& (numberOfAppliedMutation % saveInformationInterval == 0)) {
					mutationListManager.generateMutationListFile();
				}
				LOGGER.info("Executing test(s) on {}", mutationFileInformation.getAbsolutePath());
				if (testExecutor.execute()) { // This mutant cannot be killed
					unkilledMutantsInfo.put(description, mutationFileInformation.toString());
					LOGGER.info("mutant {} is not be killed", description);
				} else {
					mutationFileInformation.setState(MutationFileInformation.State.KILLED);
				}
				String message = testExecutor.getMessageOnLastExecution();
				if (message != null) {
					LOGGER.info(message);
				}
				logProgress(numberOfAppliedMutation, numberOfMaxMutants);
			}
			// execution can be canceled from outside.
			if (!conducting) {
				break;
			}
		}
		for (String description : nameOfMutations) {
			for (MutationFileInformation mutationFileInformation : mutationListManager
					.getMutationFileInformationList(description)) {
				System.out.println(mutationFileInformation.getState());
			}
		}
		if (conducting) {
			commandReceiver.interrupt();
			conducting = false;
		}
		return numberOfAppliedMutation;
	}

	private int applyMutationAnalysis(List<TestExecutor> testExecutors) {

		int numberOfAppliedMutation = 0;
		int numberOfMaxMutants = mutationListManager.getNumberOfUnkilledMutants();
		Thread commandReceiver = new Thread(new CommandReceiver());
		commandReceiver.start();
		List<String> original = Util.readFromFile(pathToJsFile);
		List<String> nameOfMutations = mutationListManager.getListOfMutationName();

		// スレッド数無制限実行
		// ExecutorService executor = Executors.newCachedThreadPool();

		// スレッド数指定実行
		ExecutorService executor = Executors.newFixedThreadPool(3);

		// 逐次実行
		// ExecutorService executor = Executors.newSingleThreadExecutor();

		List<Future<Boolean>> futureList = new ArrayList<Future<Boolean>>();

		for (String description : nameOfMutations) {
			LOGGER.info("Start applying {}", description);

			for (MutationFileInformation mutationFileInformation : mutationListManager
					.getMutationFileInformationList(description)) {
				// execution can be canceled from outside.
				if (!conducting) {
					break;
				}
				if (mutationFileInformation.canBeSkipped() || !createMutationFile(original, mutationFileInformation)) {
					continue;
				}
				numberOfAppliedMutation++;
				if (numberOfAppliedMutation >= saveInformationInterval
						& (numberOfAppliedMutation % saveInformationInterval == 0)) {
					mutationListManager.generateMutationListFile();
				}
				LOGGER.info("Executing test(s) on {}", mutationFileInformation.getAbsolutePath());
				Future<Boolean> future = null;
				String mutantname = Util.getFileNameWithoutExtension(mutationFileInformation.getFileName());
				future = executor.submit(new TestCallable(getTargetTestExecutor(testExecutors, mutantname),
						mutationFileInformation, description, numberOfAppliedMutation, numberOfMaxMutants));
				futureList.add(future);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// execution can be canceled from outside.
			if (!conducting) {
				break;
			}
		}
		for (Future<Boolean> future : futureList) {
			try {
				future.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		for (String description : nameOfMutations) {
			for (MutationFileInformation mutationFileInformation : mutationListManager
					.getMutationFileInformationList(description)) {
				System.out.println(mutationFileInformation.getState());
			}
		}

		executor.shutdown();

		if (conducting) {
			commandReceiver.interrupt();
			conducting = false;
		}
		return numberOfAppliedMutation;
	}

	private TestExecutor getTargetTestExecutor(List<TestExecutor> executors, String mutantname) {
		for (TestExecutor executor : executors) {
			if (executor.getTargetClassName().contains(mutantname)) {
				return executor;
			}
		}
		return null;
	}

	public class TestCallable implements Callable<Boolean> {
		private TestExecutor testExecutor;
		private MutationFileInformation mutationFileInformation;
		private String description;
		private int numberOfAppliedMutation;
		private int numberOfMaxMutants;

		public TestCallable(TestExecutor testExecutor, MutationFileInformation mutationFileInformation,
				String description, int numberOfAppliedMutation, int numberOfMaxMutants) {
			this.testExecutor = testExecutor;
			this.mutationFileInformation = mutationFileInformation;
			this.description = description;
			this.numberOfAppliedMutation = numberOfAppliedMutation;
			this.numberOfMaxMutants = numberOfMaxMutants;
		}

		@Override
		public Boolean call() throws Exception {

			boolean testSuccess;

			if (testExecutor.execute()) { // This mutant cannot be killed
				synchronized (unkilledMutantsInfo) {
					unkilledMutantsInfo.put(description, mutationFileInformation.toString());
				}
				synchronized (LOGGER) {
					LOGGER.info("mutant {} is not be killed", description);
				}
				testSuccess = false;
			} else {
				synchronized (mutationFileInformation) {
					mutationFileInformation.setState(MutationFileInformation.State.KILLED);
				}
				testSuccess = true;
			}

			String message = testExecutor.getMessageOnLastExecution();

			if (message != null) {
				LOGGER.info(message);
			}
			logProgress(numberOfAppliedMutation, numberOfMaxMutants);
			return testSuccess;
		}

	}

	private synchronized void logProgress(int finished, int total) {
		LOGGER.info("{} in {} finished: {} %", finished, total, Math.floor(finished * 1000.0 / total) / 10);
	}

	/**
	 * @return if successfully file is wrote.
	 */
	@SuppressWarnings("unused")
	private boolean applyMutationFile(List<String> original, MutationFileInformation fileInfo) {
		Patch patch = DiffUtils.parseUnifiedDiff(Util.readFromFile(fileInfo.getAbsolutePath()));
		try {
			List<?> mutated = patch.<String> applyTo(original);
			Util.writeToFile(pathToJsFile, Util.join(mutated.toArray(new String[0]), System.lineSeparator()));
		} catch (PatchFailedException e) {
			LOGGER.error("Applying mutation file '{}' failed.", fileInfo.getFileName(), e);
			return false;
		}
		return true;
	}

	/**
	 * @return if successfully file is wrote.
	 */
	@SuppressWarnings("unused")
	private boolean createMutationFile(List<String> original, MutationFileInformation fileInfo) {

		Patch patch = DiffUtils.parseUnifiedDiff(Util.readFromFile(fileInfo.getAbsolutePath()));
		try {

			List<?> mutated = patch.<String> applyTo(original);

			String[] pathHierarchyOfJsFile = pathToJsFile.split("/", 0);

			String newPathToJsFile = pathHierarchyOfJsFile[0];
			if (pathHierarchyOfJsFile.length != 0) {
				for (int i = 1; i < pathHierarchyOfJsFile.length - 1; i++) {
					newPathToJsFile = newPathToJsFile + "/" + pathHierarchyOfJsFile[i];
				}
			}

			File testedDir = new File(newPathToJsFile + "/tested");

			if (!testedDir.exists()) {
				testedDir.mkdir();
			}

			newPathToJsFile = testedDir.getPath() + "/" + Util.getFileNameWithoutExtension(fileInfo.getFileName()) + "-"
					+ pathHierarchyOfJsFile[pathHierarchyOfJsFile.length - 1];

			Util.writeToFile(newPathToJsFile, Util.join(mutated.toArray(new String[0]), System.lineSeparator()));

		} catch (PatchFailedException e) {
			LOGGER.error("Applying mutation file '{}' failed.", fileInfo.getFileName(), e);
			return false;
		}
		return true;
	}

	private void checkIfSetuped() {
		if (!setup)
			throw new IllegalStateException("You 'must' call setup method before you use.");
	}

	private void logExecutionDetail(int numberOfAppliedMutation) {
		LOGGER.info("---------------------------------------------");
		StringBuilder detailedInfo = new StringBuilder();
		int numberOfUnkilledMutatns = 0;
		for (String key : unkilledMutantsInfo.keySet()) {
			numberOfUnkilledMutatns += unkilledMutantsInfo.get(key).size();
			detailedInfo.append(key).append(": ").append(unkilledMutantsInfo.get(key).size())
					.append(System.lineSeparator());
			for (String info : unkilledMutantsInfo.get(key)) {
				detailedInfo.append(info).append(System.lineSeparator());
			}
			detailedInfo.append(System.lineSeparator());
		}

		LOGGER.info(detailedInfo.toString());

		int numberOfMaxMutants = mutationListManager.getNumberOfMaxMutants();
		double score = Math.floor((1.0 - (1.0 * numberOfUnkilledMutatns / numberOfMaxMutants)) * 100 * 10) / 10;
		LOGGER.info("{} unkilled mutants among {} ({})", numberOfUnkilledMutatns, numberOfAppliedMutation,
				numberOfMaxMutants);
		LOGGER.info("Mutation score is: {} %", score);
	}

	private void addShutdownHookToRestoreBackup() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				// restore backup
				Util.copyFile(pathToBackupFile(), pathToJsFile);
				LOGGER.info("backup file restored");
			}
		});
	}

	private class CommandReceiver implements Runnable {
		@Override
		public void run() {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			LOGGER.info("You can stop execution any time by entering 'q'");
			while (true) {
				try {
					while (conducting && !reader.ready()) {
						Thread.sleep(300);
					}
					if (!conducting || isQuitCommand(reader.readLine()))
						break;
				} catch (InterruptedException e) {
					LOGGER.info("I/O thread interrupt, " + "which may mean program successfully finished");
					break;
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
			conducting = false;
			LOGGER.info("thread finish");
		}

		private boolean isQuitCommand(String command) {
			if (null == command || "q".equals(command))
				return true;
			LOGGER.info(command);
			return false;
		}
	}

	private String pathToBackupFile() {
		return context.getJsPath() + ".backup";
	}

	/**
	 * Specify the integer N that represents interval of saving mutation
	 * information; mutation file updated every N execution. Default value is
	 * Integer.MAX_VALUE.
	 */
	public void setSaveInformationInterval(int saveInformationInterval) {
		Preconditions.checkArgument(saveInformationInterval > 0, "interval must be positive integer.");
		this.saveInformationInterval = saveInformationInterval;
	}
}
