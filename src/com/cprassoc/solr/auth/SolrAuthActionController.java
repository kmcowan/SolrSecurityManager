/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 *
 * @author kevin
 */
public class SolrAuthActionController {
    
    public final static SolrHttpHandler SOLR = new SolrHttpHandler();
    
    public static void doSavePropertiesAction(Properties props){
        try{
        File f = new File(SolrAuthManager.SOLR_AUTH_PROPERTIES);
        OutputStream out = new FileOutputStream( f );
        props.store(out, "SolrAuthManager Properties");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
 
}
