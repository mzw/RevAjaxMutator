package jp.mzw.revajaxmutator.fixer;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
import jp.mzw.ajaxmutator.detector.event.TimerEventDetector;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.TimerEventAttachment;
import jp.mzw.ajaxmutator.mutator.Mutator;
import jp.mzw.ajaxmutator.mutator.MutatorTestBase;
import jp.mzw.revajaxmutator.parser.RepairSource;

public class TimerEventCallbackERFixerTest extends MutatorTestBase {
	private String[] callbacks;
	private String[] durations;
	private Collection<TimerEventAttachment> timerEventAttachments;
	
	@Override
	public void prepare() {
		callbacks = new String[] {"func1", "func2"};
		durations = new String[] {"300", "duration"};
		inputs = new String[2];
		for (int i = 0; i < 2; i++) {
			inputs[i] = (i == 1 ? "window." : "") + setTimeout(callbacks[i], durations[i], i == 0);
		}
		
		Set<TimerEventDetector> attacherDetector = ImmutableSet.of(new TimerEventDetector());
		MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
		builder.setTimerEventDetectors(attacherDetector);
		visitor = builder.build();
		timerEventAttachments = visitor.getTimerEventAttachmentExpressions();
	}
	
	@Test
	public void testTimerEventCallbackERFixer() {
		Collection<? extends RepairSource> repairSources = Sets.newHashSet();
		Mutator<TimerEventAttachment> fixer = 
				new TimerEventCallbackERFixer(timerEventAttachments, repairSources);
		List<Mutation> mutationList;
		mutationList = fixer.generateMutationList(Iterables.get(timerEventAttachments, 0));
		assertEquals(callbacks[1], mutationList.get(0).getMutatingContent());
		mutationList = fixer.generateMutationList(Iterables.get(timerEventAttachments, 1));
		assertEquals(callbacks[0], mutationList.get(0).getMutatingContent());
	}
	
	private String setTimeout(String func, String duration, boolean recurcive) {
		return (recurcive ? "setInterval" : "setTimeout") + "(" + func + ", "+ duration + ");";
	}
}
