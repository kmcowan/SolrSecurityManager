/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.model;

import com.cprassoc.solr.auth.forms.html.HTMLResource;
import com.cprassoc.solr.auth.util.Log;
import com.cprassoc.solr.auth.util.Utils;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.json.JSONObject;

/**
 *
 * @author kevin
 */
public class ContextualHelp {
 
    
    private static ContextualHelp help = null;
    private LinkedHashMap<String, LinkedHashMap<String,String>> data = null;
    
    private ContextualHelp(){
        load();
    }
    
    private void load(){
        JSONObject map = getDB();//new JSONObject(Utils.streamToString(HTMLResource.class.getResourceAsStream("help_db.json")));
        Iterator<String> iter = map.getJSONObject("help").keySet().iterator();
        String hkey,title,content;
        Object value;
        JSONObject json;
        LinkedHashMap lmap;
 
        data = new LinkedHashMap<>();
        while(iter.hasNext()){
            hkey = iter.next();
            value = map.getJSONObject("help").get(hkey);
            if(value instanceof JSONObject){
                json = (JSONObject)value;
                title = json.getString("title");
                System.out.println(title);
                content = Utils.streamToString(HTMLResource.class.getResourceAsStream(json.getString("template")));
                lmap = new LinkedHashMap<>();
                lmap.put("title", title);
                lmap.put("content", content);
                data.put(hkey, lmap);
            }
        }
    }
    
    public LinkedHashMap<String,String> getHelpContent(String key){
        return data.get(key);
    }

    public LinkedHashMap<String,String> getHelpTopics(String key){
        LinkedHashMap<String,String> topics = new LinkedHashMap<>();
        Iterator<String> iter = data.keySet().iterator();
        String ckey,title,url;
        LinkedHashMap<String,String> map;
        while(iter.hasNext()){
            ckey = iter.next();
            map = data.get(ckey);
            title = map.get("title");
            url = "<a href=\"file://"+ckey+"\">" + title +"</a>";
            Log.log("Add URL: "+url);
            topics.put(title, url);
            
        }
        topics.put("title","Solr Security Manager Help Topics");
        
        return topics;
    }
    
    private JSONObject getDB(){
          JSONObject map = new JSONObject(Utils.streamToString(HTMLResource.class.getResourceAsStream("help_db.json")));
      
        return map;
    }
        
    public static ContextualHelp getInstance(){
        if(help == null){
            help = new ContextualHelp();
        }
        
        return help;
    }
}
