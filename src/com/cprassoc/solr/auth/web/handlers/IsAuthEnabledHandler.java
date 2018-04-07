/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.web.handlers;

import com.cprassoc.solr.auth.SolrAuthActionController;
import com.cprassoc.solr.auth.util.Utils;
import com.cprassoc.solr.auth.web.handlers.model.Handler;
import com.cprassoc.solr.auth.web.html.HTML;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONObject;

/**
 *
 * @author kevin
 */
public class IsAuthEnabledHandler implements Handler{
    
    public byte[] handle(JSONObject action, HttpExchange ex){
      
        JSONObject obj = new JSONObject();
        boolean isAuthEnabled = SolrAuthActionController.SOLR.isAuthEnabled();
        obj.put("enabled", Boolean.toString(isAuthEnabled));
  
        return obj.toString().getBytes();
    }
    
}
