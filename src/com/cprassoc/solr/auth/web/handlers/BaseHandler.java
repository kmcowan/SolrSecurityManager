/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.web.handlers;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kevin
 */
public abstract class BaseHandler {

    public Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }
    
    protected boolean isValidHandlerAction(String action){
        for(int i=0; i<HandlerAction.values().length; i++){
            if(HandlerAction.values()[i].name().equals(action)){
                return true;
            }
        }
        return false;
    }
    
    protected HandlerAction getHandlerActionEnum(String action){
          for(int i=0; i<HandlerAction.values().length; i++){
            if(HandlerAction.values()[i].name().equals(action)){
                return HandlerAction.values()[i];
            }
        }
        return HandlerAction.noaction;
    }
    
    protected enum HandlerAction {
        create,
        read,
        update,
        delete,
        noaction
    }
}
