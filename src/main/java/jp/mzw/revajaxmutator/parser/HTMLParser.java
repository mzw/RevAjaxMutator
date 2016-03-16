package jp.mzw.revajaxmutator.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HTMLParser {
	private Document document;
	
	public HTMLParser(String fileUrl) throws IOException {
		document = Jsoup.parse(new File(fileUrl), "UTF-8");
	}
	
	public List<String> getAllAttributes(){
		List<String> attributes = new ArrayList<String>();
		for(Element element: document.getAllElements()){
			for(Attribute attribute: element.attributes()){
				if(attribute.getKey().equals("class") || attribute.getKey().equals("id")){
					//for non-JQuery
					attributes.add("\"" + attribute.getValue() + "\"");
					//for JQuery
					attributes.add("\"" + getValueAppendedJQuerySelector(attribute) + "\"");
				}
			}
		}
		return attributes;
	}
	
	private String getValueAppendedJQuerySelector(Attribute attribute){
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
