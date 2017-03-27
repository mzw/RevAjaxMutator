package jp.mzw.ajaxmutator.detector.generic;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ForLoop;

import jp.mzw.ajaxmutator.detector.AbstractDetector;
import jp.mzw.ajaxmutator.mutatable.generic.For;

public class ForDetector extends AbstractDetector<For> {

	public For detect(AstNode node) {
		return detectFromBranch(node, true);
	}

	@Override
	protected For detectFromBranch(ForLoop forloop, AstNode condition,
			AstNode body, AstNode initializer, AstNode increment) {
		return new For(forloop, condition, body, initializer, increment);
	}
}
