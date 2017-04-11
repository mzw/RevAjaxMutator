package jp.mzw.revajaxmutator.fixer;

import java.util.Collection;

import jp.mzw.ajaxmutator.mutatable.EventAttachment;
import jp.mzw.revajaxmutator.parser.RepairSource;

import org.mozilla.javascript.ast.AstNode;

import com.google.common.collect.Sets;

/**
 * 
 * @author Junto Nakaoka
 *
 */
public class EventTargetTSFixer extends AbstractReplacingAmongFixer<EventAttachment> {

	public EventTargetTSFixer(Collection<EventAttachment> mutationTargets, Collection<? extends RepairSource> repairSources) {
		super(EventAttachment.class, mutationTargets, repairSources);
	}

	public EventTargetTSFixer(Collection<EventAttachment> mutationTargets) {
		super(EventAttachment.class, mutationTargets, Sets.newHashSet());
	}

	@Override
	protected AstNode getFocusedNode(EventAttachment node) {
		return node.getTarget();
	}
}
