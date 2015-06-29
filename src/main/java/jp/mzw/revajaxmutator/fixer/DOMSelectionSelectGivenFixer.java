package jp.mzw.revajaxmutator.fixer;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMSelection;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.AbstractMutator;

import org.mozilla.javascript.ast.AstNode;

public class DOMSelectionSelectGivenFixer extends AbstractMutator<DOMSelection> {
    private final String selector;

    public DOMSelectionSelectGivenFixer(String selector) {
        super(DOMSelection.class);
        this.selector = selector;
    }

    @Override
    public Mutation generateMutation(DOMSelection originalNode) {
        AstNode node = originalNode.getAstNode();
        String code = node.toSource().replace(originalNode.getSelector().toSource(), selector);
        return new Mutation(node, code);
    }
}
