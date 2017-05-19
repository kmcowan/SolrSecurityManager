/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.util;

import java.io.File;
import java.io.FileWriter;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author kevin
 */
public class AuthManagerScriptRenderer {
    
    public static void renderSolrAuthScript(String pathToSolr){
        String mime = "sh";
        if(Utils.isWindows()){
            mime = "bat";
        }
        
        try{
            
          String script = "cd "+ pathToSolr + "\n\r"; 
          
        script += "server"+File.separator+"scripts"+File.separator+"cloud-scripts"+File.separator+"zkcli.sh -zkhost localhost:9983 -cmd putfile "+File.separator+"security.json security.json \n\r";
      //  IOUtils.write(script, new FileWriter(new File(SOLR_AUTH_SCRIPT_NAME+mime)));
      Utils.writeBytesToFile(SOLR_AUTH_SCRIPT_NAME+mime, script);
        
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public final static String SOLR_AUTH_SCRIPT_NAME = "solrAuth.";
}
