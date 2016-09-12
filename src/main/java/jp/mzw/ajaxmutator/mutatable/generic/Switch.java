package jp.mzw.ajaxmutator.mutatable.generic;

import java.util.List;

import jp.mzw.ajaxmutator.mutatable.Mutatable;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.SwitchCase;

public class Switch extends Mutatable {

	private AstNode discriminant;
	private List<SwitchCase> cases;

	public Switch(AstNode node, AstNode discriminant, List<SwitchCase> cases) {
		super(node);
		this.discriminant = discriminant;
		this.cases = cases;
	}

	public List<SwitchCase> getCases() {
		return cases;
	}

	public AstNode getDiscriminant() {
		return discriminant;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(super.toString()).append('\n')
				.toString();
	}

}
