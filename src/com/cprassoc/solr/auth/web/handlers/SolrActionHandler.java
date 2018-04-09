/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.web.handlers;

import com.cprassoc.solr.auth.SolrAuthActionController;
import com.cprassoc.solr.auth.model.SecurityJson;

import com.cprassoc.solr.auth.web.handlers.model.Handler;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONObject;

/**
 *
 * @author kevin
 */
public class SolrActionHandler extends BaseHandler implements Handler{
     private SecurityJson securityJson = null;
     
    public byte[] handle(JSONObject action, HttpExchange ex){
       JSONObject obj = new JSONObject();
        securityJson = null;
        SolrAuthActionController.SOLR.getAuthentication();
        String currAction = action.getString("name");
        switch(currAction){
            case "listusers":
                
                break;
        }
      
    
        return obj.toString().getBytes();
    }
    
}
