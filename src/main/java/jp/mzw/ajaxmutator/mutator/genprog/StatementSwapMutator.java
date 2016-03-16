package jp.mzw.ajaxmutator.mutator.genprog;

import jp.mzw.ajaxmutator.mutatable.genprog.Statement;

import org.mozilla.javascript.ast.AstNode;

public class StatementSwapMutator extends AbstractStatementsMutator<Statement> {
	
	public StatementSwapMutator(Statement src, Statement dst) {
		super(Statement.class, src, dst);
	}

	@Override
	protected String formatAccordingTo(AstNode srcNode, AstNode dstNode) {
		StringBuilder builder = new StringBuilder("/* [Swap by AjaxGenProg] */");
		// another statement is inserted "after" it
		builder.append(dstNode.toSource());
		return builder.toString();
	}
}
