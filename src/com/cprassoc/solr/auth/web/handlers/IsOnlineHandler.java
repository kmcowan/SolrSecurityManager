/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.web.handlers;

import com.cprassoc.solr.auth.SolrAuthActionController;

import com.cprassoc.solr.auth.web.handlers.model.Handler;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONObject;

/**
 *
 * @author kevin
 */
public class IsOnlineHandler extends BaseHandler implements Handler{
    
    public byte[] handle(JSONObject action, HttpExchange ex){
       
        JSONObject obj = SolrAuthActionController.getServerStatus();
    
        return obj.toString().getBytes();
    }
    
}
