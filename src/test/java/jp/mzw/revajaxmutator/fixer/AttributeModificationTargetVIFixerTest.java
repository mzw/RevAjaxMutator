package jp.mzw.revajaxmutator.fixer;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
import jp.mzw.ajaxmutator.detector.dom.AttributeAssignmentDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryAttributeModificationDetector;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.AttributeModification;
import jp.mzw.ajaxmutator.mutator.Mutator;
import jp.mzw.ajaxmutator.mutator.MutatorTestBase;
import jp.mzw.revajaxmutator.parser.RepairSource;

public class AttributeModificationTargetVIFixerTest extends MutatorTestBase {
	private String[] targetAttributes;
	private String[] assignedValues;
	
	@Override
	public void prepare() {
		MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
		builder.setAttributeModificationDetectors(ImmutableSet.of(
				new AttributeAssignmentDetector(), new JQueryAttributeModificationDetector()));
		visitor = builder.build();
		targetAttributes = new String[] { "id", "hidden" };
		assignedValues = new String[] { "hoge", "'true'" };
		inputs = new String[2];
		inputs[0] = getJQueryAssignment(targetAttributes[0], assignedValues[0]);
		inputs[1] = getAssignment(targetAttributes[1], assignedValues[1]);
	}
	
	@Test
	public void testAttributeModificationTargetVIFixer() {
		Collection<? extends RepairSource> repairSources = Sets.newHashSet();
		Collection<AttributeModification> attributeModifications = visitor.getAttributeModifications();
		Mutator<AttributeModification> fixer = 
				new AttributeModificationTargetVIFixer(attributeModifications, repairSources);
		AttributeModification modification;
		modification = Iterables.get(attributeModifications, 0);
		List<Mutation> mutationList= fixer.generateMutationList(modification);
		assertEquals(targetAttributes[1], mutationList.get(0).getMutatingContent());
		modification = Iterables.get(attributeModifications, 1);
		mutationList = fixer.generateMutationList(modification);
		assertEquals(targetAttributes[0], mutationList.get(0).getMutatingContent());
	}
	
	private String getJQueryAssignment(String attribute, String value) {
		return "$('#hoge').attr(" + attribute + ", " + value + ");";
	}
	
	private String getAssignment(String attribute, String value) {
		return "element." + attribute + " = " + value + ";";
	}
}
