package jp.mzw.revajaxmutator.parser;

import org.mozilla.javascript.ast.AstNode;

public class RepairValue {

	public enum Type {
		MUTATABLE, REPAIRSOURCE, TEXT, NONE
	}

	private final Type type;
	private final AstNode astNode;
	private final RepairSource repairSource;
	private final String text;

	public RepairValue(AstNode astNode) {
		this.type = Type.MUTATABLE;
		this.astNode = astNode;
		this.repairSource = null;
		this.text = null;
	}

	public RepairValue(RepairSource repairSource) {
		this.type = Type.REPAIRSOURCE;
		this.astNode = null;
		this.repairSource = repairSource;
		this.text = null;
	}

	public RepairValue(String text) {
		this.type = Type.TEXT;
		this.text = text;
		this.astNode = null;
		this.repairSource = null;
	}

	public RepairValue() {
		this.type = Type.NONE;
		this.astNode = null;
		this.repairSource = null;
		this.text = null;
	}

	public Type getType() {
		return this.type;
	}

	public String getRepairValue() {
		if (this.type == Type.MUTATABLE && this.astNode != null) {
			return this.astNode.toSource();
		} else if (this.type == Type.REPAIRSOURCE && this.repairSource != null) {
			return this.repairSource.getValue();
		} else if (this.type == Type.TEXT && this.text != null) {
			return this.text;
		}
		return "";
	}

	public RepairSource.Type getRepairSource() {
		if (this.type == Type.MUTATABLE && this.astNode != null) {
			return RepairSource.Type.JavaScript;
		} else if (this.type == Type.REPAIRSOURCE && this.repairSource != null) {
			return this.repairSource.getType();
		} else if (this.type == Type.TEXT && this.text != null) {
			return RepairSource.Type.JavaScript;
		}
		return RepairSource.Type.NONE;
	}
}
