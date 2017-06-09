package jp.mzw.revajaxmutator.parser;

public class RepairSource implements Comparable<RepairSource> {

	/** Contains value used for program repair */
	private String value;
	
	/** Represents type of source which repair value comes from */
	private Type type;

	/**
	 * Constructor
	 * 
	 * @param value used for program repair
	 * @param type representing source which value comes from
	 */
	public RepairSource(String value, Type type) {
		if (value == null || type == null) {
			throw new NullPointerException("Both value and type should not be null");
		}
		this.value = value;
		this.type = type;
	}

	/**
	 * Return 0 when repair values are same regardless repair sources
	 * Otherwise return 1
	 * 
	 */
	@Override
	public int compareTo(RepairSource o) {
		if (this.value.equals(o.getValue()))
			return 0;
		else
			return 1;
	}

	/**
	 * Represents types of sources which repair values come from
	 * 
	 * @author Yuta Maezawa
	 *
	 */
	public enum Type {
		Default(0), TestCase(1), JavaScript(2), HTML(3), None(4);
		int weight;
		Type(int weight) {
			this.weight = weight;
		}
		public int getWeight() {
			return this.weight;
		}
	}

	/**
	 * Get value used for program repair
	 * 
	 * @return value used for program repair
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Get source which repair value comes from
	 * 
	 * @return type representing source which repair value comes from
	 */
	public Type getType() {
		return this.type;
	}

	/**
	 * Get source which repair value comes from by given type.
	 * If given type is invalid, return None type.
	 * 
	 * 
	 * @param name represents source type
	 * @return type representing source which repair value comes from
	 */
	public static Type getType(String name) {
		for(Type type : Type.values()) {
			if(type.name().equals(name)) {
				return type;
			}
		}
		return Type.None;
	}
}
