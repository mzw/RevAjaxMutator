package jp.mzw.ajaxmutator.prioritizer;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import jp.mzw.ajaxmutator.generator.MutationFileInformation;
import jp.mzw.ajaxmutator.test.executor.TestExecutor;
import jp.mzw.revajaxmutator.test.result.Coverage;

/**
 * TODO
 *
 * @author Yuta Maezawa
 *
 */
public class CoverageBasedPrioritizer extends Prioritizer {
	protected static Logger LOGGER = LoggerFactory.getLogger(CoverageBasedPrioritizer.class);

	protected Map<File, boolean[]> coverages;

	protected int pointer;

	public CoverageBasedPrioritizer() {
	}

	@Override
	public Prioritizer setParameters(Object... params) {
		this.coverages = (Map<File, boolean[]>) params[0];
		return this;
	}

	/**
	 *
	 * @param mutant
	 * @return
	 */
	public List<String> getOrderedMethodNames(MutationFileInformation mutant) {
		// Create
		final Map<String, Integer> map = Maps.newHashMap();
		for (final Map.Entry<File, boolean[]> entry : this.coverages.entrySet()) {
			final String methodName = Coverage.getTestMethodName(entry.getKey());
			if (!Coverage.isCovered(this.coverages, mutant.getStartLine(), mutant.getEndLine(), methodName)) {
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
		final List<Entry<String, Integer>> entries = Lists.newArrayList(map.entrySet());
		Collections.sort(entries, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

		// Return
		final List<String> orderedMethodNames = Lists.newArrayList();
		int i = 1;
		for (final Entry<String, Integer> e : entries) {
			orderedMethodNames.add(e.getKey());
			LOGGER.info("<{}> : [{}]methodName = {} , coverage = {}", mutant.getFileName(), i, e.getKey(),
					e.getValue());
			i++;
		}

		return orderedMethodNames;
	}

	public TestExecutor getTargetTestExecutor(List<TestExecutor> executors, String mutationType) {
		for (final TestExecutor executor : executors) {
			System.out.println(mutationType + " " + executor.getMutationFixAssignment());
			if (mutationType.contains(executor.getMutationFixAssignment())) {
				return executor;
			}
		}
		return null;
	}

	// Get executor according to type of mutation
	@Override
	public TestExecutor getTestExecutor(MutationFileInformation mutant, List<TestExecutor> executors) {
		final List<String> orderedMethodNames = this.getOrderedMethodNames(mutant);
		// final String mutationType = mutant.getFixer();
		// TODO this is just returning a random element in the list
		// Util.getFileNameWithoutExtension(mutant.getFileName());
		// final TestExecutor executor = this.getTargetTestExecutor(executors,
		// mutationType);
		final TestExecutor executor = executors.get(this.pointer % executors.size());
		this.pointer++;
		executor.setOrderedMethodNames(orderedMethodNames);
		return executor;
	}
}
