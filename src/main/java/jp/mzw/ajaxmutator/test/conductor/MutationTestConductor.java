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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import jp.mzw.ajaxmutator.Context;
import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.ParserWithBrowser;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.generator.MutationFileInformation;
import jp.mzw.ajaxmutator.generator.MutationFileWriter;
import jp.mzw.ajaxmutator.generator.MutationListManager;
import jp.mzw.ajaxmutator.generator.UnifiedDiffGenerator.DiffLine;
import jp.mzw.ajaxmutator.mutatable.Mutatable;
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

/**
 * Executor to apply mutation testing to target applications. <br>
 * Note: Currently we assume that mutation target is single JavaScript file.
 *
 * @author Kazuki Nishiura
 */
public class MutationTestConductor {
	protected static final Logger LOGGER = LoggerFactory.getLogger(MutationTestConductor.class);

	/*
	 * -------------------------------------------------- Fields
	 * --------------------------------------------------
	 */

	MutationFileWriter mutationFileWriter;
	MutationListManager mutationListManager;
	Multimap<String, String> unkilledMutantsInfo;
	protected Context context = Context.INSTANCE;
	protected boolean setup = false;
	int saveInformationInterval = Integer.MAX_VALUE;
	protected ParserWithBrowser parser;
	private AstRoot astRoot;
	boolean conducting;
	private boolean dryRun;
	protected MutateVisitor visitor;
	String pathToJsFile;
	String targetURL;
	private Map<Mutator<?>, Integer> numOfMutation;
	long timeoutMin;

	/**
	 * Setting information required for mutation testing. This method MUST be
	 * called before conducting mutation testing.
	 *
	 * @return if setup is successfully finished.
	 */
	public boolean setup(final String pathToJSFile, String targetURL, MutateVisitor visitor) {
		this.setup = false;
		this.pathToJsFile = pathToJSFile;
		this.context.registerJsPath(pathToJSFile);
		this.pathToJsFile = pathToJSFile;
		final File jsFile = new File(pathToJSFile);
		Util.normalizeLineBreak(jsFile);
		this.mutationFileWriter = new MutationFileWriter(jsFile);
		Util.copyFile(pathToJSFile, this.pathToBackupFile());
		this.timeoutMin = Long.MAX_VALUE;

		this.targetURL = targetURL;
		this.parser = ParserWithBrowser.getParser();
		try {
			final FileReader fileReader = new FileReader(jsFile);
			this.astRoot = this.parser.parse(fileReader, targetURL, 1);
		} catch (final IOException e) {
			LOGGER.error("IOException: cannot parse AST.");
			return false;
		}

		if (this.astRoot != null) {
			this.astRoot.visit(visitor);
			this.setup = true;
		} else {
			LOGGER.error("Cannot parse AST.");
		}
		this.visitor = visitor;
		return this.setup;
	}

	/**
	 * Need to set up before mutation analysis. If not, throw
	 * {@code IllegalStateException}.
	 */
	void checkIfSetuped() {
		if (!this.setup) {
			throw new IllegalStateException("You 'must' call setup method before you use.");
		}
	}

