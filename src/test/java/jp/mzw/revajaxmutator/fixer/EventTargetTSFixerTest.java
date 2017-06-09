package jp.mzw.revajaxmutator.fixer;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
import jp.mzw.ajaxmutator.detector.EventAttacherDetector;
import jp.mzw.ajaxmutator.detector.event.AddEventListenerDetector;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.EventAttachment;
import jp.mzw.ajaxmutator.mutator.MutatorTestBase;

public class EventTargetTSFixerTest extends MutatorTestBase{
	Collection<EventAttachment> eventAttachments;
	private String[] targets;
	private String[] events;
	private String[] callbacks;
	
	@Override
	protected void prepare() {
		targets = new String[] {"element", "document.getElementById('hoge')"};
		events = new String[] {"'blur'", "'click'"};
		callbacks = new String[] {"func1", "func2"};
		inputs = new String[2];
		for (int i = 0; i < 2; i++) {
			inputs[i] = addEventListener(targets[i], events[i], callbacks[i]);
		}
		
		Set<EventAttacherDetector> attacherDetector = ImmutableSet
				.of((EventAttacherDetector) new AddEventListenerDetector());
		MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
		builder.setEventAttacherDetectors(attacherDetector);
		visitor = builder.build();
		eventAttachments = visitor.getEventAttachments();
	}
	
	@Test
	public void testEventTargetTSFixer() {
		EventTargetTSFixer fixer = new EventTargetTSFixer(eventAttachments);
		List<Mutation> mutationList = fixer.generateMutationList(Iterables.get(eventAttachments,  0));
		assertEquals(targets[1], mutationList.get(0).getMutatingContent());
	}
	
	private String addEventListener(String target, String event, String callback) {
		return target + ".addEventListener(" + event + ", " + callback + ");";
	}
}
