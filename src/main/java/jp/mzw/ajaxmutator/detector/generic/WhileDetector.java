package jp.mzw.ajaxmutator.detector.generic;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.WhileLoop;

import jp.mzw.ajaxmutator.detector.AbstractDetector;
import jp.mzw.ajaxmutator.mutatable.generic.While;

public class WhileDetector extends AbstractDetector<While> {

	public While detect(AstNode node) {
		return detectFromBranch(node, true);
	}

	@Override
	protected While detectFromBranch(WhileLoop whileloop, AstNode condition,
			AstNode body) {
		return new While(whileloop, condition, body);
	}
}
