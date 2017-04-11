package jp.mzw.revajaxmutator.parser.html;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class HTMLParser {

	/** Represents HTML document */
	private Document document;

	/**
	 * Constructor
	 * 
	 * @param file is HTML file
	 * @throws IOException causes when given file does not exist
	 */
	public HTMLParser(File file) throws IOException {
		document = Jsoup.parse(file, "UTF-8");
	}

	/** Attributes representing event handlers */
	private static final Set<String> EVENT_HTML_DOMLEVEL2_ATTRIBUTES = ImmutableSet.of("onclick", "ondblclick", "onmousedown", "onmouseenter", "onmouseleave",
			"onmousemove", "onmouseover", "onmouseout", "onmouseup", "onkeydown", "onkeypress", "onkeyup", "onabort", "onbeforeunload", "onerror", "onload",
			"onresize", "onscroll", "onunload", "onblur", "onchange", "onfocus", "onfocusin", "onfocusout", "onreset", "onselect", "onsubmit");

	/**
	 * Get all event set consisting of
	 * 
	 * Target: value of 'id' attribute if any
	 * Type: name of attribute corresponding to event type
	 * Callback: value of attribute corresponding to event type
	 * 
	 * @return set of all event handlers implemented in given HTML file
	 */
	public EventSet getAllEventSet() {
		Set<String> eventTargetSet = Sets.newHashSet();
		Set<String> eventTypeSet = Sets.newHashSet();
		Set<String> eventCallbackSet = Sets.newHashSet();
		for (Element element : document.getAllElements()) {
			for (Attribute attribute : element.attributes()) {
				if (EVENT_HTML_DOMLEVEL2_ATTRIBUTES.contains(attribute.getKey())) {
					eventTypeSet.add(attribute.getKey());
					eventCallbackSet.add(attribute.getValue());
					if (!"".equals(element.id())) { // TODO 'id' attribute only?
						eventTargetSet.add(element.id());
					}
				}
			}
		}
		return new EventSet(eventTargetSet, eventTypeSet, eventCallbackSet);
	}

	/**
	 * Get all element identifier (i.e., values of 'id' or 'class')
	 * 
	 * TODO focusing on 'id' and 'class', for simplicity. Others are: name, tag name, css selector, link, and xpath.
	 * 
	 * @return "id" or "class" values
	 */
	public Set<String> getAllElementIdentifier() {
		Set<String> attributes = Sets.newHashSet();
		for (Element element : document.getAllElements()) {
			for (Attribute attribute : element.attributes()) {
				if (attribute.getKey().equals("class") || attribute.getKey().equals("id")) {
					if (attribute.getValue().contains(",")) { // TODO limited to selection of multiple elements
						continue;
					}
					// for non-JQuery
					attributes.add("\"" + attribute.getValue() + "\""); // TODO Only jQuery style is enough?
					// for JQuery
					attributes.add("\"" + getValueAppendedJQuerySelector(attribute) + "\"");
				}
			}
		}
		return attributes;
	}

	/**
	 * Get all attributed values
	 * 
	 * @return all attribute values
	 */
	public Set<String> getAllAttributeValues() {
		Set<String> values = Sets.newHashSet();
		for (Element element : document.getAllElements()) {
			for (Attribute attribute : element.attributes()) {
				if (attribute.getValue().contains(",")) {
					continue;
				}
				// for non-JQuery
				values.add("\"" + attribute.getValue() + "\""); // TODO Only jQuery style is enough?
				// for JQuery
				values.add("\"" + getValueAppendedJQuerySelector(attribute) + "\"");
			}
		}
		return values;
	}

	/**
	 * Get value of attribute according to its key
	 * 
	 * @param attribute
	 * @return value according key
	 */
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
}
