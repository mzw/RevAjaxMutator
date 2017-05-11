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
import jp.mzw.ajaxmutator.util.Randomizer;

public class DOMSelectionSelectNearbyFixerTest extends MutatorTestBase{
	private String[] selectors;
	private String[] operations;
	
	@Override
	public void prepare() {
		selectors = new String[] {
				"document.getElementById('piyo')", "$('#abc')", "($('.hoge'))"};
		operations = new String[] {
				".className = 'abc';", ".attr('id', 'another');", ".hide();"};
		inputs = new String[4];
		for (int i = 0; i < 3; i++)
			inputs[i] = selectors[i] + operations[i];
		inputs[3] = "var hoge = $('#fuga');";
		MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
		builder.setDomSelectionDetectors(ImmutableSet.of(
				new DOMSelectionDetector(), new JQueryDOMSelectionDetector()));
		visitor = builder.build();
		
		Randomizer.initializeWithMockValues(new double[] {0.8, 0.2, 0.7, 0.2});
	}
	
	@AfterClass
	public static void resetRandomizer() {
		Randomizer.setMockMode(false);
	}
	
	@Test
	public void testDOMSelectionSelectNearbyFixerTest() {
		String[] expectedTarget = {selectors[0], selectors[1], "$('.hoge')", "$('#fuga')"};
		String[] expectedMutatingContents = {
				"(document.getElementById('piyo')).children[0]",
				"($('#abc')).parent()",
				"($('.hoge')).children(':first')",
				"($('#fuga')).parent()"};
		
		Collection<DOMSelection> domSelections = visitor.getDomSelections();
		Mutator<DOMSelection> fixer = new DOMSelectionSelectNearbyFixer();
		List<Mutation> mutationList = new ArrayList<Mutation>();
		for (int i = 0; i < 4; i++) {
			mutationList = fixer.generateMutationList(Iterables.get(domSelections, i));
			assertEquals(expectedTarget[i], mutationList.get(0).getOriginalNode().toSource());
			assertEquals(expectedMutatingContents[i], mutationList.get(0).getMutatingContent());
		}
	}
}
