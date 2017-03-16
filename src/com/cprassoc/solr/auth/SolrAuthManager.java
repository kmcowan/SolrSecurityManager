/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth;

import com.cprassoc.solr.auth.model.SecurityJson;
import com.cprassoc.solr.auth.ui.SolrAuthMainWindow;
import com.cprassoc.solr.auth.ui.SolrSecurityPropertyManagerFrame;
import java.awt.Desktop;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;

/**
 *
 * @author kevin
 */
public class SolrAuthManager {

    private static Properties properties = null;
    private static final Desktop desktop = Desktop.getDesktop();
    private static String[] originalArgs = null;

    public final static String SOLR_AUTH_PROPERTIES = "solr-security.properties";

    public SolrAuthManager() {

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args != null) {
            originalArgs = args;
        }
        try {
            File props = new File(SOLR_AUTH_PROPERTIES);
            if (props.exists()) {
                System.out.println("Properties File exists...");
                properties = new Properties();
                properties.load(new FileReader(props));
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

    /**
     * @return the desktop
     */
    public static Desktop getDesktop() {
        return desktop;
    }

}
