/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth;

import java.io.File;
import org.json.JSONObject;
import com.cprassoc.solr.auth.util.Utils;
import java.io.FileInputStream;
import java.util.HashMap;
import org.json.JSONArray;

/**
 *
 * @author kevin
 */
public class ActionDefinitionsManager {

    private static String SOLR_WEB_AUTH_JSON = "solr_auth_web.json";
    private static String ACTIONS = "actions", SCRIPTS = "scripts", PAGES = "pages";
    private static JSONObject json = null;
    private static ActionDefinitionsManager actionMgr = null;
    private HashMap<String, JSONObject> actionMap = null;
    private HashMap<String, File> pages = null;

    private ActionDefinitionsManager() {
        init();
    }

    private void init() {
        try {
            File file = new File(SOLR_WEB_AUTH_JSON);
            if (file.exists()) {
                String jstr = Utils.streamToString(new FileInputStream(file));
                json = new JSONObject(jstr);
            }

            // load actions
            actionMap = new HashMap<>();
            JSONArray arr = json.getJSONArray(ACTIONS);
            JSONObject obj = null;
            for (int i = 0; i < arr.length(); i++) {
                obj = arr.getJSONObject(i);
                System.out.println("Add ACTION: "+obj.getString(ActionKey.name.name()));
                actionMap.put(obj.getString(ActionKey.name.name()), obj);
            }
            
            // load pages
            pages = new HashMap<>();
            arr = json.getJSONArray(PAGES);
            String basePath = json.getString("action_page_dir");
            File page = null;
            String pageName = "";
            for (int i = 0; i < arr.length(); i++) {
                obj = arr.getJSONObject(i);
                pageName = obj.getString("page");
                page = new File(basePath + pageName);
                pages.put(pageName, page);
            }
            
            /**@TODO need to add scripts */ 

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public JSONArray getActions() {
        return json.getJSONArray(ACTIONS);

    }

    public JSONArray getPages() {
        return json.getJSONArray(PAGES);

    }

    public JSONArray getScripts() {
        return json.getJSONArray(SCRIPTS);

    }

    public JSONObject getAction(String key) {
        if (actionMap.get(key) != null) {
            return actionMap.get(key);
        }
        return null;
    }
    
    public File getPage(String key){
        if(pages.get(key) != null){
            return pages.get(key);
        }
        return null;
    }

    public boolean isValidAction(String key) {
        if (actionMap.get(key) != null) {
            System.out.println("Action is VALID: "+key);
            return true;
        }
        // System.out.println("Action is ** INVALID ** : "+key);
        return false;
    }

    public enum ActionKey {
        name,
        page,
        ContentType
    }

    public static ActionDefinitionsManager getInstance() {
        if (actionMgr == null) {
            actionMgr = new ActionDefinitionsManager();
        }
        return actionMgr;
    }

}
