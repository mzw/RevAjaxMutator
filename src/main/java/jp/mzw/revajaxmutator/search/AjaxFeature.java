package jp.mzw.revajaxmutator.search;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * Represents Ajax features consisting of event-driven, asynchronous communication, and DOM manipulation.
 * 
 * @author Yuta Maezawa
 *
 */
public enum AjaxFeature {

	/** Three types of Ajax features */
	EVENT_DRIVEN_MODEL(0), ASYNCHRONOUS_COMMUNICATION(1), DOM_MANIPULATION(2), UNKNOWN(3);

	/**
	 * Integer value representing each Ajax feature. (0, 1, 2) = (Event_Driven_Model, ASYNCHRONOUS_COMMUNICATION, DOM_MANIPULATION)
	 */
	private int value;

	/**
	 * Constructor
	 * 
	 * @param value represents each Ajax feature
	 */
	private AjaxFeature(final int value) {
		this.value = value;
	}

	/**
	 * Get integer value representing each Ajax feature
	 * 
	 * @return integer value representing each Ajax feature
	 */
	public int getValue() {
		return this.value;
	}

	/**
	 * Get Ajax feature according to given mutable name
	 * 
	 * @param name is name of mutable syntax element
	 * @return Ajax feature if valid name, otherwise null
	 */
	public static AjaxFeature fromMutatableName(final String name) {
		final Set<String> eventDrivenModel = ImmutableSet.of("EventAttachment", "TimerEventAttachment");
		final Set<String> asynchronousCommunication = ImmutableSet.of("Request");
		final Set<String> domManipulation = ImmutableSet.of("DOMAppending", "DOMCloning", "DOMCreation", "DOMNormalization", "DOMRemoval", "DOMReplacement",
				"AttributeModification", "DOMSelection");
		if (eventDrivenModel.contains(name)) {
			return values()[0]; // event-driven
		} else if (asynchronousCommunication.contains(name)) {
			return values()[1]; // asynchronous
		} else if (domManipulation.contains(name)) {
			return values()[2]; // dom
		} else {
			return UNKNOWN;
		}
	}
}
