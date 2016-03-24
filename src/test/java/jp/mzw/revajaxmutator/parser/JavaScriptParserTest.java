package jp.mzw.revajaxmutator.parser;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import jp.mzw.revajaxmutator.parser.javascript.JavaScriptParser;

import org.junit.Test;

public class JavaScriptParserTest {

	@Test
	public void parse() throws IOException, URISyntaxException {
		File file = getTestCaseFile("quizzy.js");
		JavaScriptParser parser = new JavaScriptParser(file);
		assertNotNull(parser.getAstRoot());
	}
	
	@Test
	public void getAllFunctions() throws URISyntaxException, IOException {
		File file = getTestCaseFile("quizzy.js");
		JavaScriptParser parser = new JavaScriptParser(file);
		assertEquals(Arrays.asList("startQuiz", "requestNextQuestion", "checkQuestion", "restartQuizzy"), parser.getFunctionNames());
	}

	private File getTestCaseFile(String filename) throws URISyntaxException {
		ClassLoader loader = JavaScriptParserTest.class.getClassLoader();
		URL res = loader.getResource(filename);
		String path = res.toURI().getPath();
		return new File(path);
	}
}
