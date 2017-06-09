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
public class EventCallbackERFixer extends AbstractReplacingAmongFixer<EventAttachment> {

	public EventCallbackERFixer(Collection<EventAttachment> mutationTargets, Collection<? extends RepairSource> repairSources) {
		super(EventAttachment.class, mutationTargets, repairSources);
	}

	public EventCallbackERFixer(Collection<EventAttachment> mutationTargets) {
		super(EventAttachment.class, mutationTargets, Sets.newHashSet());
	}

	@Override
	protected AstNode getFocusedNode(EventAttachment node) {
		return node.getCallback();
	}
}
