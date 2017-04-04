package jp.mzw.revajaxmutator.test.result;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class CoverageTest {

	protected static File jscoverReportDir;
	protected static File recordedJsFile;

	@BeforeClass
	public static void setUpBeforeClass() {
		jscoverReportDir = new File("src/test/resources/jscover-test/quizzy");
		recordedJsFile = new File("jscover/quizzy/http%3A%2F%2Fmzw.jp%3A80%2Fyuta%2Fresearch%2Fram%2Fexample%2Fbefore%2Finitial%2Fquizzy%2Fquizzy%2Fquizzy.js");
	}

	@Test
	public void testGetCoverageResults() throws IOException {
		List<File> files = Coverage.getCoverageResults(jscoverReportDir);
		Assert.assertEquals(2, files.size());
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

	@Test
	public void testGetTargetCoverageResults() throws JSONException, IOException {
		List<File> files = Coverage.getCoverageResults(jscoverReportDir);
		Map<File, boolean[]> results = Coverage.getTargetCoverageResults(files, recordedJsFile);
		Assert.assertEquals(2, results.size());
	}

	@Test
	public void testGetCoverFreq() {
		Assert.assertEquals(0, Coverage.getCoverFreq(null));
		Assert.assertEquals(0, Coverage.getCoverFreq(new Integer(0)));
		Assert.assertEquals(1, Coverage.getCoverFreq(new Integer(1)));
		Assert.assertEquals(0, Coverage.getCoverFreq(new Integer(1).toString()));
	}

	@Test
	public void testGetCoverageResultsFromJsonObject() throws JSONException, IOException {
		File file = new File("src/test/resources/jscover-test/quizzy/jp.mzw.revajaxmutator.test.quizzy.QuizzyTest#clickQuizLabel/jscoverage.json");
		JSONObject json = Coverage.parse(file);
		Assert.assertNull(Coverage.getCoverageResults(json, ""));
		Assert.assertNotNull(Coverage.getCoverageResults(json, "/yuta/research/ram/example/before/initial/quizzy/quizzy/quizzy.js"));
	}

	@Test
	public void testIsCovered() throws JSONException, IOException {
		List<File> files = Coverage.getCoverageResults(jscoverReportDir);
		Map<File, boolean[]> results = Coverage.getTargetCoverageResults(files, recordedJsFile);
		Assert.assertFalse(Coverage.isCovered(results, 0, 3));
		Assert.assertTrue(Coverage.isCovered(results, 4, 6));
	}

	@Test
	public void testIsCovetedWithTestMethodName() throws JSONException, IOException {
		List<File> files = Coverage.getCoverageResults(jscoverReportDir);
		Map<File, boolean[]> results = Coverage.getTargetCoverageResults(files, recordedJsFile);
		Assert.assertFalse(Coverage.isCovered(results, 0, 3, "jp.mzw.revajaxmutator.test.quizzy.QuizzyTest#clickQuizLabel"));
		Assert.assertTrue(Coverage.isCovered(results, 4, 6, "jp.mzw.revajaxmutator.test.quizzy.QuizzyTest#clickQuizLabel"));
	}

	@Test
	public void testGetTestMethodName() {
		File file = new File("src/test/resources/jscover-test/quizzy/jp.mzw.revajaxmutator.test.quizzy.QuizzyTest#clickQuizLabel/jscoverage.json");
		String actual = Coverage.getTestMethodName(file);
		Assert.assertArrayEquals("jp.mzw.revajaxmutator.test.quizzy.QuizzyTest#clickQuizLabel".toCharArray(), actual.toCharArray());
	}
}
