package jp.mzw.revajaxmutator;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.owasp.webscarab.httpclient.HTTPClient;
import org.owasp.webscarab.model.Request;
import org.owasp.webscarab.model.Response;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;

public class RewriterPlugin extends ProxyPlugin {
    private String mDirname;
    private List<String> mRewriteFiles;

    public RewriterPlugin(String dirname) {
        mDirname = dirname;
        mRewriteFiles = new ArrayList<String>();
    }

    @Override
    public String getPluginName() {
        return "RewriterPlugin";
    }

    @Override
    public HTTPClient getProxyPlugin(HTTPClient client) {
        return new Plugin(client);
    }
    
    public void setRewriteFile(String filename) {
    	if(filename != null && !mRewriteFiles.contains(filename)) {
    		mRewriteFiles.add(filename);
    	}
    }

    private synchronized void rewriteResponseContent(Request request, Response response) {
        try {
        	
            String filename = URLEncoder.encode(request.getURL().toString(), "utf-8");
            
            //print request
//            System.out.println("====<REQUEST>====");
//            System.out.println(request.getMethod());
//            System.out.println(request.getURL());
//            System.out.println(request.getVersion());
//            String[] headers=request.getHeaderNames();
//            for(String header : headers){
//            	System.out.println(header+" : " + request.getHeader(header));
//            }
//            System.out.println("====</REQUEST>====");
            
            boolean matched = false;
            
            for(String _filename : mRewriteFiles) {
            	if(filename.equals(_filename)) {
            		matched = true;
            		break;
            	}
            }
            
            if(!matched) {
            	return;
            }
            
            String mutantname = request.getHeader("mutant");
            
            String testedFilename = "";
            
            File jsFile = new File(mDirname + "/" + filename);
            File dir = new File(mDirname + "/" + "tested");
            File[] files = dir.listFiles();
            for(File file : files){
            	if(file.getName().contains(mutantname)){
            		testedFilename = file.getName();
            	}
            }
            
            System.out.println("testedFilename:"+testedFilename);;
            
            //record/appフォルダのjsファイルの読み込み
            BufferedInputStream in = new BufferedInputStream(
                    new FileInputStream(mDirname + "/" + "tested" + "/" +testedFilename));
            
            //出力用の領域
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024 * 8];
            int len = 0;
            
            //jsファイルの中身を書き込む
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            in.close();
            
            //応答の内容を書き換え
            response.setContent(out.toByteArray());

        }
        catch (FileNotFoundException e) {
            // ignore non-recorded urls
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Plugin implements HTTPClient {
        private HTTPClient mClient;

        public Plugin(HTTPClient client) {
            mClient = client;
        }

        public Response fetchResponse(Request request) throws IOException {
            // remove if-modified-since and if-none-matche to avoid 304
            request.deleteHeader("If-Modified-Since");
            request.deleteHeader("If-None-Match");

            Response response = mClient.fetchResponse(request);

            if ("200".equals(response.getStatus())) {
                rewriteResponseContent(request, response);
            }
            return response;
        }

    }

}
