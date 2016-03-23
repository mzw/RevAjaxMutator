package jp.mzw.revajaxmutator.parser;

public class RepairSource implements Comparable<RepairSource>{

	private String value;
	private Type type;

	public RepairSource(String value, Type type) {
		this.value = value;
		this.type = type;
	}

	@Override
	public int compareTo(RepairSource o) {	
		if(this.value.equals(o.getValue())) return 0;
		else return 1;
	}
	
	public enum Type {
		DEFAULT,
		TESTCASE,
		JavaScript,
		HTML,
		NONE,
	}
	
	public String getValue() {
		return this.value;
	}
	
	public Type getType() {
		return this.type;
	}
	
//	public int getOrder() {
//		switch(this.type) {
//		case DEFAULT:
//			return 0;
//		case TESTCASE:
//			return 1;
//		case JavaScript:
//			return 2;
//		case HTML:
//			return 3;
//		case NONE:
//			return 4;
//		}
//		return Integer.MAX_VALUE;
//	}
	
}
