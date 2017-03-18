/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 *
 * "authorization":{ "class":"solr.RuleBasedAuthorizationPlugin",
 * "permissions":[{"name":"security-edit","role":"admin"},{"name":"security-read","role":"admin"}
 * ], "user-role":{"solr":"admin"} }}
 */
public class Authorization {

    private String className = "";
    private ArrayList<LinkedHashMap> permissions = null;
    private LinkedHashMap userRoles = null;

    public Authorization(LinkedHashMap map) {
        LinkedHashMap authMap = (LinkedHashMap) map.get("authorization");
        if (authMap != null) {
            // loading from a live api response
            userRoles = (LinkedHashMap) authMap.get("user-role");
        } else {
            // loading from default security json
            userRoles = (LinkedHashMap) map.get("user-role");
        }
        if (userRoles != null) {
            Iterator<String> iter = userRoles.keySet().iterator();
            String key;
            Object value;
            while (iter.hasNext()) {
                key = iter.next();
                value = userRoles.get(key);
                if(value instanceof String[]){
                    value = Arrays.toString((String[])value);
                } else if(value instanceof ArrayList){
                    ArrayList temp = (ArrayList)value;
                    value = temp.toString();
                }
                System.out.println("user: " + key + " role: " + value);
            }
        } else {
            System.out.println("user roles was null");
            Iterator<String> iter = map.keySet().iterator();
            String key;
            Object value;
            while (iter.hasNext()) {
                key = iter.next();
                value = map.get(key);
                System.out.println("key: " + key + " value: " + value.getClass().getSimpleName());
            }
        }
        LinkedHashMap resp = (LinkedHashMap) map.get("responseHeader");
        Iterator<String> iter;
        if (resp != null) {
            iter = resp.keySet().iterator();
            while (iter.hasNext()) {
                System.out.println("Key0: " + iter.next());
            }
        }

        if (authMap != null) {
            iter = authMap.keySet().iterator();
            while (iter.hasNext()) {
                System.out.println("Key1: " + iter.next());
            }
            permissions = (ArrayList) authMap.get("permissions");
        } else {
            permissions = (ArrayList) map.get("permissions");
        }
        if (permissions != null) {
            LinkedHashMap perm;
            for (int i = 0; i < permissions.size(); i++) {
                perm = (LinkedHashMap) permissions.get(i);
                System.out.println("Permission: " + perm.get("name"));

                // System.out.println("Index: "+i+" value: "+permissions.get(i).getClass().getSimpleName());
            }
        } else {
            iter = authMap.keySet().iterator();
            while (iter.hasNext()) {
                System.out.println("Key2: " + iter.next());
            }
        }

    }

    public boolean hasPermission(String key) {
        if (permissions != null) {
            LinkedHashMap perm;
            for (int i = 0; i < permissions.size(); i++) {
                perm = (LinkedHashMap) permissions.get(i);
                if (perm.get("name").equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return the permissions
     */
    public ArrayList<LinkedHashMap> getPermissions() {
        return permissions;
    }

    /**
     * @return the userRoles
     */
    public LinkedHashMap getUserRoles() {
        return userRoles;
    }
}
