package jp.mzw.revajaxmutator.fixer;

import java.util.Collection;

import jp.mzw.ajaxmutator.mutatable.DOMSelection;

import org.mozilla.javascript.ast.AstNode;

public class DOMSelectionAtrributeFixer extends AbstractReplacingAmongFixer<DOMSelection>{

	public DOMSelectionAtrributeFixer(Collection<DOMSelection> mutationTargets, String[] parseResult) {
		super(DOMSelection.class, mutationTargets, parseResult);
	}

	@Override
	protected AstNode getFocusedNode(DOMSelection node) {
		return node.getSelector();
	}

}
