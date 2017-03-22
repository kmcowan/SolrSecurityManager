/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth;

import com.cprassoc.solr.auth.SolrAuthActionController.SolrManagerAction;
import com.cprassoc.solr.auth.forms.resources.Resources;
import java.util.LinkedHashMap;

/**
 *
 * @author kevin
 */
public interface Frameable {
    
    public java.awt.Frame getFrame();
    public void fireAction(SolrManagerAction action, LinkedHashMap<String,String> args, Object optional);
    public void showOKOnlyMessageDialog(String message, Resources.Resource resc);
}
