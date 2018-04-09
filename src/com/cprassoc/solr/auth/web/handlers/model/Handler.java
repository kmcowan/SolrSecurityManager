/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.web.handlers.model;

import com.cprassoc.solr.auth.web.handlers.RequestHandler;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONObject;

/**
 *
 * @author kevin
 */
public interface Handler extends RequestHandler {
    
    public byte[] handle(JSONObject action, HttpExchange ex);
    
}
