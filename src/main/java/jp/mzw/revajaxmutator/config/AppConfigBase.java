package jp.mzw.revajaxmutator.config;

public abstract class AppConfigBase {
	public abstract String getRecordDir();
	public abstract String getUrl();
	public abstract String getPathToJsFile();
	public String getAbsolutePathToJSFile() {
		return null;
	}
	public String getPathToJSFileForCoverageJson(){
		return null;
	}
	
	public String getPathToJSFileToParseCoverageJson() {
		return null;
	}
	
	public abstract String getPathToSuccessCoverageFile();
	public abstract String getPathToFailureCoverageFile();
}
