package jp.mzw.ajaxmutator.test.conductor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ArrayListMultimap;

import jp.mzw.ajaxmutator.generator.MutationFileInformation;
import jp.mzw.ajaxmutator.generator.MutationListManager;
import jp.mzw.ajaxmutator.prioritizer.Prioritizer;
import jp.mzw.ajaxmutator.sampling.Sampling;
import jp.mzw.ajaxmutator.test.executor.TestExecutor;
import jp.mzw.ajaxmutator.util.Util;
import jp.mzw.revajaxmutator.test.result.Coverage;

public class RichMutationTestConductor extends MutationTestConductor {
	protected static Logger LOGGER = LoggerFactory.getLogger(RichMutationTestConductor.class);

	/* --------------------------------------------------
	 * Fields
	 -------------------------------------------------- */

	protected Map<String, boolean[]> coverage;

	protected Sampling sampling;

	protected int numOfThreads = Runtime.getRuntime().availableProcessors();

	protected Prioritizer prioritizer;

	/* --------------------------------------------------
	 * Set up
	 -------------------------------------------------- */

	/**
	 * Utility function to instantiate {@code MutationTestConductorConcurrently} 
	 * 
	 * @param conductor 
	 * @return instance
	 */
	public static RichMutationTestConductor setup(MutationTestConductor conductor) {
		RichMutationTestConductor ret = new RichMutationTestConductor();
		ret.setup(conductor.getPathToJsFile(), "", conductor.getMutateVisitor());
		return ret;
	}

	public void setCoverageResults(HashMap<String, File> coverages) {
		this.coverage = Coverage.getCoverageInfo(coverages, this.pathToJsFile);
	}

	public void setSamplingStrategy(Sampling sampling) {
		this.sampling = sampling;
	}

	public void setThreadNum(int n) {
		if (0 < n) {
			numOfThreads = n;
		} else {
			throw new IllegalStateException("The number of thread should be possitive.");
		}
	}

	public void setPrioritizeStrategy(Prioritizer prioritizer) {
		this.prioritizer = prioritizer;
	}

	/* --------------------------------------------------
	 * Extensions
	 -------------------------------------------------- */

	/**
	 * Run test case on each mutants
	 * in a multiple-threads manner
	 * 
	 * @param testExecutors
	 * @param coverages
	 */
	public void mutationAnalysisUsingExistingMutations(List<TestExecutor> testExecutors) {
		mutationListManager = new MutationListManager(mutationFileWriter.getDestinationDirectory());
		mutationListManager.readExistingMutationListFile();
		unkilledMutantsInfo = ArrayListMultimap.create();

		checkIfSetuped();
		applyMutationAnalysis(testExecutors, Stopwatch.createStarted());
	}

