package jp.mzw.ajaxmutator.test.conductor;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import jp.mzw.ajaxmutator.generator.MutationFileInformation;
import jp.mzw.ajaxmutator.generator.MutationListManager;
import jp.mzw.ajaxmutator.test.executor.JUnitExecutor;

public class MutationTestConductorConcurrently extends MutationTestConductor {
	protected static Logger LOGGER = LoggerFactory.getLogger(MutationTestConductorConcurrently.class);
	
	public static MutationTestConductorConcurrently setup(MutationTestConductor conductor) {
		MutationTestConductorConcurrently ret = new MutationTestConductorConcurrently();
		ret.setup(conductor.getPathToJsFile(), "", conductor.getMutateVisitor());
		return ret;
	}
	
	public void mutationAnalysisUsingExistingMutations(int numOfThreads, JUnitExecutor executor) {
		mutationListManager = new MutationListManager(mutationFileWriter.getDestinationDirectory());
		mutationListManager.readExistingMutationListFile();
		checkIfSetuped();
		
		List<Callable<Task>> tasks = Lists.newArrayList();
		for (MutationFileInformation mutant : mutationListManager.getMutationFileInformationList()) {
			tasks.add(new Task(this, executor, mutant));
		}

		ExecutorService service = Executors.newFixedThreadPool(numOfThreads);
		try {
			List<Future<Task>> futures = service.invokeAll(tasks);
			Task task = null;
			for (Future<Task> future : futures) {
				task = future.get();
			}
			LOGGER.info("{}", task);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			if (service != null) {
				service.shutdown();
			}
		} finally {
			if (service != null) {
				service.shutdown();
			}
		}
		
	}

	protected static class Task implements Callable<Task> {
		private MutationTestConductor conductor;
		private JUnitExecutor executor;
		private MutationFileInformation mutant;
		
		public Task(MutationTestConductor conductor, JUnitExecutor executor, MutationFileInformation mutant) {
			this.conductor = conductor;
			this.executor = executor;
			this.mutant = mutant;
		}
		
		@Override
		public Task call() throws Exception {
			this.conductor.tryToKillSpecificMutant(mutant.getFileName(), this.executor);
			return this;
		}
	}
	
}
