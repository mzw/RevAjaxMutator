package jp.mzw.revajaxmutator.fixer;

import java.util.Collection;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.StringLiteral;

import com.google.common.collect.Sets;

import jp.mzw.ajaxmutator.mutatable.EventAttachment;
import jp.mzw.revajaxmutator.parser.RepairSource;

/**
 *
 * @author Junto Nakaoka
 *
 */
public class EventTypeTSFixer extends AbstractReplacingAmongFixer<EventAttachment> {

	public EventTypeTSFixer(Collection<EventAttachment> mutationTargets,
			Collection<? extends RepairSource> repairSources) {
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
		if ((mutatedNode instanceof StringLiteral) && (mutatingNode instanceof Name)) {
			final String mutatingEventType = ((Name) mutatingNode).getIdentifier();
			final String mutatedEventType = mutatedNode.toSource();
			return '"' + this.manipulateOnPrefix(mutatingEventType, mutatedEventType) + '"';
		} else if ((mutatedNode instanceof Name) && (mutatingNode instanceof StringLiteral)) {
			final String mutatingEventType = mutatingNode.toSource();
			final String mutatedEventType = ((Name) mutatedNode).getIdentifier();
			return this.manipulateOnPrefix(mutatingEventType, mutatedEventType);
		} else if ((mutatedNode instanceof StringLiteral) && (mutatingNode instanceof StringLiteral)) {
			final String mutatingEventType = ((StringLiteral) mutatingNode).getValue();
			final String mutatedEventType = ((StringLiteral) mutatedNode).getValue();
			return '"' + this.manipulateOnPrefix(mutatingEventType, mutatedEventType) + '"';
		}
		return super.formatAccordingTo(mutatingNode, mutatedNode);
	}

	/**
	 * Case #1: If mutated start with 'on' but mutating does not start with
	 * 'on', return 'on' + mutating. Case #2: If mutated does not start with
	 * 'on' but mutating starts with 'on', return mutating without 'on'.
	 * Otherwise, return mutating.
	 *
	 * @param mutatingEventType
	 *            candidate
	 * @param mutatedEventType
	 *            base
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
