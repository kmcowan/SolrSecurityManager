/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.web.handlers;

import com.sun.net.httpserver.HttpExchange;

/**
 *
 * @author kevin
 */
public interface RequestHandler {
    
    public String process(HttpExchange ex);
}
