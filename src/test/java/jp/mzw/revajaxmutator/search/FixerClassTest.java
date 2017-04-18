package jp.mzw.revajaxmutator.search;

import org.junit.Assert;
import org.junit.Test;

import jp.mzw.revajaxmutator.fixer.AppendedDOMRAFixer;
import jp.mzw.revajaxmutator.fixer.AttributeModificationTargetVIFixer;
import jp.mzw.revajaxmutator.fixer.AttributeModificationValueERFixer;
import jp.mzw.revajaxmutator.fixer.DOMCloningToNoOpFixer;
import jp.mzw.revajaxmutator.fixer.DOMCreationToNoOpFixer;
import jp.mzw.revajaxmutator.fixer.DOMNormalizationToNoOpFixer;
import jp.mzw.revajaxmutator.fixer.DOMRemovalToNoOpFixer;
import jp.mzw.revajaxmutator.fixer.DOMReplacementSrcTargetFixer;
import jp.mzw.revajaxmutator.fixer.DOMSelectionAtrributeFixer;
import jp.mzw.revajaxmutator.fixer.DOMSelectionSelectNearbyFixer;
import jp.mzw.revajaxmutator.fixer.EventCallbackERFixer;
import jp.mzw.revajaxmutator.fixer.EventTargetTSFixer;
import jp.mzw.revajaxmutator.fixer.EventTypeTSFixer;
import jp.mzw.revajaxmutator.fixer.RequestMethodRAFixer;
import jp.mzw.revajaxmutator.fixer.RequestOnSuccessHandlerERFixer;
import jp.mzw.revajaxmutator.fixer.RequestResponseBodyVIFixer;
import jp.mzw.revajaxmutator.fixer.RequestURLVIFixer;
import jp.mzw.revajaxmutator.fixer.TimerEventCallbackERFixer;
import jp.mzw.revajaxmutator.fixer.TimerEventDurationVIFixer;

public class FixerClassTest {

	@Test
	public void testFixerClass() {
		FixerClass[] fixers = FixerClass.values();
		Assert.assertEquals(19, fixers.length);
	}

	@Test
	public void testEventTarget() {
		FixerClass fixer = FixerClass.fromFixerName(EventTargetTSFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.EVENT_TARGET, fixer);
		Assert.assertEquals(0, fixer.getValue());
	}

	@Test
	public void testEventType() {
		FixerClass fixer = FixerClass.fromFixerName(EventTypeTSFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.EVENT_TYPE, fixer);
		Assert.assertEquals(1, fixer.getValue());
	}

	@Test
	public void testEventCallback() {
		FixerClass fixer = FixerClass.fromFixerName(EventCallbackERFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.EVENT_CALLBACK, fixer);
		Assert.assertEquals(2, fixer.getValue());
	}

	@Test
	public void testTimerEventDuration() {
		FixerClass fixer = FixerClass.fromFixerName(TimerEventDurationVIFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.TIMER_EVENT_DURATION, fixer);
		Assert.assertEquals(0, fixer.getValue());
	}

	@Test
	public void testTimeEventCallback() {
		FixerClass fixer = FixerClass.fromFixerName(TimerEventCallbackERFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.TIMER_EVENT_CALLBACK, fixer);
		Assert.assertEquals(1, fixer.getValue());
	}

	@Test
	public void testRequestOnSuccessHandler() {
		FixerClass fixer = FixerClass.fromFixerName(RequestOnSuccessHandlerERFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.REQUEST_ON_SUCCESS_HANDLER, fixer);
		Assert.assertEquals(0, fixer.getValue());
	}

	@Test
	public void testRequestMethod() {
		FixerClass fixer = FixerClass.fromFixerName(RequestMethodRAFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.REQUEST_METHOD, fixer);
		Assert.assertEquals(1, fixer.getValue());
	}

	@Test
	public void testRequestUrl() {
		FixerClass fixer = FixerClass.fromFixerName(RequestURLVIFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.REQUEST_URL, fixer);
		Assert.assertEquals(2, fixer.getValue());
	}

	@Test
	public void testRequestResponseBody() {
		FixerClass fixer = FixerClass.fromFixerName(RequestResponseBodyVIFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.REQUEST_RESPONSE_BODY, fixer);
		Assert.assertEquals(3, fixer.getValue());
	}

	@Test
	public void testAppendedDom() {
		FixerClass fixer = FixerClass.fromFixerName(AppendedDOMRAFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.APPENDED_DOM, fixer);
		Assert.assertEquals(0, fixer.getValue());
	}

	@Test
	public void testDomCreationToNoOp() {
		FixerClass fixer = FixerClass.fromFixerName(DOMCreationToNoOpFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.DOM_CREATION_TO_NO_OP, fixer);
		Assert.assertEquals(0, fixer.getValue());
	}

	@Test
	public void testDomRemovalToNoOp() {
		FixerClass fixer = FixerClass.fromFixerName(DOMRemovalToNoOpFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.DOM_REMOVAL_TO_NO_OP, fixer);
		Assert.assertEquals(0, fixer.getValue());
	}

	@Test
	public void testDomCloningToNoOp() {
		FixerClass fixer = FixerClass.fromFixerName(DOMCloningToNoOpFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.DOM_CLONING_TO_NO_OP, fixer);
		Assert.assertEquals(0, fixer.getValue());
	}

	@Test
	public void testDomNormalizationToNoOp() {
		FixerClass fixer = FixerClass.fromFixerName(DOMNormalizationToNoOpFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.DOM_NORMALIZATION_TO_NO_OP, fixer);
		Assert.assertEquals(0, fixer.getValue());
	}

	@Test
	public void testDomReplacementSrcTarget() {
		FixerClass fixer = FixerClass.fromFixerName(DOMReplacementSrcTargetFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.DOM_REPLACEMENT_SRC_TARGET, fixer);
		Assert.assertEquals(0, fixer.getValue());
	}

	@Test
	public void testDomSelectionSelectNeaby() {
		FixerClass fixer = FixerClass.fromFixerName(DOMSelectionSelectNearbyFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.DOM_SELECTION_SELECT_NEARBY, fixer);
		Assert.assertEquals(0, fixer.getValue());
	}

	@Test
	public void testDomSelectionAttribute() {
		FixerClass fixer = FixerClass.fromFixerName(DOMSelectionAtrributeFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.DOM_SELECTION_ATTRIBUTE, fixer);
		Assert.assertEquals(1, fixer.getValue());
	}

	@Test
	public void testAttributeModificationTarget() {
		FixerClass fixer = FixerClass.fromFixerName(AttributeModificationTargetVIFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.ATTRIBUTE_MODIFICATION_TARGET, fixer);
		Assert.assertEquals(0, fixer.getValue());
	}

	@Test
	public void testAttributeModificationValue() {
		FixerClass fixer = FixerClass.fromFixerName(AttributeModificationValueERFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.ATTRIBUTE_MODIFICATION_VALUE, fixer);
		Assert.assertEquals(1, fixer.getValue());
	}

	@Test
	public void voidTestInvalidFixerName() {
		FixerClass fixer = FixerClass.fromFixerName("invalid");
		Assert.assertNull(fixer);
	}
}
