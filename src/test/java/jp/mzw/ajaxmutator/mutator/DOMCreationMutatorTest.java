package jp.mzw.ajaxmutator.mutator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
import jp.mzw.ajaxmutator.detector.dom.CreateElementDetector;
import jp.mzw.ajaxmutator.mutatable.DOMCreation;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutator.replace.nop.DOMCreationToNoOpMutator;
import jp.mzw.ajaxmutator.mutator.replace.nop.ReplacingToNoOpMutator;

import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DOMCreationMutatorTest extends MutatorTestBase {
    @Override
    protected void prepare() {
        inputs = new String[] {
                "var btn = document.createElement('div');",
                "// some comment here" + System.lineSeparator(),
                "btn = document.createElement('span');",
                "var hoge = fuga;",
                "hoge.appendChild(document.createElement('a'));"
        };

        MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
        builder.setDomCreationDetectors(ImmutableSet.of(new CreateElementDetector()));
        visitor = builder.build();
    }

    @Test
    public void testDOMCreationToNoOpMutator() {
        Set<DOMCreation> domCreations = visitor.getDomCreations();
        Mutator<DOMCreation> mutator = new DOMCreationToNoOpMutator();
        List<Mutation> mutationList = mutator.generateMutationList(Iterables.get(domCreations, 0));
        assertEquals(inputs[0],
                mutationList.get(0).getOriginalNode().toSource().trim());
        assertEquals(ReplacingToNoOpMutator.NO_OPERATION_STR, mutationList.get(0).getMutatingContent());
        mutationList = mutator.generateMutationList(Iterables.get(domCreations, 1));
        assertEquals(inputs[2],
                mutationList.get(0).getOriginalNode().toSource().trim());
        assertEquals(ReplacingToNoOpMutator.NO_OPERATION_STR, mutationList.get(0).getMutatingContent());
        mutationList = mutator.generateMutationList(Iterables.get(domCreations, 2));
        assertEquals(inputs[4],
                mutationList.get(0).getOriginalNode().toSource().trim());
        assertEquals(ReplacingToNoOpMutator.NO_OPERATION_STR, mutationList.get(0).getMutatingContent());
    }
}