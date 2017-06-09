package jp.mzw.ajaxmutator.sampling;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.mzw.ajaxmutator.generator.MutationFileInformation;

public abstract class Sampling {
	protected static Logger LOGGER = LoggerFactory.getLogger(Sampling.class);

	/**
	 *
	 * @author Yuta Maezawa
	 *
	 */
	public enum Strategy {
		EventHandler,
	}

	/**
	 *
	 * @param strategy
	 * @return
	 */
	public static Sampling getSampling(Strategy strategy) {
		switch (strategy) {
		case EventHandler:
			return new EventHandlerSampling();
		}
		return null;
	}

	protected List<MutationFileInformation> samples;

	/**
	 * Sampling mutants according to given strategy
	 *
	 * @param mutants
	 *            Containing original mutants
	 * @return List of sampled mutants
	 */
	public abstract List<MutationFileInformation> sample(List<MutationFileInformation> mutants);

	/**
	 * Determine whether given mutant is sampled or not
	 *
	 * @param mutant
	 *            is that in original list
	 * @return true if given mutant is in samples, otherwise false
	 */
	public boolean isSampled(MutationFileInformation mutant) {
		if (this.samples == null) {
			LOGGER.warn("No sampling strategies were applied.");
			return true;
		}
		for (final MutationFileInformation sample : this.samples) {
			if (sample.getFileName().equals(mutant.getFileName())) {
				return true;
			}
		}
		LOGGER.info(mutant.getFileName() + " is skipped by do fewer");
		return false;
	}
}
