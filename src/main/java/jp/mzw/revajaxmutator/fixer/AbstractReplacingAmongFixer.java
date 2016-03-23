package jp.mzw.revajaxmutator.fixer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.mzw.ajaxmutator.mutatable.Mutatable;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;
import jp.mzw.ajaxmutator.util.Randomizer;
import jp.mzw.revajaxmutator.parser.RepairSource;
import jp.mzw.revajaxmutator.parser.RepairValue;

import org.mozilla.javascript.ast.AstNode;

/**
 * @author Junto Nakaoka
 *
 */
public abstract class AbstractReplacingAmongFixer<T extends Mutatable> extends AbstractMutator<T>{

	private List<AstNode> candidates;
	private List<RepairSource> repairSources;

    public AbstractReplacingAmongFixer(
            Class<? extends T> applicableClass, Collection<T> mutationTargets, List<RepairSource> repairSources) {
        super(applicableClass);
        candidates = new ArrayList<AstNode>(mutationTargets.size());
        for (T attachment : mutationTargets) {
            candidates.add(getFocusedNode(attachment));
        }
        this.repairSources = repairSources;
    }

    /**
     * @return focus of Mutator object. If the class try to mutate event kind
     *         from event attachment statement like
     *         'element.addEventListener('click', func);', this method must
     *         return something like 'click'.
     */
    abstract protected AstNode getFocusedNode(T node);

    @Override
    public List<Mutation> generateMutationList(T originalNode) {
    	Set<AstNode> improperNodes = new HashSet<AstNode>();
        AstNode focusedNode = getFocusedNode(originalNode);
        improperNodes.add(focusedNode);
        List<Mutation> mutationList = new ArrayList<Mutation>();
        
        //create mutation with replacing original node with parseResult
        if(repairSources.size() > 0){
        	for(RepairSource repairSource: repairSources){
        		mutationList.add(new Mutation(focusedNode, repairSource.getValue(), new RepairValue(repairSource)));
        	}
        }
        
        
        //create mutation with replacing among AST nodes in the same file
        while (improperNodes.size() < candidates.size()) {
            AstNode candidate = candidates.get(Randomizer
                    .getInt(candidates.size()));
            if (isEqual(getFocusedNode(originalNode), candidate)
                    || include(originalNode.getAstNode(), candidate)
                    || include(candidate, originalNode.getAstNode())) {
                improperNodes.add(candidate);
            } else {
                mutationList.add(new Mutation(
                        focusedNode, formatAccordingTo(candidate, focusedNode), new RepairValue(candidate)));
                break;
            }
        }
        if (getDefaultReplacingNode() != null && getDefaultReplacingNode().toSource() != focusedNode.toSource()) {
            mutationList.add(new Mutation(focusedNode, formatAccordingTo(getDefaultReplacingNode(), focusedNode), new RepairValue(getDefaultReplacingNode())));
        }
        
        if(mutationList.size() > 0)return mutationList;
        else return null;
    }
    

    private boolean include(AstNode mayParent, AstNode mayChild) {
        if (mayParent == null || mayChild == null) {
            return false;
        }
        boolean parentStartsBeforeChild
                = mayParent.getAbsolutePosition() < mayChild.getAbsolutePosition();
        boolean parentEndsAfterChild
                = (mayParent.getAbsolutePosition() + mayParent.getLength())
                > (mayChild.getAbsolutePosition() + mayChild.getLength());
        return parentStartsBeforeChild && parentEndsAfterChild;
    }

    /**
     * This method is called to format String representation to be suitable for
     * mutation. For instance, if you try to request method in
     * <code>$.ajax(url, 'GET', callback);</code>, request method should be
     * string whereas our subclass try to get mutation candidate from
     * <code>$.post(url, callback);</code> where request method is a function
     * name. This method should be override to reformat the given
     * AstNode to a proper representation for mutation.
     * Default implementation returns mutatingNode#toSource().
     *
     * @param mutatingNode AstNode that will be used to replace existing node.
     * @param mutatedNode AstNode that will be mutated.
     * @return a proper string representation of mutatingNode.
     */
    protected String formatAccordingTo(
            AstNode mutatingNode, AstNode mutatedNode) {
        return mutatingNode.toSource();
    }

    /**
     * Subclass can override this method to specify what should be used when no replacing candidate
     * found. Default implementation returns null, i.e., no replacement occur.
     *
     * @return node that is used for replacement if no other replacing candidate found.
     */
    public AstNode getDefaultReplacingNode() {
        return null;
    }
}
