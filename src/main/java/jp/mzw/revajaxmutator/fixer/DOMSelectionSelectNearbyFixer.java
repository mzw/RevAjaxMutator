package jp.mzw.revajaxmutator.fixer;

import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.daisy.ajax_mutator.JSType;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMSelection;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.AbstractMutator;
import jp.gr.java_conf.daisy.ajax_mutator.util.StringToAst;

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


