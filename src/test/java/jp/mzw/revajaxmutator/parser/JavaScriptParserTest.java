package jp.mzw.revajaxmutator.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

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
		assertEquals(4, parser.getFunctionNames().size());
		assertTrue(parser.getFunctionNames().contains("startQuiz"));
		assertTrue(parser.getFunctionNames().contains("requestNextQuestion"));
		assertTrue(parser.getFunctionNames().contains("checkQuestion"));
		assertTrue(parser.getFunctionNames().contains("restartQuizzy"));
	}

	@Test
	public void getAttributeValuesFromInfixExpression() throws URISyntaxException, IOException {
		final File file = this.getTestCaseFile("parser-test/jsparser_test.js");
		final JavaScriptParser parser = new JavaScriptParser(file);
		assertEquals(1, parser.getAttributeValuesFromInfixExpression().size());
		assertTrue(parser.getAttributeValuesFromInfixExpression().contains("\"myid\""));
	}

	@Test
	public void getEventTypes() throws URISyntaxException, IOException {
		final File file = this.getTestCaseFile("parser-test/jsparser_test2.js");
		final JavaScriptParser parser = new JavaScriptParser(file);
		assertEquals(1, parser.getEventTypes().size());
		assertTrue(parser.getEventTypes().contains("click"));
	}

	private File getTestCaseFile(String filename) throws URISyntaxException {
		final ClassLoader loader = JavaScriptParserTest.class.getClassLoader();
		final URL res = loader.getResource(filename);
		final String path = res.toURI().getPath();
		return new File(path);
	}
}
