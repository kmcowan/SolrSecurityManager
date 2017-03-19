/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.forms;

import com.cprassoc.solr.auth.Frameable;
import com.cprassoc.solr.auth.SolrAuthActionController;
import com.cprassoc.solr.auth.forms.resources.Resources;
import com.cprassoc.solr.auth.ui.SolrAuthMainWindow;
import com.cprassoc.solr.auth.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;

/**
 *
 * @author kevin
 */
public class AddRoleForm extends BaseDialog {

    private LinkedHashMap<String, String> users = null;
    private LinkedHashMap<String, Object> roles = null;
    private LinkedHashMap<String, String> newRoles = null;
    private LinkedHashMap<String, LinkedHashMap<String, String>> roleMap = null;
    private Frameable frame = null;

    public static String ROLE_USER_KEY = "args-role-user-key-from-solr-auth-manager-application";

    /**
     * Creates new form AddRoleForm
     */
    public AddRoleForm(Frameable parent, boolean modal, LinkedHashMap<String, String> users, LinkedHashMap<String, Object> roles) {
        super(parent.getFrame(), modal);
        this.users = users;
        this.roles = roles;
        this.frame = parent;
        newRoles = new LinkedHashMap<>();
        roleMap = new LinkedHashMap<>();

        initComponents();
        super.center();
    }

