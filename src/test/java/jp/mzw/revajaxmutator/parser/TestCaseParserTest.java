package jp.mzw.revajaxmutator.parser;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TestCaseParserTest {
	
	@Test
	public void getIdValues() throws IOException, URISyntaxException {
		TestCaseParser parser = new TestCaseParser(getTestCaseFile("testcases/ByTest.java.txt"));
		List<String> values = parser.getIdValues();
		assertEquals(Arrays.asList("\"#foo\"", "\"#bar\"", "\"#foo\"", "\"#bar\""), values);
	}

	@Test
	public void getTagNames() throws IOException, URISyntaxException {
		TestCaseParser parser = new TestCaseParser(getTestCaseFile("testcases/ByTest.java.txt"));
		List<String> values = parser.getTagNames();
		assertEquals(Arrays.asList("\"foo\"", "\"bar\"", "\"foo\"", "\"bar\""), values);
	}
	
	@Test
	public void getClassValues()  throws IOException, URISyntaxException {
		TestCaseParser parser = new TestCaseParser(getTestCaseFile("testcases/ByTest.java.txt"));
		List<String> values = parser.getClassValues();
		assertEquals(Arrays.asList("\".foo\"", "\".bar\"", "\".foo\"", "\".bar\""), values);
		
	}
	

	private File getTestCaseFile(String filename) throws URISyntaxException {
		ClassLoader loader = TestCaseParserTest.class.getClassLoader();
		URL res = loader.getResource(filename);
		String path = res.toURI().getPath();
		return new File(path);
	}
}
