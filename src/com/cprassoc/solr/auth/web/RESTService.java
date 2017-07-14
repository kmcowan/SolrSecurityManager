/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.web;

import com.cprassoc.solr.auth.util.Utils;
import com.cprassoc.solr.auth.web.html.HTML;
import com.cprassoc.solr.auth.web.html.HTML.Page;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cytopia.tofu.ProcessorServlet;
 

/**
 *
 * @author kevin
 */
public class RESTService extends GenericServlet implements MessageHandler,HttpHandler{
    private String message;
    public final static ProcessorServlet PROCESSOR = new ProcessorServlet();

    @Override
  public void init() throws ServletException
  {
      // Do required initialization
      message = "Hello World";
  }

  public void doGet(HttpServletRequest request,
                    HttpServletResponse response)
            throws ServletException, IOException
  {
      // Set response content type
      response.setContentType("text/html");

      // Actual logic goes here.
      PrintWriter out = response.getWriter();
      out.println("<h1>" + message + "</h1>");
  }
  @Override
  public void destroy()
  {
      // do nothing.
  }
  
  @Override
  public  void service(ServletRequest req,
                             ServletResponse res)
                      throws ServletException,
                             java.io.IOException {
      
      	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
 
        out.println("<h1>Hello World example using" +
	    		" GenericServlet class.</h1>");
        out.close();
      
  }
      public static void main(String[] args) {
        try {
            RESTService service = new RESTService();
            service.init();
            
            /*
            // open websocket
            final WebClientEndpoint clientEndPoint = new WebClientEndpoint(new URI("wss://localhost:80"));
            clientEndPoint.start();

            // add listener
            clientEndPoint.addMessageHandler(new RESTService());

            // send message to websocket
            clientEndPoint.sendMessage("{'event':'addChannel','channel':'ok_btccny_ticker'}");

            // wait 5 seconds for messages from websocket
            Thread.sleep(5000);
            */
 
        } catch (Exception ex) {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        }
    }
      
    public void handleMessage(String message){
        System.out.println("message rec'd: "+message);
    }
    
    @Override
    public void handle(HttpExchange ex) throws IOException{
        try{
        InputStream in = ex.getRequestBody();
        
                
                ByteArrayOutputStream _out = new ByteArrayOutputStream();
                byte[] buf = new byte[2048];
                int read = 0;
                String line;
                String content = Utils.streamToString(in) +"\n";
                content += headersAsString(ex.getRequestHeaders())+"\n";
                content += "Query: " + ex.getRequestURI().getQuery()+"\n";
                content += "Path: "+ ex.getRequestURI().getPath()+"\n";
                
                System.out.println("REQUEST: \n" + content);
                
                ex.getResponseHeaders().add("Content-Type", "text/html");
                ex.getResponseHeaders().add("X-Powered-by", "Solr Auth Manager Server");
               
                byte[] outbuf = RequestProcessor.process(ex).getBytes();
                 ex.sendResponseHeaders(200, 0);
                OutputStream out = ex.getResponseBody();
                out.write(outbuf);
             //   in = new ByteArrayInputStream(_out.toByteArray());
              /*  while ((read = in.read(outbuf)) != -1) {
                    out.write(outbuf, 0, read);
                }*/
                out.flush();
                ex.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    private String headersAsString(Headers h){
        String result = "";
        String key;
        List<String> value;
        
        Iterator<String> iter = h.keySet().iterator();
        while(iter.hasNext()){
            key = iter.next();
            value = h.get(key);
          // result += header.toString() + "\n";
             result += key +":" + String.join(",", value) +"\n";
        }
        return result;
    }
}
