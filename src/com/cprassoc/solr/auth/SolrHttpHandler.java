/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth;

import com.cprassoc.solr.auth.model.Authentication;
import com.cprassoc.solr.auth.util.JsonHelper;
import com.cprassoc.solr.auth.util.Log;
import com.cprassoc.solr.auth.util.Utils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.json.JSONObject;

/**
 *
 * solr.install.path=/servers/solr_security_test solr.host.port=localhost:8983
 * solr.zookeeper.port=9983 solr.ssl.enabled=false
 */
public class SolrHttpHandler {

    public final static String AUTHORIZATION_URL_PART = "/solr/admin/authorization";
    public final static String AUTHENTICATION_URL_PART = "/solr/admin/authentication";
    private HttpClient client = null;
    private Properties props = null;
    private CloudSolrClient cloudClient = null;
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
                = new UsernamePasswordCredentials("solr", "SolrRocks");
        provider.setCredentials(AuthScope.ANY, credentials);

        // client = new DefaultHttpClient(cm);
        client = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(provider)
                .build();

        cloudClient = new CloudSolrClient(solrBaseUrl, client);

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

            request.setEntity(params);
            HttpResponse response = client.execute(request);
            result = Utils.streamToString(response.getEntity().getContent());
        } catch (Exception e) {

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

    public String addUser(String uname, String pw) {
        String result = "";
        String path = solrBaseUrl + AUTHENTICATION_URL_PART;
        String data = "{ \"set-user\": {\"" + uname + "\" : \"" + pw + "\" }}";

        /*
          curl --user solr:SolrRocks http://localhost:8983/solr/admin/authentication -H 'Content-type:application/json'-d '{ 
  "set-user": {"tom" : "TomIsCool" , 
               "harry":"HarrysSecret"}}'
         */
        // first, post the user to solr. 
        result = post(path, data);
        Log.log(getClass(), result);
        // then pull back authentication to get the pwd hash
        String authentication = SolrAuthActionController.SOLR.getAuthentication();
        JSONObject authoeJson = new JSONObject(authentication);
        LinkedHashMap authoeMap = new LinkedHashMap(JsonHelper.jsonToMap(authoeJson));
        Authentication newauthentication = new Authentication(authoeMap);

        // return the new pwd hash as result. 
        String pwhash = newauthentication.getCredentials().get(uname);
        Log.log(getClass(), "New User hash: " + pwhash);
        return pwhash;
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
     * @return the cloudClient
     */
    public CloudSolrClient getCloudClient() {
        return cloudClient;
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
}
