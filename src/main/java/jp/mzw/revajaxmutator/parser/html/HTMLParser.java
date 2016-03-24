package jp.mzw.revajaxmutator.parser.html;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.collect.ImmutableSet;

public class HTMLParser {
	private Document document;

	public HTMLParser(File file) throws IOException {
		document = Jsoup.parse(file, "UTF-8");
	}

	private String getValueAppendedJQuerySelector(Attribute attribute) {
		switch (attribute.getKey()) {
		case "class":
			return "." + attribute.getValue();
		case "id":
			return "#" + attribute.getValue();
		default:
			return attribute.getValue();
		}
	}

	public List<String> getAllAttributeKeys() {
		List<String> keys = new ArrayList<String>();
		for (Element element : document.getAllElements()) {
			for (Attribute attribute : element.attributes()) {
				keys.add(attribute.getKey());
			}
		}
		return keys;
	}

	private static final Set<String> EVENT_HTML_DOMLEVEL2_ATTRIBUTES = ImmutableSet
			.of("onclick", "ondblclick", "onmousedown", "onmouseenter",
					"onmouseleave", "onmousemove", "onmouseover", "onmouseout",
					"onmouseup", "onkeydown", "onkeypress", "onkeyup",
					"onabort", "onbeforeunload", "onerror", "onload",
					"onresize", "onscroll", "onunload", "onblur", "onchange",
					"onfocus", "onfocusin", "onfocusout", "onreset",
					"onselect", "onsubmit");

	public EventSet getAllEventSet() {
		Set<String> eventTargetSet = new TreeSet<String>();
		Set<String> eventTypeSet = new TreeSet<String>();
		Set<String> eventCallbackSet = new TreeSet<String>();

		for (Element element : document.getAllElements()) {
			for (Attribute attribute : element.attributes()) {
				if (EVENT_HTML_DOMLEVEL2_ATTRIBUTES
						.contains(attribute.getKey())) {
					eventTypeSet.add(attribute.getKey());
					eventCallbackSet.add(attribute.getValue());
					if (!"".equals(element.id())) {
						eventTargetSet.add(element.id());
					}
				}
			}
		}
		return new EventSet(eventTargetSet, eventTypeSet, eventCallbackSet);
	}

	/**
	 * 
	 * @return "id" or "class" values
	 */
	public List<String> getAllElementIdentifier() {
		List<String> attributes = new ArrayList<String>();
		for (Element element : document.getAllElements()) {
			for (Attribute attribute : element.attributes()) {
				if (attribute.getKey().equals("class")
						|| attribute.getKey().equals("id")) {
					if (attribute.getValue().contains(",")) {
						continue;
					}
					// for non-JQuery
					attributes.add("\"" + attribute.getValue() + "\"");
					// for JQuery
					attributes.add("\""
							+ getValueAppendedJQuerySelector(attribute) + "\"");
				}
			}
		}
		return attributes;
	}

	public List<String> getAllAttributeValues() {
		List<String> values = new ArrayList<String>();
		for (Element element : document.getAllElements()) {
			for (Attribute attribute : element.attributes()) {
				if (attribute.getValue().contains(",")) {
					continue;
				}
				// for non-JQuery
				values.add("\"" + attribute.getValue() + "\"");
				// for JQuery
				values.add("\"" + getValueAppendedJQuerySelector(attribute)
						+ "\"");
			}
		}
		return values;
	}
}
