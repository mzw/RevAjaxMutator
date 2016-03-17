package jp.mzw.ajaxmutator.mutator.replace.among;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
import jp.mzw.ajaxmutator.detector.EventAttacherDetector;
import jp.mzw.ajaxmutator.detector.event.AddEventListenerDetector;
import jp.mzw.ajaxmutator.mutatable.EventAttachment;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutator.Mutator;

import org.junit.Test;
import org.mozilla.javascript.ast.AstRoot;

import java.util.Collection;
import java.util.List;

import static jp.mzw.ajaxmutator.util.StringToAst.parseAstRoot;
import static org.junit.Assert.assertEquals;

/**
 * @author Kazuki Nishiura
 */
public class AbstractReplacingAmongMutatorTest {
// Note: this class rely on EventCallbackRAMutator to test it's parent class
// AbstractReplacingAmongMutator.

    public Collection<EventAttachment> parseAndGetEventAttachment(String jsProgram) {
        MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
        builder.setEventAttacherDetectors(
                ImmutableSet.<EventAttacherDetector>of(new AddEventListenerDetector()));
        MutateVisitor visitor = builder.build();
        AstRoot ast = parseAstRoot(jsProgram);
        ast.visit(visitor);
        return visitor.getEventAttachments();
    }

    @Test
    public void testEventCallbackRAMutator() {
        Collection<EventAttachment> eventAttachments = parseAndGetEventAttachment(
                getAddEventListenerString("elm", "'click'", "callback1")
                        + getAddEventListenerString("element", "'blur'", "callback2"));
        Mutator<EventAttachment> mutator = new EventCallbackRAMutator(eventAttachments);
        assertEquals("callback1", Iterables.get(eventAttachments, 0).getCallback().toSource());
        List<Mutation> mutationList = mutator.generateMutationList(Iterables.get(eventAttachments, 0));
        assertEquals("callback1", mutationList.get(0).getOriginalNode().toSource());
        assertEquals("callback2", mutationList.get(0).getMutatingContent());
    }

    @Test
    public void testDefaultMutationShouldBeUsedWhenOnlySameNodeAvailable() {
        Collection<EventAttachment> eventAttachments = parseAndGetEventAttachment(
                getAddEventListenerString("elm", "'click'", "callback1")
                        + getAddEventListenerString("element", "'blur'", "callback1"));
        AbstractReplacingAmongMutator<EventAttachment> mutator = new EventCallbackRAMutator(eventAttachments);
        List<Mutation> mutationList = mutator.generateMutationList(Iterables.get(eventAttachments, 0));
        assertEquals(mutator.getDefaultReplacingNode().toSource(), mutationList.get(0).getMutatingContent());
    }

    @Test
    public void testDefaultMutationShouldBeUsedWhenInclusiveRelationshipExists() {
        Collection<EventAttachment> eventAttachments = parseAndGetEventAttachment(
                getAddEventListenerString(
                        "elm", "'click'", "function() {"
                        + getAddEventListenerString("element", "'blur'", "callback2")
                        + "}"));
        assertEquals(2, eventAttachments.size());
        AbstractReplacingAmongMutator<EventAttachment> mutator = new EventCallbackRAMutator(eventAttachments);
        List<Mutation> mutationList = mutator.generateMutationList(Iterables.get(eventAttachments, 0));
        assertEquals(mutator.getDefaultReplacingNode().toSource(), mutationList.get(0).getMutatingContent());
        mutationList = mutator.generateMutationList(Iterables.get(eventAttachments, 1));
        assertEquals(mutator.getDefaultReplacingNode().toSource(), mutationList.get(0).getMutatingContent());
    }

    @Test
    public void testDefaultIsUsedWhenOnlyOneTargetExists() {
        Collection<EventAttachment> eventAttachments = parseAndGetEventAttachment(
                getAddEventListenerString("elm", "click", "func"));
        AbstractReplacingAmongMutator<EventAttachment> mutator = new EventCallbackRAMutator(eventAttachments);
        List<Mutation> mutationList = mutator.generateMutationList(Iterables.get(eventAttachments, 0));
        assertEquals(mutator.getDefaultReplacingNode().toSource(), mutationList.get(0).getMutatingContent());
    }

    private String getAddEventListenerString(String target, String event, String callback) {
        return target + ".addEventListener(" + event + ", " + callback + ");";
    }
}
