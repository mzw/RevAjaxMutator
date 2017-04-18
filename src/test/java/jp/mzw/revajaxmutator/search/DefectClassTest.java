package jp.mzw.revajaxmutator.search;

import org.junit.Assert;
import org.junit.Test;

import jp.mzw.ajaxmutator.mutatable.DOMAppending;
import jp.mzw.ajaxmutator.mutatable.DOMSelection;
import jp.mzw.ajaxmutator.mutatable.EventAttachment;
import jp.mzw.ajaxmutator.mutatable.Request;
import jp.mzw.ajaxmutator.mutatable.TimerEventAttachment;

public class DefectClassTest {

	@Test
	public void testDefectClass() {
		DefectClass[] defects = DefectClass.values();
		Assert.assertEquals(6, defects.length);
	}

	@Test
	public void testFromMutableNameEventAttachment() {
		DefectClass actual = DefectClass.fromMutableName(EventAttachment.class.getSimpleName());
		Assert.assertEquals(DefectClass.EVENT_ATTACHMENTS, actual);
		Assert.assertEquals(0, actual.getValue());
	}

	@Test
	public void testFromMutableNameTimerEventAttachment() {
		DefectClass actual = DefectClass.fromMutableName(TimerEventAttachment.class.getSimpleName());
		Assert.assertEquals(DefectClass.TIMER_EVENT_ATTACHMENTS, actual);
		Assert.assertEquals(1, actual.getValue());
	}

	@Test
	public void testFromMutableNameRequest() {
		DefectClass actual = DefectClass.fromMutableName(Request.class.getSimpleName());
		Assert.assertEquals(DefectClass.REQUESTS, actual);
		Assert.assertEquals(0, actual.getValue());
	}

	@Test
	public void testFromMutableNameDomModification() {
		DefectClass actual = DefectClass.fromMutableName(DOMAppending.class.getSimpleName());
		Assert.assertEquals(DefectClass.DOM_MODIFICATION, actual);
		Assert.assertEquals(0, actual.getValue());
	}

	@Test
	public void testFromMutableNameDomSelection() {
		DefectClass actual = DefectClass.fromMutableName(DOMSelection.class.getSimpleName());
		Assert.assertEquals(DefectClass.DOM_SELECTIONS, actual);
		Assert.assertEquals(1, actual.getValue());
	}

	@Test
	public void testFromInvalidMutableName() {
		DefectClass actual = DefectClass.fromMutableName("invalid");
		Assert.assertNull(actual);
	}
}
