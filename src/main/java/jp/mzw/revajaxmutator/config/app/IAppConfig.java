package jp.mzw.revajaxmutator.config.app;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public interface IAppConfig {
	public File getRecordDir();
	public URL getUrl() throws MalformedURLException;
	public String pathToJsFile();
	public File getJscoverReportDir();
}
