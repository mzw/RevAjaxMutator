package jp.mzw.ajaxmutator.mutatable.generic;

import jp.mzw.ajaxmutator.mutatable.Mutatable;

import org.mozilla.javascript.ast.AstNode;

public class While extends Mutatable {

	private AstNode condition;
	private AstNode body;

	public While(AstNode node, AstNode condition, AstNode body) {
		super(node);
		this.condition = condition;
		this.body = body;
	}

	public AstNode getCondition() {
		return condition;
	}

	public AstNode getBody() {
		return body;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(super.toString()).append('\n')
				.append("  While ").append(condition).append("").toString();
	}

}
