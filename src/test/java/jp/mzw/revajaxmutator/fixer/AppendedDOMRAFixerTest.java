package jp.mzw.revajaxmutator.fixer;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
import jp.mzw.ajaxmutator.detector.dom.AppendChildDetector;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.DOMAppending;
import jp.mzw.ajaxmutator.mutator.Mutator;
import jp.mzw.ajaxmutator.mutator.MutatorTestBase;
import jp.mzw.revajaxmutator.parser.RepairSource;

public class AppendedDOMRAFixerTest  extends MutatorTestBase {
	private String[] appendTo;
	private String[] appendedElements;
	
	@Override
	protected void prepare() {
		appendTo = new String[] {"element", "document.getElementByID('hoge')"};
		appendedElements = new String[] {"document.createElement('p')", "elm"};
		inputs = new String[2];
		for (int i = 0; i < 2; i++) {
			inputs[i] = appendChild(appendTo[i], appendedElements[i]);
		}
		MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
		builder.setDomAppendingDetectors(ImmutableSet.of(new AppendChildDetector()));
		visitor = builder.build();
	}
	
	@Test
	public void testAppendedDOMRAFixer() {
		Collection<? extends RepairSource> repairSources = Sets.newHashSet();
		Collection<DOMAppending> domAppendings = visitor.getDomAppendings();
		Mutator<DOMAppending> fixer = new AppendedDOMRAFixer(visitor.getDomAppendings(), repairSources);
		List<Mutation> mutationList = fixer.generateMutationList(Iterables.get(domAppendings, 0));
		assertEquals("elm", mutationList.get(0).getMutatingContent());
		mutationList = fixer.generateMutationList(Iterables.get(domAppendings,  1));
		assertEquals("document.createElement('p')", mutationList.get(0).getMutatingContent());
	}
	
	private String appendChild(String appendTo, String appendedElement) {
		return appendTo + ".appendChild(" + appendedElement + ");";
	}
}
