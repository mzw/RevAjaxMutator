package jp.mzw.ajaxmutator.prioritizer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.mzw.ajaxmutator.generator.MutationFileInformation;
import jp.mzw.ajaxmutator.test.executor.TestExecutor;

public abstract class Prioritizer {
	protected static Logger LOGGER = LoggerFactory.getLogger(Prioritizer.class);
	
	/**
	 * 
	 * @author Yuta Maezawa
	 *
	 */
	public enum Strategy {
		Coverage,
	}
	
	/**
	 * 
	 * @param strategy
	 * @return
	 */
	public static Prioritizer getPrioritizer(Strategy strategy) {
		switch (strategy) {
		case Coverage:
			return new CoverageBasedPrioritizer();
		}
		return null;
	}

	public abstract TestExecutor getTestExecutor(MutationFileInformation mutant, List<TestExecutor> executors);
	public abstract Prioritizer setParameters(Object...params);
	
}
