package jp.mzw.revajaxmutator.config;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public interface IAppConfigBase {
	public File getRecordDir();
	public URL getUrl() throws MalformedURLException;
	public String pathToJsFile();
	public File getSuccessCoverageFile();
	public File getFailureCoverageFile();
}
