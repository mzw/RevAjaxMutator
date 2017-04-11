package jp.mzw.revajaxmutator.fixer;

import java.util.Collection;

import jp.mzw.ajaxmutator.mutatable.EventAttachment;
import jp.mzw.revajaxmutator.parser.RepairSource;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.StringLiteral;

import com.google.common.collect.Sets;

/**
 * 
 * @author Junto Nakaoka
 *
 */
public class EventTypeTSFixer extends AbstractReplacingAmongFixer<EventAttachment> {

	public EventTypeTSFixer(Collection<EventAttachment> mutationTargets, Collection<? extends RepairSource> repairSources) {
		super(EventAttachment.class, mutationTargets, repairSources);
	}

	public EventTypeTSFixer(Collection<EventAttachment> mutationTargets) {
		super(EventAttachment.class, mutationTargets, Sets.newHashSet());
	}

	@Override
	protected AstNode getFocusedNode(EventAttachment node) {
		return node.getEvent();
	}

	/**
	 * jQuery uses event types that do not contain 'on' prefix.
	 */
	@Override
	protected String formatAccordingTo(AstNode mutatingNode, AstNode mutatedNode) {
		if ((mutatedNode instanceof StringLiteral) && !(mutatingNode instanceof StringLiteral)) {
			String mutatingEventType = ((StringLiteral) mutatingNode).getValue();
			String mutatedEventType = mutatedNode.toSource();
			return '"' + manipulateOnPrefix(mutatingEventType, mutatedEventType) + '"';
		} else if (!(mutatedNode instanceof StringLiteral) && (mutatingNode instanceof StringLiteral)) {
			String mutatingEventType = mutatingNode.toSource();
			String mutatedEventType = ((StringLiteral) mutatedNode).getValue();
			return manipulateOnPrefix(mutatingEventType, mutatedEventType);
		} else if ((mutatedNode instanceof StringLiteral) && (mutatingNode instanceof StringLiteral)) {
			String mutatingEventType = ((StringLiteral) mutatingNode).getValue();
			String mutatedEventType = ((StringLiteral) mutatedNode).getValue();
			return '"' + manipulateOnPrefix(mutatingEventType, mutatedEventType) + '"';
		}
		return super.formatAccordingTo(mutatingNode, mutatedNode);
	}

	/**
	 * Case #1: If mutated start with 'on' but mutating does not start with 'on', return 'on' + mutating.
	 * Case #2: If mutated does not start with 'on' but mutating starts with 'on', return mutating without 'on'.
	 * Otherwise, return mutating.
	 * 
	 * @param mutatingEventType candidate
	 * @param mutatedEventType base
	 * @return proper event type
	 */
	private String manipulateOnPrefix(String mutatingEventType, String mutatedEventType) {
		if (mutatingEventType.startsWith("on") && !mutatedEventType.startsWith("on")) {
			return mutatingEventType.substring(2);
		} else if (!mutatingEventType.startsWith("on") && mutatedEventType.startsWith("on")) {
			return "on" + mutatingEventType;
		}
		return mutatingEventType;
	}
}
