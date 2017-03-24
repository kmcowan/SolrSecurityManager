/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.forms;

import com.cprassoc.solr.auth.Frameable;
import com.cprassoc.solr.auth.SolrAuthActionController;
import com.cprassoc.solr.auth.ui.SolrAuthMainWindow;
import com.cprassoc.solr.auth.util.Log;
import java.util.Iterator;
import java.util.LinkedHashMap;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.json.JSONObject;

/**
 *
 * @author kevin
 */
public class ServerStatusDialog extends BaseDialog {
     private Frameable frame = null;
    /**
     * Creates new form ServerStatusDialog
     */
    public ServerStatusDialog(Frameable parent, boolean modal) {
        super(parent.getFrame(), modal);
        frame = parent;
        initComponents();
    }
    
    
     private DefaultMutableTreeNode getRootNode() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Server Status Information");
        JSONObject json = SolrAuthActionController.getServerStatus();
        
        if (json != null) {
            Iterator<String> iter = json.getJSONObject("status").keys();
 
            String key;
            Object value;
            while (iter.hasNext()) {
                key = iter.next();
                value = json.getJSONObject("status").get(key);
                  Log.log(getClass(), "Key: "+key+" value class: "+value.getClass());
                DefaultMutableTreeNode cnode = new DefaultMutableTreeNode(key);
                if(value instanceof String || value instanceof Integer){
                   cnode.setUserObject(key+" : "+value.toString());
 
                } else if(value instanceof LinkedHashMap){
                      loadNodesRecursive(cnode, (LinkedHashMap)value);
 
                } else if(value instanceof JSONObject){
                    loadNodesFromJson(cnode, (JSONObject)value);
                }  
                
                 node.add(cnode);
            
            }
            
        }
        
      
        return node;
    }
     
    private void loadNodesRecursive(DefaultMutableTreeNode node, LinkedHashMap<String,Object> map){
       if(map != null){
           Iterator<String> iter = map.keySet().iterator();
           String key;
           Object value;
           while(iter.hasNext()){
               key = iter.next();
             
              value = map.get(key);
                Log.log(getClass(), "Key: "+key+" value class: "+value.getClass());
               if(value instanceof String || value instanceof Integer){
                   node.setUserObject(key+" : "+value.toString());
                   node.add(node);
                } else if(value instanceof LinkedHashMap){
                     loadNodesRecursive(node, (LinkedHashMap)value);
                      node.add(node);
                } else {
                    node.add(node);
                }
           }
       }
    }
    
    private void loadNodesFromJson(DefaultMutableTreeNode node, JSONObject json){
         Iterator<String> iter = json.keys();
 
            String key;
            Object value;
            while (iter.hasNext()) {
                key = iter.next();
                value = json.get(key);
                  Log.log(getClass(), "Key: "+key+" value class: "+value.getClass());
                DefaultMutableTreeNode cnode = new DefaultMutableTreeNode(key);
                if(value instanceof String || value instanceof Integer){
                   cnode.setUserObject(key+" : "+value.toString());
 
                } else if(value instanceof LinkedHashMap){
                      loadNodesRecursive(cnode, (LinkedHashMap)value);
 
                } else if(value instanceof JSONObject){
                    loadNodesFromJson(cnode, (JSONObject)value);
                }  
                
                 node.add(cnode);
            
            }
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new JTree(getRootNode());
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 51, 102));

        jLabel1.setFont(new java.awt.Font("Dialog", 2, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Server Status");

        jScrollPane1.setViewportView(jTree1);

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doOKAction(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(96, 96, 96)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(96, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(26, 26, 26))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void doOKAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doOKAction
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_doOKAction

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ServerStatusDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ServerStatusDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ServerStatusDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ServerStatusDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ServerStatusDialog dialog = new ServerStatusDialog(new SolrAuthMainWindow(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables
}