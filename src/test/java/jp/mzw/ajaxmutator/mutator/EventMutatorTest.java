package jp.mzw.ajaxmutator.mutator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
import jp.mzw.ajaxmutator.detector.EventAttacherDetector;
import jp.mzw.ajaxmutator.detector.event.AddEventListenerDetector;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.EventAttachment;
import jp.mzw.ajaxmutator.mutator.replace.among.EventCallbackRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.EventTargetRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.EventTypeRAMutator;

import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class EventMutatorTest extends MutatorTestBase {
    Collection<EventAttachment> eventAttachments;
    private String[] targets;
    private String[] events;
    private String[] callbacks;

    @Override
    protected void prepare() {
        targets = new String[] { "element", "document.getElementById('hoge')" };
        events = new String[] { "'blur'", "'click'" };
        callbacks = new String[] { "func1", "func2" };
        inputs = new String[2];
        for (int i = 0; i < 2; i++)
            inputs[i] = addEventListner(targets[i], events[i], callbacks[i]);

        Set<EventAttacherDetector> attacherDetector = ImmutableSet
                .of((EventAttacherDetector) new AddEventListenerDetector());
        MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
        builder.setEventAttacherDetectors(attacherDetector);
        visitor = builder.build();
        eventAttachments = visitor.getEventAttachments();
    }

    @Test
    public void testEventTargetRAMutator() {
        Mutator<EventAttachment> mutator = new EventTargetRAMutator(eventAttachments);
        List<Mutation> mutationList = mutator.generateMutationList(Iterables.get(eventAttachments, 0));
        assertEquals(targets[1], mutationList.get(0).getMutatingContent());
    }

    @Test
    public void testEventTypeMutator() {
        Mutator<EventAttachment> mutator = new EventTypeRAMutator(eventAttachments);
        List<Mutation> mutationList = mutator.generateMutationList(Iterables.get(eventAttachments, 0));
        assertEquals(events[1], mutationList.get(0).getMutatingContent());

    }

    @Test
    public void testEventCallbackMutator() {
        Mutator<EventAttachment> mutator = new EventCallbackRAMutator(eventAttachments);
        List<Mutation> mutationList = mutator.generateMutationList(Iterables.get(eventAttachments, 0));
        assertEquals(callbacks[1], mutationList.get(0).getMutatingContent());

    }

    private String addEventListner(String target, String event, String callback) {
        return target + ".addEventListener(" + event + ", " + callback + ");";
    }
}
