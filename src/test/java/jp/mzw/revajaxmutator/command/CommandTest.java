package jp.mzw.revajaxmutator.command;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.hamcrest.core.StringStartsWith;;

public class CommandTest {

	private ByteArrayOutputStream baos;
	private PrintStream out;
	
	@Before
	public void setUp() {
		baos = new ByteArrayOutputStream();
		out = System.out;
		System.setOut(new PrintStream(new BufferedOutputStream(baos)));
	}

	@Test
	public void testShowUsage() {
		Command.command(null, null);
		System.out.flush();
		String actual = baos.toString();
		Assert.assertThat(actual, StringStartsWith.startsWith("$ java -cp ${ClassPath} jp.mzw.revajaxmutator.CLI"));
	}
	
	@After
	public void tearDown() {
		System.setOut(out);
	}
}
