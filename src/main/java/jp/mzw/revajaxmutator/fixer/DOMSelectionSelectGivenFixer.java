package jp.mzw.revajaxmutator.fixer;

import java.util.ArrayList;
import java.util.List;

import jp.mzw.ajaxmutator.mutatable.DOMSelection;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;

import org.mozilla.javascript.ast.AstNode;


public class DOMSelectionSelectGivenFixer extends AbstractMutator<DOMSelection> {
    private final String selector;
//    private String[] parseResult;

    public DOMSelectionSelectGivenFixer(String selector) {
        super(DOMSelection.class);
        this.selector = selector;
//        this.parseResult = parseResult;
    }

    @Override
    public List<Mutation> generateMutationList(DOMSelection originalNode) {
        AstNode node = originalNode.getAstNode();
        //using selector
        String code = node.toSource().replace(originalNode.getSelector().toSource(), selector);
        List<Mutation> mutationList = new ArrayList<Mutation>();
        mutationList.add(new Mutation(node, code));
        //using parseResult
//        for(String candidate : parseResult){
//        	code = node.toSource().replace(originalNode.getSelector().toSource(), candidate);
//            mutationList.add(new Mutation(node, code));
//        }
        return mutationList;
    }
}
