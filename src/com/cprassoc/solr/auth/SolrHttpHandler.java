/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth;

import com.cprassoc.solr.auth.util.Log;
import com.cprassoc.solr.auth.util.Utils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * solr.install.path=/servers/solr_security_test solr.host.port=localhost:8983
 * solr.zookeeper.port=9983 solr.ssl.enabled=false
 */
public class SolrHttpHandler {

    public final static String AUTHORIZATION_URL_PART = "/solr/admin/authorization";
    public final static String AUTHENTICATION_URL_PART = "/solr/admin/authentication";
    public final static String COLLECTION_LIST_URL_PART = "/solr/admin/collections?action=LIST&wt=json";
    public final static String STATUS_URL_PART = "/solr/admin/cores?action=STATUS&wt=json";
    
    private HttpClient client = null;
    private Properties props = null;
    private CloudSolrClient zkCloudClient = null;
    private CloudSolrClient solrCloudClient = null;
    private String solrBaseUrl = "";

    protected SolrHttpHandler() {
        PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
        if (props == null) {
            props = SolrAuthManager.getProperties();
        }

        if (props.getProperty("solr.ssl.enabled").equals("true")) {
            solrBaseUrl = "https://";
        } else {
            solrBaseUrl = "http://";
        }
        solrBaseUrl += props.getProperty("solr.host.port");
        //  solr.crawler.cloud.server=localhost:9983
        //admin:password123@
        CredentialsProvider provider = new BasicCredentialsProvider();
   
        UsernamePasswordCredentials credentials
                = new UsernamePasswordCredentials(props.getProperty("solr.admin.user"), props.getProperty("solr.admin.pwd"));
        provider.setCredentials(AuthScope.ANY, credentials);

        // client = new DefaultHttpClient(cm);
        client = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(provider)
                .build();

        zkCloudClient = new CloudSolrClient(props.getProperty("solr.zookeeper.port"), client);
        zkCloudClient.setDefaultCollection(props.getProperty("solr.default.collection"));
        
    //     solrCloudClient = new CloudSolrClient(props.getProperty("solr.host.port"), client);
    //    solrCloudClient.setDefaultCollection(props.getProperty("solr.default.collection"));

        System.out.println("Solr Base URL: " + solrBaseUrl);
    }

    public String get(String path) {
        String result = "";
        try {
            HttpGet get = new HttpGet(path);
            HttpResponse response = client.execute(get);
            result = IOUtils.toString(response.getEntity().getContent());
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }

        return result;
    }

    public String post(String path, String data) {
        String result = "";
        try {
            HttpPost request = new HttpPost(path);
            StringEntity params = new StringEntity(data);
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = client.execute(request);
            result = Utils.streamToString(response.getEntity().getContent());
        } catch (Exception e) {

        }
        return result;
    }

    public String put(String path, String data) {
        String result = "";
        try {
            HttpPut request = new HttpPut(path);
            StringEntity params = new StringEntity(data, "UTF-8");
            params.setContentType("application/json");
            request.addHeader("content-type", "application/json");
            request.addHeader("Accept", "*/*");
            request.addHeader("Accept-Encoding", "gzip,deflate,sdch");
            request.addHeader("Accept-Language", "en-US,en;q=0.8");
            request.setEntity(params);

            HttpResponse response = client.execute(request);
            result = Utils.streamToString(response.getEntity().getContent());
        } catch (Exception e) {

        }
        return result;
    }

    public String delete(String path, String data) {
        String result = "";
        try {
            HttpDelete request = new HttpDelete(path);

            HttpResponse response = client.execute(request);
            result = Utils.streamToString(response.getEntity().getContent());
        } catch (Exception e) {

        }
        return result;
    }

