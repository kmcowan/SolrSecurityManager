/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.web;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

/**
 *
 * @author kevin
 */
public class WebServer {
    
    public static void main(String[] args){
        try{
            HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
            server.start();
            server.createContext("/", new RESTService());
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
