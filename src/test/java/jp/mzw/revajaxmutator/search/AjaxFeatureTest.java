package jp.mzw.revajaxmutator.search;

import org.junit.Assert;
import org.junit.Test;

import jp.mzw.ajaxmutator.mutatable.DOMAppending;
import jp.mzw.ajaxmutator.mutatable.EventAttachment;
import jp.mzw.ajaxmutator.mutatable.Request;

public class AjaxFeatureTest {

	@Test
	public void testAjaxFeature() {
		AjaxFeature[] features = AjaxFeature.values();
		Assert.assertEquals(3, features.length);
	}

	@Test
	public void testFromMutableNameEventDriven() {
		String name = EventAttachment.class.getSimpleName();
		AjaxFeature feature = AjaxFeature.fromMutatableName(name);
		Assert.assertEquals(AjaxFeature.EVENT_DRIVEN_MODEL, feature);
		Assert.assertEquals(0, feature.getValue());
	}

	@Test
	public void testFromMutableNameAsyncComm() {
		String name = Request.class.getSimpleName();
		AjaxFeature feature = AjaxFeature.fromMutatableName(name);
		Assert.assertEquals(AjaxFeature.ASYNCHRONOUS_COMMUNICATION, feature);
		Assert.assertEquals(1, feature.getValue());
	}

	@Test
	public void testFromMutableNameDomManipl() {
		String name = DOMAppending.class.getSimpleName();
		AjaxFeature feature = AjaxFeature.fromMutatableName(name);
		Assert.assertEquals(AjaxFeature.DOM_MANIPULATION, feature);
		Assert.assertEquals(2, feature.getValue());
	}

	@Test
	public void testFormMutableNameInvalid() {
		AjaxFeature feature = AjaxFeature.fromMutatableName("foo");
		Assert.assertNull(feature);
	}
}
