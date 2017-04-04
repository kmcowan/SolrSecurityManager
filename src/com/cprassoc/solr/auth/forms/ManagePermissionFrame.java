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
import com.cprassoc.solr.auth.security.Permission;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.TableModel;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 *
 * @author kevin
 */
public class ManagePermissionFrame extends BaseDialog implements Frameable {

    private SecurityJson securityJson = null;
    private LinkedHashMap<String, Object> permission = null;
    private final static String[] REGISTERED_PATHS = "/admin/mbeans,/browse,/update/json/docs,/admin/luke,/export,/get,/admin/properties,/elevate,/update/json,/admin/threads,/query,/analysis/field,/analysis/document,/spell,/update/csv,/sql,/graph,/tvrh,/select,/admin/segments,/admin/system,/replication,/config,/stream,/schema,/admin/plugins,/admin/logging,/admin/ping,/update,/admin/file,/terms,/debug/dump,/update/extract".split(",");
    private Frameable frame = null;
    private boolean isEditing = false;
    private LinkedHashMap<String, String> methods = new LinkedHashMap<>();
    private static String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyz-_";
    private LinkedHashMap<String, String> knownPermissionNames = null;
    private String[] allmethods = {"GET", "PUT", "POST", "DELETE", "HEAD"};
    private boolean paramFrameIsShowing = false;

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
            Log.log("IS EDITING");
            if (permission.get(SecurityJson.PermissionAttributes.path.name()) != null) {
                this.pathComboBox.setSelectedItem(permission.get(SecurityJson.PermissionAttributes.path.name()));
            }

            this.roleNameField.setText((String) permission.get(SecurityJson.PermissionAttributes.name.name()));

            if (permission.get(SecurityJson.PermissionAttributes.index.name()) != null) {
                this.indexLabel.setText("" + (Integer) permission.get(SecurityJson.PermissionAttributes.index.name()));
            }

            if (permission.get(SecurityJson.PermissionAttributes.role.name()) != null) {
                this.roleComboBox.setSelectedItem(permission.get(SecurityJson.PermissionAttributes.role.name()));
            }

            if (permission.get(SecurityJson.PermissionAttributes.collection.name()) != null) {
                this.collectionComboBox.setSelectedItem(permission.get(SecurityJson.PermissionAttributes.collection.name()));
            }

            if (permission.get(SecurityJson.PermissionAttributes.before.name()) != null) {
                this.beforeComboBox.setSelectedItem(permission.get(SecurityJson.PermissionAttributes.before.name()));
            }

            if (permission.get(SecurityJson.PermissionAttributes.params.name()) != null) {
                populateParamTable();
            }

