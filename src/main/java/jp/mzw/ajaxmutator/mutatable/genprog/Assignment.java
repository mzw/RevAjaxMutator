package jp.mzw.ajaxmutator.mutatable.genprog;

import org.mozilla.javascript.ast.AstNode;

public class Assignment extends Statement {

	public Assignment(AstNode node) {
		super(node);
		this.type = Statement.Type.Assignment;
	}
}
