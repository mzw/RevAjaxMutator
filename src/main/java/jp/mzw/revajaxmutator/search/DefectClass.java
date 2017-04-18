package jp.mzw.revajaxmutator.search;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * Represents fault class
 * 
 * @author Yuta Maezawa
 *
 */
public enum DefectClass {

	/** AjaxFeature:Event */
	EVENT_ATTACHMENTS(0, "EventAttachment"),

	/** AjaxFeature:Event */
	TIMER_EVENT_ATTACHMENTS(1, "TimerEventAttachment"),

	/** AjaxFeature:Request */
	REQUESTS(0, "Request"),

	/** AjaxFeature:DOM */
	DOM_MODIFICATION(0, "DOMOModification"),

	/** AjaxFeature:DOM */
	DOM_SELECTIONS(1, "DOMSelection"),

	/** AjaxFeature:DOM */
	ATTRIBUTE_MODIFICATIONS(2, "AttributeModification");

	/** Set of mutable names relevant to DOM modification */
	static Set<String> domModificationSet = ImmutableSet.of("DOMAppending", "DOMCloning", "DOMCreation", "DOMNormalization", "DOMRemoval", "DOMReplacement");

	/** Weight to apply repair operators */
	int value;

	/** Corresponding mutable name */
	private String name;

	/**
	 * Constructor
	 * 
	 * @param value is weight to apply repair operators
	 * @param name representing corresponding mutable
	 */
	private DefectClass(int value, String name) {
		this.value = value;
		this.name = name;
	}

	/**
	 * Get DefectClass from given mutable name
	 * 
	 * @param name should represent mutable
	 * @return null if invalid mutable name is given
	 */
	public static DefectClass fromMutableName(String name) {
		if (domModificationSet.contains(name)) {
			return DefectClass.DOM_MODIFICATION;
		}
		for (DefectClass defect : DefectClass.values()) {
			if (defect.name.equals(name)) {
				return defect;
			}
		}
		return null;
	}

	/**
	 *
	 * @return integer value representing Ajax feature
	 */
	public int getValue() {
		return value;
	}

}
