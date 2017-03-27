package jp.mzw.ajaxmutator.mutatable.generic;

import jp.mzw.ajaxmutator.mutatable.Mutatable;

import org.mozilla.javascript.ast.AstNode;

public class Return extends Mutatable {

	private AstNode value;

	public Return(AstNode node, AstNode value) {
		super(node);
		this.value = value;
	}

	public AstNode getValue() {
		return value;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(super.toString()).append('\n')
				.append("  Return ").append(value).append("").toString();
	}

}
