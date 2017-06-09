package jp.mzw.revajaxmutator.test.result;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.mzw.revajaxmutator.test.result.TestResult;

public class TestResultTest {

	protected static File jscoverReportDir;

	@BeforeClass
	public static void setUpBeforeClass() {
		jscoverReportDir = new File("src/test/resources/jscover-test/quizzy");
	}

	@Test
	public void testParseTestResults() throws IOException {
		List<TestResult> results = TestResult.parseTestResults(jscoverReportDir);
		Assert.assertEquals(2, results.size());
		
		TestResult result1 = results.get(0);
		Assert.assertArrayEquals("jp.mzw.revajaxmutator.test.quizzy.QuizzyTest".toCharArray(), result1.getClassName().toCharArray());
		Assert.assertArrayEquals("clickQuizLabel".toCharArray(), result1.getMethodName().toCharArray());
		Assert.assertEquals(1, result1.getRunCount());
		Assert.assertEquals(0, result1.getIgnoreCount());
		Assert.assertEquals(1, result1.getFailureCount());

		TestResult result2 = results.get(1);
		Assert.assertArrayEquals("jp.mzw.revajaxmutator.test.quizzy.QuizzyTest".toCharArray(), result2.getClassName().toCharArray());
		Assert.assertArrayEquals("clickQuizButton".toCharArray(), result2.getMethodName().toCharArray());
		Assert.assertEquals(1, result2.getRunCount());
		Assert.assertEquals(0, result2.getIgnoreCount());
		Assert.assertEquals(0, result2.getFailureCount());
	}
	
}
