package jp.mzw.ajaxmutator.sampling;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;

import jp.mzw.ajaxmutator.generator.MutationFileInformation;
import jp.mzw.ajaxmutator.generator.MutationListManager;

public class EventHandlerSamplingTest {
	
	private static Sampling sampling;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		sampling = new EventHandlerSampling();
	}
	
	@Test
	public void testEventHandlerSampling() {
		Assert.assertNotNull(sampling);
	}
	
	@Test
	public void testSample() {
		MutationListManager manager = new MutationListManager("src/test/resources/record-test/quizzy/mutants");
		manager.readExistingMutationListFile();
		
		List<MutationFileInformation> mutants = manager.getMutationFileInformationList();
		List<MutationFileInformation> clones = clone(mutants);
		List<MutationFileInformation> samples = sampling.sample(clones);
		
		Assert.assertTrue(samples.size() <= mutants.size());
	}	

	private static <T> List<T> clone(List<T> list) {
		List<T> ret = Lists.newArrayList();
		for (T item : list) {
			ret.add(item);
		}
		return ret;
	}
	
	// TODO with sampling preference
}
