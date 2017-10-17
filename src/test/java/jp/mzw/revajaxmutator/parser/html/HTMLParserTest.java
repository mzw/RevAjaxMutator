package jp.mzw.revajaxmutator.parser.html;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import org.jsoup.nodes.Attribute;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Iterables;

public class HTMLParserTest {

	private static final String PATH_TO_HTML_FILE = "src/test/resources/record-test/app-test/foo.html";
	private static File file;
	private static HTMLParser parser;

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		file = new File(PATH_TO_HTML_FILE);
		Assert.assertTrue(file.exists());
		parser = new HTMLParser(file);
		Assert.assertNotNull(parser);
	}

	@Test
	public void testGetAllEventSet() {
		EventSet events = parser.getAllEventSet();
		Assert.assertEquals(1, events.getTargetSet().size());
		Assert.assertArrayEquals("element".toCharArray(), Iterables.get(events.getTargetSet(), 0).toCharArray());
		Assert.assertEquals(2, events.getTypeSet().size());
		Assert.assertArrayEquals("onclick".toCharArray(), Iterables.get(events.getTypeSet(), 0).toCharArray());
		Assert.assertEquals(1, events.getCallbackSet().size());
		Assert.assertArrayEquals("callback();".toCharArray(), Iterables.get(events.getCallbackSet(), 0).toCharArray());
	}

	@Test
	public void testGetAllElementIdentifier() {
		Set<String> identifiers = parser.getAllElementIdentifier();
		Assert.assertEquals(2, identifiers.size());
		Assert.assertTrue(Iterables.get(identifiers, 0).contains("element"));
		Assert.assertTrue(Iterables.get(identifiers, 1).contains("element"));
	}

	@Test
	public void testGetAllAttributeValues() {
		Set<String> values = parser.getAllAttributeValues();
		Assert.assertEquals(13, values.size());
	}

	@Test
	public void testGetValueAppendedJQuerySelector()
			throws IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		HTMLParser parser = new HTMLParser(file);
		Method method = HTMLParser.class.getDeclaredMethod("getValueAppendedJQuerySelector", Attribute.class);
		method.setAccessible(true);
		{
			Attribute attr = Attribute.createFromEncoded("id", "elementId");
			String actual = (String) method.invoke(parser, attr);
			Assert.assertArrayEquals("#elementId".toCharArray(), actual.toCharArray());
		}
		{
			Attribute attr = Attribute.createFromEncoded("class", "elementClass");
			String actual = (String) method.invoke(parser, attr);
			Assert.assertArrayEquals(".elementClass".toCharArray(), actual.toCharArray());
		}
		{
			Attribute attr = Attribute.createFromEncoded("width", "100px");
			String actual = (String) method.invoke(parser, attr);
			Assert.assertArrayEquals("100px".toCharArray(), actual.toCharArray());
		}
	}
}
