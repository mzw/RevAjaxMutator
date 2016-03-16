package jp.mzw.ajaxmutator;

/**
 * Singleton class that contains various information used across classes.
 */
public class Context {
    public static Context INSTANCE = new Context();

    private String pathToJsFile;

    private Context() { }

    public void registerJsPath(String pathToJsFile) {
        this.pathToJsFile = pathToJsFile;
    }

    public String getJsPath() {
        return pathToJsFile;
    }
}
