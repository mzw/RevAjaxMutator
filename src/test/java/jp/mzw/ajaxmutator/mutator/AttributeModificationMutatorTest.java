package jp.mzw.ajaxmutator.mutator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
import jp.mzw.ajaxmutator.detector.dom.AttributeAssignmentDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryAttributeModificationDetector;
import jp.mzw.ajaxmutator.mutatable.AttributeModification;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutator.replace.among.AttributeModificationTargetRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.AttributeModificationValueRAMutator;

import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AttributeModificationMutatorTest extends MutatorTestBase {
    private String[] targetAttributes;
    private String[] assignedValues;

    @Override
    public void prepare() {
        MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
        builder.setAttributeModificationDetectors(ImmutableSet.of(
                new AttributeAssignmentDetector(),
                new JQueryAttributeModificationDetector()));
        visitor = builder.build();
        targetAttributes = new String[] { "id", "hidden" };
        assignedValues = new String[] { "hoge", "'true'" };
        inputs = new String[2];
        inputs[0] = getJQueryAssignment(targetAttributes[0], assignedValues[0]);
        inputs[1] = getAssignment(targetAttributes[1], assignedValues[1]);
    }

    @Test
    public void testAttributeModificationAttributeRAMutator() {
        Collection<AttributeModification> attributeModifications
                = visitor.getAttributeModifications();
        Mutator<AttributeModification> mutator = new AttributeModificationTargetRAMutator(
                attributeModifications);
        AttributeModification modification;
        modification = Iterables.get(attributeModifications, 0);
        List<Mutation> mutationList= mutator.generateMutationList(modification);
        assertEquals(targetAttributes[1], mutationList.get(0).getMutatingContent());
        modification = Iterables.get(attributeModifications, 1);
        mutationList = mutator.generateMutationList(modification);
        assertEquals(targetAttributes[0], mutationList.get(0).getMutatingContent());
    }

    @Test
    public void testAttributeModificationValueRAMutator() {
        Collection<AttributeModification> attributeModifications
                = visitor.getAttributeModifications();
        Mutator<AttributeModification> mutator = new AttributeModificationValueRAMutator(
                attributeModifications);
        AttributeModification modification;
        modification = Iterables.get(attributeModifications, 0);
        List<Mutation> mutationList = mutator.generateMutationList(modification);
        assertEquals(assignedValues[1], mutationList.get(0).getMutatingContent());
        modification = Iterables.get(attributeModifications, 1);
        mutationList = mutator.generateMutationList(modification);
        assertEquals(assignedValues[0],mutationList.get(0).getMutatingContent());
    }

    private String getJQueryAssignment(String attribute, String value) {
        return "$('#hoge').attr(" + attribute + ", " + value + ");";
    }

    private String getAssignment(String attribute, String value) {
        return "element." + attribute + " = " + value + ";";
    }
}
