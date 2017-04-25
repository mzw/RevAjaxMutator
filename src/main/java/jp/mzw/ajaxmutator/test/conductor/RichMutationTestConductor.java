package jp.mzw.ajaxmutator.test.conductor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ArrayListMultimap;

import difflib.DiffUtils;
import difflib.Patch;
import difflib.PatchFailedException;
import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.generator.MutationFileInformation;
import jp.mzw.ajaxmutator.generator.MutationListManager;
import jp.mzw.ajaxmutator.prioritizer.Prioritizer;
import jp.mzw.ajaxmutator.sampling.Sampling;
import jp.mzw.ajaxmutator.test.executor.TestExecutor;
import jp.mzw.ajaxmutator.util.Util;

/**
 * RichMutationTestConductor extends {@link MutationTestConductor} at the
 * following functionalities.
 *
 * <ol>
 * <li>Coverage-based computational cost reduction: If given test cases do not
 * cover mutated locations of mutants, RichMutationTestConductor is designed to
 * skip the mutants to run the test cases on.</li>
 * <li>Concurrent execution: Run test cases on each mutant in a multiple-threads
 * manner.</li>
 * <li>(Optional) Mutation sampling:</li>
 * </ol>
 *
 * @author Yuta Maezawa
 * @since 0.0.2
 */
public class RichMutationTestConductor extends MutationTestConductor {
	protected static Logger LOGGER = LoggerFactory.getLogger(RichMutationTestConductor.class);

	/**
	 * A barrier that prevents a thread from running a new test until all the
	 * other threads from the same batch finish their run
	 */
	private CyclicBarrier batchTestBarrier;

	/** Contains coverage results of target JavaScript code */
	protected Map<File, boolean[]> coverages;

	/**
	 * The number of threads to concurrently run test cases on each mutant
	 * (default: the number of available processors).
	 */
	protected int numOfThreads = Runtime.getRuntime().availableProcessors();

	protected Sampling sampling;
	protected Prioritizer prioritizer;

	/**
	 * Set up RichMutationTestConductor directly
	 *
	 * @param pathToJSFile
	 * @param targetURL
	 * @param visitor
	 * @param coverages
	 */
	public void setup(final String pathToJSFile, final String targetURL, final MutateVisitor visitor,
			final Map<File, boolean[]> coverages) {
		super.setup(pathToJSFile, targetURL, visitor);
		if (coverages != null) {
			this.coverages = coverages;
		} else {
			LOGGER.warn("Coverage results are null");
		}
	}

	/**
	 * Set up RichMutationTestConductor with MutationTestConductor
	 *
	 * @param conductor
	 * @param coverages
	 */
	public void setup(final MutationTestConductor conductor, final Map<File, boolean[]> coverages) {
		this.setup(conductor.pathToJsFile, conductor.targetURL, conductor.visitor, coverages);
	}

	public void setSamplingStrategy(Sampling sampling) {
		this.sampling = sampling;
	}

	public void setThreadNum(int n) {
		if (0 < n) {
			this.numOfThreads = n;
		} else {
			throw new IllegalStateException("The number of thread should be bigger than 0.");
		}
	}

	public void setPrioritizeStrategy(Prioritizer prioritizer) {
		this.prioritizer = prioritizer;
	}

	/*
	 * -------------------------------------------------- Extensions
	 * --------------------------------------------------
	 */

	/**
	 * Run test case on each mutant in a multiple-threads manner
	 *
	 * @param testExecutors
	 * @param coverages
	 */
	public void mutationAnalysisUsingExistingMutations(List<TestExecutor> testExecutors) {
		this.mutationListManager = new MutationListManager(this.mutationFileWriter.getDestinationDirectory());
		this.mutationListManager.readExistingMutationListFile();
		this.unkilledMutantsInfo = ArrayListMultimap.create();

		this.checkIfSetuped();
		this.applyMutationAnalysis(testExecutors, Stopwatch.createStarted());
	}

	/**
	 *
	 * @param testExecutors
	 * @param runningStopwatch
	 */
	protected void applyMutationAnalysis(List<TestExecutor> testExecutors, Stopwatch runningStopwatch) {
		this.conducting = true;
		this.addShutdownHookToRestoreBackup();
		final int numberOfAppliedMutation = this.applyMutationAnalysis(testExecutors);

		runningStopwatch.stop();
		LOGGER.info("Updating mutation list file...");
		this.mutationListManager.generateMutationListFile();

		this.logExecutionDetail(numberOfAppliedMutation);
		LOGGER.info("restoring backup file...");
		Util.copyFile(this.pathToBackupFile(), this.context.getJsPath());
		LOGGER.info("finished! " + runningStopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000.0 + " sec.");
	}

