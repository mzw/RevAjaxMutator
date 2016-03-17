package jp.mzw.revajaxmutator.fixer;

import org.mozilla.javascript.ast.AstNode;

/**
 * 
 * @author Junto Nakaoka
 *
 */
public class Candidate {
	private final Type type;
	private final AstNode astNode;// mutating node (i.e. candidate)
	private final RepairSource repairSource;
	private final String text;

	public enum Type {
		MUTATABLE, REPAIRSOURCEVALUE, TEXT, NONE
	}

	public enum CandidateSource {
		DEFAULT(0, "Default"), TESTCASE(1, "TestCase"), JS(2, "JavaScript"), HTML(
				3, "HTML"), NONE(4, "None");

		private final String source;
		private final int value;

		private CandidateSource(int value, final String source) {
			this.value = value;
			this.source = source;
		}

		public int getValue() {
			return this.value;
		}

		private String getString() {
			return this.source;
		}

		public static CandidateSource fromStringName(String name) {
			for (int i = 0; i < values().length; i++) {
				if (values()[i].source.equals(name)) {
					return values()[i];
				}
			}
			return null;
		}

		private int[][] permutationTable = { { 0, 1, 2, 3 }, { 0, 1, 3, 2 },
				{ 0, 2, 1, 3 }, { 0, 2, 3, 1 }, { 0, 3, 1, 2 }, { 0, 3, 2, 1 },
				{ 1, 0, 2, 3 }, { 1, 0, 3, 2 }, { 1, 2, 0, 3 }, { 1, 2, 3, 0 },
				{ 1, 3, 0, 2 }, { 1, 3, 2, 0 }, { 2, 0, 1, 3 }, { 2, 0, 3, 1 },
				{ 2, 1, 0, 3 }, { 2, 1, 3, 0 }, { 2, 3, 0, 1 }, { 2, 3, 1, 0 },
				{ 3, 0, 1, 2 }, { 3, 0, 2, 1 }, { 3, 1, 0, 2 }, { 3, 1, 2, 0 },
				{ 3, 2, 0, 1 }, { 3, 2, 1, 0 } };

		public int getValueForAllPermutationsTrial(int num) {
			if (this.value > 3) {
				return this.value;
			} else {
				return permutationTable[num][this.value];
			}
		}
	}

	public Candidate(RepairSource repairSource) {
		this.type = Type.REPAIRSOURCEVALUE;
		this.astNode = null;
		this.repairSource = repairSource;
		this.text = null;
	}

	public Candidate(AstNode astNode) {
		this.type = Type.MUTATABLE;
		this.astNode = astNode;
		this.repairSource = null;
		this.text = null;
	}

	public Candidate(String text) {
		this.type = Type.TEXT;
		this.text = text;
		this.astNode = null;
		this.repairSource = null;
	}

	public Candidate(Type type) {
		this.type = Type.NONE;
		this.astNode = null;
		this.repairSource = null;
		this.text = null;

	}

	public String getCandidateValue() {
		if (this.type == Type.MUTATABLE && this.astNode != null) {
			return this.astNode.toSource();
		} else if (this.type == Type.REPAIRSOURCEVALUE
				&& this.repairSource != null) {
			return this.repairSource.getRepairValue();
		} else if (this.type == Type.TEXT && this.text != null) {
			return this.text;
		} else {
			return "";// NO
		}
	}

	public String getCandidateSource() {
		if (this.type == Type.MUTATABLE && this.astNode != null) {
			return CandidateSource.JS.getString();
		} else if (this.type == Type.REPAIRSOURCEVALUE
				&& this.repairSource != null) {
			return this.repairSource.getRepairSource().getString();
		} else if (this.type == Type.TEXT && this.text != null) {
			return CandidateSource.JS.getString();
		} else {
			return CandidateSource.NONE.getString();// NO
		}
	}

}
