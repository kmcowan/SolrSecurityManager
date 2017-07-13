/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.web;

import com.cprassoc.solr.auth.web.handlers.CreationRequestHandler;
import com.cprassoc.solr.auth.web.handlers.DefaultRequestHandler;
import com.cprassoc.solr.auth.web.handlers.NoActionFoundHandler;
import com.cprassoc.solr.auth.web.handlers.RequestHandler;
import com.cprassoc.solr.auth.web.html.HTML;
import com.sun.net.httpserver.HttpExchange;
import java.net.URI;

/**
 *
 * @author kevin
 */
public class RequestProcessor {

    public static String process(HttpExchange ex) {
        String result = "";

        try {
            RequestHandler handler = getHandler(ex.getRequestURI());
            result = handler.process(ex);

        } catch (Exception e) {

        } finally {
            if (result == null || result.equals("")) {
                result = HTML.getHTMLContent("Response contained no content...");
            }
        }

        return result;
    }

    private static RequestHandler getHandler(URI uri) {
        RequestHandler handler = null;
        String path = uri.getPath();
        switch (path) {
            case "/":
                handler = new DefaultRequestHandler();
                break;

            case "/create":
                handler = new CreationRequestHandler();
                break;

            default:
                handler = new NoActionFoundHandler();
                break;
        }
        return handler;
    }
}
