package jp.mzw.revajaxmutator.fixer;

import java.util.Collection;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMSelection;

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
