package jp.mzw.ajaxmutator.mutator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
import jp.mzw.ajaxmutator.detector.dom.ReplaceChildDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryReplaceWithDetector;
import jp.mzw.ajaxmutator.mutatable.DOMReplacement;
import jp.mzw.ajaxmutator.generator.Mutation;

import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author Kazuki Nishiura
 */
public class DOMReplacementMutatorTest extends MutatorTestBase {
    @Override
    protected void prepare() {
        inputs = new String[] {
                "document.findElementById('hoge').replaceChild(child1, child2);",
                "$foo.replaceWith($('#bar'));"
        };

        MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
        builder.setDomReplacementDetectors(
                ImmutableSet.of(new ReplaceChildDetector(), new JQueryReplaceWithDetector()));
        visitor = builder.build();
    }

    @Test
    public void testDOMRemovalToNoOpMutator() {
        Set<DOMReplacement> domReplacements = visitor.getDomReplacements();
        Mutator<DOMReplacement> mutator = new DOMReplacementSrcTargetMutator();
        List<Mutation> mutationList = mutator.generateMutationList(Iterables.get(domReplacements, 0));
        assertEquals(
                "document.findElementById('hoge').replaceChild(child1, child2)",
                mutationList.get(0).getOriginalNode().toSource());
        assertEquals(
                "document.findElementById('hoge').replaceChild(child2, child1)",
                mutationList.get(0).getMutatingContent());
        mutationList = mutator.generateMutationList(Iterables.get(domReplacements, 1));
        assertEquals("$foo.replaceWith($('#bar'))", mutationList.get(0).getOriginalNode().toSource());
        assertEquals("$('#bar').replaceWith($foo)", mutationList.get(0).getMutatingContent());
    }
}
