package jp.mzw.ajaxmutator.prioritizer;

import java.util.List;

import jp.mzw.ajaxmutator.generator.MutationFileInformation;
import jp.mzw.ajaxmutator.test.executor.TestExecutor;

public abstract class Prioritizer {

	public abstract TestExecutor getTestExecutor(MutationFileInformation mutant, List<TestExecutor> executors);
	
}
