package jp.mzw.revajaxmutator.config.mutation.defaults;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class DefaultParameters {
	
	/**
	 * Default target elements for DOM manipulation
	 * 
	 * Selected only 'document' among top-level BOM because others are not related to DOM.
	 * cf. {@link <a href="https://codescracker.com/js/js-window-object.htm">link</a>}
	 * 
	 * Selected 'this' because of its generic usage
	 * 
	 */
	public static final Set<String> DOM_SELECTION_ATTRIBUTE_VALUES = ImmutableSet.of("document", "this");

	/**
	 * Default target elements for event handlers
	 * 
	 * Selected top-level BOM
	 * cf. {@link <a href="https://codescracker.com/js/js-window-object.htm">link</a>}
	 * 
	 * Selected 'this' because of its generic usage
	 * 
	 */
	public static final Set<String> TARGET_ELEMENTS_HANDLING_EVENT = ImmutableSet.of("$(window)", "$(document)", "$(history)", "$(location)", "$(navigator)", "$(screen)", "$(this)");

	/** To add similar event types (related to keyboard) as that implemented in JavaScript file */
	public static final Set<String> EVENT_TYPES_KEYBOARD = ImmutableSet.of("textinput", "keyup", "keypress", "keydown");

	/** To add similar event types (related to mouse) as that implemented in JavaScript file */
	public static final Set<String> EVENT_TYPES_MOUSE = ImmutableSet.of("click", "contextmenu", "dblclick", "mousedown", "mouseup", "mouseenter", "mouseleave",
			"mouseover", "mousewheel", "wheel");

	/** To add similar event types (related to edit) as that implemented in JavaScript file */
	public static final Set<String> EVENT_TYPES_EDIT = ImmutableSet.of("change", "copy", "cut", "paste", "reset", "select", "textinput");

	/** Default durations for timer events */
	public static final Set<Integer> DURATIONS = ImmutableSet.of(0, 50, 100, 250, 500, 750, 1000);

}
