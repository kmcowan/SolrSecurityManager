/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.ui.tasks;

import com.cprassoc.solr.auth.SolrAuthActionController;
import com.cprassoc.solr.auth.util.Log;
import java.awt.Color;
import java.util.TimerTask;
import javax.swing.JToggleButton;

/**
 *
 * @author kevin
 */
public class SolrPingTimerTask extends TimerTask implements Runnable{
    JToggleButton serverStatusButton = null;
    
    public SolrPingTimerTask(JToggleButton p){
        this.serverStatusButton = p;
    }
    public void run(){
          Log.log(getClass(), "Ping SOLR...");
          try {
            boolean online = SolrAuthActionController.SOLR.isOnline();
            if (online) {
                this.serverStatusButton.setText("ONLINE");
                this.serverStatusButton.setBackground(Color.green);
                this.serverStatusButton.setForeground(Color.black);
            } else {
                 this.serverStatusButton.setText("OFFLINE");
                this.serverStatusButton.setBackground(Color.red);
            }
            
          }catch(Exception e){
              
          }
    }
}
