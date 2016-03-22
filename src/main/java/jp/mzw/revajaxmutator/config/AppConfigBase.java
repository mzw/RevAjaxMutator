package jp.mzw.revajaxmutator.config;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.Set;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.mutator.DOMSelectionSelectNearbyMutator;
import jp.mzw.ajaxmutator.mutator.Mutator;
import jp.mzw.ajaxmutator.mutator.replace.among.AttributeModificationTargetRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.AttributeModificationValueRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.EventCallbackRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.EventTargetRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.EventTypeRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.RequestOnSuccessHandlerRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.RequestUrlRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.TimerEventCallbackRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.TimerEventDurationRAMutator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

public abstract class AppConfigBase implements IAppConfigBase {
	protected Logger LOGGER = LoggerFactory.getLogger(AppConfigBase.class);
	
	public AppConfigBase() {
		// for configure by override
	}
	
	Properties config;
	public AppConfigBase(String filename) throws IOException {
		config = new Properties();
		config.load(AppConfigBase.class.getClassLoader().getResourceAsStream(filename));
	}
	
	public File getRecordDir() {
		String record_dir = config.getProperty("ram_record_dir") != null ? config.getProperty("ram_record_dir") : "record/app";
		return new File(record_dir);
	}
	
	public URL getUrl() throws MalformedURLException {
		String url = config.getProperty("url") != null ? config.getProperty("url") : "http://127.0.0.1:80/index.php?query=string";
		return new URL(url);
	}
	
	public String pathToJsFile() {
		return config.getProperty("path_to_js_file") != null ? config.getProperty("path_to_js_file") : "js/foo.js";
	}
	
	public File getSuccessCoverageFile() {
		String path = config.getProperty("success_cov_file") != null ? config.getProperty("success_cov_file") : "jscover/app/jscoverage.success.json";
		return new File(path);
		
	}
	
	public File getFailureCoverageFile() {
		String path = config.getProperty("failure_cov_file") != null ? config.getProperty("failure_cov_file") : "jscover/app/jscoverage.failure.json";
		return new File(path);
	}

	public File getRecordedJsFile() throws MalformedURLException, UnsupportedEncodingException {
		URL url = getUrl();
		if(url.getPort() == -1) {
			LOGGER.warn("Not specified port number: " + url.getPath());
		}
		
		URL url_js_file = new URL(url, pathToJsFile());
		String js_filename = URLEncoder.encode(url_js_file.toString(), "utf-8");
		File js_file = new File(getRecordDir(), js_filename);
		return js_file;
	}
	
	public Set<Mutator<?>> getDefaultMutators(MutateVisitor visitor) {
		return ImmutableSet.<Mutator<?>>of(
	                    new EventTargetRAMutator(visitor.getEventAttachments()),
	                    new EventTypeRAMutator(visitor.getEventAttachments()),
	                    new EventCallbackRAMutator(visitor.getEventAttachments()),
	                    new TimerEventDurationRAMutator(visitor.getTimerEventAttachmentExpressions()),
	                    new TimerEventCallbackRAMutator(visitor.getTimerEventAttachmentExpressions()),
	                    new RequestUrlRAMutator(visitor.getRequests()),
	                    new RequestOnSuccessHandlerRAMutator(visitor.getRequests()),
	                    new DOMSelectionSelectNearbyMutator(),
	                    new AttributeModificationTargetRAMutator(visitor.getAttributeModifications()),
	                    new AttributeModificationValueRAMutator(visitor.getAttributeModifications())
	                    );
	}
	
}
