/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.web;

import com.cprassoc.solr.auth.util.Utils;
import com.cprassoc.solr.auth.web.html.HTML;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
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
                String contentType = "application/json";
            
                String page = ex.getRequestURI().getPath();
                if( page.equals("/") )
                    page += "index.html";
            
                boolean isService = false;
            
                if( page.endsWith(".js") ){
                    contentType = "application/javascript";
                }
                else if( page.endsWith(".png") ){
                    contentType = "image/png";
                }
                else if( page.endsWith(".jpg") || page.endsWith(".jpeg") ){
                    contentType = "image/jpeg";
                }
                else if( page.endsWith(".ico") ){
                    contentType = "image/icon";
                }
                else if( page.endsWith(".css") ){
                    contentType = "text/css";
                }
                else if( page.endsWith(".html") || page.equals("") || page.contains("index.html") ){
                    contentType = "text/html";
                }
                else {
                    isService = true;
                }
                ex.getResponseHeaders().add("Content-Type", contentType);
                ex.getResponseHeaders().add("X-Powered-by", "Solr Auth Manager Server");
                OutputStream out = ex.getResponseBody();
                System.out.println("Process request FOR: "+page);
                if( isService ){
                   // System.out.println("Process request as SERVICE");
                    byte[] outbuf = RequestProcessor.process(ex).getBytes();
                     ex.sendResponseHeaders(200, 0);
                    
                    out.write(outbuf);
                }
                else {
                  //   System.out.println("Process request as NON-SERVICE");
                    if( page.startsWith("/") ){
                        page = page.substring(1);
                    }
                    ex.sendResponseHeaders(200, 0);
                    java.io.InputStream stream = HTML.class.getResourceAsStream(page);
                    byte bytes[] = Utils.streamToBytes(stream);
                    if(Utils.isHTML(bytes)){
                          ex.getResponseHeaders().add("Content-Type",  "text/html");
                    }
                    out.write(bytes);
                }
            
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
