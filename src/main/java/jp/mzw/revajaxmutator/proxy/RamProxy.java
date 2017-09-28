package jp.mzw.revajaxmutator.proxy;

public interface RamProxy {

    void stop();

    void setup(String address);

    void launch();

    void addRecorderPlugin(String directory);

    void addFilterPlugin(String urlPrefix, String method);

    void addRewriterPlugin(String directory, String filename);

    void restart();
}
