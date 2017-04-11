package jp.mzw.revajaxmutator.parser;

import org.mozilla.javascript.ast.AstNode;

public class RepairValue {

	/**
	 * Represents types of source which repair values come from
	 * 
	 * Mutable: from detection results of mutation
	 * RepairSouce: from ConfigHelper
	 * Test: from given text
	 * None: others, basically invalid
	 * 
	 * @author Yuta Maezawa
	 *
	 */
	public enum Type {
		Mutable, RepairSource, Text, None
	}

	/** Represents type of source which repair value comes from */
	private final Type type;

	/** Contains repair value obtained from detection results of mutation */
	private final AstNode astNode;

	/** Contains repair value obtained from ConfigHelper */
	private final RepairSource repairSource;

	/** Contains repair value from given text */
	private final String text;

	/**
	 * Instantiate with AstNode obtained from detection results of mutation
	 * 
	 * @param astNode is repair value
	 */
	public RepairValue(AstNode astNode) {
		if (astNode == null) {
			throw new NullPointerException("AstNode should not be null");
		}
		this.type = Type.Mutable;
		this.astNode = astNode;
		this.repairSource = null;
		this.text = null;
	}

	/**
	 * Instantiate with RepairSource obtained from ConfigHelper
	 * 
	 * @param repairSource provided from ConfigHelper
	 */
	public RepairValue(RepairSource repairSource) {
		if (repairSource == null) {
			throw new NullPointerException("RepairSource should not be null");
		}
		this.type = Type.RepairSource;
		this.astNode = null;
		this.repairSource = repairSource;
		this.text = null;
	}

	/**
	 * Instantiate with given text
	 * 
	 * @param text
	 */
	public RepairValue(String text) {
		if (text == null) {
			throw new NullPointerException("Text should not be null");
		}
		this.type = Type.Text;
		this.text = text;
		this.astNode = null;
		this.repairSource = null;
	}

	/**
	 * Empty repair value
	 * 
	 */
	public RepairValue() {
		this.type = Type.None;
		this.astNode = null;
		this.repairSource = null;
		this.text = null;
	}

	/**
	 * Get type of repair value
	 * 
	 * @return repair source type
	 */
	public Type getType() {
		return this.type;
	}

	/**
	 * Get string values according to repair source types
	 * 
	 * If None type, return ""
	 * 
	 * @return string value
	 */
	public String getValue() {
		if (this.type == Type.Mutable && this.astNode != null) {
			return this.astNode.toSource();
		} else if (this.type == Type.RepairSource && this.repairSource != null) {
			return this.repairSource.getValue();
		} else if (this.type == Type.Text && this.text != null) {
			return this.text;
		}
		return "";
	}

	/**
	 * Get repair source type
	 * 
	 * @return
	 */
	public RepairSource.Type getRepairSourceType() {
		if (this.type == Type.Mutable && this.astNode != null) {
			return RepairSource.Type.JavaScript;
		} else if (this.type == Type.RepairSource && this.repairSource != null) {
			return this.repairSource.getType();
		} else if (this.type == Type.Text && this.text != null) {
			return RepairSource.Type.JavaScript;
		}
		return RepairSource.Type.None;
	}
}
