package jp.mzw.ajaxmutator.mutatable.genprog;

import org.mozilla.javascript.ast.AstNode;

public class FunctionCall extends Statement {

	public FunctionCall(AstNode node) {
		super(node);
		this.type = Statement.Type.FunctionCall;
	}
}
