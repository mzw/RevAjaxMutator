package jp.mzw.ajaxmutator.mutator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
import jp.mzw.ajaxmutator.detector.dom.DOMNormalizationDetector;
import jp.mzw.ajaxmutator.mutatable.DOMNormalization;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutator.replace.nop.DOMNormalizationToNoOpMutator;
import jp.mzw.ajaxmutator.mutator.replace.nop.ReplacingToNoOpMutator;

import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DOMNormalizationMutatorTest extends MutatorTestBase {
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
    public void testDOMCloningToNoOpMutator() {
        Set<DOMNormalization> normalizations = visitor.getDomNormalizations();
        Mutator<DOMNormalization> mutator = new DOMNormalizationToNoOpMutator();
        List<Mutation> mutationList= mutator.generateMutationList(Iterables.get(normalizations, 0));
        assertEquals(inputs[0], mutationList.get(0).getOriginalNode().toSource().trim());
        assertEquals(ReplacingToNoOpMutator.NO_OPERATION_STR, mutationList.get(0).getMutatingContent());
    }
}
