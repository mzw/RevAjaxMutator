package jp.mzw.ajaxmutator.mutator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
import jp.mzw.ajaxmutator.detector.dom.CloneNodeDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryCloneDetector;
import jp.mzw.ajaxmutator.mutatable.DOMCloning;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutator.replace.nop.DOMCloningToNoOpMutator;
import jp.mzw.ajaxmutator.mutator.replace.nop.ReplacingToNoOpMutator;

import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DOMCloningMutatorTest extends MutatorTestBase {
    @Override
    protected void prepare() {
        inputs = new String[] {
                "$elm.clone();",
                "// some comment here" + System.lineSeparator(),
                "elm.cloneNode(false);",
        };

        MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
        builder.setDomCloningDetectors(ImmutableSet.of(new CloneNodeDetector(), new JQueryCloneDetector()));
        visitor = builder.build();
    }

    @Test
    public void testDOMCloningToNoOpMutator() {
        Set<DOMCloning> domClonings = visitor.getDomClonings();
        Mutator<DOMCloning> mutator = new DOMCloningToNoOpMutator();
        List<Mutation> mutationList = mutator.generateMutationList(Iterables.get(domClonings, 0));
        assertEquals(inputs[0], mutationList.get(0).getOriginalNode().toSource().trim());
        assertEquals(ReplacingToNoOpMutator.NO_OPERATION_STR, mutationList.get(0).getMutatingContent());
        mutationList = mutator.generateMutationList(Iterables.get(domClonings, 1));
        assertEquals(inputs[2], mutationList.get(0).getOriginalNode().toSource().trim());
        assertEquals(ReplacingToNoOpMutator.NO_OPERATION_STR, mutationList.get(0).getMutatingContent());
    }
}