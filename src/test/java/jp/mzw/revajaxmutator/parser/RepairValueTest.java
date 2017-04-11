package jp.mzw.revajaxmutator.parser;

import org.junit.Assert;
import org.junit.Test;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;

import jp.mzw.ajaxmutator.util.StringToAst;

public class RepairValueTest {

	@Test
	public void testType() {
		Assert.assertEquals(4, RepairValue.Type.values().length);
	}

	@Test
	public void testRepairValueFromMutation() {
		String string = "$('#id').on('event', callback);";
		AstRoot ast = StringToAst.parseAstRoot(string);
		RepairValue value = new RepairValue(ast);
		Assert.assertNotNull(value);
		Assert.assertEquals(RepairValue.Type.Mutable, value.getType());
		Assert.assertArrayEquals(string.trim().toCharArray(), value.getValue().trim().toCharArray());
		Assert.assertEquals(RepairSource.Type.JavaScript, value.getRepairSourceType());
	}

	@Test(expected = NullPointerException.class)
	public void testRepairValueFromNullMutation() {
		AstNode _null = (AstNode) null;
		new RepairValue(_null);
	}

	@Test
	public void testRepairValueFromRepairSource() {
		String string = "$('#id').on('event', callback);";
		RepairSource.Type rstype = RepairSource.Type.Default;
		RepairSource source = new RepairSource(string, rstype);
		RepairValue value = new RepairValue(source);
		Assert.assertNotNull(value);
		Assert.assertEquals(RepairValue.Type.RepairSource, value.getType());
		Assert.assertArrayEquals(string.trim().toCharArray(), value.getValue().trim().toCharArray());
		Assert.assertEquals(rstype, value.getRepairSourceType());
	}

	@Test(expected = NullPointerException.class)
	public void testRepairValueFromNullRepairSource() {
		RepairSource _null = (RepairSource) null;
		new RepairValue(_null);
	}

	@Test
	public void testRepairValueFromText() {
		String string = "$('#id').on('event', callback);";
		RepairValue value = new RepairValue(string);
		Assert.assertNotNull(value);
		Assert.assertEquals(RepairValue.Type.Text, value.getType());
		Assert.assertArrayEquals(string.trim().toCharArray(), value.getValue().trim().toCharArray());
		Assert.assertEquals(RepairSource.Type.JavaScript, value.getRepairSourceType());
	}

	@Test(expected = NullPointerException.class)
	public void testRepairValueFromNullText() {
		String _null = (String) null;
		new RepairValue(_null);
	}

	@Test
	public void testRepairValue() {
		RepairValue value = new RepairValue();
		Assert.assertEquals(RepairValue.Type.None, value.getType());
		Assert.assertArrayEquals("".toCharArray(), value.getValue().toCharArray());
		Assert.assertEquals(RepairSource.Type.None, value.getRepairSourceType());
	}
}
