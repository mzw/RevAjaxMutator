package jp.mzw.revajaxmutator.parser.javascript;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

public class JavaScriptParserTest {

	@Test
	public void parse() throws IOException, URISyntaxException {
		final File file = this.getTestCaseFile("quizzy.js");
		final JavaScriptParser parser = new JavaScriptParser(file);
		assertNotNull(parser.getAstRoot());
	}

	@Test
	public void getAllFunctions() throws URISyntaxException, IOException {
		final File file = this.getTestCaseFile("quizzy.js");
		final JavaScriptParser parser = new JavaScriptParser(file);
		final Set<String> result = parser.getFunctionNames();
		final Set<String> expected = Sets.newHashSet("startQuiz", "requestNextQuestion", "checkQuestion",
				"restartQuizzy");
		final int expectedSize = expected.size();
		expected.retainAll(result);

		assertEquals(expectedSize, expected.size());
	}

	@Test
	public void getAttributeValuesFromInfixExpression() throws URISyntaxException, IOException {
		final File file = this.getTestCaseFile("parser-test/jsparser_test.js");
		final JavaScriptParser parser = new JavaScriptParser(file);
		assertEquals("\"myid\"", parser.getAttributeValuesFromInfixExpression().iterator().next());
	}

	/**
	 * Tests event types in the style of "el.on(<event_type>)"
	 *
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void getEventTypes1() throws URISyntaxException, IOException {
		final File file = this.getTestCaseFile("parser-test/roundcubemail.app.js");
		final JavaScriptParser parser = new JavaScriptParser(file);
		final Set<String> result = parser.getEventTypes();
		final Set<String> expectedTypes = Sets.newHashSet("select", "keypress", "click", "dblclick", "mouseup", "keyup",
				"keydown", "mousedown", "mouseover");
		final int expectedNrElements = expectedTypes.size();
		expectedTypes.retainAll(result);
		assertEquals(expectedNrElements, expectedTypes.size());
	}

	/**
	 * Tests event types in the style of "el.event_type(action)"
	 *
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void getEventTypes2() throws URISyntaxException, IOException {
		final File file = this.getTestCaseFile(
				"record-test/quizzy/http%3A%2F%2Fmzw.jp%3A80%2Fyuta%2Fresearch%2Fram%2Fexample%2Fafter%2Ffaulty%2Fquizzy%2Fquizzy%2Fquizzy.js");
		final JavaScriptParser parser = new JavaScriptParser(file);
		final Set<String> result = parser.getEventTypes();
		final Set<String> expectedTypes = Sets.newHashSet("click", "get");
		final int expectedNrElements = expectedTypes.size();
		expectedTypes.retainAll(result);

		assertEquals(expectedNrElements, expectedTypes.size());
	}

	private File getTestCaseFile(String filename) throws URISyntaxException {
		final ClassLoader loader = JavaScriptParserTest.class.getClassLoader();
		final URL res = loader.getResource(filename);
		final String path = res.toURI().getPath();
		return new File(path);
	}
}
