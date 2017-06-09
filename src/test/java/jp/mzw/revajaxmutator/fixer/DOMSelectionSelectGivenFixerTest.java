package jp.mzw.revajaxmutator.fixer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
import jp.mzw.ajaxmutator.detector.dom.DOMSelectionDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryDOMSelectionDetector;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.DOMSelection;
import jp.mzw.ajaxmutator.mutator.Mutator;
import jp.mzw.ajaxmutator.mutator.MutatorTestBase;

public class DOMSelectionSelectGivenFixerTest extends MutatorTestBase{
	private String[] selectors;
	private String[] operations;
	
	@Override
	public void prepare() {
		selectors = new String[] {"document.getElementById('piyo')", "$('#abc')"};
		operations = new String[] {".className = 'abc';", ".attr('id', 'another');"};
		inputs = new String[2];
		for (int i = 0; i < 2; i++) {
			inputs[i] = selectors[i] + operations[i];
		}
		MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
		builder.setDomSelectionDetectors(ImmutableSet.of(
				new DOMSelectionDetector(), new JQueryDOMSelectionDetector()));
		visitor = builder.build();
	}
	
	@Test
	public void testDOMSelectionSelectGivenFixer() {
		String[] expectedTarget = {selectors[0], selectors[1]};
		String[] expectedMutatingContents = {
				"document.getElementById(document.getElementById('piyo'))", "$($('#abc'))"};
		
		Collection<DOMSelection> domSelections = visitor.getDomSelections();
		for (int i = 0; i < 2; i++) {
			Mutator<DOMSelection> fixer = new DOMSelectionSelectGivenFixer(selectors[i]);
			List<Mutation> mutationList = new ArrayList<Mutation>();
			mutationList = fixer.generateMutationList(Iterables.get(domSelections, i));
			assertEquals(expectedTarget[i], mutationList.get(0).getOriginalNode().toSource());
			assertEquals(expectedMutatingContents[i], mutationList.get(0).getMutatingContent());
		}
	}
}
