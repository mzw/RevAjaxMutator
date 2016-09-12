package jp.mzw.ajaxmutator.mutatable.generic;

import java.util.List;

import jp.mzw.ajaxmutator.mutatable.Mutatable;

import org.mozilla.javascript.ast.AstNode;

public class FuncNode extends Mutatable {

	private List<AstNode> params;
	private AstNode body;
	private String name;
	private AstNode member;

	public FuncNode(AstNode node, List<AstNode> params, AstNode body,
			String name, AstNode member) {
		super(node);
		this.params = params;
		this.name = name;
		this.body = body;
		this.member = member;
	}

	public List<AstNode> getParams() {
		return params;
	}

	public AstNode getBody() {
		return body;
	}

	public String getName() {
		return name;
	}

	public AstNode getMemberExpr() {
		return member;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(super.toString()).append('\n')
				.append("  FunctionNode name: ").append(name).append("")
				.append("  Parameters: ").append(params).append("").toString();
	}

}
