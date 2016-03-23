package jp.mzw.revajaxmutator.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Coverage {

	public static int getCoverFreq(Object line) {
		if(line instanceof Integer) {
			return (Integer) line;
		} else {
			return 0;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static JSONArray getCoverageData(JSONObject coverage, String path_to_js_file) throws JSONException {
		for(Iterator<Object> i = coverage.keys() ; i.hasNext();){
			String filename = i.next().toString();
			if(filename.equals(path_to_js_file)) {
				JSONObject _coverage = coverage.getJSONObject(filename);
				for(Iterator<Object> j = _coverage.keys(); j.hasNext();){
					Object type = j.next();
					if("lineData".equals(type.toString())) {
						return _coverage.getJSONArray(type.toString());
					}
				}
			}
		}
		return null;
	}
	
	public static JSONObject parse(File file) throws IOException, JSONException {
		// Parse JSCover result
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr); 
		StringBuilder builder = new StringBuilder();
		String line; 
		String delim = "";
		while((line = br.readLine()) != null) {
			builder.append(delim).append(line);
			delim = "\n";
		} 
		fr.close();
		return new JSONObject(builder.toString());
	}
	
}
