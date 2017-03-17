/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth;

import static com.cprassoc.solr.auth.SolrHttpHandler.AUTHENTICATION_URL_PART;
import com.cprassoc.solr.auth.model.Authentication;
import com.cprassoc.solr.auth.util.JsonHelper;
import com.cprassoc.solr.auth.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Properties;
import org.json.JSONObject;

/**
 *
 * @author kevin
 */
public class SolrAuthActionController {

    public final static SolrHttpHandler SOLR = new SolrHttpHandler();

    public static void doSavePropertiesAction(Properties props) {
        try {
            File f = new File(SolrAuthManager.SOLR_AUTH_PROPERTIES);
            OutputStream out = new FileOutputStream(f);
            props.store(out, "SolrAuthManager Properties");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String addUser(String uname, String pw) {
        String result = "";
        String path = SOLR.getSolrBaseUrl() + AUTHENTICATION_URL_PART;
        String data = "{ \"set-user\": {\"" + uname + "\" : \"" + pw + "\" }}";

        /*
          curl --user solr:SolrRocks http://localhost:8983/solr/admin/authentication -H 'Content-type:application/json'-d '{ 
  "set-user": {"tom" : "TomIsCool" , 
               "harry":"HarrysSecret"}}'
         */
        // first, post the user to solr. 
        result = SOLR.post(path, data);
        Log.log(SolrAuthActionController.class, result);
        // then pull back authentication to get the pwd hash
        String authentication = SolrAuthActionController.SOLR.getAuthentication();
        JSONObject authoeJson = new JSONObject(authentication);
        LinkedHashMap authoeMap = new LinkedHashMap(JsonHelper.jsonToMap(authoeJson));
        Authentication newauthentication = new Authentication(authoeMap);

        // return the new pwd hash as result. 
        String pwhash = newauthentication.getCredentials().get(uname);
        Log.log(SolrAuthActionController.class, "New User hash: " + pwhash);
        return pwhash;
    }

    public static String deleteUser(String uname) {
        String result = "";
        String path = SOLR.getSolrBaseUrl() + AUTHENTICATION_URL_PART;
        String data = "{ \"delete-user\": [\"" + uname + "\" ]}";

        /*
         curl --user solr:SolrRocks http://localhost:8983/solr/admin/authentication -H 'Content-type:application/json'-d  '{
 "delete-user": ["tom","harry"]}'
         */
        // first, post the user to solr. 
        Log.log(SolrAuthActionController.class, result);
        result = SOLR.post(path, data);

        return result;
    }
}
