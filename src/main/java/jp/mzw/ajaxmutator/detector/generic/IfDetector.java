package jp.mzw.ajaxmutator.detector.generic;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.IfStatement;

import jp.mzw.ajaxmutator.detector.AbstractDetector;
import jp.mzw.ajaxmutator.mutatable.generic.If;

public class IfDetector extends AbstractDetector<If> {

	public If detect(AstNode node) {
		return detectFromBranch(node, true);
	}

	@Override
	protected If detectFromBranch(IfStatement ifstatement, AstNode condition,
			AstNode elsepart) {
		return new If(ifstatement, condition, elsepart);
	}
}
