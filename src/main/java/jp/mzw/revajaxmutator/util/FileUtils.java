package jp.mzw.revajaxmutator.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class FileUtils {
	/**
	 * Retrieves target document via Internet
	 * @param url is a URL of target document
	 * @return Target document
	 * @throws IOException 
	 */
	public static String wget(String url) throws IOException {
		String ret = null;
		URL _url = new URL(url);
		InputStreamReader isr = new InputStreamReader(_url.openStream());
		BufferedReader br = new BufferedReader(isr);
		String line;
		while((line = br.readLine()) != null) {
			ret += line + "\n";
		}
		br.close();
		return ret;
	}
	
	/**
	 * Retrieves target document via local host
	 * @param filename is a file path of target document
	 * @return Target document
	 * @throws IOException 
	 */
	public static String cat(String filename) throws IOException {
		String ret = null;
		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);
		String line;
		while((line = br.readLine()) != null) {
			ret += line + "\n";
		}
		br.close();
		return ret;
	}

	/**
	 * Retrieves target document via local host
	 * @param dir 
	 * @param filename is a file path of target document
	 * @return Target document
	 * @throws IOException 
	 */
	public static String cat(String dir, String filename) throws IOException {
		File file = new File(dir, filename);
		return cat(file.getAbsolutePath());
	}
	
	/**
	 * Retrieves lines which matches given regular expression
	 * @param content is a text which may contains the lines 
	 * @param regex is the regular expression
	 * @return Matched lines
	 */
	public static ArrayList<String> grep(String content, String regex) {
		ArrayList<String> ret = new ArrayList<String>();
		String line = "";
		for(int i = 0; i < content.length(); i++) {
			int n = content.indexOf('\n');
			if(n != -1) {
				line = content.substring(0, n);
				content = content.substring(n+1, content.length()-1);
				if(line.matches(regex)) {
					ret.add(line);
				}
			}
		}
		return ret;
	}
	
	/**
	 * Writes a text in file system
	 * @param dir is a parent directory
	 * @param filename is a name of the text file
	 * @param content is a content to be written
	 * @throws IOException 
	 */
	public static void write(String dir, String filename, String content) throws IOException {
		File _dir = new File(dir);
		_dir.mkdirs();
		write(new File(dir, filename), content);
	}
	
	/**
	 * Writes a text in file system
	 * @param filename is a file path
	 * @param content is a content to be written
	 * @throws IOException 
	 */
	public static void write(String filename, String content) throws IOException {
		write(new File(filename), content);
	}
	
	/**
	 * Writes a text in file system
	 * @param file is a File instance
	 * @param content is a content to be written
	 * @throws IOException 
	 */
	public static void write(File file, String content) throws IOException {
		FileWriter fw = new FileWriter(file);
		fw.write(content);
		fw.close();
	}
	
}
