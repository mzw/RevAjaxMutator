package jp.mzw.ajaxmutator.sampling;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import jp.mzw.ajaxmutator.generator.MutationFileInformation;
import jp.mzw.ajaxmutator.mutatable.EventAttachment;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.EventTargetRAMutator;

public class EventHandlerSampling extends Sampling {
	protected static final Logger LOGGER = LoggerFactory.getLogger(EventHandlerSampling.class);

	/**
	 * 1. EventTargetRAMutator.class
	 * 2. EventTypeRAMutator.class
	 * 3. EventCallbackRAMutator.class
	 */
	private Class<? extends AbstractMutator<EventAttachment>> preference;

	/**
	 * Constructor
	 */
	public EventHandlerSampling() {
		this.preference = EventTargetRAMutator.class; // default
	}

	/**
	 * Constructor with preference
	 * 
	 * @param preference Give null to randomly sample mutants
	 */
	public EventHandlerSampling(Class<? extends AbstractMutator<EventAttachment>> preference) {
		this.preference = preference;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Our assumption is that faults at the same event handler may cause the same failures.
	 */
	@Override
	public List<MutationFileInformation> sample(List<MutationFileInformation> all) {
		// Collect mutations related to Ajax event-driven feature
		List<MutationFileInformation> mutants = Lists.newArrayList();
		for (MutationFileInformation candidate : all) {
			String mutatable = candidate.getMutatable();
			// Check whether candidate is target or not
			if (mutatable.equals(EventAttachment.class.getSimpleName())) {
				mutants.add(candidate);
			}
		}
		// To get mutants by their start-line number
		Map<Integer, List<MutationFileInformation>> mutantsByLine = Maps.newHashMap();
		for (MutationFileInformation mutant : mutants) {
			Integer startLine = mutant.getStartLine();
			if (mutantsByLine.containsKey(startLine)) {
				mutantsByLine.get(startLine).add(mutant);
			} else {
				List<MutationFileInformation> ls = Lists.newArrayList();
				ls.add(mutant);
				mutantsByLine.put(startLine, ls);
			}
		}
		// Sample mutants based on start-line number
		List<MutationFileInformation> samples = Lists.newArrayList();
		for (Map.Entry<Integer, List<MutationFileInformation>> entry : mutantsByLine.entrySet()) {
			List<MutationFileInformation> candidates = entry.getValue();
			if (candidates.size() == 1) { // Sample this candidate
				samples.add(candidates.get(0));
			} else if (1 < candidates.size()) { // Sample one mutant these among candidates
				// If preference is given
				MutationFileInformation preferred = getPreferenceAmong(candidates);
				if (preferred != null) {
					samples.add(preferred);
				}
				// Otherwise, randomly sampling
				else {
					int index = new Random().nextInt(candidates.size());
					samples.add(candidates.get(index));
				}
			}
		}
		return samples;
	}

	/**
	 * 
	 * @param mutants
	 * @return
	 */
	private MutationFileInformation getPreferenceAmong(List<MutationFileInformation> mutants) {
		if (this.preference == null) {
			return null;
		}
		String name = this.preference.getSimpleName().replace("Mutator", "Mutation"); // TODO Remove replace part
		for (MutationFileInformation mutant : mutants) {
			if (mutant.getFixer().equals(name)) {
				return mutant; // TODO Currently returns first preferred mutant, but want to randomly sample a mutant among those preferred.
			}
		}
		return null;
	}

}
