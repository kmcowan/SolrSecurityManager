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
import org.apache.cxf.helpers.IOUtils;

/**
 *
 * @author kevin
 */
public class SecurityJson {

    private Authorization authorization = null;
    private Authentication authentication = null;
    private Map map = null;

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
