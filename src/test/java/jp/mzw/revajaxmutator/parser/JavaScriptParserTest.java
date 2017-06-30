package jp.mzw.revajaxmutator.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import org.junit.Test;

import jp.mzw.revajaxmutator.parser.javascript.JavaScriptParser;

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
		assertEquals(Arrays.asList("startQuiz", "requestNextQuestion", "checkQuestion", "restartQuizzy"),
				parser.getFunctionNames());
	}

	@Test
	public void getAttributeValuesFromInfixExpression() throws URISyntaxException, IOException {
		final File file = this.getTestCaseFile("jsparser_test.js");
		final JavaScriptParser parser = new JavaScriptParser(file);
		assertEquals(Arrays.asList("\"myid\""), parser.getAttributeValuesFromInfixExpression());
	}

	@Test
	public void getEventTypes() throws URISyntaxException, IOException {
		final File file = this.getTestCaseFile("jsparser_test2.js");
		final JavaScriptParser parser = new JavaScriptParser(file);
		assertEquals(Arrays.asList("click"), parser.getEventTypes());
	}

	private File getTestCaseFile(String filename) throws URISyntaxException {
		final ClassLoader loader = JavaScriptParserTest.class.getClassLoader();
		final URL res = loader.getResource(filename);
		final String path = res.toURI().getPath();
		return new File(path);
	}
}
