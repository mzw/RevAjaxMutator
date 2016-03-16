package jp.mzw.revajaxmutator.fixer;

import java.util.ArrayList;
import java.util.List;

import jp.mzw.ajaxmutator.JSType;
import jp.mzw.ajaxmutator.mutatable.DOMSelection;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;
import jp.mzw.ajaxmutator.util.StringToAst;

import org.mozilla.javascript.ast.AstNode;

/**
 * 
 * @author Junto Nakaoka
 *
 */
public class DOMSelectionSelectNearbyFixer extends AbstractMutator<DOMSelection>{
	private final boolean isParentCreatedWhenMutation;
	public DOMSelectionSelectNearbyFixer(Boolean isParentCreatedWhenMutation) {
        super(DOMSelection.class);
        this.isParentCreatedWhenMutation = isParentCreatedWhenMutation;
    }

    @Override
    public List<Mutation> generateMutationList(DOMSelection originalNode) {
        AstNode node = originalNode.getAstNode();
        JSType domType
                = (originalNode.getSelectionMethod() == DOMSelection.SelectionMethod.JQUERY)
                ? JSType.JQUERY_OBJECT
                : JSType.DOM_ELEMENT;
        List<Mutation> mutationList = new ArrayList<Mutation>();
        if (!isParentCreatedWhenMutation) {
            mutationList.add(new Mutation(
                    originalNode.getAstNode(),
                    StringToAst.createParentNodeAsString(node, domType)));
        } else {
            mutationList.add(new Mutation(
                    originalNode.getAstNode(),
                    StringToAst.createChildNodeAsString(node, domType)));
        }
        return mutationList;
    }
}


