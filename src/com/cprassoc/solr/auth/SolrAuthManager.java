/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth;

import com.cprassoc.solr.auth.ui.SolrAuthMainWindow;
import java.io.File;

/**
 *
 * @author kevin
 */
public class SolrAuthManager {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File props = new File("solr-security.properties");
        if(props.exists()){
        SolrAuthMainWindow.main(args);
        } else {
            
        }
    }
    
}
