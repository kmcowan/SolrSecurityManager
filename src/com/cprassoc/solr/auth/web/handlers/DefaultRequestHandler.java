/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.web.handlers;

import com.cprassoc.solr.auth.web.DataBean;
import com.cprassoc.solr.auth.web.Helper;
import com.cprassoc.solr.auth.web.html.HTML;

import com.sun.net.httpserver.HttpExchange;

/**
 *
 * @author kevin
 */
public class DefaultRequestHandler extends BaseHandler implements RequestHandler {
    
    public String process(HttpExchange ex){
        String mode = ex.getRequestMethod();
        String result = HTML.getPage(HTML.Page.index);
        
        if( mode.equalsIgnoreCase("POST") ){
            DataBean args = Helper.readPostParameters(ex);
            result = Helper.toJSON(args);
        }
        else if( mode.equalsIgnoreCase("PUT") ){
            DataBean args = Helper.readAnyParameters(ex);
            result = Helper.toJSON(args);
        }
        else if( mode.equalsIgnoreCase("DELETE") ){
            DataBean args = Helper.readAllParameters(ex);
            result = Helper.toJSON(args);
        }
        else {
            DataBean args = Helper.readAllParameters(ex);
            result = Helper.toJSON(args);
        }
        
        return result + mode;
    }
}
