package jp.mzw.revajaxmutator.config.mutation;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.mzw.revajaxmutator.config.mutation.defaults.DefaultParameters;
import jp.mzw.revajaxmutator.parser.RepairSource;

public class ConfigHelperTest {

	private static File RECORDED_DIR = new File("src/test/resources/record-test/quizzy");
	private static String HTML_FILENAME = "http%3A%2F%2Fmzw.jp%3A80%2Fyuta%2Fresearch%2Fram%2Fexample%2Fafter%2Ffaulty%2Fquizzy%2Fmain.php";
	private static String JAVASCRIPT_FILENAME = "http%3A%2F%2Fmzw.jp%3A80%2Fyuta%2Fresearch%2Fram%2Fexample%2Fafter%2Ffaulty%2Fquizzy%2Fquizzy%2Fquizzy.js";
	private static String PATH_TO_TESTCASE_FILE = "src/test/resources/src-test/test/java/jp/mzw/revajaxmutator/test/quizzy/QuizzyTest.java";

	@Test
	public void testConfigHelper() {
		Assert.assertNotNull(new ConfigHelper());
	}

	@Test
	public void testParseHtmlWithValidFile() throws IOException {
		final ConfigHelper instance = new ConfigHelper();
		instance.parseHtml(new File(RECORDED_DIR, HTML_FILENAME));
	}

	@Test(expected = IOException.class)
	public void testParseHtmlWithInvalidFile() throws IOException {
		final ConfigHelper instance = new ConfigHelper();
		instance.parseHtml(new File("dir", "file.html"));
	}

	@Test
	public void testParseTestCaseWithValidFile() throws IOException {
		final ConfigHelper instance = new ConfigHelper();
		instance.parseTestCase(new File(PATH_TO_TESTCASE_FILE));
	}

	@Test(expected = IOException.class)
	public void testParseTestCaseWithInvalidFile() throws IOException {
		final ConfigHelper instance = new ConfigHelper();
		instance.parseTestCase(new File("path/to/src/test/java/package/class.java"));
	}

	@Test
	public void testParseJavaScriptWithValidFile() throws IOException {
		final ConfigHelper instance = new ConfigHelper();
		instance.parseJavaScript(new File(RECORDED_DIR, JAVASCRIPT_FILENAME));
	}

	@Test(expected = IOException.class)
	public void testParseJavaScriptWithInvalidFile() throws IOException {
		final ConfigHelper instance = new ConfigHelper();
		instance.parseJavaScript(new File("dir", "file.js"));
	}

	private static ConfigHelper helper;

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		helper = new ConfigHelper();
		helper.parseHtml(new File(RECORDED_DIR, HTML_FILENAME));
		helper.parseTestCase(new File(PATH_TO_TESTCASE_FILE));
		helper.parseJavaScript(new File(RECORDED_DIR, JAVASCRIPT_FILENAME));
	}

	@Test
	public void testGetRepairSourcesForDomSelectionAttributeFixer() {
		final Collection<? extends RepairSource> parameters = helper.getRepairSourcesForDomSelectionAttributeFixer();
		assertRepairSourceNums(parameters, DefaultParameters.DOM_SELECTION_ATTRIBUTE_VALUES.size(), 0, 8, 6, 0);
	}

	@Test
	public void testGetRepairSourcesForEventTarget() {
		final Collection<? extends RepairSource> parameters = helper.getRepairSourcesForEventTarget();
		assertRepairSourceNums(parameters, DefaultParameters.TARGET_ELEMENTS_HANDLING_EVENT.size(), 0, 0, 6, 0);
	}

	@Test
	public void testGetRepairSourcesForEventType() {
		final Collection<? extends RepairSource> parameters = helper.getRepairSourcesForEventType();
		assertRepairSourceNums(parameters, 0, DefaultParameters.EVENT_TYPES_MOUSE.size(), 0, 0, 0);
	}

	@Test
	public void testGetRepairSourcesForEventCallback() {
		final Collection<? extends RepairSource> parameters = helper.getRepairSourcesForEventCallback();
		assertRepairSourceNums(parameters, 0, 7, 0, 0, 0);
	}

	@Test
	public void testGetRepairSourcesForAttributeValues() {
		final Collection<? extends RepairSource> parameters = helper.getRepairSourcesForAttributeValues();
		assertRepairSourceNums(parameters, 0, 1, 23, 0, 0);
	}

	@Test
	public void testGetRepairSourcesForTimerEventDuration() {
		final Collection<? extends RepairSource> durations = helper.getRepairSourcesForTimerEventDuration();
		for (final RepairSource duration : durations) {
			final Integer actual = Integer.parseInt(duration.getValue());
			Assert.assertTrue(DefaultParameters.DURATIONS.contains(actual));
		}
		for (final Integer expect : DefaultParameters.DURATIONS) {
			boolean contains = false;
			for (final RepairSource duration : durations) {
				final Integer actual = Integer.parseInt(duration.getValue());
				if (actual.equals(expect)) {
					contains = true;
					break;
				}
			}
			Assert.assertTrue(contains);
		}
	}

	private static void assertRepairSourceNums(final Collection<? extends RepairSource> parameters,
			final int expectedDefaultNum, final int expectedJsNum, final int expectedHtmlNum,
			final int expectedTestcaseNum, final int expectedNoneNum) {
		int defaults = 0;
		int js = 0;
		int html = 0;
		int testcases = 0;
		int none = 0;
		for (final RepairSource param : parameters) {
			if (RepairSource.Type.Default.equals(param.getType())) {
				defaults++;
			} else if (RepairSource.Type.JavaScript.equals(param.getType())) {
				js++;
			} else if (RepairSource.Type.HTML.equals(param.getType())) {
				html++;
			} else if (RepairSource.Type.TestCase.equals(param.getType())) {
				testcases++;
			} else if (RepairSource.Type.None.equals(param.getType())) {
				none++;
			} else {
				Assert.fail("Unknown type of RepairSource");
			}
		}
		Assert.assertEquals("The number of parameters from default pool", expectedDefaultNum, defaults);
		Assert.assertEquals("The number of parameters from javascript file", expectedJsNum, js);
		Assert.assertEquals("The number of parameters from html file", expectedHtmlNum, html);
		Assert.assertEquals("The number of parameters from test-case file", expectedTestcaseNum, testcases);
		Assert.assertEquals("The number of none parameters", expectedNoneNum, none);
	}
}
