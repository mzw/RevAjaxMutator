package jp.mzw.revajaxmutator.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import jp.mzw.revajaxmutator.parser.javascript.JavaScriptParser;

public class JavaScriptParserTest {

	@Ignore
	@Test
	public void parse() throws IOException, URISyntaxException {
		final File file = this.getTestCaseFile("quizzy.js");
		final JavaScriptParser parser = new JavaScriptParser(file);
		assertNotNull(parser.getAstRoot());
	}

	@Ignore
	@Test
	public void getAllFunctions() throws URISyntaxException, IOException {
		final File file = this.getTestCaseFile("quizzy.js");
		final JavaScriptParser parser = new JavaScriptParser(file);
		assertEquals(Arrays.asList("startQuiz", "requestNextQuestion", "checkQuestion", "restartQuizzy"),
				parser.getFunctionNames());
	}

	@Ignore
	@Test
	public void getAttributeValuesFromInfixExpression() throws URISyntaxException, IOException {
		final File file = this.getTestCaseFile("parser-test/jsparser_test.js");
		final JavaScriptParser parser = new JavaScriptParser(file);
		assertEquals(Arrays.asList("\"myid\""), parser.getAttributeValuesFromInfixExpression());
	}

	@Ignore
	@Test
	public void getEventTypes() throws URISyntaxException, IOException {
		final File file = this.getTestCaseFile("parser-test/roundcubemail.app.js");
		final JavaScriptParser parser = new JavaScriptParser(file);
		@SuppressWarnings("unchecked")
		final List<String> eventTypes = (List<String>) parser.getEventTypes();

		assertEquals(Arrays.asList("select", "keypress", "click", "dblclick", "click", "mouseup", "keypress",
				"dblclick", "select", "select", "keypress", "keypress", "select", "keypress", "select", "select",
				"keyup", "select", "keydown", "mouseup", "mouseup", "mouseup", "mousedown", "mouseup", "keypress",
				"mouseover", "mouseover", "keydown", "keydown", "select", "click"), eventTypes);
	}

	private File getTestCaseFile(String filename) throws URISyntaxException {
		final ClassLoader loader = JavaScriptParserTest.class.getClassLoader();
		final URL res = loader.getResource(filename);
		final String path = res.toURI().getPath();
		return new File(path);
	}
}
