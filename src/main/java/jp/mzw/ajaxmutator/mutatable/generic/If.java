package jp.mzw.ajaxmutator.mutatable.generic;

import jp.mzw.ajaxmutator.mutatable.Mutatable;

import org.mozilla.javascript.ast.AstNode;

public class If extends Mutatable {

	private AstNode condition;
	private AstNode elsepart;

	public If(AstNode node, AstNode condition, AstNode elsepart) {
		super(node);
		this.condition = condition;
		this.elsepart = elsepart;
	}

	public AstNode getValue() {
		return condition;
	}

	public AstNode getElse() {
		return elsepart;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(super.toString()).append('\n')
				.append("  If ").append(condition).append("").toString();
	}

}