	/**
	 * Run test cases on each mutant in a concurrent manner.
	 *
	 * @param testExecutors
	 *            Executors of test cases
	 * @return The number of applied mutants
	 */
	protected int applyMutationAnalysis(final List<TestExecutor> testExecutors) {
		// Set up
		int numberOfAppliedMutation = 0;
		final int numberOfMaxMutants = this.mutationListManager.getNumberOfUnkilledMutants();
		final Thread commandReceiver = new Thread(new CommandReceiver());
		commandReceiver.start();
		final List<String> original = Util.readFromFile(this.pathToJsFile);
		final List<String> nameOfMutations = this.mutationListManager.getListOfMutationName();

		// TODO Apply do-fewer approach
		// this.sampling.sample(this.mutationListManager.getMutationFileInformationList());

		// Running test cases on each mutant in a multiple-threads manner
		final ExecutorService executor = Executors.newFixedThreadPool(this.numOfThreads);
		this.batchTestBarrier = new CyclicBarrier(this.numOfThreads);
		final List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
		for (final String description : nameOfMutations) {
			LOGGER.info("Start applying {}", description);
			for (final MutationFileInformation mutant : this.mutationListManager
					.getMutationFileInformationList(description)) {
				// execution can be canceled from outside.
				if (!this.conducting) {
					executor.shutdownNow();
					break;
				}
				// When mutant is not "live non-equivalent", skip to run test
				// cases on it.
				if (mutant.canBeSkipped() // ||
											// !this.applyMutationFile(original,
											// mutant)
				) {
					continue;
				}
				// TODO
				// When test cases do not cover mutated locations of mutants,
				// skip to run the test cases on the mutants
				// if (!Coverage.isCovered(this.coverages,
				// mutant.getStartLine(), mutant.getEndLine())) {
				// LOGGER.info(mutant.getFileName() + " is skipped by
				// coverage");
				// continue;
				// }

				// TODO Apply mutation sampling
				// if (!this.sampling.isSampled(mutant)) {
				// LOGGER.info(mutant.getFileName() + " is skipped by
				// sampling");
				// continue;
				// }

				numberOfAppliedMutation++;
				if (numberOfAppliedMutation >= this.saveInformationInterval
						& (numberOfAppliedMutation % this.saveInformationInterval == 0)) {
					this.mutationListManager.generateMutationListFile();
				}
				LOGGER.info("Executing test(s) on {}", mutant.getAbsolutePath());

				// Create the patched/mutant file which will be used by the
				// proxy to replace the server .js file in the GET call
				if (!this.createMutantFile(numberOfAppliedMutation, original, mutant)) {
					continue;
				}

				// Execute the test case
				final TestExecutor targetTestExecutor = this.prioritizer.getTestExecutor(mutant, testExecutors);
				final Future<Boolean> future = executor.submit(new TestCallable(targetTestExecutor, mutant, description,
						numberOfAppliedMutation, numberOfMaxMutants));
				futures.add(future);
			}

			// execution can be canceled from outside.
			if (!this.conducting) {
				executor.shutdownNow();
				break;
			}
		}
		for (final Future<Boolean> future : futures) {
			try {
				future.get();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			} catch (final ExecutionException e) {
				e.printStackTrace();
			}
		}

		for (final String description : nameOfMutations) {
			for (final MutationFileInformation mutationFileInformation : this.mutationListManager
					.getMutationFileInformationList(description)) {
				LOGGER.info(mutationFileInformation.getState().toString());
			}
		}

		executor.shutdown();

		if (this.conducting) {
			commandReceiver.interrupt();
			this.conducting = false;
		}
		return numberOfAppliedMutation;
	}

	/**
	 * Task for running test cases on each mutant in a concurrent manner
	 *
	 * @author Yuta Maezawa
	 *
	 */
	public class TestCallable implements Callable<Boolean> {
		private final TestExecutor executor;
		private final MutationFileInformation mutant;
		private final String description;
		private final int numberOfAppliedMutation;
		private final int numberOfMaxMutants;

		public TestCallable(TestExecutor executor, MutationFileInformation mutant, String description,
				int numberOfAppliedMutation, int numberOfMaxMutants) {
			this.executor = executor;
			this.mutant = mutant;
			this.description = description;
			this.numberOfAppliedMutation = numberOfAppliedMutation;
			this.numberOfMaxMutants = numberOfMaxMutants;
		}

		@Override
		public Boolean call() throws Exception {
			boolean success;
			// Execute the call with an identifier to let the proxy know which
			// mutated file to replace
			final String mutationId = Integer.toString(this.numberOfAppliedMutation);
			if (this.executor.execute(mutationId)) {
				// Unkilled mutant
				synchronized (RichMutationTestConductor.this.unkilledMutantsInfo) {
					RichMutationTestConductor.this.unkilledMutantsInfo.put(this.description, this.mutant.toString());
				}
				// TODO check if this needs to be synchronized
				synchronized (LOGGER) {
					LOGGER.info("mutant {} is not be killed", this.description);
				}
				success = false;
			} else {
				// Killed mutant
				// TODO check if this needs to be synchronized
				synchronized (this.mutant) {
					this.mutant.setState(MutationFileInformation.State.KILLED);
				}
				success = true;
			}
			final String message = this.executor.getMessageOnLastExecution();
			if (message != null) {
				synchronized (LOGGER) {
					LOGGER.info(message);
				}
			}

			RichMutationTestConductor.this.removeMutantFile(this.numberOfAppliedMutation);
			RichMutationTestConductor.this.logProgress(this.numberOfAppliedMutation, this.numberOfMaxMutants);

			// TODO workaround for issue where a new test would make all current
			// running tests fail.
			// Block until all threads finish before continuing to run a new
			// test.
			RichMutationTestConductor.this.batchTestBarrier.await();
			RichMutationTestConductor.this.batchTestBarrier.reset();

			return success;
		}
	}

