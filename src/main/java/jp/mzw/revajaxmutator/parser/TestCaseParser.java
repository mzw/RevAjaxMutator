package jp.mzw.revajaxmutator.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestCaseParser {
	Scanner scanner;
	Matcher matcher;
	
	public TestCaseParser(String filePath) throws FileNotFoundException {
		InputStream in = new FileInputStream(filePath);
		scanner = new Scanner(in);
		
	}
	
	public List<String> getFindElementSelector(){
		String regex = "By.id(.*)|By.className(.*)";
		Pattern pattern = Pattern.compile(regex);
		List<String> rtnList = new ArrayList<String>();
		while(scanner.hasNext()){
			String str = scanner.next();
			matcher = pattern.matcher(str);
			while(matcher.find()){
				rtnList.add(matcher.group());
				System.out.println(matcher.group());
			}
		}
		return rtnList;
	}
	
	public List<String> getAttributeValues(List<String> list){
		final int BEGININDEX_ID = 7;
//		final int ENDINDEX_ID = 9;
		final int BEGININDEX_CLASS = 14;
//		final int ENDINDEX_CLASS = 16;
		List<String> attributeList = new ArrayList<String>();
		for(String statement: list){
			if(statement.startsWith("By.id")){
				String str = statement.substring(BEGININDEX_ID);
				int endIndex = str.indexOf("\"");
				String value = str.substring(0,endIndex);
				attributeList.add("\"" + value + "\"");
				attributeList.add("\"" + "#" + value + "\"");
			}else if(statement.startsWith("By.className")){
				String str = statement.substring(BEGININDEX_CLASS);
				int endIndex = str.indexOf("\"");
				System.out.println("temp:" + str + "num:" + endIndex);
				String value = str.substring(0,endIndex);
				attributeList.add("\"" + value + "\"");
				attributeList.add("\"" + "." + value + "\"");
			}
		}
		return attributeList;
	}
	
	
	
	
}
