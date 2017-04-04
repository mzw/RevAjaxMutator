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

import org.junit.runner.Result;
import org.mozilla.javascript.ast.AstRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Executor to apply mutation testing to target applications. <br>
 * Note: Currently we assume that mutation target is single JavaScript file.
 *
 * @author Kazuki Nishiura
 */
public class MutationTestConductor {
	protected static final Logger LOGGER = LoggerFactory.getLogger(MutationTestConductor.class);

	/* --------------------------------------------------
	 * Fields
	 -------------------------------------------------- */

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
	protected String targetURL;
	protected Map<Mutator<?>, Integer> numOfMutation;

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

		this.targetURL = targetURL;
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
	 * Need to set up before mutation analysis. If not, throw {@code IllegalStateException}.
	 */
	protected void checkIfSetuped() {
		if (!setup) {
			throw new IllegalStateException("You 'must' call setup method before you use.");
		}
	}

	/* --------------------------------------------------
	 * Getters and Setters
	 -------------------------------------------------- */

	/**
	 * Get path to target JavaScript file
	 * 
	 * @return path to target JavaScript file
	 */
	protected String getPathToJsFile() {
		return this.pathToJsFile;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	protected String pathToBackupFile() {
		return context.getJsPath() + ".backup";
	}

	/**
	 * 
	 * @return
	 */
	protected MutateVisitor getMutateVisitor() {
		return this.visitor;
	}

	/**
	 * Specify the integer N that represents interval of saving mutation
	 * information; mutation file updated every N execution. Default value is
	 * Integer.MAX_VALUE.
	 * 
	 * @param saveInformationInterval represents interval of saving mutation information
	 */
	public void setSaveInformationInterval(int saveInformationInterval) {
		Preconditions.checkArgument(saveInformationInterval > 0, "interval must be positive integer.");
		this.saveInformationInterval = saveInformationInterval;
	}

	/* --------------------------------------------------
	 * Functionalities of Mutation Analysis
	 -------------------------------------------------- */

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

	/**
	 * 
	 * 
	 * @param testExecutor
	 */
	public void mutationAnalysisUsingExistingMutations(TestExecutor testExecutor) {
		mutationListManager = new MutationListManager(mutationFileWriter.getDestinationDirectory());
		mutationListManager.readExistingMutationListFile();
		unkilledMutantsInfo = ArrayListMultimap.create();

		checkIfSetuped();
		applyMutationAnalysis(testExecutor, Stopwatch.createStarted());
	}

	/**
	 * Generate mutations
	 * 
	 * Note: register new mutations here.
	 * 
	 * @param visitor provides detection results of mutable syntax elements
	 * @param mutators provides how to mutate mutable syntax elements
	 * @return map containing mutator as key and the number of its mutations as value
	 */
	protected Map<Mutator<?>, Integer> generateMutationFiles(MutateVisitor visitor, Set<Mutator<?>> mutators) {
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

	/**
	 * Generate mutations
	 * 
	 * @param mutatables represent locations where mutators mutate
	 * @param mutators represent how to mutate mutable syntax elements
	 */
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
		for (@SuppressWarnings("rawtypes")
		Mutator mutator : applicableMutator) {
			LOGGER.info("---mutator: {}", mutator.toString());
			numOfMutation.put(mutator, 0);
			LOGGER.info("using {}", mutator.mutationName());
			for (Mutatable mutatable : mutatables) {
				@SuppressWarnings("unchecked")
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

	/**
	 * Run test cases on each mutant in addition to measure the elapsed time
	 * 
	 * @param testExecutor
	 * @param runningStopwatch
	 */
	protected void applyMutationAnalysis(TestExecutor testExecutor, Stopwatch runningStopwatch) {
		conducting = true;
		addShutdownHookToRestoreBackup();

		int numberOfAppliedMutation = applyMutationAnalysis(testExecutor);

		runningStopwatch.stop();
		LOGGER.info("Updating mutation list file...");
		mutationListManager.generateMutationListFile();

		logExecutionDetail(numberOfAppliedMutation);
		LOGGER.info("restoring backup file...");
		Util.copyFile(pathToBackupFile(), context.getJsPath());
		LOGGER.info("finished! " + runningStopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000.0 + " sec.");
	}

	/**
	 * Run test cases on each mutant
	 */
	protected int applyMutationAnalysis(TestExecutor testExecutor) {
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

	/**
	 * 
	 * @param original
	 * @param fileInfo
	 * @return if successfully file is wrote.
	 */
	protected boolean applyMutationFile(List<String> original, MutationFileInformation fileInfo) {
		Patch patch = DiffUtils.parseUnifiedDiff(Util.readFromFile(fileInfo.getAbsolutePath()));
		try {
			@SuppressWarnings("unused")
			List<?> mutated = patch.<String> applyTo(original);
			Util.writeToFile(pathToJsFile, Util.join(mutated.toArray(new String[0]), System.lineSeparator()));
		} catch (PatchFailedException e) {
			LOGGER.error("Applying mutation file '{}' failed.", fileInfo.getFileName(), e);
			return false;
		}
		return true;
	}

	/**
	 * Log progress while running mutation analysis
	 * 
	 * @param finished represents the number of mutants examined by test cases
	 * @param total represents the total number of generated mutants
	 */
	protected synchronized void logProgress(int finished, int total) {
		LOGGER.info("{} in {} finished: {} %", finished, total, Math.floor(finished * 1000.0 / total) / 10);
	}

	/**
	 * Log results after completing mutation analysis
	 * 
	 * @param numberOfAppliedMutation
	 */
	protected void logExecutionDetail(int numberOfAppliedMutation) {
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

	/* --------------------------------------------------
	 * Related Threads
	 -------------------------------------------------- */

	/**
	 * Restore original JavaScript file after completing mutation analysis
	 */
	protected void addShutdownHookToRestoreBackup() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				// restore backup
				Util.copyFile(pathToBackupFile(), pathToJsFile);
				LOGGER.info("backup file restored");
			}
		});
	}

	/**
	 * When a user enters 'q' at console while running mutation analysis,
	 * {@code CommandReceiver} interrupts AjaxMutator to quit mutation analysis.
	 * 
	 * TODO: Limited time (in addition to current user interaction manner)
	 * 
	 * @author Kazuki Nishiura
	 *
	 */
	protected class CommandReceiver implements Runnable {
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

	/* --------------------------------------------------
	 * Provide Defaults
	 -------------------------------------------------- */

	/**
	 * Provides default set of mutators
	 * 
	 * @param visitor contains mutable elements
	 * @return default set of mutators
	 */
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
	
	/**
	 * TODO: Separate this method into other class because this method is implemented for GenProg
	 * 
	 * @param mutation
	 * @param executor
	 * @return
	 */
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
}
