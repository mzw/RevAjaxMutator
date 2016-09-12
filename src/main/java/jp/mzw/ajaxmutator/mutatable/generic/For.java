package jp.mzw.ajaxmutator.mutatable.generic;

import jp.mzw.ajaxmutator.mutatable.Mutatable;

import org.mozilla.javascript.ast.AstNode;

public class For extends Mutatable {

	private AstNode condition;
	private AstNode body;
	private AstNode initializer;
	private AstNode increment;

	public For(AstNode node, AstNode condition, AstNode body,
			AstNode initializer, AstNode increment) {
		super(node);
		this.condition = condition;
		this.body = body;
		this.initializer = initializer;
		this.increment = increment;
	}

	public AstNode getCondition() {
		return condition;
	}

	public AstNode getBody() {
		return body;
	}

	public AstNode getInitializer() {
		return initializer;
	}

	public AstNode getIncrement() {
		return increment;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(super.toString()).append('\n')
				.append("  For ").append(condition).append("").toString();
	}

}
