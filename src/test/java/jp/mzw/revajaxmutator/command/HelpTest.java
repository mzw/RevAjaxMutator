package jp.mzw.revajaxmutator.command;

import org.junit.Assert;
import org.junit.Test;

public class HelpTest {
	
	@Test
	public void testGetUsageContent() {
		String actual = new Help().getUsageContent();
		Assert.assertNotNull(actual);
	}
}
