/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth;

import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;

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
    private String solrBaseUrl = "";

    protected SolrHttpHandler() {
        PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
        client = new DefaultHttpClient(cm);
        props = SolrAuthManager.getProperties();
        if (props.getProperty("solr.ssl.enabled").equals("true")) {
            solrBaseUrl = "https://";
        } else {
            solrBaseUrl = "http://";
        }
        solrBaseUrl += props.getProperty("solr.host.port");
        System.out.println("Solr Base URL: "+solrBaseUrl);
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

}
