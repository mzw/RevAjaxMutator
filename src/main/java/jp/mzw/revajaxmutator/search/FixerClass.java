package jp.mzw.revajaxmutator.search;

public enum FixerClass {
	EVENT_TARGET(0, "EventTargetTSFixer"), EVENT_TYPE(1, "EventTypeTSFixer"), EVENT_CALLBACK(
			2, "EventCallbackERFixer"), TIMER_EVENT_DURATION(0,
			"TimerEventDurationVIFixer"), TIMER_EVENT_CALLBACK(1,
			"TimerEventCallbackERFixer"), REQUEST_ON_SUCCESS_HANDLER(0,
			"RequestOnSuccessHandlerERFixer"), REQUEST_METHOD(1,
			"RequestMethodRAFixer"), REQUEST_URL(2, "RequestURLVIFixer"), REQUEST_RESPONSE_BODY(
			3, "RequestResponseBodyVIFixer"), REQUEST_MOOTOOL(1,
			"MootoolsRequestMethodFixer"), APPENDED_DOM(0, "AppendedDOMRAFixer"), DOM_CREATION_TO_NO_OP(
			0, "DOMCreationToNoOpFixer"), DOM_REMOVAL_TO_NO_OP(0,
			"DOMRemovalToNoOpFixer"), DOM_CLONING_TO_NO_OP(0,
			"DOMCloningToNoOpFixer"), DOM_NORMALIZATION_TO_NO_OP(0,
			"OMNormalizationToNoOpFixer"), DOM_REPLACEMENT_SRC_TARGET(0,
			"DOMReplacementSrcTargetFixer"), DOM_SELECTION_SELECT_NEARBY(0,
			"DOMSelectionSelectNearbyFixer"), DOM_SELECTION_ATTRIBUTE(1,
			"DOMSelectionAtrributeFixer"), ATTRIBUTE_MODIFICATION_TARGET(0,
			"AttributeModificationTargetVIFixer"), ATTRIBUTE_MODIFICATION_VALUE(
			1, "AttributeModificationValueERFixer");

	private int value;
	private String name;

	private FixerClass(int value, String name) {
		this.value = value;
		this.name = name;
	}

	public int getValue() {
		return this.value;
	}

	public static FixerClass fromStringName(String name) {
		for (int i = 0; i < values().length; i++) {
			if (values()[i].name.equals(name)) {
				return values()[i];
			}
		}
		return null;
	}
}
