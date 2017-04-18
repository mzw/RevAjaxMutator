package jp.mzw.revajaxmutator.search;

public enum FixerClass {

	EVENT_TARGET(0, "EventTargetTSFixer"),
	EVENT_TYPE(1, "EventTypeTSFixer"),
	EVENT_CALLBACK(2, "EventCallbackERFixer"),
	TIMER_EVENT_DURATION(0, "TimerEventDurationVIFixer"),
	TIMER_EVENT_CALLBACK(1, "TimerEventCallbackERFixer"),
	REQUEST_ON_SUCCESS_HANDLER(0,"RequestOnSuccessHandlerERFixer"),
	REQUEST_METHOD(1, "RequestMethodRAFixer"),
	REQUEST_URL(2, "RequestURLVIFixer"),
	REQUEST_RESPONSE_BODY(3, "RequestResponseBodyVIFixer"),
	APPENDED_DOM(0, "AppendedDOMRAFixer"),
	DOM_CREATION_TO_NO_OP(0, "DOMCreationToNoOpFixer"),
	DOM_REMOVAL_TO_NO_OP(0, "DOMRemovalToNoOpFixer"),
	DOM_CLONING_TO_NO_OP(0, "DOMCloningToNoOpFixer"),
	DOM_NORMALIZATION_TO_NO_OP(0, "DOMNormalizationToNoOpFixer"),
	DOM_REPLACEMENT_SRC_TARGET(0, "DOMReplacementSrcTargetFixer"),
	DOM_SELECTION_SELECT_NEARBY(0, "DOMSelectionSelectNearbyFixer"),
	DOM_SELECTION_ATTRIBUTE(1, "DOMSelectionAtrributeFixer"),
	ATTRIBUTE_MODIFICATION_TARGET(0, "AttributeModificationTargetVIFixer"),
	ATTRIBUTE_MODIFICATION_VALUE(1, "AttributeModificationValueERFixer");

	/** Weight to apply repair operators */
	private int value;

	/** Corresponding mutable name */
	private String name;

	/**
	 * Constructor
	 * 
	 * @param value is weight to apply repair operators
	 * @param name represents corresponding mutable
	 */
	private FixerClass(int value, String name) {
		this.value = value;
		this.name = name;
	}

	/**
	 * Get integer value representing weight to apply repair operators
	 * 
	 * @return weight to apply repair operators
	 */
	public int getValue() {
		return this.value;
	}

	/**
	 * Get FixerClass corresponding to given mutable name
	 * 
	 * @param name represents mutable
	 * @return null if invalid mutable name is given
	 */
	public static FixerClass fromMutableName(String name) {
		for (FixerClass fixer : FixerClass.values()) {
			if (fixer.name.equals(name)) {
				return fixer;
			}
		}
		return null;
	}
}
