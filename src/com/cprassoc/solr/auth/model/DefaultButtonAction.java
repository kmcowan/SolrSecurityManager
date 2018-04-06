/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.model;

import com.cprassoc.solr.auth.Frameable;
import com.cprassoc.solr.auth.SolrAuthActionController;
import com.cprassoc.solr.auth.SolrAuthActionController.SolrManagerAction;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import static javax.swing.Action.MNEMONIC_KEY;
import static javax.swing.Action.SHORT_DESCRIPTION;
import javax.swing.ImageIcon;

/**
 *
 * @author kevin
 */
public class DefaultButtonAction  extends AbstractAction {
    private Frameable frame = null;
    private SolrAuthActionController.SolrManagerAction action=null;
    public DefaultButtonAction(String text, ImageIcon icon,
                      String desc, Integer mnemonic, Frameable frame) {
        super(text, icon);
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        this.frame = frame;
    }
    
  public DefaultButtonAction(){
      
  }
  
    
    
    public DefaultButtonAction( Frameable frame, SolrManagerAction action) {
      //  super("", Resources.INFO_ICON);
     //   putValue(SHORT_DESCRIPTION, "Default Button Action");
   
        this.frame = frame;
        this.action = action;
    }
    
    public void actionPerformed(ActionEvent e) {
      //  displayResult("Action for first button/menu item", e);
      System.out.println(e.toString());
      if(action ==  null){
          action = SolrManagerAction.dialog_fired_action;
      }
      if(frame != null){
          frame.fireAction(action, null, this);
      }
      
    }
}
