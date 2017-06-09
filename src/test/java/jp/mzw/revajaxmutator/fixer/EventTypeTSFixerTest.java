package jp.mzw.revajaxmutator.fixer;

import static jp.mzw.ajaxmutator.util.StringToAst.parseAstRoot;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
import jp.mzw.ajaxmutator.detector.EventAttacherDetector;
import jp.mzw.ajaxmutator.detector.event.AddEventListenerDetector;
import jp.mzw.ajaxmutator.detector.event.AttachEventDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryEventAttachmentDetector;
import jp.mzw.ajaxmutator.mutatable.EventAttachment;

public class EventTypeTSFixerTest {

	@Test
	public void testFormatAccordingTo() {

		Set<EventAttacherDetector> attacherDetector = ImmutableSet.of(new AddEventListenerDetector(), new AttachEventDetector(),
				new JQueryEventAttachmentDetector());
		MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
		builder.setEventAttacherDetectors(attacherDetector);
		MutateVisitor visitor = builder.build();

		StringBuilder source = new StringBuilder();
		source.append("$('#element').on('click', callback);");
		source.append("document.getElementById('target').attachEvent('onblur', function() {});");
		AstRoot ast = parseAstRoot(source.toString());
		ast.visit(visitor);

		Set<EventAttachment> events = visitor.getEventAttachments();
		EventTypeTSFixer fixer = new EventTypeTSFixer(events);
		AstNode click = fixer.getFocusedNode(Iterables.get(events, 0));
		AstNode onblur = fixer.getFocusedNode(Iterables.get(events, 1));

		Assert.assertArrayEquals("\"onclick\"".toCharArray(), fixer.formatAccordingTo(click, onblur).toCharArray());
		Assert.assertArrayEquals("\"blur\"".toCharArray(), fixer.formatAccordingTo(onblur, click).toCharArray());
	}

}