	/*
	 * -------------------------------------------------- Getters and Setters
	 * --------------------------------------------------
	 */

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
	String pathToBackupFile() {
		return this.context.getJsPath() + ".backup";
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
	 * @param saveInformationInterval
	 *            represents interval of saving mutation information
	 */
	public void setSaveInformationInterval(int saveInformationInterval) {
		Preconditions.checkArgument(saveInformationInterval > 0, "interval must be positive integer.");
		this.saveInformationInterval = saveInformationInterval;
	}

	/*
	 * -------------------------------------------------- Functionalities of
	 * Mutation Analysis --------------------------------------------------
	 */

	/**
	 * Check how many mutants are generated and so on.
	 *
	 * @return Mapping of mutator classes and how many times it should be
	 *         applied.
	 */
	public Map<Mutator<?>, Integer> dryRun(Set<Mutator<?>> mutators) {
		this.dryRun = true;
		this.checkIfSetuped();
		this.mutationListManager = new MutationListManager(this.mutationFileWriter.getDestinationDirectory());
		this.generateMutationFiles(this.visitor, mutators);
		return this.numOfMutation;
	}

	/**
	 * Generate mutation files corresponding to given {@link Mutator}.
	 */
	public void generateMutations(Set<Mutator<?>> mutators) {
		this.unkilledMutantsInfo = ArrayListMultimap.create();
		this.checkIfSetuped();
		this.mutationListManager = new MutationListManager(this.mutationFileWriter.getDestinationDirectory());
		this.generateMutationFiles(this.visitor, mutators);
		this.mutationListManager.generateMutationListFile();
	}

	/**
	 * Generate mutation files corresponding to given {@link Mutator}, and then
	 * running test.
	 */
	public void generateMutationsAndApplyTest(TestExecutor testExecutor, Set<Mutator<?>> mutators) {
		final Stopwatch stopwatch = Stopwatch.createStarted();
		this.generateMutations(mutators);
		this.applyMutationAnalysis(testExecutor, stopwatch);
	}

	/**
	 *
	 *
	 * @param testExecutor
	 */
	public void mutationAnalysisUsingExistingMutations(TestExecutor testExecutor) {
		this.mutationListManager = new MutationListManager(this.mutationFileWriter.getDestinationDirectory());
		this.mutationListManager.readExistingMutationListFile();
		this.unkilledMutantsInfo = ArrayListMultimap.create();

		this.checkIfSetuped();
		this.applyMutationAnalysis(testExecutor, Stopwatch.createStarted());
	}

	/**
	 * Generate mutations
	 *
	 * Note: register new mutations here.
	 *
	 * @param visitor
	 *            provides detection results of mutable syntax elements
	 * @param mutators
	 *            provides how to mutate mutable syntax elements
	 * @return map containing mutator as key and the number of its mutations as
	 *         value
	 */
	private Map<Mutator<?>, Integer> generateMutationFiles(MutateVisitor visitor,
		Set<Mutator<?>> mutators) {
		this.numOfMutation = new HashMap<Mutator<?>, Integer>();

		// Events
		this.generateMutationFiles(visitor.getEventAttachments(), mutators);
		this.generateMutationFiles(visitor.getTimerEventAttachmentExpressions(), mutators);
		// Asynchronous communications
		this.generateMutationFiles(visitor.getRequests(), mutators);
		// DOM manipulations
		this.generateMutationFiles(visitor.getDomCreations(), mutators);
		this.generateMutationFiles(visitor.getDomAppendings(), mutators);
		this.generateMutationFiles(visitor.getDomSelections(), mutators);
		this.generateMutationFiles(visitor.getDomRemovals(), mutators);
		this.generateMutationFiles(visitor.getAttributeModifications(), mutators);
		this.generateMutationFiles(visitor.getDomClonings(), mutators);
		this.generateMutationFiles(visitor.getDomNormalizations(), mutators);
		this.generateMutationFiles(visitor.getDomReplacements(), mutators);

		// Statements (referring to GenProg)
		this.generateMutationFiles(visitor.getStatements(), mutators);

		// Generic
		this.generateMutationFiles(visitor.getAssignmentExpressions(), mutators);
		this.generateMutationFiles(visitor.getBreaks(), mutators);
		this.generateMutationFiles(visitor.getContinues(), mutators);
		this.generateMutationFiles(visitor.getFors(), mutators);
		this.generateMutationFiles(visitor.getFuncnodes(), mutators);
		this.generateMutationFiles(visitor.getIfs(), mutators);
		this.generateMutationFiles(visitor.getReturns(), mutators);
		this.generateMutationFiles(visitor.getSwitches(), mutators);
		this.generateMutationFiles(visitor.getVariableDecss(), mutators);
		this.generateMutationFiles(visitor.getWhiles(), mutators);

		LOGGER.debug("Random values used for generating mutations: {}",
				Arrays.toString(Randomizer.getReturnedValues()));
		return this.numOfMutation;
	}

	/**
	 * Generate mutations
	 *
	 * @param mutatables
	 *            represent locations where mutators mutate
	 * @param mutators
	 *            represent how to mutate mutable syntax elements
	 */
	private void generateMutationFiles(Set<? extends Mutatable> mutatables, Set<Mutator<?>> mutators) {
		if (mutatables.size() == 0) {
			LOGGER.info("mutable: 0");
			return;
		}

		final Set<Mutator<?>> applicableMutator = new HashSet<>();
		final Mutatable aMutatable = Iterables.get(mutatables, 0);
		LOGGER.info("try to create mutations for {}. {} elements exist.", aMutatable.getClass().getSimpleName(),
				mutatables.size());
		for (final Mutator<?> mutator : mutators) {
			LOGGER.info("mutator: {}", mutator.toString());
			if (mutator.isApplicable(aMutatable.getClass())) {
				LOGGER.info("--applicabable");
				applicableMutator.add(mutator);
			} else {
				LOGGER.info("--not applicabable");
			}
		}
		for (@SuppressWarnings("rawtypes")
		final Mutator mutator : applicableMutator) {
			LOGGER.info("---mutator: {}", mutator.toString());
			this.numOfMutation.put(mutator, 0);
			LOGGER.info("using {}", mutator.mutationName());
			for (final Mutatable mutatable : mutatables) {
				@SuppressWarnings("unchecked")
				final List<Mutation> mutations = mutator.generateMutationList(mutatable);
				if (mutations == null) {
					continue;
				}
				for (final Mutation mutation : mutations) {
					if (mutation == null || mutation.getOriginalNode() == null) {
						LOGGER.info("Cannot create mutation for {} by using {}", mutatable, mutator.mutationName());
						continue;
					}
					this.numOfMutation.put(mutator, this.numOfMutation.get(mutator) + 1);
					if (this.dryRun) {
						LOGGER.info("dry run: true");
						continue;
					}
					final File generatedFile = this.mutationFileWriter.writeToFile(mutation);
					if (generatedFile == null) {
						LOGGER.error("failed to generate mutation file");
						continue;
					}

					final DiffLine diffLine = this.mutationFileWriter.getDiffLine(mutation);
					final MutationFileInformation info = new MutationFileInformation(generatedFile.getName(),
							generatedFile.getAbsolutePath(), MutationFileInformation.State.NON_EQUIVALENT_LIVE,
							diffLine.getStartLine(), diffLine.getEndLine(), mutatable.getClass().getSimpleName(),
							mutator.mutationName(), mutation.getRepairValue().getValue(),
							mutation.getRepairSource().name());

					this.mutationListManager.addMutationFileInformation(mutator.mutationName(), info);
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
	private void applyMutationAnalysis(TestExecutor testExecutor, Stopwatch runningStopwatch) {
		this.conducting = true;
		this.addShutdownHookToRestoreBackup();

		final int numberOfAppliedMutation = this.applyMutationAnalysis(testExecutor);

		runningStopwatch.stop();
		LOGGER.info("Updating mutation list file...");
		this.mutationListManager.generateMutationListFile();

		this.logExecutionDetail(numberOfAppliedMutation);
		LOGGER.info("restoring backup file...");
		Util.copyFile(this.pathToBackupFile(), this.context.getJsPath());
		LOGGER.info("finished! " + runningStopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000.0 + " sec.");
	}

	/**
	 * Run test cases on each mutant
	 */
	private int applyMutationAnalysis(TestExecutor testExecutor) {
		int numberOfAppliedMutation = 0;
		final int numberOfMaxMutants = this.mutationListManager.getNumberOfUnkilledMutants();

		// Start thread that listens for an external "kill" command
		final Thread commandReceiver = new Thread(new CommandReceiver());
		commandReceiver.start();
		final Thread timeout = new Thread(new Timeout(this.timeoutMin));
		timeout.start();

		// Run the test-cases for each mutant
		final List<String> original = Util.readFromFile(this.pathToJsFile);
		final List<String> nameOfMutations = this.mutationListManager.getListOfMutationName();
		for (final String description : nameOfMutations) {
			LOGGER.info("Start applying {}", description);
			for (final MutationFileInformation mutationFileInformation : this.mutationListManager
					.getMutationFileInformationList(description)) {
				// execution can be canceled from outside.
				if (!this.conducting) {
					break;
				}

				// Modify .js file with this mutation/patch
				if (mutationFileInformation.canBeSkipped()
						|| !this.applyMutationFile(original, mutationFileInformation)) {
					continue;
				}
				numberOfAppliedMutation++;

				// Save progress every "n" mutations
				if (numberOfAppliedMutation >= this.saveInformationInterval
						& (numberOfAppliedMutation % this.saveInformationInterval == 0)) {
					this.mutationListManager.generateMutationListFile();
				}

				// Execute the test case with the mutated/patched file
				LOGGER.info("Executing test(s) on {}", mutationFileInformation.getAbsolutePath());
				if (testExecutor.execute()) {
					// This mutant cannot be killed
					this.unkilledMutantsInfo.put(description, mutationFileInformation.toString());
					LOGGER.info("mutant {} is not be killed", description);
				} else {
					mutationFileInformation.setState(MutationFileInformation.State.KILLED);
				}
				final String message = testExecutor.getMessageOnLastExecution();
				if (message != null) {
					LOGGER.info(message);
				}
				this.logProgress(numberOfAppliedMutation, numberOfMaxMutants);
			}
			// execution can be canceled from outside.
			if (!this.conducting) {
				break;
			}
		}
		for (final String description : nameOfMutations) {
			for (final MutationFileInformation mutationFileInformation : this.mutationListManager
					.getMutationFileInformationList(description)) {
				LOGGER.info(mutationFileInformation.getState().toString());
			}
		}
		if (this.conducting) {
			commandReceiver.interrupt();
			timeout.interrupt();
			this.conducting = false;
		}
		return numberOfAppliedMutation;
	}

	/**
	 *
	 * @param original
	 * @param fileInfo
	 * @return if successfully file is wrote.
	 */
	private boolean applyMutationFile(List<String> original, MutationFileInformation fileInfo) {
		final Patch patch = DiffUtils.parseUnifiedDiff(Util.readFromFile(fileInfo.getAbsolutePath()));
		try {
			@SuppressWarnings("unused")
			final List<?> mutated = patch.<String>applyTo(original);
			Util.writeToFile(this.pathToJsFile, Util.join(mutated.toArray(new String[0]), System.lineSeparator()));
		} catch (final PatchFailedException e) {
			LOGGER.error("Applying mutation file '{}' failed.", fileInfo.getFileName(), e);
			return false;
		}
		return true;
	}

	/**
	 * Log progress while running mutation analysis
	 *
	 * @param finished
	 *            represents the number of mutants examined by test cases
	 * @param total
	 *            represents the total number of generated mutants
	 */
	synchronized void logProgress(int finished, int total) {
		LOGGER.info("{} in {} finished: {} %", finished, total, Math.floor(finished * 1000.0 / total) / 10);
	}

	/**
	 * Log results after completing mutation analysis
	 *
	 * @param numberOfAppliedMutation
	 */
	void logExecutionDetail(int numberOfAppliedMutation) {
		LOGGER.info("---------------------------------------------");
		final StringBuilder detailedInfo = new StringBuilder();
		int numberOfUnkilledMutants = 0;
		for (final String key : this.unkilledMutantsInfo.keySet()) {
			numberOfUnkilledMutants += this.unkilledMutantsInfo.get(key).size();
			detailedInfo.append(key).append(": ").append(this.unkilledMutantsInfo.get(key).size())
					.append(System.lineSeparator());
			for (final String info : this.unkilledMutantsInfo.get(key)) {
				detailedInfo.append(info).append(System.lineSeparator());
			}
			detailedInfo.append(System.lineSeparator());
		}

		LOGGER.info(detailedInfo.toString());

		final int numberOfMaxMutants = this.mutationListManager.getNumberOfMaxMutants();
		final double score = Math.floor((1.0 - (1.0 * numberOfUnkilledMutants / numberOfMaxMutants)) * 100 * 10) / 10;
		LOGGER.info("{} unkilled mutants among {} ({})", numberOfUnkilledMutants, numberOfAppliedMutation,
				numberOfMaxMutants);
		LOGGER.info("Mutation score is: {} %", score);
	}

	/*
	 * -------------------------------------------------- Related Threads
	 * --------------------------------------------------
	 */

	/**
	 * Restore original JavaScript file after completing mutation analysis
	 */
	void addShutdownHookToRestoreBackup() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				// restore backup
				Util.copyFile(MutationTestConductor.this.pathToBackupFile(), MutationTestConductor.this.pathToJsFile);
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
			final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("You can stop execution any time by entering 'q'");
			while (true) {
				try {
					while (MutationTestConductor.this.conducting && !reader.ready()) {
						Thread.sleep(300);
					}
					if (!MutationTestConductor.this.conducting || this.isQuitCommand(reader.readLine())) {
						break;
					}
				} catch (final InterruptedException e) {
					LOGGER.info("I/O thread interrupt, " + "which may mean program successfully finished");
					break;
				} catch (final IOException e) {
					e.printStackTrace();
					break;
				}
			}
			MutationTestConductor.this.conducting = false;
			LOGGER.info("thread finish");
		}

		private boolean isQuitCommand(String command) {
			if (null == command || "q".equals(command)) {
				return true;
			}
			LOGGER.info(command);
			return false;
		}
	}

	protected class Timeout implements Runnable {
		private long min = Long.MAX_VALUE;
		private boolean limited = false;
		Timeout(long min) {
			this.min = min;
			this.limited = (min == Long.MAX_VALUE ? false : true);
		}
		@Override
		public void run() {
			if (!this.limited) {
				return;
			}
			final String start = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			System.out.println("Limited time: " + this.min + " min (started from: " + start + ")");
			Stopwatch stopwatch = Stopwatch.createStarted();
			while (true) {
				try {
					Thread.sleep(1000);
					long elapsed = stopwatch.elapsed(TimeUnit.MINUTES);
					if (!MutationTestConductor.this.conducting || this.overLimitedTime(elapsed)) {
						stopwatch.stop();
						break;
					}
				} catch (InterruptedException e) {
					LOGGER.info("I/O thread (timeout) interrupt, " + "which may mean program successfully finished");
					break;
				}
			}
			MutationTestConductor.this.conducting = false;
			LOGGER.info("thread (timeout) finish");
		}
		private boolean overLimitedTime(long elapsed) {
			return this.min <= elapsed ? true : false;
		}
	}

	public void setTimeoutMin(long min) {
		this.timeoutMin = min;
	}

	/*
	 * -------------------------------------------------- Provide Defaults
	 * --------------------------------------------------
	 */

	/**
	 * Provides default set of mutators
	 *
	 * @param visitor
	 *            contains mutable elements
	 * @return default set of mutators
	 */
	static ImmutableSet<Mutator<?>> defaultMutators(MutateVisitor visitor) {
		final ImmutableSet<Mutator<?>> mutators = ImmutableSet.<Mutator<?>>of(
				new EventTargetRAMutator(visitor.getEventAttachments()),
				new EventTypeRAMutator(visitor.getEventAttachments()),
				new EventCallbackRAMutator(visitor.getEventAttachments()),
				new TimerEventDurationRAMutator(visitor.getTimerEventAttachmentExpressions()),
				new TimerEventCallbackRAMutator(visitor.getTimerEventAttachmentExpressions()),
				new RequestUrlRAMutator(visitor.getRequests()),
				new RequestOnSuccessHandlerRAMutator(visitor.getRequests()), new DOMSelectionSelectNearbyMutator(),
				new AttributeModificationTargetRAMutator(visitor.getAttributeModifications()),
				new AttributeModificationValueRAMutator(visitor.getAttributeModifications()));
		return mutators;
	}

	/**
	 * TODO: Separate this method into other class because this method is
	 * implemented for GenProg
	 *
	 * @param mutation
	 * @param executor
	 * @return
	 */
	public List<Result> testSpecificMutation(MutationFileInformation mutation, JUnitExecutor executor) {
		this.checkIfSetuped();
		final List<String> original = Util.readFromFile(this.pathToJsFile);
		if (!this.applyMutationFile(original, mutation)) {
			return null; // fail to mutation
		}
		final List<Result> testRestuls = executor.run();
		Util.copyFile(this.pathToBackupFile(), this.context.getJsPath());
		return testRestuls;
	}
}
