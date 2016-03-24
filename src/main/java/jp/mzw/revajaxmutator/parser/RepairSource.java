package jp.mzw.revajaxmutator.parser;

public class RepairSource implements Comparable<RepairSource> {

	private String value;
	private Type type;

	public RepairSource(String value, Type type) {
		this.value = value;
		this.type = type;
	}

	@Override
	public int compareTo(RepairSource o) {
		if (this.value.equals(o.getValue()))
			return 0;
		else
			return 1;
	}

	public enum Type {
		DEFAULT(0), TESTCASE(1), JavaScript(2), HTML(3), NONE(4);
		int weight;
		Type(int weight) {
			this.weight = weight;
		}
		public int getWeight() {
			return this.weight;
		}
	}

	public String getValue() {
		return this.value;
	}

	public Type getType() {
		return this.type;
	}

	public static Type getType(String name) {
		for(Type type : Type.values()) {
			if(type.name().equals(name)) {
				return type;
			}
		}
		return Type.NONE;
	}
}
