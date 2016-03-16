package jp.mzw.ajaxmutator.mutator.genprog;

import org.mozilla.javascript.ast.AstNode;

import jp.mzw.ajaxmutator.mutatable.genprog.Statement;

public class StatementDeleteMutator extends AbstractStatementsMutator<Statement> {

	public StatementDeleteMutator(Statement src) {
		super(Statement.class, src, null);
	}
	
	@Override
	protected String formatAccordingTo(AstNode srcNode, AstNode dstNode) {
		StringBuilder builder = new StringBuilder("/* [Delete by AjaxGenProg] */");
		return builder.toString();
	}
    
}
