package jp.mzw.revajaxmutator.fixer;

import java.util.ArrayList;
import java.util.List;

import jp.mzw.ajaxmutator.mutatable.TimerEventAttachment;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;
import jp.mzw.revajaxmutator.parser.RepairSource;
import jp.mzw.revajaxmutator.parser.RepairValue;

import org.mozilla.javascript.ast.AstNode;

/**
 * 
 * @author Junto Nakaoka
 *
 */
public class TimerEventDurationVIFixer extends
		AbstractMutator<TimerEventAttachment> {
	private List<RepairSource> repairSources;

	public TimerEventDurationVIFixer(List<RepairSource> repairSources) {
		super(TimerEventAttachment.class);
		this.repairSources = repairSources;
	}

	@Override
	public List<Mutation> generateMutationList(TimerEventAttachment originalNode) {
		AstNode focusedNode = getFocusedNode(originalNode);
		List<Mutation> mutationList = new ArrayList<Mutation>();
		for (RepairSource repairSource : repairSources) {
			mutationList.add(new Mutation(focusedNode, repairSource
					.getValue(), new RepairValue(repairSource)));
		}
		return mutationList;
	}

	private AstNode getFocusedNode(TimerEventAttachment node) {
		return node.getDuration();
	}

}
