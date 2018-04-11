/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.web.handlers;

import com.cprassoc.solr.auth.SolrAuthActionController;
import com.cprassoc.solr.auth.model.SecurityJson;
import com.cprassoc.solr.auth.ui.SolrAuthMainWindow;

import com.cprassoc.solr.auth.web.handlers.model.Handler;

import com.sun.net.httpserver.HttpExchange;
import java.util.Map;
import org.json.JSONObject;

/**
 *
 * @author kevin
 */
public class ManageUsersHandler extends BaseHandler implements Handler {

    private SecurityJson securityJson = null;
   // private static final SolrAuthActionController controller = new SolrAuthActionController();

    public byte[] handle(JSONObject action, HttpExchange ex) {
        JSONObject obj = new JSONObject();
        securityJson = null;
        SolrAuthActionController.SOLR.getAuthentication();
        Map params = queryToMap(ex.getRequestURI().getQuery());

        String currAction = (String) action.get("action");
        if (isValidHandlerAction(currAction, Action.values())) {
           Action cmd = (Action)getHandlerActionEnum(currAction, Action.values());
            
            switch (cmd) {
                case  listusers:
                Map users =  SolrAuthActionController.getSecurityJson().getAuthentication().getCredentials();
                obj.put("users", users);
                break;
                
                 case adduser:

                break;
                
                 case edituser:

                break;
                
                 case deleteuser:

                break;
            }
        } else {
            obj.put("error", "No Action specified. ");
        }

        return obj.toString().getBytes();
    }
    
    private static enum Action {
        listusers,
        edituser,
        adduser,
        deleteuser
    }

}
