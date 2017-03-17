/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.ui;

import com.cprassoc.solr.auth.Frameable;
import com.cprassoc.solr.auth.SolrAuthActionController;
import com.cprassoc.solr.auth.SolrAuthManager;
import com.cprassoc.solr.auth.forms.AddUserDialog;
import com.cprassoc.solr.auth.forms.AddUserDialog.SolrManagerAction;
import com.cprassoc.solr.auth.forms.OKFormWithMessage;
import com.cprassoc.solr.auth.forms.OkCancelDialog;
import com.cprassoc.solr.auth.forms.resources.Resources;
import com.cprassoc.solr.auth.model.Authentication;
import com.cprassoc.solr.auth.model.Authorization;
import com.cprassoc.solr.auth.model.SecurityJson;
import com.cprassoc.solr.auth.util.JsonHelper;
import com.cprassoc.solr.auth.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.cxf.helpers.IOUtils;
import org.json.JSONObject;

/**
 *
 * @author kevin
 */
public class SolrAuthMainWindow extends javax.swing.JFrame implements Frameable {

    private SecurityJson securityJson = null;
    private static HashMap<String, Integer> permissionMap = null;
    private static String SECURITY_JSON_FILE_NAME = "security.json";
    private String selectedUser = "";

    /**
     * Creates new form SolrAuthMainWindow
     */
    public SolrAuthMainWindow() {
        initComponents();
        init();
    }

