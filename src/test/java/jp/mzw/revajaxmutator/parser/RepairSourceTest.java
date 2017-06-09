package jp.mzw.revajaxmutator.parser;

import org.junit.Assert;
import org.junit.Test;

public class RepairSourceTest {

	@Test(expected = NullPointerException.class)
	public void testRepairSourceInvalid() {
		new RepairSource(null, null);
	}
	
	@Test
	public void testRepairSource() {
		RepairSource param = new RepairSource("document", RepairSource.Type.Default);
		Assert.assertArrayEquals("document".toCharArray(), param.getValue().toCharArray());
		Assert.assertTrue(RepairSource.Type.Default.equals(param.getType()));
	}
	
	@Test
	public void testGetType() {
		Assert.assertTrue(RepairSource.Type.Default.equals(RepairSource.getType("Default")));
		Assert.assertTrue(RepairSource.Type.TestCase.equals(RepairSource.getType("TestCase")));
		Assert.assertTrue(RepairSource.Type.JavaScript.equals(RepairSource.getType("JavaScript")));
		Assert.assertTrue(RepairSource.Type.HTML.equals(RepairSource.getType("HTML")));
		Assert.assertTrue(RepairSource.Type.None.equals(RepairSource.getType("invalid")));
	}
	
	@Test
	public void testType() {
		Assert.assertEquals(5, RepairSource.Type.values().length);
	}
	
	@Test
	public void testCompareTo() {
		RepairSource param1 = new RepairSource("document", RepairSource.Type.Default);
		RepairSource param2 = new RepairSource("document", RepairSource.Type.None);
		RepairSource param3 = new RepairSource("window", RepairSource.Type.Default);
		RepairSource param4 = new RepairSource("window", RepairSource.Type.None);
		Assert.assertEquals(0, param1.compareTo(param2));
		Assert.assertEquals(1, param1.compareTo(param3));
		Assert.assertEquals(1, param1.compareTo(param4));
	}
}
