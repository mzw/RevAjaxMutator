package jp.mzw.ajaxmutator.mutator;

import java.util.ArrayList;
import java.util.List;

import jp.mzw.ajaxmutator.JSType;
import jp.mzw.ajaxmutator.mutatable.DOMSelection;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;
import jp.mzw.ajaxmutator.util.Randomizer;
import jp.mzw.ajaxmutator.util.StringToAst;

import org.mozilla.javascript.ast.AstNode;

/**
 * {@link jp.mzw.ajaxmutator.mutator.Mutator} that creates mutation for {@link DOMSelection} of selecting
 * nearby element of originally selecting element.
 *
 * @author Kazuki Nishiura
 */
public class DOMSelectionSelectNearbyMutator extends AbstractMutator<DOMSelection> {
    public DOMSelectionSelectNearbyMutator() {
        super(DOMSelection.class);
    }

    @Override
    public List<Mutation> generateMutationList(DOMSelection originalNode) {
    	double randomValue = Randomizer.getDouble();
        AstNode node = originalNode.getAstNode();
        JSType domType
                = (originalNode.getSelectionMethod() == DOMSelection.SelectionMethod.JQUERY)
                ? JSType.JQUERY_OBJECT
                : JSType.DOM_ELEMENT;
        List<Mutation> mutationList = new ArrayList<Mutation>();
        if (randomValue < 0.5) {
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
