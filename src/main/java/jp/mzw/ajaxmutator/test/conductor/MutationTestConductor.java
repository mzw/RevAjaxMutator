package jp.mzw.ajaxmutator.test.conductor;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import difflib.DiffUtils;
import difflib.Patch;
import difflib.PatchFailedException;
import jp.mzw.ajaxmutator.mutatable.EventAttachment;
import jp.mzw.ajaxmutator.mutatable.Mutatable;
import jp.mzw.ajaxmutator.Context;
import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.ParserWithBrowser;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.generator.MutationFileInformation;
import jp.mzw.ajaxmutator.generator.MutationFileWriter;
import jp.mzw.ajaxmutator.generator.MutationListManager;
import jp.mzw.ajaxmutator.generator.UnifiedDiffGenerator.DiffLine;
import jp.mzw.ajaxmutator.mutator.DOMSelectionSelectNearbyMutator;
import jp.mzw.ajaxmutator.mutator.Mutator;
import jp.mzw.ajaxmutator.mutator.replace.among.AttributeModificationTargetRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.AttributeModificationValueRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.EventCallbackRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.EventTargetRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.EventTypeRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.RequestOnSuccessHandlerRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.RequestUrlRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.TimerEventCallbackRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.TimerEventDurationRAMutator;
import jp.mzw.ajaxmutator.test.executor.JUnitExecutor;
import jp.mzw.ajaxmutator.test.executor.TestExecutor;
import jp.mzw.ajaxmutator.util.Randomizer;
import jp.mzw.ajaxmutator.util.Util;
import jp.mzw.revajaxmutator.test.result.Coverage;

import org.junit.runner.Result;
import org.mozilla.javascript.ast.AstRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Executor to apply mutation testing to target applications. <br>
 * Note: Currently we assume that mutation target is single JavaScript file.
 *
 * @author Kazuki Nishiura
 */
public class MutationTestConductor {
	protected static final Logger LOGGER = LoggerFactory.getLogger(MutationTestConductor.class);

	protected MutationFileWriter mutationFileWriter;
	protected MutationListManager mutationListManager;
	protected Multimap<String, String> unkilledMutantsInfo;
	protected Context context = Context.INSTANCE;
	protected boolean setup = false;
	protected int saveInformationInterval = Integer.MAX_VALUE;
	protected ParserWithBrowser parser;
	protected AstRoot astRoot;
	protected boolean conducting;
	protected boolean dryRun;
	protected MutateVisitor visitor;
	protected String pathToJsFile;

	protected Map<String, boolean[]> coverageInfo;

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
	
	protected String getPathToJsFile() {
		return this.pathToJsFile;
	}
	
	protected MutateVisitor getMutateVisitor() {
		return this.visitor;
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
		Stopwatch stopwatch = Stopwatch.createStarted();
		generateMutations(mutators);
		applyMutationAnalysis(testExecutor, stopwatch);
	}

	public void mutationAnalysisUsingExistingMutations(TestExecutor testExecutor) {
		mutationListManager = new MutationListManager(mutationFileWriter.getDestinationDirectory());
		mutationListManager.readExistingMutationListFile();
		unkilledMutantsInfo = ArrayListMultimap.create();

		checkIfSetuped();
		applyMutationAnalysis(testExecutor, Stopwatch.createStarted());
	}

	public void mutationAnalysisUsingExistingMutations(List<TestExecutor> testExecutors, HashMap<String, File> failureCoverageFiles) {
		mutationListManager = new MutationListManager(mutationFileWriter.getDestinationDirectory());
		mutationListManager.readExistingMutationListFile();
		unkilledMutantsInfo = ArrayListMultimap.create();
		coverageInfo = Coverage.getCoverageInfo(failureCoverageFiles, pathToJsFile);

		checkIfSetuped();
		applyMutationAnalysis(testExecutors, Stopwatch.createStarted());
	}

