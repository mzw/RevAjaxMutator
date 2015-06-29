package jp.mzw.revajaxmutator.mutator;

import java.util.Collection;

import org.mozilla.javascript.ast.AstNode;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMSelection;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.AbstractReplacingAmongMutator;

/**
 * Mutation operator for manipulating DOM tag or id value
 * Called as DMV mutation operator`
 * @author Yuta Maezawa
 */
public class DOMIdTagValueMutator extends AbstractReplacingAmongMutator<DOMSelection> {
    public DOMIdTagValueMutator(Collection<DOMSelection> mutationTargets) {
        super(DOMSelection.class, mutationTargets);
    }

	@Override
	protected AstNode getFocusedNode(DOMSelection node) {
		return node.getSelector();
	}
}
