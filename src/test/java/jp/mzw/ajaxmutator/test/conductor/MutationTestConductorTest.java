package jp.mzw.ajaxmutator.test.conductor;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.mutator.Mutator;

public class MutationTestConductorTest {
	
	@Test
	public void testGetDefaultMutators() {
		MutateVisitor visitor = MutateVisitor.defaultBuilder().build();
		ImmutableSet<Mutator<?>> mutators = MutationTestConductor.defaultMutators(visitor);
		Assert.assertEquals(10, mutators.size());
	}

}
