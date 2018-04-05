/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.web.handlers;

import com.cprassoc.solr.auth.util.Utils;
import com.cprassoc.solr.auth.web.html.HTML;
import com.sun.net.httpserver.HttpExchange;

/**
 *
 * @author kevin
 */
public class NoActionFoundHandler extends BaseHandler implements RequestHandler {
    
    public String process(HttpExchange ex){
        //String result = "";
        //result = HTML.getHTMLContent("No action found for "+ex.getRequestURI().getPath());
        
        String page = ex.getRequestURI().getPath();
        
        if( page.startsWith("/") ){
            page = page.substring(1);
        }
        
        String result = "";
        try{
            result = Utils.streamToString(HTML.class.getResourceAsStream(page));
        }catch(Exception e){
            e.printStackTrace();
            result = Utils.streamToString(HTML.class.getResourceAsStream("404.html"));
        }
              
              
        return result;
    }
}
