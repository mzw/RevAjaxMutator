package jp.mzw.revajaxmutator.fixer;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
import jp.mzw.ajaxmutator.detector.dom.RemoveChildDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryRemoveDetector;
import jp.mzw.ajaxmutator.mutatable.DOMRemoval;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutator.Mutator;
import jp.mzw.ajaxmutator.mutator.MutatorTestBase;

public class DOMRemovalToNoOpFixerTest extends MutatorTestBase {
	@Override
	protected void prepare() {
		inputs = new String[] {
				"document.getElementById('hoge').removeChild(bar);",
				"// some comment here" + System.lineSeparator(), 
				"abc.removeChild(document.getElementByTagName('abc'));", 
				"elm.find('p').remove();"
		};
		
		MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
		builder.setDomRemovalDetectors(ImmutableSet.of(new RemoveChildDetector(), new JQueryRemoveDetector()));
		visitor = builder.build();
	}
	
	@Test
    public void testDOMRemovalToNoOpMutator() {
		Set<DOMRemoval> domRemovals = visitor.getDomRemovals();
		Mutator<DOMRemoval> mutator = new DOMRemovalToNoOpFixer();
		List<Mutation> mutationList = mutator.generateMutationList(Iterables.get(domRemovals, 0));
		assertEquals(inputs[0],
				mutationList.get(0).getOriginalNode().toSource().trim());
		assertEquals(ReplacingToNoOpFixer.NO_OPERATION_STR, mutationList.get(0).getMutatingContent());
		mutationList = mutator.generateMutationList(Iterables.get(domRemovals, 1));
		assertEquals(inputs[2],
				mutationList.get(0).getOriginalNode().toSource().trim());
		assertEquals(ReplacingToNoOpFixer.NO_OPERATION_STR, mutationList.get(0).getMutatingContent());
		mutationList = mutator.generateMutationList(Iterables.get(domRemovals, 2));
		assertEquals(ReplacingToNoOpFixer.NO_OPERATION_STR, mutationList.get(0).getMutatingContent());
	}
}