    public boolean isOnline() {
        boolean result = true;
        try {
            if(!collectionExists(zkCloudClient.getDefaultCollection())){
                Log.log("**WARN** COLLECTION DOES NOT EXIST!! ");
                
                zkCloudClient.setDefaultCollection(getCollections().getString(0));
            }
            SolrPingResponse resp = zkCloudClient.ping();
            if (resp == null) {
                Log.log(getClass(), "Ping Response was NULL");
                result = false;
            } else if (resp.getStatus() != 0) {
                result = false;
                Log.log(getClass(), "Ping Response was STATUS was: " + resp.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.log(getClass(), "Ping Response returned EXCEPTION");
            result = false;
        }
        return result;
    }
    
    
     public boolean isAuthEnabled() {
        boolean result = true;
        try {
            String test = getAuthorization();
            Log.log("auth test: "+test);
            JSONObject obj = new JSONObject(test);
            if( obj.get("errorMessages") != null || 
                    test.contains("No authorization configured")){
                result = false;
                Log.log("Auth is NOT Enabled... return false");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.log(getClass(), "AuthEnabled Response returned EXCEPTION");
            result = false;
        }
        return result;
    }
     
    public JSONArray getCollections(){
       JSONArray list = new JSONArray();
        String path = solrBaseUrl + COLLECTION_LIST_URL_PART;
        String result = get(path);
        JSONObject obj = new JSONObject(result);
        list = obj.getJSONArray("collections");
        
        return list;
    }
    
    public boolean collectionExists(String collection){
        boolean result = false;
        JSONArray cols = getCollections();
        for(int i=0; i<cols.length(); i++){
            if(cols.get(i).equals(collection)){
                result = true;
                break;
            }
        }
        return result;
    }

    public String getAuthorization() {
        String result = "";
        String path = solrBaseUrl + AUTHORIZATION_URL_PART;
        return get(path);
    }

    public String getAuthentication() {
        String result = "";
        String path = solrBaseUrl + AUTHENTICATION_URL_PART;
        return get(path);
    }

    private String cURLRequest(String url, String data, Method method) {
        String result = "";

        if (method == null) {
            method = Method.GET;
        }
        try {

            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            // conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            conn.setRequestMethod(method.name());

            String userpass = "solr" + ":" + "SolrRocks";
            String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
            conn.setRequestProperty("Authorization", basicAuth);

            //String data =  "{\"format\":\"json\",\"pattern\":\"#\"}";
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            if (data != null && !data.equals("")) {
                out.write(data);
            }
            out.close();

            // new InputStreamReader(conn.getInputStream());   
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void main(String[] args) {
        SolrHttpHandler handler = new SolrHttpHandler();

        String path = handler.getSolrBaseUrl() + AUTHORIZATION_URL_PART;
        String result = handler.post(path, "");//.cURLRequest(path, "", null);
        System.out.println(result);

    }

    /**
     * @return the client
     */
    public HttpClient getClient() {
        return client;
    }

    /**
     * @return the props
     */
    public Properties getProps() {
        return props;
    }

    /**
     * @return the zkCloudClient
     */
    public CloudSolrClient getZkCloudClient() {
        return zkCloudClient;
    }

    /**
     * @return the solrBaseUrl
     */
    public String getSolrBaseUrl() {
        return solrBaseUrl;
    }

    public static enum Method {
        POST,
        PUT,
        GET,
        DELETE
    }

    public enum ErrorCode {
        BAD_REQUEST(400),
        UNAUTHORIZED(401),
        FORBIDDEN(403),
        NOT_FOUND(404),
        CONFLICT(409),
        UNSUPPORTED_MEDIA_TYPE(415),
        SERVER_ERROR(500),
        SERVICE_UNAVAILABLE(503),
        INVALID_STATE(510),
        UNKNOWN(0);
        public final int code;

        private ErrorCode(int c) {
            code = c;
        }

        public static ErrorCode getErrorCode(int c) {
            for (ErrorCode err : values()) {
                if (err.code == c) {
                    return err;
                }
            }
            return UNKNOWN;
        }
    };
}
