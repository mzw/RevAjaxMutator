package jp.mzw.revajaxmutator.config;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class AppConfigUtils {
	
	//
	public static String getPathToRecordedJsFile(String record_dir, String url, String path_to_js_file) throws MalformedURLException, UnsupportedEncodingException {
		URL url_js_file = new URL(new URL(url), path_to_js_file);
		String js_filename = URLEncoder.encode(url_js_file.toString(), "utf-8");
		File js_file = new File(new File(record_dir), js_filename);
		return js_file.getPath();
	}

	//to parse JSON
	//TaggedGallery : /wp-content/plugins/tagged-gallery/js
	public static String getUrlPathToJsFile(String url, String path_to_js_file) throws MalformedURLException {
		URL url_js_file = new URL(new URL(url), path_to_js_file);
		return url_js_file.getFile();
	}

	public static String getPathToRecordedJsFile(Class<? extends AppConfigBase> clazz) throws MalformedURLException, UnsupportedEncodingException, InstantiationException, IllegalAccessException {
		AppConfigBase config = (AppConfigBase) clazz.newInstance();
		return getPathToRecordedJsFile(config.getRecordDir(), config.getUrl(), config.getPathToJsFile());
	}
	
	public static String getPathToRecordedJsFile(AppConfigBase config) throws MalformedURLException, UnsupportedEncodingException {
		return getPathToRecordedJsFile(config.getRecordDir(), config.getUrl(), config.getPathToJsFile());
	}
	
	public static String getAbsolutePathToRecordedJSFile(Class<? extends AppConfigBase> clazz) throws InstantiationException, IllegalAccessException {
		AppConfigBase config = (AppConfigBase) clazz.newInstance();
		return config.getAbsolutePathToJSFile();
	}
	
	public static String getAbsolutePathToRecordedJSFile(AppConfigBase config) {
		return config.getAbsolutePathToJSFile();
	}
	
	public static String getPathToJSFileToParseCoverageJson(AppConfigBase config) {
		return config.getPathToJSFileToParseCoverageJson();
	}
}
