package jp.mzw.revajaxmutator.search;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public enum AjaxFeature {
	EVENT_DRIVEN_MODEL(0), ASYNCHRONOUS_COMMUNICATION(1), DOM_MANIPULATION(2);

	private int value;

	private AjaxFeature(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public static AjaxFeature fromMutatableName(String name) {
		final Set<String> eventDrivenModel = ImmutableSet.of("EventAttachment",
				"TimerEventAttachment");
		final Set<String> asynchronousCommunication = ImmutableSet
				.of("Request");
		final Set<String> domManipulation = ImmutableSet.of("DOMAppending",
				"DOMCloning", "DOMCreation", "DOMNormalization", "DOMRemoval",
				"DOMReplacement", "AttributeModification", "DOMSelection");
		if (eventDrivenModel.contains(name)) {
			return values()[0];// eventdriven
		} else if (asynchronousCommunication.contains(name)) {
			return values()[1];// asynchronous
		} else if (domManipulation.contains(name)) {
			return values()[2];// dom
		} else {
			return null;
		}
	}
}
