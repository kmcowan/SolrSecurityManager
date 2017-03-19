/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.forms;

import com.cprassoc.solr.auth.Frameable;
import com.cprassoc.solr.auth.SolrAuthActionController;
import com.cprassoc.solr.auth.forms.resources.Resources;
import com.cprassoc.solr.auth.model.SecurityJson;
import com.cprassoc.solr.auth.security.PermissionNameProvider;
import com.cprassoc.solr.auth.security.PermissionNameProvider.Name;
import com.cprassoc.solr.auth.ui.SolrAuthMainWindow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 *
 * @author kevin
 */
public class ManagePermissionFrame extends BaseDialog {

    private SecurityJson securityJson = null;
    private LinkedHashMap<String, String> permission = null;
    private final static String[] REGISTERED_PATHS = "/admin/mbeans,/browse,/update/json/docs,/admin/luke,/export,/get,/admin/properties,/elevate,/update/json,/admin/threads,/query,/analysis/field,/analysis/document,/spell,/update/csv,/sql,/graph,/tvrh,/select,/admin/segments,/admin/system,/replication,/config,/stream,/schema,/admin/plugins,/admin/logging,/admin/ping,/update,/admin/file,/terms,/debug/dump,/update/extract".split(",");
    private Frameable frame = null;

    /**
     * Creates new form ManagePermissionForm
     */
    public ManagePermissionFrame(Frameable frame, boolean modal, SecurityJson sc, LinkedHashMap<String, String> permission) {
        super(frame.getFrame(), modal);
        this.securityJson = sc;
        this.frame = frame;
        if (permission != null) {
            this.permission = permission;
        }
        //  super.center();
        initComponents();
        init();
    }

    private void init() {

    }

    private ComboBoxModel getNameComboBoxModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        if (securityJson != null) {
            Map<String, Name> perms = PermissionNameProvider.values;
            String key;
            Name name;
            Iterator<String> iter = perms.keySet().iterator();
            while (iter.hasNext()) {
                key = iter.next();
                name = perms.get(key);
                model.addElement(name.getPermissionName());
            }

        }
        return model;
    }

    private ComboBoxModel getPathComboBoxModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (int i = 0; i < REGISTERED_PATHS.length; i++) {
            model.addElement(REGISTERED_PATHS[i]);
        }
        return model;

    }

    private ComboBoxModel getCollectionComboBoxModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        JSONObject json = SolrAuthActionController.getCollections();
        JSONArray collections = json.getJSONArray("collections");

        for (int i = 0; i < collections.length(); i++) {
            model.addElement(collections.get(i));
        }
        return model;

    }

    private ComboBoxModel getBeforeComboBoxModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        if (this.securityJson != null) {
            ArrayList<LinkedHashMap> perms = securityJson.getAuthorization().getPermissions();
            LinkedHashMap perm;
            for (int i = 0; i < perms.size(); i++) {
                perm = perms.get(i);
                model.addElement(perm.get("name"));
            }
        }
        return model;

    }

    private ComboBoxModel getRoleComboBoxModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        HashMap<String, String> permmap = new HashMap<String, String>();
        model.addElement("*");
        if (securityJson != null) {
            LinkedHashMap<String, Object> map = securityJson.getAuthorization().getUserRoles();
            Iterator<String> iter = map.keySet().iterator();
            String key;
            Object value;
            while (iter.hasNext()) {
                key = iter.next();
                value = map.get(key);
                if (value instanceof ArrayList) {
                    ArrayList<String> list = (ArrayList) value;
                    for (int i = 0; i < list.size(); i++) {
                        if (permmap.get(list.get(i)) == null) {
                            model.addElement(list.get(i));
                            permmap.put(list.get(i), key);
                        }
                    }
                } else if (value instanceof String) {
                    if (permmap.get(value) == null) {
                        permmap.put((String) value, key);
                        model.addElement(value);
                    }
                } else if (permmap.get(value.toString()) == null) {
                    model.addElement(value.toString());
                }
            }
        }
        return model;

    }

    private ArrayList<String> validatePermission() {
        ArrayList<String> results = new ArrayList<>();

        return results;
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
        permissionDisplayLabel = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        roleNameField = new javax.swing.JTextField();
        nameComboBox = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        pathComboBox = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        paramsField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        collectionComboBox = new javax.swing.JComboBox<>();
        methodComboBox = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        beforeComboBox = new javax.swing.JComboBox<>();
        roleComboBox = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 51, 102));

        jLabel1.setBackground(new java.awt.Color(0, 51, 102));
        jLabel1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Manage Permission: ");

        permissionDisplayLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        permissionDisplayLabel.setForeground(new java.awt.Color(255, 255, 255));
        permissionDisplayLabel.setText("New Permission");

        jButton1.setBackground(new java.awt.Color(0, 51, 153));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Save");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSaveAction(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(0, 51, 153));
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doCancelAction(evt);
            }
        });

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Name: ");

        nameComboBox.setModel(getNameComboBoxModel());
        nameComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doLoadRoleNameAction(evt);
            }
        });

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Role:");

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Path:");

        pathComboBox.setModel(getPathComboBoxModel());

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Params");

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Collection:");

        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Method:");

        collectionComboBox.setModel(getCollectionComboBoxModel());

        methodComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "GET", "PUT", "POST", "DELETE" }));

        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Before");

        beforeComboBox.setModel(getBeforeComboBoxModel());

        roleComboBox.setModel(getRoleComboBoxModel());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(permissionDisplayLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(beforeComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(pathComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(paramsField, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(collectionComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(methodComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(25, 25, 25))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(roleNameField)
                                            .addComponent(nameComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(roleComboBox, 0, 250, Short.MAX_VALUE))
                                        .addGap(0, 0, Short.MAX_VALUE)))))
                        .addGap(0, 128, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(permissionDisplayLabel))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(nameComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(roleNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(roleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(pathComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(paramsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(collectionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(methodComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(beforeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton1))
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

    private void doSaveAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doSaveAction
        ArrayList<String> valid = validatePermission();
        if (valid.size() == 0) {

        } else {
            String message = "Permission Validation Failed for the Following Reasons: \n " + valid.toString();
            frame.showOKOnlyMessageDialog(message, Resources.Resource.warn);
        }
    }//GEN-LAST:event_doSaveAction

    private void doCancelAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doCancelAction
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_doCancelAction

    private void doLoadRoleNameAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doLoadRoleNameAction
        String permName = (String) this.nameComboBox.getSelectedItem();

        if (!securityJson.hasPermission(permName)) {
            this.roleNameField.setText(permName);
        } else {
            int response;

            response = JOptionPane.showConfirmDialog(null, "Selecting '" + permName + "' will override the existing Permission with the same name.  Are you sure you want to do this?");
            if (response == JOptionPane.YES_OPTION) {
                roleNameField.setText(permName);
            }
        }
    }//GEN-LAST:event_doLoadRoleNameAction

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
            java.util.logging.Logger.getLogger(ManagePermissionFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ManagePermissionFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ManagePermissionFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ManagePermissionFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ManagePermissionFrame(new SolrAuthMainWindow(), true, null, null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> beforeComboBox;
    private javax.swing.JComboBox<String> collectionComboBox;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JComboBox<String> methodComboBox;
    private javax.swing.JComboBox<String> nameComboBox;
    private javax.swing.JTextField paramsField;
    private javax.swing.JComboBox<String> pathComboBox;
    private javax.swing.JLabel permissionDisplayLabel;
    private javax.swing.JComboBox<String> roleComboBox;
    private javax.swing.JTextField roleNameField;
    // End of variables declaration//GEN-END:variables
}
