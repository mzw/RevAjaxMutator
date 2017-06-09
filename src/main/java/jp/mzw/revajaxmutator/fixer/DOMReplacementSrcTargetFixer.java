package jp.mzw.revajaxmutator.fixer;

import java.util.List;

import com.google.common.collect.Lists;

import jp.mzw.ajaxmutator.mutatable.DOMReplacement;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;
import jp.mzw.revajaxmutator.parser.RepairValue;

/**
 * 
 * @author Junto Nakaoka
 *
 */
public class DOMReplacementSrcTargetFixer extends AbstractMutator<DOMReplacement> {
	public DOMReplacementSrcTargetFixer() {
		super(DOMReplacement.class);
	}

	@Override
	public List<Mutation> generateMutationList(DOMReplacement originalNode) {
		String replacement = originalNode.getAstNode().toSource();
		String PLACE_HOLDER = "__TARGET_AST_NODE__";
		String targetNodeStr = originalNode.getReplacedNode().toSource();
		String replacingNodeStr = originalNode.getReplacingNode().toSource();
		replacement = replacement.replace(targetNodeStr, PLACE_HOLDER);
		replacement = replacement.replace(replacingNodeStr, targetNodeStr);
		replacement = replacement.replace(PLACE_HOLDER, replacingNodeStr);
		List<Mutation> mutationList = Lists.newArrayList();
		mutationList.add(new Mutation(originalNode.getAstNode(), replacement, new RepairValue(replacement)));
		return mutationList;
	}
}
