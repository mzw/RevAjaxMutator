package jp.mzw.ajaxmutator.mutator;

import java.util.ArrayList;
import java.util.List;

import jp.mzw.ajaxmutator.mutatable.DOMReplacement;
import jp.mzw.ajaxmutator.generator.Mutation;

/**
 * {@link jp.mzw.ajaxmutator.mutator.Mutator} that replace src element and target
 * element of DOM replacement, e.g., elm.replaceChild(src, target)
 */
public class DOMReplacementSrcTargetMutator extends AbstractMutator<DOMReplacement> {
    public DOMReplacementSrcTargetMutator() {
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
        List<Mutation> mutationList = new ArrayList<Mutation>();
        mutationList.add(new Mutation(originalNode.getAstNode(), replacement));
        return mutationList;
    }

}
