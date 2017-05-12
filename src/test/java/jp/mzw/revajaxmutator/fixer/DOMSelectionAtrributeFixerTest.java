package jp.mzw.revajaxmutator.fixer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
import jp.mzw.ajaxmutator.detector.dom.DOMSelectionDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryDOMSelectionDetector;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.DOMSelection;
import jp.mzw.ajaxmutator.mutator.Mutator;
import jp.mzw.ajaxmutator.mutator.MutatorTestBase;
import jp.mzw.revajaxmutator.parser.RepairSource;

public class DOMSelectionAtrributeFixerTest extends MutatorTestBase {
	private String[] selectors;
	private String[] operations;
	
	@Override
	protected void prepare() {
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
	public void testDOMSelectionAtrributeFixer() {
		String[] expectedTarget = {"'piyo'", "'#abc'"};
		String[] expectedMutatingContents = {"'#abc'", "'piyo'"};
		
		Set<DOMSelection> domSelections = visitor.getDomSelections();
		Collection<? extends RepairSource> repairSources = Sets.newHashSet();
		Mutator<DOMSelection> fixer = new DOMSelectionAtrributeFixer(domSelections, repairSources);
		List<Mutation> mutationList;
		for (int i = 0; i < 2; i++) {
			mutationList = fixer.generateMutationList(Iterables.get(domSelections, i));
			assertEquals(expectedTarget[i], mutationList.get(0).getOriginalNode().toSource());
			assertEquals(expectedMutatingContents[i], mutationList.get(0).getMutatingContent());
		}
	}
}