	// public void tryToKillSpecificMutant(String mutationFileName, TestExecutor
	// testExecutor) {
	// mutationListManager = new
	// MutationListManager(mutationFileWriter.getDestinationDirectory());
	// mutationListManager.readExistingMutationListFile();
	// checkIfSetuped();
	//
	// for (String description : mutationListManager.getListOfMutationName()) {
	// for (MutationFileInformation mutationFileInformation :
	// mutationListManager.getMutationFileInformationList(description)) {
	// if (mutationFileInformation.getFileName().equals(mutationFileName)) {
	// List<String> original = Util.readFromFile(pathToJsFile);
	// if (!applyMutationFile(original, mutationFileInformation)) {
	// return;
	// }
	// if (testExecutor.execute()) { // This mutants cannot be killed
	// LOGGER.info("mutant {} is not be killed", description);
	// } else {
	// mutationFileInformation.setState(MutationFileInformation.State.KILLED);
	// LOGGER.info("mutant {} is killed", description);
	// }
	// String message = testExecutor.getMessageOnLastExecution();
	// if (message != null) {
	// LOGGER.info(message);
	// }
	//
	// mutationListManager.generateMutationListFile();
	//
	// LOGGER.info("restoring backup file...");
	// Util.copyFile(pathToBackupFile(), context.getJsPath());
	// return;
	// }
	// }
	// }
	// LOGGER.error("No mutant found for name " + mutationFileName);
	// }

	/**
	 * Create a new file with the applied mutation/patch in the format:
	 * <filename>.<mutation_id>
	 *
	 * @param id
	 *            the unique identifier for the mutation
	 * @param original
	 *            the list of lines in the original .js file
	 * @param fileInfo
	 *            the information needed to mutate the file
	 * @return true if the file was successfully created
	 */
	protected boolean createMutantFile(long id, List<String> original, MutationFileInformation fileInfo) {
		final Patch patch = DiffUtils.parseUnifiedDiff(Util.readFromFile(fileInfo.getAbsolutePath()));
		try {
			@SuppressWarnings("unused")
			final List<?> mutated = patch.<String>applyTo(original);
			Util.writeToFile(this.pathToJsFile + "." + id,
					Util.join(mutated.toArray(new String[0]), System.lineSeparator()));
		} catch (final PatchFailedException e) {
			LOGGER.error("Applying mutation file '{}' failed.", fileInfo.getFileName(), e);
			return false;
		}
		return true;
	}

	public boolean removeMutantFile(long id) {
		final String mutantPath = this.pathToJsFile + "." + id;
		final File mutantFile = new File(mutantPath);
		return mutantFile.delete();
	}

	/**
	 * @author Yuta Maezawa
	 * @param original
	 * @param fileInfo
	 * @return
	 */
	protected boolean createMutationFile(List<String> original, MutationFileInformation fileInfo) {
		final Patch patch = DiffUtils.parseUnifiedDiff(Util.readFromFile(fileInfo.getAbsolutePath()));
		try {
			@SuppressWarnings("unused")
			final List<?> mutated = patch.<String>applyTo(original);
			final String[] pathHierarchyOfJsFile = this.pathToJsFile.split("/", 0);
			String newPathToJsFile = pathHierarchyOfJsFile[0];
			if (pathHierarchyOfJsFile.length != 0) {
				for (int i = 1; i < pathHierarchyOfJsFile.length - 1; i++) {
					newPathToJsFile = newPathToJsFile + "/" + pathHierarchyOfJsFile[i];
				}
			}
			final File testedDir = new File(newPathToJsFile + "/tested");
			if (!testedDir.exists()) {
				testedDir.mkdir();
			}
			newPathToJsFile = testedDir.getPath() + "/" + Util.getFileNameWithoutExtension(fileInfo.getFileName()) + "-"
					+ pathHierarchyOfJsFile[pathHierarchyOfJsFile.length - 1];
			Util.writeToFile(newPathToJsFile, Util.join(mutated.toArray(new String[0]), System.lineSeparator()));
		} catch (final PatchFailedException e) {
			LOGGER.error("Applying mutation file '{}' failed.", fileInfo.getFileName(), e);
			return false;
		}
		return true;
	}
}
