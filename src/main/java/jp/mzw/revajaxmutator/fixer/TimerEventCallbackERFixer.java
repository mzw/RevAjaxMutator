package jp.mzw.revajaxmutator.fixer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.mzw.ajaxmutator.mutatable.TimerEventAttachment;
import jp.mzw.revajaxmutator.parser.RepairSource;

import org.mozilla.javascript.ast.AstNode;

/**
 * 
 * @author Junto Nakaoka
 *
 */
public class TimerEventCallbackERFixer extends
		AbstractReplacingAmongFixer<TimerEventAttachment> {

	public TimerEventCallbackERFixer(
			Collection<TimerEventAttachment> mutationTargets,
			List<RepairSource> repairSources) {
		super(TimerEventAttachment.class, mutationTargets, repairSources);
	}

	public TimerEventCallbackERFixer(
			Collection<TimerEventAttachment> mutationTargets) {
		super(TimerEventAttachment.class, mutationTargets,
				new ArrayList<RepairSource>());
	}

	@Override
	protected AstNode getFocusedNode(TimerEventAttachment node) {
		return node.getCallback();
	}
}
