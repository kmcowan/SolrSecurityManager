/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth;

import static com.cprassoc.solr.auth.SolrHttpHandler.AUTHENTICATION_URL_PART;
import com.cprassoc.solr.auth.model.Authentication;
import com.cprassoc.solr.auth.model.Authorization;
import com.cprassoc.solr.auth.model.SecurityJson;
import com.cprassoc.solr.auth.util.JsonHelper;
import com.cprassoc.solr.auth.util.Log;
import com.cprassoc.solr.auth.util.Utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
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

    public static String addRole(String user, ArrayList<String> roles) {
        String result = "";
        String data = "";
        try {
            String path = SOLR.getSolrBaseUrl() + SolrHttpHandler.AUTHORIZATION_URL_PART;
            if (roles.size() > 1) {
                // case for multiple roles
                data = "{ \"set-user-role\": {\"" + user + "\" : \"" + roles.toString() + "\" }}";
            } else if (roles.size() == 1 && roles.get(0).trim().equals("null")) {
                // case for null role
                data = "{ \"set-user-role\": {\"" + user + "\" : " + roles.get(0) + " }}";
            } else {
                // case for single role
                data = "{ \"set-user-role\": {\"" + user + "\" : \"" + roles.get(0) + "\" }}";
            }
            /*
        curl --user solr:SolrRocks http://localhost:8983/solr/admin/authorization -H 'Content-type:application/json' -d '{ 
  "set-user-role": {"tom":["admin","dev"},
  "set-user-role": {"harry":null}
}'
             */

            result = SOLR.post(path, data);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String addOrEditPermission(LinkedHashMap<String, Object> permission, boolean isEditing) {
        String result = "";
        String data = "";
        String actionKey = "set-permission";
        if (isEditing) {
            actionKey = "update-permission";
        }
        try {
            String path = SOLR.getSolrBaseUrl() + SolrHttpHandler.AUTHORIZATION_URL_PART;
            data = "{ \"" + actionKey + "\": " + JsonHelper.objToString(permission) + "}";
            /*
         curl --user solr:SolrRocks http://localhost:8983/solr/admin/authorization -H 'Content-type:application/json'-d '{ 
  "set-permission": { "name":"a-custom-permission-name",
                      "collection":"gettingstarted",
                      "path":"/handler-name",
                      "before": "name-of-another-permission",
                      "role": "dev"
   }
             */

            result = SOLR.post(path, data);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String deletePermission(String name, int index) {
        String result = "";
        String data = "";

        try {
            String path = SOLR.getSolrBaseUrl() + SolrHttpHandler.AUTHORIZATION_URL_PART;
            if (index != -1) { // if we have a current index, use that. 
                data = "{ \"delete-permission\": " + index + "}";
            } else {// otherwise, get the index from the api
                String authorization = SolrAuthActionController.SOLR.getAuthorization();
                JSONObject authoJson = new JSONObject(authorization);
                LinkedHashMap authoMap = new LinkedHashMap(JsonHelper.jsonToMap(authoJson));
                Authorization auth = new Authorization(authoMap);
                String key;
                Object value;
                for (int i = 0; i < auth.getPermissions().size(); i++) {
                    value = auth.getPermissions().get(i).get("index");
                    key = (String) auth.getPermissions().get(i).get("name");
                    if (value instanceof Integer) {
                        index = (Integer) value;
                        break;
                    } else if (key.equals(name)) {
                        index = i;
                        break;
                    }
                }

                data = "{ \"delete-permission\": " + index + "}";
            }

            if (index > -1) {
                result = SOLR.post(path, data);
            } else {
                result = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
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

    public static JSONObject getCollections() {
        JSONObject obj = null;
        try {
            String url = SOLR.getSolrBaseUrl() + SolrHttpHandler.COLLECTION_LIST_URL_PART;
            String result = SOLR.get(url);
            obj = new JSONObject(result);
            // http://localhost:8983/solr/admin/collections?action=LIST&wt=json

        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
    
    public static JSONObject getServerStatus() {
        JSONObject obj = null;
        try {
            String url = SOLR.getSolrBaseUrl() + SolrHttpHandler.STATUS_URL_PART;
            String result = SOLR.get(url);
            obj = new JSONObject(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static String doPushConfigToSolrAction(SecurityJson json) {
        String result = "";
        try {
            String pathToScript = System.getProperty("user.dir") + File.separator + "solrAuth.sh";
            String jsonstr = json.export();
            String filePath = SolrAuthManager.getProperties().getProperty("solr.install.path") + File.separator + "security.json";
            File backup = new File("backup");
            if(!backup.exists()){
                backup.mkdirs();
            }
            
            File file = new File(filePath);
            if(file.exists()){
                // String newFilePath = SolrAuthManager.getProperties().getProperty("solr.install.path") + File.separator + System.currentTimeMillis() + "_security.json";
                // File newFile = new File(newFilePath);
                // file.renameTo(newFile);
                 FileUtils.copyFileToDirectory(file, backup);
            }
            FileUtils.writeByteArrayToFile(file, jsonstr.getBytes());
            Thread.sleep(500);
            ProcessBuilder pb = new ProcessBuilder(pathToScript);
          

            Log.log("Run PUSH command");

            Process process = pb.start();
            if (process.waitFor() == 0) {
                result = "";
            } else {
                result = Utils.streamToString(process.getErrorStream());
                result += "\n" + Utils.streamToString(process.getInputStream());
                Log.log(result);
            }

        } catch (Exception e) {
            e.printStackTrace();
            result = e.getLocalizedMessage();
        }

        return result;
    }

    public static enum SolrManagerAction {
        create_user,
        delete_user,
        add_role,
        delete_role,
        update_role,
        add_permission,
        edit_permission,
        delete_permission,
        add_a_version,
        push_a_version,
        load_a_version,
        add_permissions_to_permissions
    }

    public static enum SystemErrors {
        EX_OK(0),
        EX__BASE(64),
        EX_USAGE(64),
        EX_DATAERR(65),
        EX_NOINPUT(66),
        EX_NOUSER(67),
        EX_NOHOST(68),
        EX_UNAVAILABLE(69),
        EX_SOFTWARE(70),
        EX_OSERR(71),
        EX_OSFILE(72),
        EX_CANTCREAT(73),
        EX_IOERR(74),
        EX_TEMPFAIL(75),
        EX_PROTOCOL(76),
        EX_NOPERM(77),
        EX_CONFIG(78),
        EX__MAX(78);
        
            private int errorValue = -1;

        SystemErrors(int value) {
            this.errorValue = value;
        }

    }
}
