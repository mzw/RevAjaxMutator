package jp.mzw.revajaxmutator.fixer;

import java.util.Collection;

import jp.mzw.ajaxmutator.mutatable.TimerEventAttachment;
import jp.mzw.revajaxmutator.parser.RepairSource;

import org.mozilla.javascript.ast.AstNode;

import com.google.common.collect.Sets;

/**
 * 
 * @author Junto Nakaoka
 *
 */
public class TimerEventCallbackERFixer extends AbstractReplacingAmongFixer<TimerEventAttachment> {

	public TimerEventCallbackERFixer(Collection<TimerEventAttachment> mutationTargets, Collection<? extends RepairSource> repairSources) {
		super(TimerEventAttachment.class, mutationTargets, repairSources);
	}

	public TimerEventCallbackERFixer(Collection<TimerEventAttachment> mutationTargets) {
		super(TimerEventAttachment.class, mutationTargets, Sets.newHashSet());
	}

	@Override
	protected AstNode getFocusedNode(TimerEventAttachment node) {
		return node.getCallback();
	}
}
