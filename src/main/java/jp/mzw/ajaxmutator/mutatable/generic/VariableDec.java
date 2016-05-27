package jp.mzw.ajaxmutator.mutatable.generic;

import java.util.List;

import jp.mzw.ajaxmutator.mutatable.Mutatable;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.VariableInitializer;

public class VariableDec extends Mutatable {

	private List<VariableInitializer> variables;

	public VariableDec(AstNode node, List<VariableInitializer> variables) {
		super(node);
		this.variables = variables;
	}

	public List<VariableInitializer> getValue() {
		return variables;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(super.toString()).append('\n')
				.append("  VariableDeclaration ").append("").toString();
	}

}
