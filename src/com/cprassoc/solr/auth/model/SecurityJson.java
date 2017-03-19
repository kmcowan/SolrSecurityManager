/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.model;

import com.cprassoc.solr.auth.util.JsonHelper;
import com.cprassoc.solr.auth.util.Log;
import java.util.LinkedHashMap;
import java.util.Map;
import net.arnx.jsonic.JSON;
import org.apache.cxf.helpers.IOUtils;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author kevin
 */
public class SecurityJson {

    private Authorization authorization = null;
    private Authentication authentication = null;
    private Map map = null;
    private final JSONParser parser = new JSONParser();

    public SecurityJson() {

        init();
    }

    public SecurityJson(Map map) {
        this.map = map;
        init();
    }

    private void init() {

        try {
            if (map == null) {
                String sec = IOUtils.readStringFromStream(getClass().getResourceAsStream("security.json"));
                Log.log(getClass(), sec);
                map = JsonHelper.parse(sec);
            }
            LinkedHashMap autho = (LinkedHashMap) map.get("authorization");
            LinkedHashMap authi = (LinkedHashMap) map.get("authentication");
            //   System.out.println("Authorization class: " + autho.getClass().getSimpleName());
            //    System.out.println("Authentication class: " + authi.getClass().getSimpleName());

            authorization = new Authorization(autho);
            authentication = new Authentication(authi);
            //String stdout = JsonHelper.objToString(map);
            // System.out.println("SECURITY: "+stdout);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String export() {
        String json = "";
        try {
            //  LinkedHashMap<String,Object> map = new LinkedHashMap();
            //   map.put("authorization", authorization.toJson());
            //   map.put("authentication", authentication.toJson());
            json += "{";
            json += "\"authorization\":" + authorization.toJson();
            json += ",";
            json += "\"authentication\":" + authentication.toJson();
            json += "}";
            //  json = JSON.encode(map);
            //  JSONObject obj = (JSONObject)parser.parse(new String(Utils.streamToString(this.getClass().getResourceAsStream("security.json"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public boolean hasPermission(String permissionName) {
        LinkedHashMap<String, Object> perm;
        for (int i = 0; i < getAuthorization().getPermissions().size(); i++) {
            if (getAuthorization().getPermissions().get(i).get("name").equals(permissionName)) {
                return true;
            }
        }
        return false;
    }

    public LinkedHashMap<String, String> getPermission(String permissionName) {
        LinkedHashMap<String, Object> perm;
        for (int i = 0; i < getAuthorization().getPermissions().size(); i++) {
            if (getAuthorization().getPermissions().get(i).get("name").equals(permissionName)) {
                return getAuthorization().getPermissions().get(i);
            }
        }
        return null;
    }

    public void addUpdatePermission(LinkedHashMap<String, String> permission) {
        if (hasPermission(permission.get("name"))) {
            for (int i = 0; i < getAuthorization().getPermissions().size(); i++) {
                if (getAuthorization().getPermissions().get(i).get("name").equals(permission.get("name"))) {
                    getAuthorization().getPermissions().set(i, permission);
                    break;
                }
            }
        } else {
            getAuthorization().getPermissions().add(permission);
        }
    }

    public void load(String config) {

    }

    /**
     * @return the authorization
     */
    public Authorization getAuthorization() {
        return authorization;
    }

    /**
     * @param authorization the authorization to set
     */
    public void setAuthorization(Authorization authorization) {
        this.authorization = authorization;
    }

    /**
     * @return the authentication
     */
    public Authentication getAuthentication() {
        return authentication;
    }

    /**
     * @param authentication the authentication to set
     */
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }
}
