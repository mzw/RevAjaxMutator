package jp.mzw.revajaxmutator.search;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public enum DefectClass {
	// AjaxFeature:Event
	EVENT_ATTACHMENTS(0, "EventAttachment"), TIMER_EVENT_ATTACHMENTS(1,
			"TimerEventAttachment"),
	// AjaxFeature:Request
	REQUESTS(0, "Request"),
	// AjaxFeature:DOM
	// DOM_APPENDINGS(0, "DOMAppending"), DOM_CLONINGS(1, "DOMCloning"),
	// DOM_CREATIONS(
	// 2, "DOMCreation"), DOM_NORMALIZATIONS(3, "DOMNormalization"),
	// DOM_REMOVALS(
	// 4, "DOMRemoval"), DOM_REPLACEMENTS(5, "DOMReplacement"),
	// DOM_SELECTIONS(6, "DOMSelection"), ATTRIBUTE_MODIFICATIONS(7,
	// "AttributeModification");
	DOM_MODIFICATION(0, "DOMOModification"), DOM_SELECTIONS(1, "DOMSelection"), ATTRIBUTE_MODIFICATIONS(
			2, "AttributeModification");

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

	public int[][] permutationTable4Event = { { 0, 1 }, { 1, 0 } };

	public int[][] permutationTable4DOM = { { 0, 1, 2 }, { 0, 2, 1 },
			{ 1, 0, 2 }, { 1, 2, 0 }, { 2, 0, 1 }, { 2, 1, 0 } };

	public int getValueForAllPermutationsTrial(int num4Event, int num4DOM) {
		switch (this) {
		case EVENT_ATTACHMENTS:
		case TIMER_EVENT_ATTACHMENTS:
			if (this.value > 1) {
				return this.value;
			}
			return permutationTable4Event[num4Event][this.value];
		case DOM_MODIFICATION:
		case DOM_SELECTIONS:
		case ATTRIBUTE_MODIFICATIONS:
			if (this.value > 2) {
				return this.value;
			}
			return permutationTable4DOM[num4DOM][this.value];
		default:
			return this.value;
		}
	}
}
