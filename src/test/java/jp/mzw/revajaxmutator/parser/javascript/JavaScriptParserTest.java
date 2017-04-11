package jp.mzw.revajaxmutator.parser.javascript;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.Name;

import com.google.common.collect.Sets;

import jp.mzw.ajaxmutator.util.StringToAst;
import jp.mzw.revajaxmutator.parser.javascript.JavaScriptParser;

public class JavaScriptParserTest {

	private static File file;
	private static JavaScriptParser parser;

	@BeforeClass
	public static void setUpBeforeClass() throws URISyntaxException, IOException {
		file = getFile("quizzy.js");
		Assert.assertTrue(file.exists());
		parser = new JavaScriptParser(file);
		Assert.assertNotNull(parser);
	}

	private static File getFile(String filename) throws URISyntaxException {
		final ClassLoader loader = JavaScriptParserTest.class.getClassLoader();
		final URL res = loader.getResource(filename);
		final String path = res.toURI().getPath();
		return new File(path);
	}

	@Test
	public void testGetAstRoot() {
		Assert.assertNotNull(parser.getAstRoot());
	}

	@Test
	public void testGetFunctionNames() throws URISyntaxException, IOException {
		Assert.assertEquals(Sets.newHashSet("startQuiz", "requestNextQuestion", "checkQuestion", "restartQuizzy"), parser.getFunctionNames());
	}

	@Test
	public void testGetAttributeValuesFromInfixExpression() throws URISyntaxException, IOException {
		final File file = getFile("jsparser_test.js");
		final JavaScriptParser parser = new JavaScriptParser(file);
		Assert.assertEquals(1, parser.getAttributeValuesFromInfixExpression().size());
		Assert.assertTrue(parser.getAttributeValuesFromInfixExpression().contains("\"myid\""));
	}

	@Test
	public void testGetEventTypes() throws URISyntaxException, IOException {
		final File file = getFile("jsparser_test2.js");
		final JavaScriptParser parser = new JavaScriptParser(file);
		Assert.assertEquals(1, parser.getEventTypes().size());
		Assert.assertTrue(parser.getEventTypes().contains("click"));
	}

	@Test
	public void testGetFunctionCallName() throws URISyntaxException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		final File file = getFile("quizzy.js");
		final JavaScriptParser parser = new JavaScriptParser(file);
		Method method = JavaScriptParser.class.getDeclaredMethod("getFunctionCallName", FunctionCall.class);
		method.setAccessible(true);

		FunctionCall functionCall = StringToAst.parseAsFunctionCall("foo('bar');");
		Name name = (Name) method.invoke(parser, functionCall);
		Assert.assertArrayEquals("foo".toCharArray(), name.toSource().toCharArray());
	}
}
