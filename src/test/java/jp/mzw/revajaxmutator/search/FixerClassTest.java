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
		FixerClass fixer = FixerClass.fromMutableName(EventTargetTSFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.EVENT_TARGET, fixer);
		Assert.assertEquals(0, fixer.getValue());
	}

	@Test
	public void testEventType() {
		FixerClass fixer = FixerClass.fromMutableName(EventTypeTSFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.EVENT_TYPE, fixer);
		Assert.assertEquals(1, fixer.getValue());
	}

	@Test
	public void testEventCallback() {
		FixerClass fixer = FixerClass.fromMutableName(EventCallbackERFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.EVENT_CALLBACK, fixer);
		Assert.assertEquals(2, fixer.getValue());
	}

	@Test
	public void testTimerEventDuration() {
		FixerClass fixer = FixerClass.fromMutableName(TimerEventDurationVIFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.TIMER_EVENT_DURATION, fixer);
		Assert.assertEquals(0, fixer.getValue());
	}

	@Test
	public void testTimeEventCallback() {
		FixerClass fixer = FixerClass.fromMutableName(TimerEventCallbackERFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.TIMER_EVENT_CALLBACK, fixer);
		Assert.assertEquals(1, fixer.getValue());
	}

	@Test
	public void testRequestOnSuccessHandler() {
		FixerClass fixer = FixerClass.fromMutableName(RequestOnSuccessHandlerERFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.REQUEST_ON_SUCCESS_HANDLER, fixer);
		Assert.assertEquals(0, fixer.getValue());
	}

	@Test
	public void testRequestMethod() {
		FixerClass fixer = FixerClass.fromMutableName(RequestMethodRAFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.REQUEST_METHOD, fixer);
		Assert.assertEquals(1, fixer.getValue());
	}

	@Test
	public void testRequestUrl() {
		FixerClass fixer = FixerClass.fromMutableName(RequestURLVIFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.REQUEST_URL, fixer);
		Assert.assertEquals(2, fixer.getValue());
	}

	@Test
	public void testRequestResponseBody() {
		FixerClass fixer = FixerClass.fromMutableName(RequestResponseBodyVIFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.REQUEST_RESPONSE_BODY, fixer);
		Assert.assertEquals(3, fixer.getValue());
	}

	@Test
	public void testAppendedDom() {
		FixerClass fixer = FixerClass.fromMutableName(AppendedDOMRAFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.APPENDED_DOM, fixer);
		Assert.assertEquals(0, fixer.getValue());
	}

	@Test
	public void testDomCreationToNoOp() {
		FixerClass fixer = FixerClass.fromMutableName(DOMCreationToNoOpFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.DOM_CREATION_TO_NO_OP, fixer);
		Assert.assertEquals(0, fixer.getValue());
	}

	@Test
	public void testDomRemovalToNoOp() {
		FixerClass fixer = FixerClass.fromMutableName(DOMRemovalToNoOpFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.DOM_REMOVAL_TO_NO_OP, fixer);
		Assert.assertEquals(0, fixer.getValue());
	}

	@Test
	public void testDomCloningToNoOp() {
		FixerClass fixer = FixerClass.fromMutableName(DOMCloningToNoOpFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.DOM_CLONING_TO_NO_OP, fixer);
		Assert.assertEquals(0, fixer.getValue());
	}

	@Test
	public void testDomNormalizationToNoOp() {
		FixerClass fixer = FixerClass.fromMutableName(DOMNormalizationToNoOpFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.DOM_NORMALIZATION_TO_NO_OP, fixer);
		Assert.assertEquals(0, fixer.getValue());
	}

	@Test
	public void testDomReplacementSrcTarget() {
		FixerClass fixer = FixerClass.fromMutableName(DOMReplacementSrcTargetFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.DOM_REPLACEMENT_SRC_TARGET, fixer);
		Assert.assertEquals(0, fixer.getValue());
	}

	@Test
	public void testDomSelectionSelectNeaby() {
		FixerClass fixer = FixerClass.fromMutableName(DOMSelectionSelectNearbyFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.DOM_SELECTION_SELECT_NEARBY, fixer);
		Assert.assertEquals(0, fixer.getValue());
	}

	@Test
	public void testDomSelectionAttribute() {
		FixerClass fixer = FixerClass.fromMutableName(DOMSelectionAtrributeFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.DOM_SELECTION_ATTRIBUTE, fixer);
		Assert.assertEquals(1, fixer.getValue());
	}

	@Test
	public void testAttributeModificationTarget() {
		FixerClass fixer = FixerClass.fromMutableName(AttributeModificationTargetVIFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.ATTRIBUTE_MODIFICATION_TARGET, fixer);
		Assert.assertEquals(0, fixer.getValue());
	}

	@Test
	public void testAttributeModificationValue() {
		FixerClass fixer = FixerClass.fromMutableName(AttributeModificationValueERFixer.class.getSimpleName());
		Assert.assertEquals(FixerClass.ATTRIBUTE_MODIFICATION_VALUE, fixer);
		Assert.assertEquals(1, fixer.getValue());
	}

	@Test
	public void voidTestInvalidMutableName() {
		FixerClass fixer = FixerClass.fromMutableName("invalid");
		Assert.assertNull(fixer);
	}
}
