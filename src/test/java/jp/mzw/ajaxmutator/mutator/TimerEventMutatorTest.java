package jp.mzw.ajaxmutator.mutator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
import jp.mzw.ajaxmutator.detector.event.TimerEventDetector;
import jp.mzw.ajaxmutator.mutatable.TimerEventAttachment;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutator.replace.among.TimerEventCallbackRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.TimerEventDurationRAMutator;

import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class TimerEventMutatorTest extends MutatorTestBase {
    private String[] callbacks;
    private String[] durations;
    private Collection<TimerEventAttachment> timerEventAttachments;

    @Override
    public void prepare() {
        callbacks = new String[] { "func1", "func2" };
        durations = new String[] { "300", "duration" };
        inputs = new String[2];
        for (int i = 0; i < 2; i++)
            inputs[i] = (i == 1 ? "window." : "")
                    + setTimeout(callbacks[i], durations[i], i == 0);

        Set<TimerEventDetector> attacherDetector = ImmutableSet
                .of(new TimerEventDetector());
        MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
        builder.setTimerEventDetectors(attacherDetector);
        visitor = builder.build();
        timerEventAttachments = visitor.getTimerEventAttachmentExpressions();
    }

    @Test
    public void testTimerDurationRAMutator() {
        Mutator<TimerEventAttachment> mutator = new TimerEventDurationRAMutator(timerEventAttachments);
        List<Mutation> mutationList;
        mutationList = mutator.generateMutationList(
                Iterables.get(timerEventAttachments, 0));
        assertEquals(durations[1], mutationList.get(0).getMutatingContent());
        mutationList = mutator.generateMutationList(
                Iterables.get(timerEventAttachments, 1));
        assertEquals(durations[0], mutationList.get(0).getMutatingContent());
    }

    public void testTimerCallbackRAMutator() {
        Mutator<TimerEventAttachment> mutator = new TimerEventCallbackRAMutator(timerEventAttachments);
        List<Mutation> mutationList;
        mutationList = mutator.generateMutationList(
                Iterables.get(timerEventAttachments, 0));
        assertEquals(callbacks[1], mutationList.get(0).getMutatingContent());
        mutationList = mutator.generateMutationList(
                Iterables.get(timerEventAttachments, 1));
        assertEquals(callbacks[0], mutationList.get(0).getMutatingContent());
    }

    private String setTimeout(String func, String duration, boolean recurcive) {
        return (recurcive ? "setInterval" : "setTimeout") + "(" + func + ", "
                + duration + ");";
    }
}
