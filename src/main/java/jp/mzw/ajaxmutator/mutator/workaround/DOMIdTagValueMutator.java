package jp.mzw.ajaxmutator.mutator.workaround;

import java.util.Collection;

import org.mozilla.javascript.ast.AstNode;

import jp.mzw.ajaxmutator.mutatable.DOMSelection;
import jp.mzw.ajaxmutator.mutator.replace.among.AbstractReplacingAmongMutator;

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
