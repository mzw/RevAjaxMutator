package jp.mzw.revajaxmutator.parser.java;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import jp.mzw.revajaxmutator.parser.java.TestCaseParser;

import org.junit.Assert;
import org.junit.Test;

public class TestCaseParserTest {

	@Test
	public void getIdValues() throws IOException, URISyntaxException {
		TestCaseParser parser = new TestCaseParser(getTestCaseFile("testcases/ByTest.java.txt"));
		List<String> values = parser.getIdValues();
		Assert.assertEquals(Arrays.asList("\"#foo\"", "\"#bar\"", "\"#foo\"", "\"#bar\""), values);
	}

	@Test
	public void getTagNames() throws IOException, URISyntaxException {
		TestCaseParser parser = new TestCaseParser(getTestCaseFile("testcases/ByTest.java.txt"));
		List<String> values = parser.getTagNames();
		Assert.assertEquals(Arrays.asList("\"foo\"", "\"bar\"", "\"foo\"", "\"bar\""), values);
	}

	@Test
	public void getClassValues() throws IOException, URISyntaxException {
		TestCaseParser parser = new TestCaseParser(getTestCaseFile("testcases/ByTest.java.txt"));
		List<String> values = parser.getClassValues();
		Assert.assertEquals(Arrays.asList("\".foo\"", "\".bar\"", "\".foo\"", "\".bar\""), values);

	}

	private File getTestCaseFile(String filename) throws URISyntaxException {
		ClassLoader loader = TestCaseParserTest.class.getClassLoader();
		URL res = loader.getResource(filename);
		String path = res.toURI().getPath();
		return new File(path);
	}
}
