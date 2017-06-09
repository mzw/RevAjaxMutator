package jp.mzw.ajaxmutator.mutatable.generic;

import jp.mzw.ajaxmutator.mutatable.Mutatable;

import org.mozilla.javascript.ast.AstNode;

public class Continue extends Mutatable {

	public Continue(AstNode node) {
		super(node);
	}

	@Override
	public String toString() {
		return new StringBuilder().append(super.toString()).append('\n')
				.append("  Continue").toString();
	}

}