    private void init() {
        Properties props = SolrAuthManager.getProperties();
        System.out.println("INIT...");
        try {
            boolean online = SolrAuthActionController.SOLR.isOnline();
            if (online) {
                String authentication = SolrAuthActionController.SOLR.getAuthentication();
                String authorization = SolrAuthActionController.SOLR.getAuthorization();

                System.out.println("Authentication: " + authentication);
                System.out.println("Authorization: " + authorization);

                //  JSON.decode(authorization, type);
                JSONObject authoeJson = new JSONObject(authentication);
                if (authoeJson.getJSONObject("authentication") != null) {
                    String clsName = authoeJson.getJSONObject("authentication").getString("class");
                    JSONObject authoJson = new JSONObject(authorization);
                    LinkedHashMap authoMap = new LinkedHashMap(JsonHelper.jsonToMap(authoJson));
                    LinkedHashMap authoeMap = new LinkedHashMap(JsonHelper.jsonToMap(authoeJson));
                    Map map = new LinkedHashMap<String, Object>();
                    map.put("authentication", authoeMap);
                    map.put("authorization", authoMap);

                    System.out.println("Auth Class Name: " + clsName);

                    securityJson = new SecurityJson(map);

                } else {
                    authoeJson = getDefaultSecurityJson();
                    LinkedHashMap authoeMap = new LinkedHashMap(JsonHelper.jsonToMap(authoeJson));
                    securityJson = new SecurityJson(authoeMap);
                }

            } else {
                JSONObject authoeJson = getDefaultSecurityJson();
                LinkedHashMap authoeMap = new LinkedHashMap(JsonHelper.jsonToMap(authoeJson));
                securityJson = new SecurityJson(authoeMap);
                logPane.setText("Solr is offline. ");
            }

            populateAuthorizationTable(securityJson.getAuthorization());
            populateUserRolesTable(securityJson.getAuthorization());
            populateAuthenticationTable(securityJson.getAuthentication());

            this.usersTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    String selectedData = null;

                    int[] selectedRow = usersTable.getSelectedRows();
                    int[] selectedColumns = usersTable.getSelectedColumns();

                    for (int i = 0; i < selectedRow.length; i++) {
                        for (int j = 0; j < selectedColumns.length; j++) {
                            selectedData = (String) usersTable.getValueAt(selectedRow[i], selectedColumns[j]);
                        }
                    }
                    if (e.getValueIsAdjusting()) {
                        System.out.println("Selected: " + selectedData);
                        selectedUser = selectedData;
                    }
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private JSONObject getDefaultSecurityJson() throws Exception {
        JSONObject obj = null;
        // check for exported properties. 
        File secu = new File(SECURITY_JSON_FILE_NAME);
        String json = "";
        if (secu.exists()) {
            json = new String(IOUtils.readBytesFromStream(new FileInputStream(secu)));
            obj = new JSONObject(json);
        } else {
            json = new String(IOUtils.readBytesFromStream(SecurityJson.class.getResourceAsStream(SECURITY_JSON_FILE_NAME)));
            obj = new JSONObject(json);
        }
        return obj;
    }

    private void populateAuthorizationTable(Authorization auth) {
        ArrayList<LinkedHashMap> list = auth.getPermissions();
        Iterator<String> iter;
        String key, value;
        Integer col;
        for (int i = 0; i < list.size(); i++) {
            iter = list.get(i).keySet().iterator();
            while (iter.hasNext()) {
                key = iter.next();
                value = (String) list.get(i).get(key);
                col = getPermissionItemColumn(key);
                this.permissionsTable.getModel().setValueAt(value, i, col);
            }
        }
    }

    private void populateUserRolesTable(Authorization auth) {
        LinkedHashMap<String, String> map = auth.getUserRoles();
        Iterator<String> iter;
        String key, value;
        Integer col;

        iter = map.keySet().iterator();
        int row = 0;
        while (iter.hasNext()) {
            key = iter.next();
            value = (String) map.get(key);
            this.rolesTable.getModel().setValueAt(key, row, 0);
            this.rolesTable.getModel().setValueAt(value, row, 1);
            row++;
        }

    }

    private void populateAuthenticationTable(Authentication auth) {
        LinkedHashMap map = auth.getCredentials();
        int row = 0;
        if (map != null && map.size() > 0) {
            Iterator<String> iter = map.keySet().iterator();
            String key, value;

            while (iter.hasNext()) {
                key = iter.next();
                value = (String) map.get(key);
                this.usersTable.getModel().setValueAt(key, row, 0);
                this.usersTable.getModel().setValueAt(value, row, 1);
                row++;
            }
        }
    }

    private static HashMap getPermissionMap() {
        if (permissionMap == null) {
            permissionMap = new HashMap<>();
            permissionMap.put("name", 0);
            permissionMap.put("role", 1);
            permissionMap.put("index", 2);
            permissionMap.put("path", 3);
            permissionMap.put("params", 4);
            permissionMap.put("collection", 5);
            permissionMap.put("method", 6);
            permissionMap.put("before", 7);
        }

        return permissionMap;
    }

    private Integer getPermissionItemColumn(String name) {

        return (Integer) getPermissionMap().get(name);
    }

    @Override
    public java.awt.Frame getFrame() {
        return this;
    }

    public void fireAction(SolrManagerAction action, LinkedHashMap<String, String> args) {
        switch (action) {
            case create_user:
                this.securityJson.getAuthentication().getCredentials().put(args.get("user"), args.get("pwd"));
                populateAuthenticationTable(this.securityJson.getAuthentication());
                break;
                
            case delete_user:
                Log.log(getClass(), "Delete User: "+selectedUser);
                break;
        }
    }
    
    private void showOKOnlyMessageDialog(String message, Resources.Resource resc) {
       
        OKFormWithMessage dialog = new OKFormWithMessage(this, true, message, resc);
        dialog.setVisible(true);
        dialog.requestFocus();
    }
    
       private void showOKCancelMessageDialog(String message, SolrManagerAction resc) {
       
        OkCancelDialog dialog = new OkCancelDialog(this, true, message, resc);
        dialog.setVisible(true);
        dialog.requestFocus();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        usersTable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        jScrollPane2 = new javax.swing.JScrollPane();
        permissionsTable = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jToolBar2 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        logPane = new javax.swing.JEditorPane();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        rolesTable = new javax.swing.JTable();
        jToolBar3 = new javax.swing.JToolBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(0, 51, 102));

        jLabel1.setBackground(new java.awt.Color(0, 51, 102));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Solr Auth Security Manager");

        usersTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "User Name", "Credentials"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        usersTable.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(usersTable);
        usersTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Authenticated Users:");

        jToolBar1.setRollover(true);

        permissionsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Name", "Role", "Index", "Path", "Params", "Collection", "Method", "Before"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        permissionsTable.setColumnSelectionAllowed(true);
        jScrollPane2.setViewportView(permissionsTable);
        permissionsTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Authorized Permissions");

        jToolBar2.setRollover(true);

        jButton1.setBackground(new java.awt.Color(0, 51, 102));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Add");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doAddUserAction(evt);
            }
        });
        jToolBar2.add(jButton1);

        jButton2.setBackground(new java.awt.Color(0, 51, 102));
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Delete");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doDeleteUserConfirmAction(evt);
            }
        });
        jToolBar2.add(jButton2);

        logPane.setBackground(new java.awt.Color(255, 255, 255));
        logPane.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        logPane.setForeground(new java.awt.Color(0, 0, 0));
        jScrollPane3.setViewportView(logPane);

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Server Response:");

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("User and Roles");

        rolesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "User", "Role"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane4.setViewportView(rolesTable);

        jToolBar3.setRollover(true);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jScrollPane1)
                                    .addComponent(jScrollPane2)
                                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jToolBar2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(20, 20, 20))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 611, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4))
                        .addGap(0, 80, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(29, 29, 29)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        jMenu1.setText("File");

        jMenuItem1.setText("Quit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doQuitAction(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Server");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("Tools");

        jMenuItem2.setText("Manage Properties");
        jMenu3.add(jMenuItem2);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void doAddUserAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doAddUserAction
        AddUserDialog dialog = new AddUserDialog(this, true);
        dialog.setVisible(true);
        dialog.requestFocus();
    }//GEN-LAST:event_doAddUserAction

    private void doQuitAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doQuitAction
      System.exit(0);
    }//GEN-LAST:event_doQuitAction

    private void doDeleteUserConfirmAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doDeleteUserConfirmAction
        if(selectedUser.equals("")){
            showOKOnlyMessageDialog("No user selected", Resources.Resource.warn);
        } else {
            showOKCancelMessageDialog("Are you sure you want to delete "+selectedUser+"? \n This action cannot be undone. ", SolrManagerAction.delete_user);
        }
    }//GEN-LAST:event_doDeleteUserConfirmAction

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
            java.util.logging.Logger.getLogger(SolrAuthMainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SolrAuthMainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SolrAuthMainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SolrAuthMainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SolrAuthMainWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JEditorPane logPane;
    private javax.swing.JTable permissionsTable;
    private javax.swing.JTable rolesTable;
    private javax.swing.JTable usersTable;
    // End of variables declaration//GEN-END:variables
}
