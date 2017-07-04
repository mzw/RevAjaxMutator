package jp.mzw.revajaxmutator.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.common.collect.Sets;
import com.google.inject.internal.cglib.core.CollectionUtils;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import java.util.List;
import java.util.Set;
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
        Set<String> result = parser.getFunctionNames();
        Set<String> expected = Sets.newHashSet("startQuiz", "requestNextQuestion", "checkQuestion", "restartQuizzy");
		assertEquals(expected, result);
	}

	@Test
	public void getAttributeValuesFromInfixExpression() throws URISyntaxException, IOException {
		final File file = this.getTestCaseFile("parser-test/jsparser_test.js");
		final JavaScriptParser parser = new JavaScriptParser(file);
        final Set<String> result = parser.getAttributeValuesFromInfixExpression();
        final Set<String> expected = Sets.newHashSet("\"myid\"");
        assertEquals(expected, result);
	}

	@Test
	public void getEventTypes() throws URISyntaxException, IOException {
		final File file = this.getTestCaseFile("parser-test/jsparser_test2.js");
		final JavaScriptParser parser = new JavaScriptParser(file);
        final Set<String> result = parser.getEventTypes();
        final Set<String> expected = Sets.newHashSet("click");
        assertEquals(expected, result);
	}

	private File getTestCaseFile(String filename) throws URISyntaxException {
		final ClassLoader loader = JavaScriptParserTest.class.getClassLoader();
		final URL res = loader.getResource(filename);
		final String path = res.toURI().getPath();
		return new File(path);
	}
}
