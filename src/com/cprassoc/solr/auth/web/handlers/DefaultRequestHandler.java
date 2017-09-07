/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.web.handlers;

import com.cprassoc.solr.auth.SolrAuthActionController;
import com.cprassoc.solr.auth.SolrAuthManager;
import com.cprassoc.solr.auth.ui.SolrSecurityPropertyManagerFrame.SolrAuthPropertyKey;
import com.cprassoc.solr.auth.web.DataBean;
import com.cprassoc.solr.auth.web.Helper;
import com.cprassoc.solr.auth.web.html.HTML;

import com.sun.net.httpserver.HttpExchange;

import java.util.Properties;

/**
 *
 * @author kevin
 */
public class DefaultRequestHandler extends BaseHandler implements RequestHandler {
    
    public String process(HttpExchange ex){
        String mode = ex.getRequestMethod();
        String result = HTML.getPage(HTML.Page.index);
        
        if( mode.equalsIgnoreCase("POST") ){
            DataBean args = Helper.readAnyParameters(ex);
            
            String contentType = args.getString("contenttype");
            
            if( contentType.equalsIgnoreCase("PROPERTIES") ){
                Properties props = SolrAuthManager.getProperties();
                props.setProperty(SolrAuthPropertyKey.solrInstallPath.getKey(),args.getString("solrinstallpath","/opt/solr"));
                props.setProperty(SolrAuthPropertyKey.solrHostPort.getKey(), args.getString("solrhostport","localhost:8983"));
                props.setProperty(SolrAuthPropertyKey.zookeeperPort.getKey(), args.getString("solrzookeeperport","localhost:9983"));
                props.setProperty(SolrAuthPropertyKey.defaultCollection.getKey(), args.getString("solrdefaultcollection","basic"));
                props.setProperty(SolrAuthPropertyKey.solrAdminUser.getKey(),args.getString("solradminuser","solr"));
                props.setProperty(SolrAuthPropertyKey.solrAdminPwd.getKey(), args.getString("solradminpwd","SolrRocks"));
                props.setProperty(SolrAuthPropertyKey.isUsingSSL.getKey(), args.getString("sslenabled","false"));
                String dResult = SolrAuthActionController.doSavePropertiesAction(props);
                
                DataBean resultBean = new DataBean();
                resultBean.setValue("message",dResult);
                if( dResult.equalsIgnoreCase("SUCCESS") ){
                    resultBean.setValue("status","1");
                    props = SolrAuthManager.getProperties();
                    resultBean.addToCollection("items",Helper.getDataBeanFromProperties(props));
                }
                else {
                    resultBean.setValue("status","0");
                }
                
                result = Helper.toJSON(resultBean);
            }
            else {
                result = Helper.toJSON(args);
            }
            
        }
        else if( mode.equalsIgnoreCase("PUT") ){
            DataBean args = Helper.readAnyParameters(ex);
            result = Helper.toJSON(args);
        }
        else if( mode.equalsIgnoreCase("DELETE") ){
            DataBean args = Helper.readAllParameters(ex);
            result = Helper.toJSON(args);
        }
        else {
            DataBean args = Helper.readAllParameters(ex);
            
            String contentType = args.getString("contenttype");
            
            if( contentType.equalsIgnoreCase("PROPERTIES") ){
                Properties props = SolrAuthManager.getProperties();
                boolean exists = props.entrySet().size() > 0;
                
                String message = exists ? "File Exists" : "No File";
                
                DataBean resultBean = new DataBean();
                resultBean.setValue("message",message);
                if( exists ){
                    resultBean.setValue("status","1");
                    resultBean.addToCollection("items",Helper.getDataBeanFromProperties(props));
                }
                else {
                    resultBean.setValue("status","0");
                }
                
                result = Helper.toJSON(resultBean);
            }
            else {
                result = Helper.toJSON(args);
            }
        }
        
        return result;
    }
}