	/**
	 * 
	 * @param testExecutors
	 * @param runningStopwatch
	 */
	protected void applyMutationAnalysis(List<TestExecutor> testExecutors, Stopwatch runningStopwatch) {
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

	/**
	 * Run test cases on each mutant
	 * in a concurrent manner.
	 * 
	 * @param testExecutors Executors of test cases
	 * @return The number of applied mutants
	 */
	protected int applyMutationAnalysis(final List<TestExecutor> testExecutors) {
		// Set up
		int numberOfAppliedMutation = 0;
		int numberOfMaxMutants = mutationListManager.getNumberOfUnkilledMutants();
		Thread commandReceiver = new Thread(new CommandReceiver());
		commandReceiver.start();
		List<String> original = Util.readFromFile(pathToJsFile);
		List<String> nameOfMutations = mutationListManager.getListOfMutationName();

		// Apply do-fewer approach
		sampling.sample(mutationListManager.getMutationFileInformationList());

		// Running test cases on each mutant in a multiple-threads manner
		ExecutorService executor = Executors.newFixedThreadPool(this.numOfThreads);
		List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
		for (String description : nameOfMutations) {
			LOGGER.info("Start applying {}", description);
			for (MutationFileInformation mutant : mutationListManager.getMutationFileInformationList(description)) {
				// execution can be canceled from outside.
				if (!conducting) {
					break;
				}
				// When mutant is not "live non-equivalent", skip to run test cases on it.
				if (mutant.canBeSkipped() || !applyMutationFile(original, mutant)) {
					continue;
				}
				// When test cases do not cover mutated locations of mutants, skip to run the test cases on the mutants
				if (!Coverage.isCovered(coverage, mutant.getStartLine(), mutant.getEndLine())) {
					LOGGER.info(mutant.getFileName() + " is skipped by coverage");
					continue;
				}

				// TODO 
				// if (!createMutationFile(original, mutant)) {
				//		continue;
				// }

				// Apply mutation sampling
				if (!sampling.isSampled(mutant)) {
					LOGGER.info(mutant.getFileName() + " is skipped by sampling");
					continue;
				}

				numberOfAppliedMutation++;
				if (numberOfAppliedMutation >= saveInformationInterval & (numberOfAppliedMutation % saveInformationInterval == 0)) {
					mutationListManager.generateMutationListFile();
				}
				LOGGER.info("Executing test(s) on {}", mutant.getAbsolutePath());

				TestExecutor targetTestExecutor = this.prioritizer.getTestExecutor(mutant, testExecutors);
				Future<Boolean> future = executor
						.submit(new TestCallable(targetTestExecutor, mutant, description, numberOfAppliedMutation, numberOfMaxMutants));
				futures.add(future);

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
		for (Future<Boolean> future : futures) {
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

	/**
	 * Task for running test cases on each mutant in a concurrent manner
	 * 
	 * @author Yuta Maezawa
	 *
	 */
	public class TestCallable implements Callable<Boolean> {
		private TestExecutor executor;
		private MutationFileInformation mutant;
		private String description;
		private int numberOfAppliedMutation;
		private int numberOfMaxMutants;

		public TestCallable(TestExecutor executor, MutationFileInformation mutant, String description, int numberOfAppliedMutation, int numberOfMaxMutants) {
			this.executor = executor;
			this.mutant = mutant;
			this.description = description;
			this.numberOfAppliedMutation = numberOfAppliedMutation;
			this.numberOfMaxMutants = numberOfMaxMutants;
		}

		@Override
		public Boolean call() throws Exception {
			boolean success;
			if (executor.execute()) { // This mutant cannot be killed
				synchronized (unkilledMutantsInfo) {
					unkilledMutantsInfo.put(description, mutant.toString());
				}
				synchronized (LOGGER) {
					LOGGER.info("mutant {} is not be killed", description);
				}
				success = false;
			} else {
				synchronized (mutant) {
					mutant.setState(MutationFileInformation.State.KILLED);
				}
				success = true;
			}
			String message = executor.getMessageOnLastExecution();
			if (message != null) {
				LOGGER.info(message);
			}
			logProgress(numberOfAppliedMutation, numberOfMaxMutants);
			return success;
		}
	}

	//	public void tryToKillSpecificMutant(String mutationFileName, TestExecutor testExecutor) {
	//		mutationListManager = new MutationListManager(mutationFileWriter.getDestinationDirectory());
	//		mutationListManager.readExistingMutationListFile();
	//		checkIfSetuped();
	//
	//		for (String description : mutationListManager.getListOfMutationName()) {
	//			for (MutationFileInformation mutationFileInformation : mutationListManager.getMutationFileInformationList(description)) {
	//				if (mutationFileInformation.getFileName().equals(mutationFileName)) {
	//					List<String> original = Util.readFromFile(pathToJsFile);
	//					if (!applyMutationFile(original, mutationFileInformation)) {
	//						return;
	//					}
	//					if (testExecutor.execute()) { // This mutants cannot be killed
	//						LOGGER.info("mutant {} is not be killed", description);
	//					} else {
	//						mutationFileInformation.setState(MutationFileInformation.State.KILLED);
	//						LOGGER.info("mutant {} is killed", description);
	//					}
	//					String message = testExecutor.getMessageOnLastExecution();
	//					if (message != null) {
	//						LOGGER.info(message);
	//					}
	//
	//					mutationListManager.generateMutationListFile();
	//
	//					LOGGER.info("restoring backup file...");
	//					Util.copyFile(pathToBackupFile(), context.getJsPath());
	//					return;
	//				}
	//			}
	//		}
	//		LOGGER.error("No mutant found for name " + mutationFileName);
	//	}

	//	protected boolean createMutationFile(List<String> original, MutationFileInformation fileInfo) {
	//		Patch patch = DiffUtils.parseUnifiedDiff(Util.readFromFile(fileInfo.getAbsolutePath()));
	//		try {
	//			@SuppressWarnings("unused")
	//			List<?> mutated = patch.<String> applyTo(original);
	//			String[] pathHierarchyOfJsFile = pathToJsFile.split("/", 0);
	//			String newPathToJsFile = pathHierarchyOfJsFile[0];
	//			if (pathHierarchyOfJsFile.length != 0) {
	//				for (int i = 1; i < pathHierarchyOfJsFile.length - 1; i++) {
	//					newPathToJsFile = newPathToJsFile + "/" + pathHierarchyOfJsFile[i];
	//				}
	//			}
	//			File testedDir = new File(newPathToJsFile + "/tested");
	//			if (!testedDir.exists()) {
	//				testedDir.mkdir();
	//			}
	//			newPathToJsFile = testedDir.getPath() + "/" + Util.getFileNameWithoutExtension(fileInfo.getFileName()) + "-"
	//					+ pathHierarchyOfJsFile[pathHierarchyOfJsFile.length - 1];
	//			Util.writeToFile(newPathToJsFile, Util.join(mutated.toArray(new String[0]), System.lineSeparator()));
	//		} catch (PatchFailedException e) {
	//			LOGGER.error("Applying mutation file '{}' failed.", fileInfo.getFileName(), e);
	//			return false;
	//		}
	//		return true;
	//	}

}
