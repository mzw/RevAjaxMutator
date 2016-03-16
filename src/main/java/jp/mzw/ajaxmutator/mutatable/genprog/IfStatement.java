package jp.mzw.ajaxmutator.mutatable.genprog;

import org.mozilla.javascript.ast.AstNode;

public class IfStatement extends Statement {

	public IfStatement(AstNode node) {
		super(node);
		this.type = Statement.Type.IfStatement;
	}
}