            if (permission.get(SecurityJson.PermissionAttributes.method.name()) != null) {
                Object ms = permission.get(SecurityJson.PermissionAttributes.method.name());
                if (ms instanceof ArrayList) {
                    ArrayList<String> mlist = (ArrayList) ms;
                    for (int j = 0; j < mlist.size(); j++) {
                        String m = mlist.get(j);
                        if (this.hasMethod(m)) {
                            enableCheckbox(m);
                        }
                    }
                } else if (ms instanceof String) {
                    String m = (String) ms;
                    if (m.contains(",")) {
                        String[] temp = m.split(",");
                        for (int j = 0; j < temp.length; j++) {
                            if (this.hasMethod(temp[j])) {
                                enableCheckbox(temp[j]);
                            }
                        }
                    } else if (this.hasMethod(m)) {
                        enableCheckbox(m);
                    }
                }
            }

        }
    }

    private void enableCheckbox(String m) {
        Log.log("enable checkbox: " + m);
        switch (m) {
            case "GET":
                this.GETCheckbox.setSelected(true);
                break;

            case "POST":
                this.POSTCheckbox.setSelected(true);
                break;

            case "PUT":
                this.PUTCheckbox.setSelected(true);
                break;

            case "DELETE":
                this.DELETECheckbox.setSelected(true);
                break;

            case "HEAD":
                this.HEADCheckbox.setSelected(true);
                break;
        }
    }

    public void fireAction(SolrAuthActionController.SolrManagerAction action, LinkedHashMap<String, String> args, Object optional) {

        switch (action) {
            case add_permissions_to_permissions:
                break;

            case add_param_to_permission:
                LinkedHashMap<String, Object> param = (LinkedHashMap) optional;
                String key = "" + System.currentTimeMillis();
                LinkedHashMap<String, Object> paramsmap = null;

                if (permission.get("params") != null) {
                    paramsmap = (LinkedHashMap) permission.get("params");
                } else {
                    paramsmap = new LinkedHashMap<>();
                }
                paramsmap.put(key, param);
                permission.put("params", paramsmap);
                clearParamTable();
                populateParamTable();
                 this.paramFrameIsShowing = false;
                break;
                
            case reset_param_showing_flag:
            this.paramFrameIsShowing = false;
            break;
        }
    }

    private boolean hasMethod(String method) {
        boolean methodExists = false;
        Object m = permission.get("method");

        String key;
        for (int i = 0; i < allmethods.length; i++) {

            if (m instanceof String) {
                key = (String) allmethods[i];
                if (key.contains(allmethods[i])) {
                    methodExists = true;
                    break;
                }
            }
        }
        return methodExists;
    }

    private void clearParamTable() {
        TableModel model = this.paramsTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                model.setValueAt("", i, j);
            }
        }
    }

    private void populateParamTable() {
        TableModel model = this.paramsTable.getModel();
        LinkedHashMap<String, LinkedHashMap<String, Object>> map = (LinkedHashMap) permission.get("params");
        Iterator<String> iter = map.keySet().iterator();
        String key;
        LinkedHashMap<String, Object> value;
        int row = 0;
        while (iter.hasNext()) {
            key = iter.next();
            value = map.get(key);
            model.setValueAt(value.get("key"), row, 0);
            model.setValueAt(value.get("value").toString(), row, 1);
            row++;
        }
    }

    public void showOKOnlyMessageDialog(String message, Resources.Resource resc) {
        frame.showOKOnlyMessageDialog(message, resc);
    }

    /**
     * @return the frame
     */
    public Frame getFrame() {
        return frame.getFrame();
    }

    private ComboBoxModel getNameComboBoxModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement("");
        this.knownPermissionNames = new LinkedHashMap<>();
        if (securityJson != null) {
            Map<String, Name> perms = PermissionNameProvider.values;
            String key;
            Name name;
            Iterator<String> iter = perms.keySet().iterator();
            while (iter.hasNext()) {
                key = iter.next();
                name = perms.get(key);
                model.addElement(name.getPermissionName());
                knownPermissionNames.put(name.getPermissionName(), key);
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
            ArrayList<LinkedHashMap<String, Object>> perms = securityJson.getAuthorization().getPermissions();
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
        String name = (String) permission.get("name");
        String role = (String) permission.get("role");

        if (name.trim().equals("")) {
            results.add("A permission name is required.");
        }
        char[] chars = name.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (!ALLOWED_CHARS.contains(Character.toString(chars[i]))) {
                results.add("'" + chars[i] + "' is not an allowed character.");
            }
        }

        if (role.equals("")) {
            results.add("Role cannot be empty. ");
        }

        if (results.equals("")) {
            // test permission
            try {
                Permission test = new Permission();
                test.load(permission);

            } catch (Exception e) {
                e.printStackTrace();
                results.add("Permission Validation FAILED. Reason: " + e.getLocalizedMessage());
            }
        }

        return results;
    }

    private void loadParamsIntoPermission(String name, Object nvalue) {
        int rows = this.paramsTable.getModel().getRowCount();
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put(name, nvalue);
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
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
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
        paramBuildButton = new javax.swing.JButton();
        GETCheckbox = new javax.swing.JCheckBox();
        PUTCheckbox = new javax.swing.JCheckBox();
        POSTCheckbox = new javax.swing.JCheckBox();
        DELETECheckbox = new javax.swing.JCheckBox();
        HEADCheckbox = new javax.swing.JCheckBox();
        wildcardCheckBox = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        indexLabel = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        mainPanel.setBackground(new java.awt.Color(0, 51, 102));

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

        roleNameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                doUpdatePermissionNameLabel(evt);
            }
        });
        roleNameField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                doCheckNamedPermissionAction(evt);
            }
        });
        roleNameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                doCheckPermissionNameChangeAction(evt);
            }
        });

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
        paramsTable.setFocusable(false);
        jScrollPane1.setViewportView(paramsTable);

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Use a comma to separate values");

        paramBuildButton.setBackground(new java.awt.Color(0, 51, 153));
        paramBuildButton.setForeground(new java.awt.Color(255, 255, 255));
        paramBuildButton.setText("Param Builder");
        paramBuildButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doInvokeParamBuilder(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(paramBuildButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(paramBuildButton)))
        );

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

        jLabel10.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 0, 51));
        jLabel10.setText("*");
        jLabel10.setToolTipText("This is a required field");

        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Index: ");

        indexLabel.setForeground(new java.awt.Color(255, 255, 255));
        indexLabel.setText("0");

        jButton5.setBackground(new java.awt.Color(0, 51, 153));
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("Test");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doTestPermission(evt);
            }
        });

        helpButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/cprassoc/solr/auth/forms/resources/help_sml.png"))); // NOI18N
        helpButton.setToolTipText("Get Help on this Topic");
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doLoadHelpContext(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton5)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(permissionDisplayLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(mainPanelLayout.createSequentialGroup()
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
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(nameComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 280, Short.MAX_VALUE)
                                            .addComponent(roleNameField, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(roleComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(pathComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(wildcardCheckBox))
                                            .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addGap(30, 30, 30)
                                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(helpButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                                        .addComponent(jLabel11)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(indexLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))))))))
                        .addGap(0, 10, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(permissionDisplayLabel)))
                    .addComponent(helpButton, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(nameComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(indexLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(roleNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(15, 15, 15)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(roleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(pathComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(wildcardCheckBox))
                .addGap(18, 18, 18)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(collectionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(GETCheckbox)
                    .addComponent(PUTCheckbox)
                    .addComponent(POSTCheckbox)
                    .addComponent(DELETECheckbox)
                    .addComponent(HEADCheckbox))
                .addGap(18, 18, 18)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(beforeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton1)
                    .addComponent(jButton5))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void doSaveAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doSaveAction
        cleanPermissionMap();
        printPermissions();
        ArrayList<String> valid = validatePermission();

        if (valid.size() == 0) {

            if (isEditing) {
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

        if (this.knownPermissionNames.get(permName) != null) {
            toggleNonGlobalFields(false);
        }
    }//GEN-LAST:event_doLoadRoleNameAction

    private void toggleNonGlobalFields(boolean b) {
        //  Log.log(getClass(), "Toggle fields: "+b);
        if (!b) {
            this.showOKOnlyMessageDialog("You have selected a predefined (known) permission.  Some attributes will be disabled.  \n You can enable these by providing a custom name for the permission. ", Resources.Resource.disk);
        }
        this.pathComboBox.setEditable(b);
        this.pathComboBox.setEnabled(b);

        this.paramsTable.setEnabled(b);
        this.beforeComboBox.setEditable(b);
        this.beforeComboBox.setEnabled(b);
        this.GETCheckbox.setEnabled(b);
        this.POSTCheckbox.setEnabled(b);
        this.DELETECheckbox.setEnabled(b);
        this.HEADCheckbox.setEnabled(b);
        // this.setParamButton.setEnabled(b);
        this.paramBuildButton.setEnabled(b);
        this.wildcardCheckBox.setEnabled(b);
    }
    private void doLoadRoleIntoPermission(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doLoadRoleIntoPermission
        Log.log(getClass(), "Load role: " + this.roleComboBox.getSelectedItem());
        this.permission.put("role", (String) this.roleComboBox.getSelectedItem());
    }//GEN-LAST:event_doLoadRoleIntoPermission

    private void doLoadPathIntoPermission(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doLoadPathIntoPermission

        this.permission.put("path", (String) this.pathComboBox.getSelectedItem());
        this.wildcardCheckBox.setEnabled(true);
    }//GEN-LAST:event_doLoadPathIntoPermission

    private void doLoadCollectionIntoPermission(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doLoadCollectionIntoPermission
        this.permission.put("collection", (String) this.collectionComboBox.getSelectedItem());
    }//GEN-LAST:event_doLoadCollectionIntoPermission

    private void doAppendGETMethod(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doAppendGETMethod
        if (this.GETCheckbox.isSelected()) {
            appendMethodToPermission("GET");
        } else {
            removeMethodFromPermission("GET");
        }
    }//GEN-LAST:event_doAppendGETMethod

    private void doAppendPUTMethodToPermission(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doAppendPUTMethodToPermission
        if (this.PUTCheckbox.isSelected()) {
            appendMethodToPermission("PUT");
        } else {
            removeMethodFromPermission("PUT");
        }
    }//GEN-LAST:event_doAppendPUTMethodToPermission

    private void doAppendPOSTMethodToPermission(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doAppendPOSTMethodToPermission
        if (this.POSTCheckbox.isSelected()) {
            appendMethodToPermission("POST");
        } else {
            removeMethodFromPermission("POST");
        }
    }//GEN-LAST:event_doAppendPOSTMethodToPermission

    private void doAppendDELETEMethodToPermission(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doAppendDELETEMethodToPermission
        if (this.DELETECheckbox.isSelected()) {
            appendMethodToPermission("DELETE");
        } else {
            removeMethodFromPermission("DELETE");
        }
    }//GEN-LAST:event_doAppendDELETEMethodToPermission

    private void doLoadBeforePermission(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doLoadBeforePermission
        this.permission.put("before", (String) this.beforeComboBox.getSelectedItem());
    }//GEN-LAST:event_doLoadBeforePermission

    private void doAppendHEADMethodToPermission(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doAppendHEADMethodToPermission
        if (this.HEADCheckbox.isSelected()) {
            appendMethodToPermission("HEAD");
        } else {
            removeMethodFromPermission("HEAD");
        }
    }//GEN-LAST:event_doAppendHEADMethodToPermission

    private void doToggleWildcardOnPathAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doToggleWildcardOnPathAction
        if (!permission.get("path").equals("")) {
            String path = (String) permission.get("path");
            if (this.wildcardCheckBox.isSelected()) {
                path = path + "/*";
            } else if (!this.wildcardCheckBox.isSelected() && path.endsWith("*")) {
                path = path.substring(0, path.length() - 2);
            }

            permission.put("path", path);
            this.pathComboBox.getModel().setSelectedItem(path);
        }

        Log.log(getClass(), "Updated path: " + permission.get("path"));
    }//GEN-LAST:event_doToggleWildcardOnPathAction

    private void doInvokeParamBuilder(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doInvokeParamBuilder
        AddParamDialog dialog = new AddParamDialog(this, true);
        dialog.setVisible(true);
    }//GEN-LAST:event_doInvokeParamBuilder

    private void doTestPermission(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doTestPermission
        try {
            Permission test = new Permission();
            test.load(permission);

            this.showOKOnlyMessageDialog("Permission Test OK", Resources.Resource.permission_key);
        } catch (Exception e) {
            e.printStackTrace();
            this.showOKOnlyMessageDialog("Permission Test FAILED: " + e.getLocalizedMessage(), Resources.Resource.warn);
        }
    }//GEN-LAST:event_doTestPermission

    private void doCheckNamedPermissionAction(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_doCheckNamedPermissionAction
        Log.log(getClass(), "Checking Permission Name...");
        if (this.knownPermissionNames.get(this.roleNameField.getText()) == null) {
            toggleNonGlobalFields(true);
        }
    }//GEN-LAST:event_doCheckNamedPermissionAction

    private void doCheckPermissionNameChangeAction(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_doCheckPermissionNameChangeAction
        if (this.knownPermissionNames.get(this.roleNameField.getText()) == null) {
            toggleNonGlobalFields(true);
        }
    }//GEN-LAST:event_doCheckPermissionNameChangeAction

    private void doLoadHelpContext(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doLoadHelpContext
        ContextualHelpDialog dialog = new ContextualHelpDialog(frame.getFrame(), true, "manage_permissions");
        dialog.setVisible(true);
    }//GEN-LAST:event_doLoadHelpContext

    private void doUpdatePermissionNameLabel(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_doUpdatePermissionNameLabel
        if(!roleNameField.getText().trim().equals("")){
            this.permissionDisplayLabel.setText(roleNameField.getText());
        }
    }//GEN-LAST:event_doUpdatePermissionNameLabel

    private synchronized void cleanPermissionMap() {
        Iterator<String> iter = permission.keySet().iterator();
        LinkedHashMap<String, Object> temp = new LinkedHashMap<>();
        String key;
        Object value;
        while (iter.hasNext()) {
            key = iter.next();

            if (permission.get(key) != null) {
                value = permission.get(key);
                if (value instanceof String) {
                    String test = (String) value;
                    if (!test.equals("")) {
                        temp.put(key, value);
                    }
                } else {
                    temp.put(key, value);
                }
            }
        }

        // check methods
        LinkedHashMap<String, Object> p = (LinkedHashMap) permission.get("params");
        if (temp.get("name") == null) {
            temp.put("name", this.roleNameField.getText());
        }

        if (temp.get("role") == null) {
            temp.put("role", this.roleComboBox.getSelectedItem());
        }
        if (temp.get("name").equals("all")
                || (p != null && p.isEmpty())) {
            temp.remove("params");
        }

        this.permission = temp;
    }

    private void printPermissionParam(String key) {
        Log.log(getClass(), "Key: " + key + " value: " + permission.get(key).toString());
    }

    private void printPermissions() {
        Iterator<String> iter = permission.keySet().iterator();
        String key;
        Object value;
        while (iter.hasNext()) {
            key = iter.next();
            value = permission.get(key);
            Log.log(getClass(), "Key: " + key + " value: " + value.toString());
        }
    }

    private void appendMethodToPermission(String method) {
        /*  if(permission.get("method").equals("")){
            permission.put("method", method);
        } else {
            String ms = (String)permission.get("method");
            ms += ","+method;
            permission.put("method", ms);
        }*/
        methods.put(method, method);
        String out = Utils.mapKeysToString(methods);

        Log.log("Upated METHOD: " + out);
        permission.put("method", out);
    }

    private void removeMethodFromPermission(String method) {
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
        methods.remove(method);
        String out = Utils.mapKeysToString(methods);

        Log.log("Upated (Remove) METHOD: " + out);
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
    private javax.swing.JButton helpButton;
    private javax.swing.JLabel indexLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JComboBox<String> nameComboBox;
    private javax.swing.JButton paramBuildButton;
    private javax.swing.JTable paramsTable;
    private javax.swing.JComboBox<String> pathComboBox;
    private javax.swing.JLabel permissionDisplayLabel;
    private javax.swing.JComboBox<String> roleComboBox;
    private javax.swing.JTextField roleNameField;
    private javax.swing.JCheckBox wildcardCheckBox;
    // End of variables declaration//GEN-END:variables
}