    private ComboBoxModel getUsersComboBoxModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        Iterator<String> iter = users.keySet().iterator();
        model.addElement("");
        String key;
        while (iter.hasNext()) {
            key = iter.next();
            model.addElement(key);
        }
        return model;
    }

    private ComboBoxModel getRolesComboBoxModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        Iterator<String> iter = roles.keySet().iterator();
        HashMap<String, String> temp = new HashMap<>();
        model.addElement("");
        String key;
        Object value;
        while (iter.hasNext()) {
            key = iter.next();
            value = roles.get(key);
            Log.log(getClass(), "Loading Role: " + value);
            if (value instanceof String[]) {
                String[] r = (String[]) value;
                for (int i = 0; i < r.length; i++) {
                    if (temp.get(r[i]) == null) {
                        model.addElement(r[i]);
                        temp.put(r[i], r[i]);
                    }
                }
            } else if (value instanceof ArrayList) {
                ArrayList rs = (ArrayList) value;
                for (int i = 0; i < rs.size(); i++) {
                    if (temp.get(rs.get(i)) == null) {
                        model.addElement(rs.get(i));
                        temp.put((String) rs.get(i), (String) rs.get(i));
                    }
                }
            } else if (value instanceof String) {
                String v = (String) value;
                if (v.startsWith("[")) {
                    Log.log(getClass(), "parse roles from array: " + v);
                    String[] r = getRolesFromStringArray(v);
                    if (r.length > 0) {
                        for (int i = 0; i < r.length; i++) {
                            if (temp.get(r[i].trim()) == null) {
                                model.addElement(r[i].trim());
                                temp.put(r[i].trim(), r[i]);
                            }
                        }
                    }

                } else if (temp.get(v) == null) {
                    model.addElement(v);
                    temp.put(v, v);
                }
            } else {
                model.addElement(value.toString());
            }
        }
        return model;
    }

    private String[] getRolesFromStringArray(String r) {
        r = r.replaceAll("\\[", "");
        r = r.replaceAll("\\]", "");
        return r.split(",");
    }

    private ListModel getRolesListModel() {
        DefaultListModel model = new DefaultListModel();
        Iterator<String> iter = newRoles.keySet().iterator();
        String key;
        while (iter.hasNext()) {
            key = iter.next();
            if (key.startsWith("[")) {
                String[] r = getRolesFromStringArray(key);
                if (r.length > 0) {
                    for (int i = 0; i < r.length; i++) {
                        model.addElement(r[i].trim());
                    }
                }
            } else {
                model.addElement(key);
            }
        }
        return model;
    }

    private void rebuildRolesList() {
        ListModel model = getRolesListModel();
        this.rolesList.setModel(model);
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
        jLabel2 = new javax.swing.JLabel();
        usersComboBox = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        rolesList = new javax.swing.JList<>();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        rolesComboBox = new javax.swing.JComboBox<>();
        jButton3 = new javax.swing.JButton();
        addRoleField = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 51, 102));

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Add Role");

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("User");

        usersComboBox.setModel(getUsersComboBoxModel());
        usersComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doUserSelectedAction(evt);
            }
        });

        rolesList.setModel(getRolesListModel());
        jScrollPane1.setViewportView(rolesList);

        jButton1.setText("Save");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSaveAction(evt);
            }
        });

        jButton2.setText("Done");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doCancelAction(evt);
            }
        });

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Roles");

        rolesComboBox.setModel(getRolesComboBoxModel());
        rolesComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                doRolesComboBoxFocusGainedAction(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(0, 51, 153));
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Add");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doAddRoleToListAction(evt);
            }
        });

        addRoleField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                doAddRoleTextFocusGainedAction(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(0, 51, 153));
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Remove");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doRemoveRole(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rolesComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(usersComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(addRoleField, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(101, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton4)))))
                .addGap(28, 28, 28))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(usersComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(rolesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addRoleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton3)
                            .addComponent(jButton4))
                        .addGap(63, 63, 63)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton2)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(29, Short.MAX_VALUE))
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

    private void doCancelAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doCancelAction
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_doCancelAction

    private void doSaveAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doSaveAction
        LinkedHashMap<String, String> args = new LinkedHashMap<>();
        String user = (String) this.usersComboBox.getSelectedItem();
        int roleCount = this.rolesList.getModel().getSize();
        String role;
        for (int i = 0; i < roleCount; i++) {
            role = (String) this.rolesList.getModel().getElementAt(i);
            args.put(role, role);
        }
        args.put(ROLE_USER_KEY, user);
        frame.fireAction(SolrAuthActionController.SolrManagerAction.add_role, args);
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_doSaveAction

    private void doAddRoleTextFocusGainedAction(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_doAddRoleTextFocusGainedAction
        this.rolesComboBox.setSelectedIndex(0);
    }//GEN-LAST:event_doAddRoleTextFocusGainedAction

    private void doAddRoleToListAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doAddRoleToListAction
        if (this.usersComboBox.getSelectedIndex() > 0) {
            if (this.rolesComboBox.getSelectedIndex() > 0) {
                Log.log(getClass(), "Add role from combo box...");
                newRoles.put((String) this.rolesComboBox.getSelectedItem(), (String) this.usersComboBox.getSelectedItem());
                rebuildRolesList();
            } else if (!this.addRoleField.getText().trim().equals("")) {
                Log.log(getClass(), "Add role from custom field...");
                newRoles.put(this.addRoleField.getText().trim(), (String) this.usersComboBox.getSelectedItem());
                rebuildRolesList();
            } else {
                Log.log(getClass(), "NO valid role found...");
                frame.showOKOnlyMessageDialog("No Role Selected, nor new role Created. \n Please select a role from the list or enter a new role in the text field.  ", Resources.Resource.warn);
            }
        } else {
            Log.log(getClass(), "NO valid user selected...");
            frame.showOKOnlyMessageDialog("No User Selected.  Please select a user from the list.   ", Resources.Resource.warn);

        }
    }//GEN-LAST:event_doAddRoleToListAction

    private void doRolesComboBoxFocusGainedAction(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_doRolesComboBoxFocusGainedAction
        this.addRoleField.setText("");
    }//GEN-LAST:event_doRolesComboBoxFocusGainedAction

    private void doUserSelectedAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doUserSelectedAction
        Log.log(getClass(), "User Selected: " + this.usersComboBox.getSelectedItem());
        loadRolesForUser();
    }//GEN-LAST:event_doUserSelectedAction

    private void doRemoveRole(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doRemoveRole
        if (this.rolesList.getSelectedValue() != null) {
            this.rolesList.remove(rolesList.getSelectedIndex());
        } else {
            this.frame.showOKOnlyMessageDialog("No role selected. ", Resources.Resource.warn);
        }
    }//GEN-LAST:event_doRemoveRole

    private void loadRolesForUser() {
        Log.log(getClass(), "Loading roles for user...");
        newRoles = new LinkedHashMap<>();
        //  Iterator<String> iter = roles.keySet().iterator();
        String user = (String) this.usersComboBox.getSelectedItem();

        String key;
        Object r = roles.get(user);
        if (r != null) {
            if (r instanceof String[]) {
                String[] rs = (String[]) r;
                for (int i = 0; i < rs.length; i++) {
                    newRoles.put(rs[i], user);
                }
            } else if (r instanceof ArrayList) {
                ArrayList rs = (ArrayList) r;
                for (int i = 0; i < rs.size(); i++) {
                    newRoles.put((String) rs.get(i), user);
                }
            } else {
                newRoles.put((String) r, user);
            }

        } else {
            Iterator<String> iter = roles.keySet().iterator();
            Object value;
            while (iter.hasNext()) {
                key = iter.next();
                value = roles.get(key);
                Log.log(getClass(), "Role key: " + key + " value: " + value);
            }
        }
        rebuildRolesList();

        /*   while (iter.hasNext()) {
            key = iter.next();
            
        }*/
    }

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
            java.util.logging.Logger.getLogger(AddRoleForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AddRoleForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AddRoleForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AddRoleForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AddRoleForm dialog = new AddRoleForm(new SolrAuthMainWindow(), true, null, null);
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
    private javax.swing.JTextField addRoleField;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> rolesComboBox;
    private javax.swing.JList<String> rolesList;
    private javax.swing.JComboBox<String> usersComboBox;
    // End of variables declaration//GEN-END:variables
}
