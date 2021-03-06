package jp.mzw.revajaxmutator.fixer;

import java.util.Collection;

import jp.mzw.ajaxmutator.mutatable.DOMSelection;
import jp.mzw.revajaxmutator.parser.RepairSource;

import org.mozilla.javascript.ast.AstNode;

public class DOMSelectionAtrributeFixer extends AbstractReplacingAmongFixer<DOMSelection> {

	public DOMSelectionAtrributeFixer(Collection<DOMSelection> mutationTargets, Collection<? extends RepairSource> repairSources) {
		super(DOMSelection.class, mutationTargets, repairSources);
	}

	@Override
	protected AstNode getFocusedNode(DOMSelection node) {
		return node.getSelector();
	}

}
