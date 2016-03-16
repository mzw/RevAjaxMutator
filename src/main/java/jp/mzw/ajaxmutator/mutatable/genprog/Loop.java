package jp.mzw.ajaxmutator.mutatable.genprog;

import org.mozilla.javascript.ast.AstNode;

public class Loop extends Statement {

	public Loop(AstNode node) {
		super(node);
		this.type = Statement.Type.Loop;
	}
}
