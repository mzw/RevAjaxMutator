package jp.mzw.revajaxmutator.fixer;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
import jp.mzw.ajaxmutator.detector.dom.DOMNormalizationDetector;
import jp.mzw.ajaxmutator.mutatable.DOMNormalization;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutator.Mutator;
import jp.mzw.ajaxmutator.mutator.MutatorTestBase;

import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DOMNormalizationToNoOpFixerTest extends MutatorTestBase{
	@Override
	protected void prepare() {
		inputs = new String[] {
				"document.findElementById('foo').normalize();",
		};
		
		MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
		builder.setDomNormalizationDetectors(ImmutableSet.of(new DOMNormalizationDetector()));
		visitor = builder.build();
	}

	@Test
	public void testDOMCloningToNoOpFixer() {
		Set<DOMNormalization> normalizations = visitor.getDomNormalizations();
		Mutator<DOMNormalization> mutator = new DOMNormalizationToNoOpFixer();
		List<Mutation> mutationList = mutator.generateMutationList(Iterables.get(normalizations, 0));
		assertEquals(inputs[0], mutationList.get(0).getOriginalNode().toSource().trim());
		assertEquals(ReplacingToNoOpFixer.NO_OPERATION_STR, mutationList.get(0).getMutatingContent());
	}
}
