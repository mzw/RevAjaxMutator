package jp.mzw.ajaxmutator.detector.generic;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ContinueStatement;

import jp.mzw.ajaxmutator.detector.AbstractDetector;
import jp.mzw.ajaxmutator.mutatable.generic.Continue;

public class ContinueDetector extends AbstractDetector<Continue> {

	public Continue detect(AstNode node) {
		return detectFromBranch(node, true);
	}

	@Override
	protected Continue detectFromBranch(ContinueStatement continuestatement) {
		return new Continue(continuestatement);
	}
}
