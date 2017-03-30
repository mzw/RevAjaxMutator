package jp.mzw.revajaxmutator.test.result;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class CoverageTest {

	protected static File jscoverReportDir;

	@BeforeClass
	public static void setUpBeforeClass() {
		jscoverReportDir = new File("src/test/resources/jscover-test/quizzy");
	}

	@Test
	public void testGetFailureCoverageResults() throws IOException {
		List<File> files = Coverage.getFailureCoverageResults(jscoverReportDir);
		Assert.assertEquals(1, files.size());
		File expect = new File(jscoverReportDir, "jp.mzw.revajaxmutator.test.quizzy.QuizzyTest#clickQuizLabel/jscoverage.json");
		Assert.assertArrayEquals(expect.getPath().toCharArray(), files.get(0).getPath().toCharArray());
	}
	
	@Test
	public void testParse() throws JSONException, IOException {
		File file = new File(jscoverReportDir, "jp.mzw.revajaxmutator.test.quizzy.QuizzyTest#clickQuizLabel/jscoverage.json");
		JSONObject resut = Coverage.parse(file);
		Assert.assertNotNull(resut);
	}
	
}