	public void tryToKillSpecificMutant(String mutationFileName, TestExecutor testExecutor) {
		mutationListManager = new MutationListManager(mutationFileWriter.getDestinationDirectory());
		mutationListManager.readExistingMutationListFile();
		checkIfSetuped();

		for (String description : mutationListManager.getListOfMutationName()) {
			for (MutationFileInformation mutationFileInformation : mutationListManager.getMutationFileInformationList(description)) {
				if (mutationFileInformation.getFileName().equals(mutationFileName)) {
					List<String> original = Util.readFromFile(pathToJsFile);
					if (!applyMutationFile(original, mutationFileInformation)) {
						return;
					}
					if (testExecutor.execute()) { // This mutants cannot be killed
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
		generateMutationFiles(visitor.getEventAttachments(), mutators);
		generateMutationFiles(visitor.getTimerEventAttachmentExpressions(), mutators);
		// Asynchronous communications
		generateMutationFiles(visitor.getRequests(), mutators);
		// DOM manipulations
		generateMutationFiles(visitor.getDomCreations(), mutators);
		generateMutationFiles(visitor.getDomAppendings(), mutators);
		generateMutationFiles(visitor.getDomSelections(), mutators);
		generateMutationFiles(visitor.getDomRemovals(), mutators);
		generateMutationFiles(visitor.getAttributeModifications(), mutators);
		generateMutationFiles(visitor.getDomClonings(), mutators);
		generateMutationFiles(visitor.getDomNormalizations(), mutators);
		generateMutationFiles(visitor.getDomReplacements(), mutators);

		// Statements (referring to GenProg)
		generateMutationFiles(visitor.getStatements(), mutators);

		// Generic
		generateMutationFiles(visitor.getAssignmentExpressions(), mutators);
		generateMutationFiles(visitor.getBreaks(), mutators);
		generateMutationFiles(visitor.getContinues(), mutators);
		generateMutationFiles(visitor.getFors(), mutators);
		generateMutationFiles(visitor.getFuncnodes(), mutators);
		generateMutationFiles(visitor.getIfs(), mutators);
		generateMutationFiles(visitor.getReturns(), mutators);
		generateMutationFiles(visitor.getSwitches(), mutators);
		generateMutationFiles(visitor.getVariableDecss(), mutators);
		generateMutationFiles(visitor.getWhiles(), mutators);

		LOGGER.debug("Random values used for generating mutations: {}", Arrays.toString(Randomizer.getReturnedValues()));
		return numOfMutation;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void generateMutationFiles(Set<? extends Mutatable> mutatables, Set<Mutator<?>> mutators) {
		if (mutatables.size() == 0) {
			LOGGER.info("mutable: 0");
			return;
		}

		Set<Mutator<?>> applicableMutator = new HashSet<>();
		Mutatable aMutatable = Iterables.get(mutatables, 0);
		LOGGER.info("try to create mutations for {}. {} elements exist.", aMutatable.getClass().getSimpleName(), mutatables.size());
		for (Mutator<?> mutator : mutators) {
			LOGGER.info("mutator: {}", mutator.toString());
			if (mutator.isApplicable(aMutatable.getClass())) {
				LOGGER.info("--applicabable");
				applicableMutator.add(mutator);
			} else {
				LOGGER.info("--not applicabable");
			}
		}
		for (Mutator mutator : applicableMutator) {
			LOGGER.info("---mutator: {}", mutator.toString());
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
						LOGGER.info("dry run: true");
						continue;
					}
					File generatedFile = mutationFileWriter.writeToFile(mutation);
					if (generatedFile == null) {
						LOGGER.error("failed to generate mutation file");
						continue;
					}

					DiffLine diffLine = mutationFileWriter.getDiffLine(mutation);
					MutationFileInformation info = new MutationFileInformation(generatedFile.getName(), generatedFile.getAbsolutePath(),
							MutationFileInformation.State.NON_EQUIVALENT_LIVE, diffLine.getStartLine(), diffLine.getEndLine(),
							mutatable.getClass().getSimpleName(), mutator.mutationName(), mutation.getRepairValue().getValue(),
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
		LOGGER.info("finished! " + runningStopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000.0 + " sec.");
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
		LOGGER.info("finished! " + runningStopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000.0 + " sec.");
	}

	// original
	private int applyMutationAnalysis(TestExecutor testExecutor) {
		int numberOfAppliedMutation = 0;
		int numberOfMaxMutants = mutationListManager.getNumberOfUnkilledMutants();
		Thread commandReceiver = new Thread(new CommandReceiver());
		commandReceiver.start();
		List<String> original = Util.readFromFile(pathToJsFile);
		List<String> nameOfMutations = mutationListManager.getListOfMutationName();
		for (String description : nameOfMutations) {
			LOGGER.info("Start applying {}", description);
			for (MutationFileInformation mutationFileInformation : mutationListManager.getMutationFileInformationList(description)) {
				// execution can be canceled from outside.
				if (!conducting) {
					break;
				}
				if (mutationFileInformation.canBeSkipped() || !applyMutationFile(original, mutationFileInformation)) {
					continue;
				}
				numberOfAppliedMutation++;
				if (numberOfAppliedMutation >= saveInformationInterval & (numberOfAppliedMutation % saveInformationInterval == 0)) {
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
			for (MutationFileInformation mutationFileInformation : mutationListManager.getMutationFileInformationList(description)) {
				LOGGER.info(mutationFileInformation.getState().toString());
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

		// スレッド数指定実行
		ExecutorService executor = Executors.newFixedThreadPool(2);

		List<Future<Boolean>> futureList = new ArrayList<Future<Boolean>>();

		// do-fewer
		List<MutationFileInformation> skipList = getSkipListbyDoFewer(mutationListManager.getMutationFileInformationList(), EventTargetRAMutator.class);

		for (String description : nameOfMutations) {
			LOGGER.info("Start applying {}", description);

			for (MutationFileInformation mutationFileInformation : mutationListManager.getMutationFileInformationList(description)) {

				// execution can be canceled from outside.
				if (!conducting) {
					break;
				}
				if (mutationFileInformation.canBeSkipped() || !createMutationFile(original, mutationFileInformation)
						|| !isCoveredMutationPointbyTest(mutationFileInformation) || isSkipListMutationOfDoFewer(skipList, mutationFileInformation)) {
					continue;
				}
				numberOfAppliedMutation++;
				if (numberOfAppliedMutation >= saveInformationInterval & (numberOfAppliedMutation % saveInformationInterval == 0)) {
					mutationListManager.generateMutationListFile();
				}
				LOGGER.info("Executing test(s) on {}", mutationFileInformation.getAbsolutePath());
				Future<Boolean> future = null;
				List<String> orderedMethodNames = getOrderedMethodNames(mutationFileInformation);
				String mutantname = Util.getFileNameWithoutExtension(mutationFileInformation.getFileName());

				TestExecutor targetTestExecutor = getTargetTestExecutor(testExecutors, mutantname);
				targetTestExecutor.setOrderdMethodNames(orderedMethodNames);

				future = executor
						.submit(new TestCallable(targetTestExecutor, mutationFileInformation, description, numberOfAppliedMutation, numberOfMaxMutants));
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
			for (MutationFileInformation mutationFileInformation : mutationListManager.getMutationFileInformationList(description)) {
				LOGGER.info(mutationFileInformation.getState().toString());
			}
		}

		executor.shutdown();

		if (conducting) {
			commandReceiver.interrupt();
			conducting = false;
		}
		return numberOfAppliedMutation;
	}

	private boolean isCoveredMutationPointbyTest(MutationFileInformation info) {
		int startLine = info.getStartLine();
		int endLine = info.getEndLine();

		for (Map.Entry<String, boolean[]> entry : coverageInfo.entrySet()) {
			boolean[] testsCoverage = entry.getValue();
			for (int line = startLine; line <= endLine; line++) {
				if (testsCoverage[line] == true) {
					return true;
				}
			}
		}
		LOGGER.info(info.getFileName() + " is skipped by coverage");
		return false;
	}

	private boolean isCoveredMutationPointbyTest(MutationFileInformation info, String MethodName) {
		int startLine = info.getStartLine();
		int endLine = info.getEndLine();
		for (int line = startLine; line <= endLine; line++) {
			boolean[] testsCoverage = coverageInfo.get(MethodName);
			if (testsCoverage[line] == true) {
				return true;
			}
		}
		return false;
	}

	private List<String> getOrderedMethodNames(MutationFileInformation info) {

		HashMap<String, Integer> map = new HashMap<String, Integer>();

		for (Map.Entry<String, boolean[]> entry : coverageInfo.entrySet()) {
			if (!isCoveredMutationPointbyTest(info, entry.getKey())) {
				continue;
			}
			int coverCnt = 0;
			for (int line = 1; line < info.getStartLine(); line++) {
				if (entry.getValue()[line]) {
					coverCnt++;
				}
			}
			map.put(entry.getKey(), new Integer(coverCnt));
		}

		List<Entry<String, Integer>> entries = new ArrayList<Entry<String, Integer>>(map.entrySet());
		Collections.sort(entries, new Comparator<Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		ArrayList<String> orderedMethodNames = new ArrayList<String>();
		int i = 1;
		for (Entry<String, Integer> e : entries) {
			orderedMethodNames.add(e.getKey());
			LOGGER.info("<{}> : [{}]methodName = {} , coverage = {}", info.getFileName(), i, e.getKey(), e.getValue());
			i++;
		}

		return orderedMethodNames;
	}

	// do-fewer
	private List<MutationFileInformation> getSkipListbyDoFewer(List<MutationFileInformation> list, Class<?> priorMutaterClass) {

		List<MutationFileInformation> eventAttachmentMutantList = new ArrayList<MutationFileInformation>();

		for (MutationFileInformation mutationFileInformation : list) {
			String mutatable = mutationFileInformation.getMutatable();
			if (mutatable.equals(EventAttachment.class.getSimpleName())) {
				eventAttachmentMutantList.add(mutationFileInformation);
			}
		}

		Map<Integer, ArrayList<MutationFileInformation>> map = new HashMap<Integer, ArrayList<MutationFileInformation>>();
		for (MutationFileInformation mutationFileInformation : eventAttachmentMutantList) {
			Integer startLine = mutationFileInformation.getStartLine();
			if (map.containsKey(startLine)) {
				map.get(startLine).add(mutationFileInformation);
			} else {
				ArrayList<MutationFileInformation> ls = new ArrayList<MutationFileInformation>();
				ls.add(mutationFileInformation);
				map.put(startLine, ls);
			}
		}

		ArrayList<MutationFileInformation> skipList = new ArrayList<MutationFileInformation>();

		for (Map.Entry<Integer, ArrayList<MutationFileInformation>> entry : map.entrySet()) {
			ArrayList<MutationFileInformation> mfList = entry.getValue();
			if (1 < mfList.size()) {
				String priorMutaterClassName = getFixerNameByClass(priorMutaterClass);
				// 指定イベントミューテーションクラスが存在する
				if (hasPriorMutaterClass(entry.getValue(), priorMutaterClassName)) {
					for (MutationFileInformation mutationFileInformation : mfList) {
						if (!mutationFileInformation.getFixer().equals(priorMutaterClassName)) {
							skipList.add(mutationFileInformation);
						}
					}
				} else { // 存在しない場合はランダムに絞るクラスを選択
					for (int i = 0; i < mfList.size() - 1; i++) {
						Random rnd = new Random();
						int index = rnd.nextInt(mfList.size());
						skipList.add(mfList.get(index));
					}
				}
			}
		}

		return skipList;
	}

	private String getFixerNameByClass(Class<?> clazz) {
		return clazz.getSimpleName().replace("Mutator", "Mutation");
	}

	private boolean hasPriorMutaterClass(List<MutationFileInformation> list, String priorMutaterClassName) {
		for (MutationFileInformation mutationFileInformation : list) {
			if (mutationFileInformation.getFixer().equals(priorMutaterClassName)) {
				return true;
			}
		}
		return false;
	}

	// do-fewer
	private boolean isSkipListMutationOfDoFewer(List<MutationFileInformation> skipList, MutationFileInformation targetMutationFileInformation) {
		for (MutationFileInformation fileInfo : skipList) {
			if (fileInfo.getFileName().equals(targetMutationFileInformation.getFileName())) {
				LOGGER.info(targetMutationFileInformation.getFileName() + " is skipped by do fewer");
				return true;
			}
		}
		return false;
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

		public TestCallable(TestExecutor testExecutor, MutationFileInformation mutationFileInformation, String description, int numberOfAppliedMutation,
				int numberOfMaxMutants) {
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

	protected void checkIfSetuped() {
		if (!setup) {
			throw new IllegalStateException("You 'must' call setup method before you use.");
		}
	}

	private void logExecutionDetail(int numberOfAppliedMutation) {
		LOGGER.info("---------------------------------------------");
		StringBuilder detailedInfo = new StringBuilder();
		int numberOfUnkilledMutatns = 0;
		for (String key : unkilledMutantsInfo.keySet()) {
			numberOfUnkilledMutatns += unkilledMutantsInfo.get(key).size();
			detailedInfo.append(key).append(": ").append(unkilledMutantsInfo.get(key).size()).append(System.lineSeparator());
			for (String info : unkilledMutantsInfo.get(key)) {
				detailedInfo.append(info).append(System.lineSeparator());
			}
			detailedInfo.append(System.lineSeparator());
		}

		LOGGER.info(detailedInfo.toString());

		int numberOfMaxMutants = mutationListManager.getNumberOfMaxMutants();
		double score = Math.floor((1.0 - (1.0 * numberOfUnkilledMutatns / numberOfMaxMutants)) * 100 * 10) / 10;
		LOGGER.info("{} unkilled mutants among {} ({})", numberOfUnkilledMutatns, numberOfAppliedMutation, numberOfMaxMutants);
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
			System.out.println("You can stop execution any time by entering 'q'");
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

	public static ImmutableSet<Mutator<?>> defaultMutators(MutateVisitor visitor) {
		ImmutableSet<Mutator<?>> mutators = ImmutableSet.<Mutator<?>> of(new EventTargetRAMutator(visitor.getEventAttachments()),
				new EventTypeRAMutator(visitor.getEventAttachments()), new EventCallbackRAMutator(visitor.getEventAttachments()),
				new TimerEventDurationRAMutator(visitor.getTimerEventAttachmentExpressions()),
				new TimerEventCallbackRAMutator(visitor.getTimerEventAttachmentExpressions()), new RequestUrlRAMutator(visitor.getRequests()),
				new RequestOnSuccessHandlerRAMutator(visitor.getRequests()), new DOMSelectionSelectNearbyMutator(),
				new AttributeModificationTargetRAMutator(visitor.getAttributeModifications()),
				new AttributeModificationValueRAMutator(visitor.getAttributeModifications()));
		return mutators;
	}
}
