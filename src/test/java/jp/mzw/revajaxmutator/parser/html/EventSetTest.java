package jp.mzw.revajaxmutator.parser.html;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Sets;

public class EventSetTest {

	private static final String target1 = "$(document)";
	private static final String target2 = "docuemnt.getElementById('id')";
	private static final String type1 = "click";
	private static final String type2 = "keyup";
	private static final String callback1 = "foo";
	private static final String callback2 = "callback";

	@Test
	public void testEventSet() {
		EventSet events = new EventSet();
		Assert.assertNotNull(events);
		Assert.assertEquals(0, events.getTargetSet().size());
		Assert.assertEquals(0, events.getTypeSet().size());
		Assert.assertEquals(0, events.getCallbackSet().size());
	}

	@Test
	public void testEventSetWithGivenSets() {
		EventSet events = new EventSet(Sets.newHashSet(target1, target2), Sets.newHashSet(type1, type2), Sets.newHashSet(callback1, callback2));
		Assert.assertNotNull(events);
		Assert.assertEquals(2, events.getTargetSet().size());
		Assert.assertEquals(2, events.getTypeSet().size());
		Assert.assertEquals(2, events.getCallbackSet().size());
	}

	@Test
	public void testAddTarget() {
		EventSet events = new EventSet(Sets.newHashSet(target1, target2), Sets.newHashSet(type1, type2), Sets.newHashSet(callback1, callback2));
		events.addTarget(target1);
		Assert.assertEquals(2, events.getTargetSet().size());
		events.addTarget("element");
		Assert.assertEquals(3, events.getTargetSet().size());
		try {
			events.addTarget(null);
			Assert.fail("Null should be excepted");
		} catch (NullPointerException e) {
			// expected
		}
	}

	@Test
	public void testAddType() {
		EventSet events = new EventSet(Sets.newHashSet(target1, target2), Sets.newHashSet(type1, type2), Sets.newHashSet(callback1, callback2));
		events.addType(type1);
		Assert.assertEquals(2, events.getTypeSet().size());
		events.addType("event");
		Assert.assertEquals(3, events.getTypeSet().size());
		try {
			events.addType(null);
			Assert.fail("Null should be excepted");
		} catch (NullPointerException e) {
			// expected
		}
	}

	@Test
	public void testAddCallback() {
		EventSet events = new EventSet(Sets.newHashSet(target1, target2), Sets.newHashSet(type1, type2), Sets.newHashSet(callback1, callback2));
		events.addCallback(callback1);
		Assert.assertEquals(2, events.getCallbackSet().size());
		events.addCallback("function");
		Assert.assertEquals(3, events.getCallbackSet().size());
		try {
			events.addCallback(null);
			Assert.fail("Null should be excepted");
		} catch (NullPointerException e) {
			// expected
		}
	}
}
