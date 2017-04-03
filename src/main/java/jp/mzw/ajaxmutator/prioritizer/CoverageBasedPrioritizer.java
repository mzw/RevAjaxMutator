package jp.mzw.ajaxmutator.prioritizer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import jp.mzw.ajaxmutator.generator.MutationFileInformation;
import jp.mzw.ajaxmutator.test.executor.TestExecutor;
import jp.mzw.ajaxmutator.util.Util;
import jp.mzw.revajaxmutator.test.result.Coverage;

public class CoverageBasedPrioritizer extends Prioritizer {
	protected static Logger LOGGER = LoggerFactory.getLogger(CoverageBasedPrioritizer.class);

	protected Map<String, boolean[]> coverage;
	
	public CoverageBasedPrioritizer(Map<String, boolean[]> coverage) {
		this.coverage = coverage;
	}

	/**
	 * 
	 * @param mutant
	 * @return
	 */
	public List<String> getOrderedMethodNames(MutationFileInformation mutant) {
		// Create
		Map<String, Integer> map = Maps.newHashMap();
		for (Map.Entry<String, boolean[]> entry : coverage.entrySet()) {
			String methodName = entry.getKey();
			if (!Coverage.isCovered(coverage, mutant.getStartLine(), mutant.getEndLine(), methodName)) {
				continue;
			}
			int count = 0;
			for (int line = 1; line < mutant.getStartLine(); line++) {
				if (entry.getValue()[line]) {
					count++;
				}
			}
			map.put(methodName, new Integer(count));
		}
		
		// Sort
		List<Entry<String, Integer>> entries = Lists.newArrayList(map.entrySet());
		Collections.sort(entries, new Comparator<Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		// Return
		List<String> orderedMethodNames = Lists.newArrayList();
		int i = 1;
		for (Entry<String, Integer> e : entries) {
			orderedMethodNames.add(e.getKey());
			LOGGER.info("<{}> : [{}]methodName = {} , coverage = {}", mutant.getFileName(), i, e.getKey(), e.getValue());
			i++;
		}

		return orderedMethodNames;
	}

	public TestExecutor getTargetTestExecutor(List<TestExecutor> executors, String mutantname) {
		for (TestExecutor executor : executors) {
			if (executor.getTargetClassName().contains(mutantname)) {
				return executor;
			}
		}
		return null;
	}

	@Override
	public TestExecutor getTestExecutor(MutationFileInformation mutant, List<TestExecutor> testExecutors) {
		List<String> orderedMethodNames = getOrderedMethodNames(mutant);
		String mutantname = Util.getFileNameWithoutExtension(mutant.getFileName());
		TestExecutor targetTestExecutor = getTargetTestExecutor(testExecutors, mutantname);
		targetTestExecutor.setOrderdMethodNames(orderedMethodNames);
		return targetTestExecutor;
	}
}
