package jp.mzw.ajaxmutator.detector.generic;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ReturnStatement;

import jp.mzw.ajaxmutator.detector.AbstractDetector;
import jp.mzw.ajaxmutator.mutatable.generic.Return;

public class ReturnDetector extends AbstractDetector<Return> {

	public Return detect(AstNode node) {
		return detectFromBranch(node, true);
	}

	@Override
	protected Return detectFromBranch(ReturnStatement returnstatement,
			AstNode value) {
		return new Return(returnstatement, returnstatement.getReturnValue());
	}
}
