/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.web.handlers;

import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 *
 * @author kevin
 */
public abstract class BaseHandler {

    public Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        if(query != null && 
                !query.trim().equals("") && 
                query.contains("&")){
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        }
        return result;
    }
    
    protected boolean isValidHandlerAction(String action, Enum[] list){
        for(int i=0; i<list.length; i++){
            if(list[i].name().equals(action)){
                return true;
            }
        }
        return false;
    }
    
    protected Enum getHandlerActionEnum(String action, Enum[] list){
        
          for(int i=0; i<list.length; i++){
            if(list[i].name().equals(action)){
                return list[i];
            }
        }
        return null;
    }
    
     public String process(HttpExchange ex){
         return "";
     }
    
    
    
    public enum JsonKey {
        error,
        message,
        request,
        json,
        documents
    }
}
