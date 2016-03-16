package jp.mzw.ajaxmutator.mutatable.genprog;

import org.mozilla.javascript.ast.AstNode;

import jp.mzw.ajaxmutator.mutatable.Mutatable;

public class Statement extends Mutatable {
	
	protected Statement.Type type;
	protected double weight;
	
	public Statement(AstNode node) {
		super(node);
		this.type = Statement.Type.Unknown;
		weight = 0;
	}

	public enum Type {
		Unknown, Assignment, FunctionCall, Return, IfStatement, Loop
	}

	public Statement.Type getType() {
		return this.type;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public double getWeight() {
		return this.weight;
	}
}
