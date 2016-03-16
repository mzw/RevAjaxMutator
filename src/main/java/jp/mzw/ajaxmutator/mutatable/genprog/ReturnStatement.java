package jp.mzw.ajaxmutator.mutatable.genprog;

import org.mozilla.javascript.ast.AstNode;

public class ReturnStatement extends Statement {

	public ReturnStatement(AstNode node) {
		super(node);
		this.type = Statement.Type.Return;
	}
}
