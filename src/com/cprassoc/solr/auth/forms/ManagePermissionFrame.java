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
import com.cprassoc.solr.auth.util.Log;
import com.cprassoc.solr.auth.util.Utils;
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
    private LinkedHashMap<String, Object> permission = null;
    private final static String[] REGISTERED_PATHS = "/admin/mbeans,/browse,/update/json/docs,/admin/luke,/export,/get,/admin/properties,/elevate,/update/json,/admin/threads,/query,/analysis/field,/analysis/document,/spell,/update/csv,/sql,/graph,/tvrh,/select,/admin/segments,/admin/system,/replication,/config,/stream,/schema,/admin/plugins,/admin/logging,/admin/ping,/update,/admin/file,/terms,/debug/dump,/update/extract".split(",");
    private Frameable frame = null;
    private boolean isEditing = false;
    private LinkedHashMap<String,String> params = new LinkedHashMap<>();
    private static String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyz-_";

    /**
     * Creates new form ManagePermissionForm
     */
    public ManagePermissionFrame(Frameable frame, boolean modal, SecurityJson sc, LinkedHashMap<String, Object> permission) {
        super(frame.getFrame(), modal);
        this.securityJson = sc;
        this.frame = frame;

        //  super.center();
        initComponents();
        if (permission != null) {
            this.permission = permission;
            isEditing = true;
            this.permissionDisplayLabel.setText((String) permission.get("name"));
        } else {
            this.permission = sc.getAuthorization().getEmptyPermissionMap();
        }
        init();
    }

    private void init() {
        if (permission != null && isEditing) {
            if (permission.get("path") != null) {
                this.pathComboBox.setSelectedItem(permission.get("path"));
            }
        }
    }

    private ComboBoxModel getNameComboBoxModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement("");
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
        model.addElement("");
        for (int i = 0; i < REGISTERED_PATHS.length; i++) {
            model.addElement(REGISTERED_PATHS[i]);
        }
        return model;

    }

    private ComboBoxModel getCollectionComboBoxModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement("");
        JSONObject json = SolrAuthActionController.getCollections();
        JSONArray collections = json.getJSONArray("collections");

        for (int i = 0; i < collections.length(); i++) {
            model.addElement(collections.get(i));
        }
        return model;

    }

    private ComboBoxModel getBeforeComboBoxModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement("");
        if (this.securityJson != null) {
            ArrayList<LinkedHashMap<String,Object>> perms = securityJson.getAuthorization().getPermissions();
            LinkedHashMap perm;
            for (int i = 0; i < perms.size(); i++) {
                perm = perms.get(i);
                model.addElement(perm.get("name"));
            }
        }
        return model;

    }

    private ComboBoxModel getMethodComboBoxModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement("");
        model.addElement("GET");
        model.addElement("PUT");
        model.addElement("POST");
        model.addElement("DELETE");
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
        String name = (String)permission.get("name");
        String role = (String)permission.get("role");
        
        if(name.trim().equals("")){
            results.add("A permission name is required.");
        }
        char[] chars = name.toCharArray();
        for(int i=0; i<chars.length; i++){
            if(!ALLOWED_CHARS.contains(Character.toString(chars[i]))){
                results.add("'"+chars[i]+"' is not an allowed character.");
            }
        }
        
        if(role.equals("")){
            results.add("Role cannot be empty. ");
        }
        
        
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
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        collectionComboBox = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        beforeComboBox = new javax.swing.JComboBox<>();
        roleComboBox = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        paramsTable = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        GETCheckbox = new javax.swing.JCheckBox();
        PUTCheckbox = new javax.swing.JCheckBox();
        POSTCheckbox = new javax.swing.JCheckBox();
        DELETECheckbox = new javax.swing.JCheckBox();
        HEADCheckbox = new javax.swing.JCheckBox();
        wildcardCheckBox = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 51, 102));

        jLabel1.setBackground(new java.awt.Color(0, 51, 102));
        jLabel1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Manage Permissions: ");

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
        pathComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doLoadPathIntoPermission(evt);
            }
        });

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Params");

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Collection:");

        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Method:");

        collectionComboBox.setModel(getCollectionComboBoxModel());
        collectionComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doLoadCollectionIntoPermission(evt);
            }
        });

        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Before");

        beforeComboBox.setModel(getBeforeComboBoxModel());
        beforeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doLoadBeforePermission(evt);
            }
        });

        roleComboBox.setModel(getRoleComboBoxModel());
        roleComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doLoadRoleIntoPermission(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(0, 51, 102));

        paramsTable.setModel(new javax.swing.table.DefaultTableModel(
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
                {null, null}
            },
            new String [] {
                "Key", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(paramsTable);

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Use a comma to separate values");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jButton3.setBackground(new java.awt.Color(0, 51, 153));
        jButton3.setText("Set Params");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSetParamsInPermissionAction(evt);
            }
        });

        GETCheckbox.setBackground(new java.awt.Color(0, 51, 102));
        GETCheckbox.setForeground(new java.awt.Color(255, 255, 255));
        GETCheckbox.setText("GET");
        GETCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doAppendGETMethod(evt);
            }
        });

        PUTCheckbox.setBackground(new java.awt.Color(0, 51, 102));
        PUTCheckbox.setForeground(new java.awt.Color(255, 255, 255));
        PUTCheckbox.setText("PUT");
        PUTCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doAppendPUTMethodToPermission(evt);
            }
        });

        POSTCheckbox.setBackground(new java.awt.Color(0, 51, 102));
        POSTCheckbox.setForeground(new java.awt.Color(255, 255, 255));
        POSTCheckbox.setText("POST");
        POSTCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doAppendPOSTMethodToPermission(evt);
            }
        });

        DELETECheckbox.setBackground(new java.awt.Color(0, 51, 102));
        DELETECheckbox.setForeground(new java.awt.Color(255, 255, 255));
        DELETECheckbox.setText("DELETE");
        DELETECheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doAppendDELETEMethodToPermission(evt);
            }
        });

        HEADCheckbox.setBackground(new java.awt.Color(0, 51, 102));
        HEADCheckbox.setForeground(new java.awt.Color(255, 255, 255));
        HEADCheckbox.setText("HEAD");
        HEADCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doAppendHEADMethodToPermission(evt);
            }
        });

        wildcardCheckBox.setBackground(new java.awt.Color(0, 51, 102));
        wildcardCheckBox.setForeground(new java.awt.Color(255, 255, 255));
        wildcardCheckBox.setText("Add Wildcard");
        wildcardCheckBox.setEnabled(false);
        wildcardCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doToggleWildcardOnPathAction(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(153, 0, 51));
        jLabel10.setText("*");

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
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(permissionDisplayLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton3))
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(GETCheckbox)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(PUTCheckbox)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(POSTCheckbox)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(DELETECheckbox)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(HEADCheckbox))
                                        .addComponent(collectionComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(beforeComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(nameComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 280, Short.MAX_VALUE)
                                            .addComponent(roleNameField, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(roleComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(pathComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(18, 18, 18)
                                        .addComponent(wildcardCheckBox)))))
                        .addGap(0, 17, Short.MAX_VALUE)))
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(roleNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(roleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(pathComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(wildcardCheckBox))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jButton3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(collectionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(GETCheckbox)
                    .addComponent(PUTCheckbox)
                    .addComponent(POSTCheckbox)
                    .addComponent(DELETECheckbox)
                    .addComponent(HEADCheckbox))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(beforeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
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
        cleanPermissionMap();
         printPermissions();
        ArrayList<String> valid = validatePermission();
           
        if (valid.size() == 0) {
       
            if(isEditing){
               frame.fireAction(SolrAuthActionController.SolrManagerAction.edit_permission, null, permission);
            } else {
               frame.fireAction(SolrAuthActionController.SolrManagerAction.add_permission, null, permission);
            }
              this.setVisible(false);
        this.dispose();
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
            this.permission.put("name", permName);
            this.permissionDisplayLabel.setText(permName);
            this.roleComboBox.setSelectedItem(0);
            this.permission.put("role", (String) this.roleComboBox.getSelectedItem());
        } else {
            int response;

            response = JOptionPane.showConfirmDialog(null, "Selecting '" + permName + "' will override the existing Permission with the same name.  Are you sure you want to do this?");
            if (response == JOptionPane.YES_OPTION) {
                roleNameField.setText(permName);
                this.permission.put("name", permName);
            }
        }
    }//GEN-LAST:event_doLoadRoleNameAction

    private void doLoadRoleIntoPermission(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doLoadRoleIntoPermission
        Log.log(getClass(), "Load role: "+this.roleComboBox.getSelectedItem());
        this.permission.put("role", (String) this.roleComboBox.getSelectedItem());
    }//GEN-LAST:event_doLoadRoleIntoPermission

    private void doLoadPathIntoPermission(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doLoadPathIntoPermission
       
        this.permission.put("path", (String) this.pathComboBox.getSelectedItem());
        this.wildcardCheckBox.setEnabled(true);
    }//GEN-LAST:event_doLoadPathIntoPermission

    private void doSetParamsInPermissionAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doSetParamsInPermissionAction
        int rows = this.paramsTable.getModel().getRowCount();
        LinkedHashMap<String, ArrayList<String>> map = new LinkedHashMap<>();
        String key, value;
        ArrayList<String> paramlist;
        for (int i = 0; i < rows; i++) {
            key = (String) paramsTable.getModel().getValueAt(i, 0);
            value = (String) paramsTable.getModel().getValueAt(i, 1);
            paramlist = new ArrayList<>();
            if (value.contains(",")) {
                String[] values = value.split(",");
                for (int j = 0; j < values.length; j++) {
                    paramlist.add(values[j]);
                }
            } else {
                paramlist.add(value);
            }
            map.put(key, paramlist);

        }
        
        this.permission.put("params", map);
    }//GEN-LAST:event_doSetParamsInPermissionAction

    private void doLoadCollectionIntoPermission(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doLoadCollectionIntoPermission
        this.permission.put("collection", (String) this.collectionComboBox.getSelectedItem());
    }//GEN-LAST:event_doLoadCollectionIntoPermission

    private void doAppendGETMethod(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doAppendGETMethod
      if(this.GETCheckbox.isSelected()){
        appendMethodToPermission("GET");
      } else {
          removeMethodFromPermission("GET");
      }
    }//GEN-LAST:event_doAppendGETMethod

    private void doAppendPUTMethodToPermission(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doAppendPUTMethodToPermission
        if(this.PUTCheckbox.isSelected()){
        appendMethodToPermission("PUT");
      } else {
          removeMethodFromPermission("PUT");
      }
    }//GEN-LAST:event_doAppendPUTMethodToPermission

    private void doAppendPOSTMethodToPermission(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doAppendPOSTMethodToPermission
        if(this.POSTCheckbox.isSelected()){
        appendMethodToPermission("POST");
      } else {
          removeMethodFromPermission("POST");
      }
    }//GEN-LAST:event_doAppendPOSTMethodToPermission

    private void doAppendDELETEMethodToPermission(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doAppendDELETEMethodToPermission
        if(this.DELETECheckbox.isSelected()){
        appendMethodToPermission("DELETE");
      } else {
          removeMethodFromPermission("DELETE");
      }
    }//GEN-LAST:event_doAppendDELETEMethodToPermission

    private void doLoadBeforePermission(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doLoadBeforePermission
        this.permission.put("before", (String) this.beforeComboBox.getSelectedItem());
    }//GEN-LAST:event_doLoadBeforePermission

    private void doAppendHEADMethodToPermission(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doAppendHEADMethodToPermission
        if(this.HEADCheckbox.isSelected()){
        appendMethodToPermission("HEAD");
      } else {
          removeMethodFromPermission("HEAD");
      }
    }//GEN-LAST:event_doAppendHEADMethodToPermission

    private void doToggleWildcardOnPathAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doToggleWildcardOnPathAction
        if(!permission.get("path").equals("")){
             String path = (String)permission.get("path");
            if(this.wildcardCheckBox.isSelected()){
                 path = path +"/*";
            } else if(!this.wildcardCheckBox.isSelected() && path.endsWith("*")){
                path = path.substring(0, path.length()-2);
            }
            
            permission.put("path", path);
            this.pathComboBox.getModel().setSelectedItem(path);
        }
        
        Log.log(getClass(), "Updated path: "+permission.get("path"));
    }//GEN-LAST:event_doToggleWildcardOnPathAction

    private synchronized void cleanPermissionMap(){
        Iterator<String> iter = permission.keySet().iterator();
        LinkedHashMap<String,Object> temp = new LinkedHashMap<>();
        String key;
        Object value;
        while(iter.hasNext()){
            key = iter.next();
            
            if(permission.get(key) != null){
                value = permission.get(key);
                if(value instanceof String){
                    String test = (String)value;
                    if(!test.equals("")){
                        temp.put(key, value);
                    }
                } else {
                temp.put(key, value);
                }
            }
        }
        
        // check params
        LinkedHashMap<String,Object> p = (LinkedHashMap)permission.get("params");
        if(temp.get("name").equals("all") || p.size() == 0){
            temp.remove("params");
        }
        
        this.permission = temp;
    }
    private void printPermissionParam(String key){
        Log.log(getClass(), "Key: "+key+" value: "+permission.get(key).toString());
    }
    private void printPermissions(){
        Iterator<String> iter = permission.keySet().iterator();
        String key;
        Object value;
        while(iter.hasNext()){
            key = iter.next();
            value = permission.get(key);
            Log.log(getClass(), "Key: "+key+" value: "+value.toString());
        }
    }
    private void appendMethodToPermission(String method){
      /*  if(permission.get("method").equals("")){
            permission.put("method", method);
        } else {
            String ms = (String)permission.get("method");
            ms += ","+method;
            permission.put("method", ms);
        }*/
        params.put(method, method);
        String out = Utils.mapKeysToString(params);
        
        Log.log("Upated METHOD: "+out);
         permission.put("method", out);
    }
    
    private void removeMethodFromPermission(String method){
      /*  if(!permission.get("method").equals("")){
            String methods = (String)permission.get("method");
            if(methods.contains(",")){
                String temp = "";
                String[] ms = methods.split(",");
                for(int i=0; i<ms.length; i++){
                    if(!ms[i].equals(method)){
                    if(i>0){
                        temp += ",";
                    }
                       temp += ms[i];
                    }
                }
                Log.log("Set Method: "+temp);
                
            } else {
                //only one, so just reset
                Log.log("RESET method to empty");
                permission.put("method", "");
            }
        }*/
      params.remove(method);
       String out = Utils.mapKeysToString(params);
        
        Log.log("Upated (Remove) METHOD: "+out);
         permission.put("method", out);
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
    private javax.swing.JCheckBox DELETECheckbox;
    private javax.swing.JCheckBox GETCheckbox;
    private javax.swing.JCheckBox HEADCheckbox;
    private javax.swing.JCheckBox POSTCheckbox;
    private javax.swing.JCheckBox PUTCheckbox;
    private javax.swing.JComboBox<String> beforeComboBox;
    private javax.swing.JComboBox<String> collectionComboBox;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> nameComboBox;
    private javax.swing.JTable paramsTable;
    private javax.swing.JComboBox<String> pathComboBox;
    private javax.swing.JLabel permissionDisplayLabel;
    private javax.swing.JComboBox<String> roleComboBox;
    private javax.swing.JTextField roleNameField;
    private javax.swing.JCheckBox wildcardCheckBox;
    // End of variables declaration//GEN-END:variables
}
