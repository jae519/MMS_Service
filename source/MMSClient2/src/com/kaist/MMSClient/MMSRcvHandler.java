package com.kaist.MMSClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import com.kaist.MMSClient.MMSClientHandler.reqCallBack;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class MMSRcvHandler {
	public MyHandler mh;
	public MMSRcvHandler(int port) throws IOException{
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		mh = new MyHandler();
        server.createContext("/", mh);
        server.setExecutor(null); // creates a default executor
        server.start();
	}
	
    static class MyHandler implements HttpHandler {
    	com.kaist.MMSClient.MMSClientHandler.reqCallBack myreqCallBack;
    	public void setReqCallBack(com.kaist.MMSClient.MMSClientHandler.reqCallBack callback){
    		this.myreqCallBack = callback;
    	}
    	
        @Override
        public void handle(HttpExchange t) throws IOException {
        	InputStream in = t.getRequestBody();
            ByteArrayOutputStream _out = new ByteArrayOutputStream();
            byte[] buf = new byte[2048];
            int read = 0;
            while ((read = in.read(buf)) != -1) {
                _out.write(buf, 0, read);
            }
            //System.out.println(new String( buf, Charset.forName("UTF-8") ));
            String response = this.processRequest(new String( buf, Charset.forName("UTF-8")));
            //String response = "This is the response";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        private String processRequest(String data) {
    		String ret = this.myreqCallBack.callbackMethod(data);
    		return ret;
    		
    	}
    }
    
}
