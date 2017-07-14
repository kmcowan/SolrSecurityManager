/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth;

import com.cprassoc.solr.auth.model.ContextualHelp;
import com.cprassoc.solr.auth.ui.SolrAuthMainWindow;
import com.cprassoc.solr.auth.ui.SolrSecurityPropertyManagerFrame;
import com.cprassoc.solr.auth.util.Log;
import com.cprassoc.solr.auth.web.WebServer;
import java.awt.Desktop;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

/**
 *
 * @author kevin
 */
public class SolrAuthManager {

    private static Properties properties = null;
    private static final Desktop desktop = Desktop.getDesktop();
    private static String[] originalArgs = null;
    private static final ContextualHelp help = ContextualHelp.getInstance();

    public final static String SOLR_AUTH_PROPERTIES = "solr-security.properties";

    public SolrAuthManager() {

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            originalArgs = args;
            WebServer.main(args);
            
        } else {
            try {
                File props = new File(SOLR_AUTH_PROPERTIES);

                if (props.exists()) {
                    System.out.println("Properties File exists...");
                    properties = new Properties();
                    properties.load(new FileReader(props));
                    Iterator<Object> iter = properties.keySet().iterator();
                    Object key;
                    String value;
                    while (iter.hasNext()) {
                        key = iter.next();
                        value = properties.getProperty(key.toString());
                        Log.log("property " + key.toString() + " value: " + value);
                    }

                    SolrAuthMainWindow.main(args);
                } else {
                    System.out.println("NO Properties File found...");
                    properties = SolrSecurityPropertyManagerFrame.getSolrAuthProperties();
                    SolrSecurityPropertyManagerFrame.main(args);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * @return the properties
     */
    public static Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
            try {
                File props = new File(SOLR_AUTH_PROPERTIES);
                properties.load(new FileReader(props));
            } catch (Exception e) {
            }
        }
        return properties;
    }

    public static void setProperties(Properties p) {
        properties = p;
    }

   

    /**
     * @return the desktop
     */
    public static Desktop getDesktop() {
        return desktop;
    }

}
