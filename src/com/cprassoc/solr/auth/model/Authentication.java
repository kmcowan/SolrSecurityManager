/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.model;

import java.util.Iterator;
import java.util.LinkedHashMap;
import net.arnx.jsonic.JSON;

/**
 *
 *  "authentication":{
    "class":"solr.BasicAuthPlugin",
    "credentials":{
      "solr":"IV0EHq1OnNrj6gvRCwvFwTrZ1+z1oBbnQdiVC3otuq0= Ndd7LKvVBAaZIF0QAVi1ekCfAJXr1GGfLtRUXhgrF8c=",
      "devuser":"IV0EHq1OnNrj6gvRCwvFwTrZ1+z1oBbnQdiVC3otuq0= Ndd7LKvVBAaZIF0QAVi1ekCfAJXr1GGfLtRUXhgrF8c="}}}
 */
public class Authentication {
        String className = "";
    private LinkedHashMap<String,String> credentials = null;
    public Authentication(LinkedHashMap map){
        init(map);
    }
    
    private void init(LinkedHashMap map){
            LinkedHashMap authMap = (LinkedHashMap)map.get("authentication");
            if(authMap != null){
            credentials = (LinkedHashMap)authMap.get("credentials");
            } else {
               credentials = (LinkedHashMap)map.get("credentials");
            }
        className = (String)map.get("class");
        if(credentials != null){
        //System.out.println("Credentials: "+credentials.getClass().getSimpleName());
          System.out.println("Found: "+credentials.size()+" credentialed users...");
          Iterator<String> iter = credentials.keySet().iterator();
          String key, value;
          while(iter.hasNext()){
              key = iter.next();
              value = credentials.get(key);
              System.out.println("User: "+key);
          }
        } else {
            System.err.println(" Credentials NOT FOUND! Printing available keys...");
            Iterator<String> iter = map.keySet().iterator();
            String key;
            Object value;
            while(iter.hasNext()){
               key = iter.next();
               value = map.get(key);
               System.out.println("key: "+key+" value: "+value.getClass().getSimpleName());
            }
            
        }
    }
    
    public void removeCredentials(String key){
        if(credentials.get(key) != null){
            credentials.remove(key);
        }
    }

    /**
     * @return the credentials
     */
    public LinkedHashMap<String,String> getCredentials() {
        return credentials;
    }
    
    public String toJson(){
        String result = "";
        result += "  { ";
        result += "\"class\":\"solr.BasicAuthPlugin\",";
        result += "\"credentials\":"+ JSON.encode(credentials);//, true){"solr":"IV0EHq1OnNrj6gvRCwvFwTrZ1+z1oBbnQdiVC3otuq0= Ndd7LKvVBAaZIF0QAVi1ekCfAJXr1GGfLtRUXhgrF8c="}
        result += "}";//"
        return result;
    }
}
