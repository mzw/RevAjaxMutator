package jp.mzw.revajaxmutator.fixer;

import java.util.Collection;
import java.util.List;

import jp.mzw.ajaxmutator.mutatable.DOMSelection;

import org.mozilla.javascript.ast.AstNode;

public class DOMSelectionAtrributeFixer extends
		AbstractReplacingAmongFixer<DOMSelection> {

	public DOMSelectionAtrributeFixer(Collection<DOMSelection> mutationTargets,
			List<RepairSource> repairSources) {
		super(DOMSelection.class, mutationTargets, repairSources);
	}

	@Override
	protected AstNode getFocusedNode(DOMSelection node) {
		return node.getSelector();
	}

}
