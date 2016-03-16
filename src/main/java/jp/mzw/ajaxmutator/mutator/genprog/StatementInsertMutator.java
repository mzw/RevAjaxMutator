package jp.mzw.ajaxmutator.mutator.genprog;

import org.mozilla.javascript.ast.AstNode;

import jp.mzw.ajaxmutator.mutatable.genprog.Statement;

public class StatementInsertMutator extends AbstractStatementsMutator<Statement> {

	public StatementInsertMutator(Statement src, Statement dst) {
		super(Statement.class, src, dst);
	}

	@Override
	protected String formatAccordingTo(AstNode srcNode, AstNode dstNode) {
		StringBuilder builder = new StringBuilder("/* [Insert by AjaxGenProg] */");
		// another statement is inserted "after" it
		builder.append(srcNode.toSource()).append(dstNode.toSource());
		return builder.toString();
	}
}
