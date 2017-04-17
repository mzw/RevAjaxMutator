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
	// AjaxFeature:Event
	EVENT_ATTACHMENTS(0, "EventAttachment"), TIMER_EVENT_ATTACHMENTS(1, "TimerEventAttachment"),
	// AjaxFeature:Request
	REQUESTS(0, "Request"),
	// AjaxFeature:DOM
	DOM_MODIFICATION(0, "DOMOModification"), DOM_SELECTIONS(1, "DOMSelection"), ATTRIBUTE_MODIFICATIONS(2, "AttributeModification");

	int value;
	private String name;

	static Set<String> domModificationSet = ImmutableSet.of("DOMAppending",
			"DOMCloning", "DOMCreation", "DOMNormalization", "DOMRemoval",
			"DOMReplacement");

	private DefectClass(int value, String name) {
		this.value = value;
		this.name = name;
	}

	public static DefectClass fromStringName(String name) {
		if (domModificationSet.contains(name)) {
			return DefectClass.DOM_MODIFICATION;
		}

		for (int i = 0; i < values().length; i++) {
			if (values()[i].name.equals(name)) {
				return values()[i];
			}
		}
		return null;
	}

	public int getValue() {
		return value;
	}

}
