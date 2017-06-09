package jp.mzw.revajaxmutator.search;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

public class SortKeyTest {
	
	@Test
	public void testCalculateSimpleAjaxFeatureAndDefectClassPrioritizationWeight() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = SortKey.class.getDeclaredMethod("calculateSimpleAjaxFeatureAndDefectClassPrioritizationWeight");
		method.setAccessible(true);
		int actual = (int) method.invoke(new SortKey(0, "Request", "EventTargetTSFixer", "Default"));
		Assert.assertEquals(10, actual);
	}
	
	// TODO implement test cases for weight calculation functionality with each weighting ways
}
