package jp.mzw.revajaxmutator.tracer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JUnitParser {
	private static Logger log = LoggerFactory.getLogger(JUnitParser.class);
	
	protected ArrayList<TraceInfo> errorClassLineList;
	public JUnitParser(String filename) throws IOException {
		String content = FileUtils.readFileToString(new File(filename));
		Document doc = Jsoup.parse(content);
		if(doc == null) {
			log.error("Invalid JUnit report file");
			System.exit(-1);
		}
		
		this.errorClassLineList = new ArrayList<TraceInfo>();
		for(Element test_suite: doc.getElementsByTag("testsuite")) {
			String test_suite_name = test_suite.attr("name");
			for(Element test_case: test_suite.getElementsByTag("testcase")) {
				String test_case_name = test_case.attr("name");
				String test_case_classname = test_case.attr("classname");
				
				/// at test_class.test_name(Class.java:line)
				String regex = "at " + test_case_classname + "." + test_case_name;
				for(Element error: test_case.getElementsByTag("error")) {
					String[] error_splitted = error.toString().split(regex);
					for(int i = 1; i < error_splitted.length; i++) {
						String _error_splitted = error_splitted[i];
						int beginIndex = _error_splitted.indexOf("(");
						int endIndex = _error_splitted.indexOf(")");
						
						/// Class.java:line
						String error_pos = _error_splitted.substring(beginIndex + 1, endIndex);
						String _line = error_pos.split(":")[1];
						int line = Integer.parseInt(_line);
						
						TraceInfo ti = new TraceInfo(
								test_suite_name, test_case_name, test_case_classname, line);
						errorClassLineList.add(ti);
						break;
					}
				}

				for(Element failure: test_case.getElementsByTag("failure")) {
					String[] failure_splitted = failure.toString().split(regex);
					for(int i = 1; i < failure_splitted.length; i++) {
						String _failure_splitted = failure_splitted[i];
						int beginIndex = _failure_splitted.indexOf("(");
						int endIndex = _failure_splitted.indexOf(")");
						
						/// Class.java:line
						String failure_pos = _failure_splitted.substring(beginIndex + 1, endIndex);
						String _line = failure_pos.split(":")[1];
						int line = Integer.parseInt(_line);
						
						TraceInfo ti = new TraceInfo(
								test_suite_name, test_case_name, test_case_classname, line);
						errorClassLineList.add(ti);
						break;
					}
				}
			}
		}
	}
	
	public ArrayList<TraceInfo> getErrorClassLineList() {
		return this.errorClassLineList;
	}
}
