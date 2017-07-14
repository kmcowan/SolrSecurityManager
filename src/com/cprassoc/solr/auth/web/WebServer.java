/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.web;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.HashMap;

/**
 *
 * @author kevin
 */
public class WebServer {
    private static HashMap<String,String> config = null;
    private WebServer(){
      
    }
    public static void main(String[] args){
        try{
            config = argsAsMap(args);
            int port = 8081;
            if(config != null && config.get("-p") != null){
                port = Integer.parseInt(config.get("-p"));
            }
            System.out.println("Starting web server on port: "+port);
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.start();
            server.createContext("/", new RESTService());
            System.out.println("Server Started OK...");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
     public static HashMap<String, String> argsAsMap(String[] args) {
        String key, value;
        HashMap<String, String> params = new HashMap<>();
                 params.put("-p", "8081");
        params.put("-s", "localhost");
        for (int i = 0; i < args.length; i++) {
            key = args[i];
            if (params.get(key) != null) {
                value = args[i + 1];// assumes value follows key in args list. 
                params.put(key, value);
            }
        }
        return params;
    }
}
