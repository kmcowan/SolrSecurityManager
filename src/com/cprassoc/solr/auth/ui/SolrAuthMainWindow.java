/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.ui;

import com.cprassoc.solr.auth.Frameable;
import com.cprassoc.solr.auth.SolrAuthActionController;
import com.cprassoc.solr.auth.SolrAuthManager;
import com.cprassoc.solr.auth.forms.AddRoleForm;
import com.cprassoc.solr.auth.forms.AddUserDialog;
import com.cprassoc.solr.auth.SolrAuthActionController.SolrManagerAction;
import com.cprassoc.solr.auth.forms.AddVersionForm;
import com.cprassoc.solr.auth.forms.ContextualHelpDialog;
import com.cprassoc.solr.auth.forms.HistoryViewerDialog;
import com.cprassoc.solr.auth.forms.ManagePermissionFrame;
import com.cprassoc.solr.auth.forms.OKFormWithMessage;
import com.cprassoc.solr.auth.forms.OkCancelDialog;
import com.cprassoc.solr.auth.forms.ServerStatusDialog;
import com.cprassoc.solr.auth.forms.resources.Resources;
import com.cprassoc.solr.auth.model.Authentication;
import com.cprassoc.solr.auth.model.Authorization;
import com.cprassoc.solr.auth.model.HistoryVersion;
import com.cprassoc.solr.auth.model.SavedVersion;
import com.cprassoc.solr.auth.model.SecurityJson;
import com.cprassoc.solr.auth.util.JsonHelper;
import com.cprassoc.solr.auth.util.Log;
import com.cprassoc.solr.auth.util.Utils;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
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
    private String selectedRole = "";
    private String selectedRoleUser = "";
    private String selectedPermission = "";
    public static int OK_RESPONSE = 0;
    private static final HistoryVersion VERSIONS = new HistoryVersion();

    /**
     * Creates new form SolrAuthMainWindow
     */
    public SolrAuthMainWindow() {
        initComponents();
        SysTray tray = new SysTray(this);
        init();
    }

    private void init() {
        Properties props = SolrAuthManager.getProperties();
        Log.log("INIT...");
        Log.setTextPane(logPane);
        Log.log("Solr Auth Manager is starting up...");
        File cmdFile = new File("solrAuth.sh");
        if (!cmdFile.exists()) {
            String template = Utils.streamToString(SolrAuthMainWindow.class.getResourceAsStream("solrAuth.sh.template"));
            template = template.replaceAll("\\{PATH_TO_SOLR\\}", props.getProperty("solr.install.path") + File.separator);
            template = template.replaceAll("\\{PATH_TO_SOLR_AUTH\\}", System.getProperty("user.dir") + File.separator);
            Utils.writeBytesToFile("solrAuth.sh", template);

        }
        try {
            boolean online = SolrAuthActionController.SOLR.isOnline();
            if (online) {
                this.serverStatusButton.setText("ONLINE");
                this.serverStatusButton.setBackground(Color.green);
                this.serverStatusButton.setForeground(Color.black);
                String authentication = SolrAuthActionController.SOLR.getAuthentication();
                String authorization = SolrAuthActionController.SOLR.getAuthorization();

                Log.log("Authentication: " + authentication);
                Log.log("Authorization: " + authorization);

                //  JSON.decode(authorization, type);
                JSONObject authoeJson = new JSONObject(authentication);
                if (authoeJson.getJSONObject("authentication") != null) {
                    String clsName = authoeJson.getJSONObject("authentication").getString("class");
                    JSONObject authoJson = new JSONObject(authorization);
                    LinkedHashMap authoMap = new LinkedHashMap(JsonHelper.jsonToMap(authoJson));
                    LinkedHashMap authoeMap = new LinkedHashMap(JsonHelper.jsonToMap(authoeJson));
                    Map map = new LinkedHashMap<>();
                    map.put("authentication", authoeMap);
                    map.put("authorization", authoMap);

                    //   System.out.println("Auth Class Name: " + clsName);
                    securityJson = new SecurityJson(map);

                } else {
                    authoeJson = getDefaultSecurityJson();
                    LinkedHashMap authoeMap = new LinkedHashMap(JsonHelper.jsonToMap(authoeJson));
                    securityJson = new SecurityJson(authoeMap);
                }

            } else {
                this.serverStatusButton.setText("OFFLINE");
                this.serverStatusButton.setBackground(Color.red);
                JSONObject authoeJson = getDefaultSecurityJson();
                LinkedHashMap<String, Object> authoeMap = new LinkedHashMap(JsonHelper.jsonToMap(authoeJson));
                securityJson = new SecurityJson(authoeMap);
                logPane.setText("Solr is offline. ");
            }

            populateAuthorizationTable(securityJson.getAuthorization());
            populateUserRolesTable(securityJson.getAuthorization());
            populateAuthenticationTable(securityJson.getAuthentication());

            // add table listeners
            // Users Table listener
            usersTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
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
                        System.out.println("Selected User: " + selectedData);
                        selectedUser = selectedData;
                    }
                }

            });

            // roles table listener
            rolesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    String selectedData = null;

                    /*    int[] selectedRow = rolesTable.getSelectedRows();
                    int[] selectedColumns = rolesTable.getSelectedColumns();

                    for (int i = 0; i < selectedRow.length; i++) {
                        for (int j = 0; j < selectedColumns.length; j++) {
                            selectedData = (String) rolesTable.getValueAt(selectedRow[i], selectedColumns[j]);
                        }
                    }*/
                    if (e.getValueIsAdjusting()) {
                        // Log.log(getClass(), "Selected Role: " + selectedData);
                        selectedRole = (String) rolesTable.getValueAt(rolesTable.getSelectedRow(), 1);//selectedData;
                        selectedRoleUser = (String) rolesTable.getValueAt(rolesTable.getSelectedRow(), 0);

                        Log.log(SolrAuthMainWindow.class, "Selected Role: " + selectedRole + "  role user: " + selectedRoleUser);
                    }
                }

            });

            // permissions table listener
            permissionsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    String selectedData = null;

                    /*   int[] selectedRow = permissionsTable.getSelectedRows();
                    int[] selectedColumns = permissionsTable.getSelectedColumns();

                    for (int i = 0; i < selectedRow.length; i++) {
                        for (int j = 0; j < selectedColumns.length; j++) {
                            selectedData = (String) permissionsTable.getValueAt(selectedRow[i], selectedColumns[j]);
                        }
                    }*/
                    if (e.getValueIsAdjusting()) {

                        selectedPermission = (String) permissionsTable.getValueAt(permissionsTable.getSelectedRow(), 0);
                        System.out.println("Selected Permission: " + selectedPermission);
                    }
                }

            });

            usersTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            rolesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            permissionsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SysTray.displayMessage("Solr Auth Manager Startup Complete. ");
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
        ArrayList<LinkedHashMap<String, Object>> list = auth.getPermissions();
        Iterator<String> iter;
        String key, value;
        Object tempVal;
        Integer col;
        for (int i = 0; i < list.size(); i++) {
            iter = list.get(i).keySet().iterator();
            while (iter.hasNext()) {
                key = iter.next();
                tempVal = list.get(i).get(key);
                if (tempVal instanceof Integer) {
                    value = "" + (Integer) list.get(i).get(key);
                } else if (tempVal instanceof ArrayList) { // for params
                    value = list.get(i).get(key).toString();
                } else if (tempVal instanceof LinkedHashMap) {
                    value = Utils.mapValuesToString((LinkedHashMap) tempVal);
                } else {
                    value = (String) list.get(i).get(key);
                }
                col = getPermissionItemColumn(key);
                this.permissionsTable.getModel().setValueAt(value, i, col);
            }
        }
    }

    private void populateUserRolesTable(Authorization auth) {
        LinkedHashMap<String, Object> map = auth.getUserRoles();
        Iterator<String> iter;
        String key;
        Object value;
        iter = map.keySet().iterator();
        int row = 0;
        while (iter.hasNext()) {
            key = iter.next();
            value = map.get(key);
            if (value instanceof ArrayList) {
                ArrayList temp = (ArrayList) value;
                value = temp.toString();
            } else if (value instanceof String) {
                value = (String) map.get(key);
            }
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

    public void fireAction(SolrManagerAction action, LinkedHashMap<String, String> args, Object optional) {
        JSONObject resp = null;
        LinkedHashMap<String, Object> map = null;

        switch (action) {
            case create_user:
                this.securityJson.getAuthentication().getCredentials().put(args.get("user"), args.get("pwd"));
                populateAuthenticationTable(this.securityJson.getAuthentication());
                break;

            case delete_user:
                Log.log(getClass(), "Delete User: " + selectedUser);
                String result = SolrAuthActionController.deleteUser(selectedUser);
                Log.log(result);
                /*
                {
  "responseHeader":{
    "status":0,
    "QTime":12}}
                 */
                resp = new JSONObject(result);
                if (resp.getJSONObject("responseHeader").getInt("status") == 0) {
                    Log.log(getClass(), "User Deleted OK");
                    showOKOnlyMessageDialog("User " + selectedUser + " deleted successfully", null);
                    securityJson.getAuthentication().removeCredentials(selectedUser);
                    clearTable(this.usersTable.getModel());
                    populateAuthenticationTable(securityJson.getAuthentication());
                }

                break;

            case add_role:
                Log.log(getClass(), "FIRE Add Role...");
                ArrayList<String> roles = new ArrayList<>();
                String user = getUserFromRoleArgs(args);
                String key;
                Iterator<String> iter = args.keySet().iterator();
                while (iter.hasNext()) {
                    key = iter.next();
                    // we have pass a user in with the args, so filter it out here. 
                    if (!key.equals(AddRoleForm.ROLE_USER_KEY)) {
                        roles.add(args.get(key));
                    }
                }
                result = SolrAuthActionController.addRole(user, roles);
                resp = new JSONObject(result);
                if (getResponseStatus(resp) == OK_RESPONSE) {
                    securityJson.getAuthorization().updateAddUserRoles(user, roles);
                    clearTable(this.rolesTable.getModel());
                    populateUserRolesTable(securityJson.getAuthorization());
                }
                Log.log(getClass(), result);

                break;

            case add_permission:
                map = (LinkedHashMap) optional;
                result = SolrAuthActionController.addOrEditPermission(map, false);
                resp = new JSONObject(result);
                if (getResponseStatus(resp) == OK_RESPONSE) {
                    // securityJson.getAuthorization().updateOrAddPermission(map);
                    securityJson.reloadAuthorization();
                    clearTable(this.rolesTable.getModel());
                    populateAuthorizationTable(securityJson.getAuthorization());
                }
                Log.log(getClass(), result);
                break;

            case edit_permission:
                map = (LinkedHashMap) optional;
                result = SolrAuthActionController.addOrEditPermission(map, true);
                resp = new JSONObject(result);
                if (getResponseStatus(resp) == OK_RESPONSE) {
                    // securityJson.getAuthorization().updateOrAddPermission(map);
                    securityJson.reloadAuthorization();
                    clearTable(this.rolesTable.getModel());
                    populateAuthorizationTable(securityJson.getAuthorization());
                }
                Log.log(getClass(), result);
                break;

            case delete_permission:
                break;

            case add_a_version:
                String title = args.get("title");
                String desc = args.get("description");
                result = VERSIONS.saveVersion(title, desc, securityJson);
                if (result == null) {
                    this.showOKOnlyMessageDialog("An error occurred saving the json. ", Resources.Resource.warn);
                } else {
                    this.showOKOnlyMessageDialog("Version saved successfully. ", Resources.Resource.info);
                }
                break;

            case push_a_version:
                String skey = (String) optional;
                SavedVersion version = VERSIONS.getHistory().get(skey);
                if (version != null) {
                    Log.log(getClass(), "PUSH version: " + version.getTitle());
                    pushToSolr(version.getSeucrityJson());
                }
                

                break;

            case load_a_version:

                skey = (String) optional;
                version = VERSIONS.getHistory().get(skey);
                if (version != null) {
                    Log.log(getClass(), "LOAD version: " + version.getTitle());
                    securityJson = version.getSeucrityJson();
                    clearTables();
                    populateAuthorizationTable(securityJson.getAuthorization());
                    populateUserRolesTable(securityJson.getAuthorization());
                    populateAuthenticationTable(securityJson.getAuthentication());
                }

                break;
        }
    }
    
    private void pushToSolr(SecurityJson secu){
         String result = SolrAuthActionController.doPushConfigToSolrAction(secu);
        if (result.equals("")) {
            this.showOKOnlyMessageDialog("Pushed config to Solr OK...", Resources.Resource.info);
        } else {
            this.showOKOnlyMessageDialog("An Error Occured Pushing Security: " + result, Resources.Resource.warn);
        }
    }

    private int getResponseStatus(JSONObject resp) {
        return resp.getJSONObject("responseHeader").getInt("status");
    }

    private JSONObject getResponse(String resp) {
        return new JSONObject(resp);
    }

    private String getUserFromRoleArgs(LinkedHashMap<String, String> args) {

        Iterator<String> iter = args.keySet().iterator();
        String key;
        while (iter.hasNext()) {
            key = iter.next();
            if (key.equals(AddRoleForm.ROLE_USER_KEY)) {
                return args.get(key);
            }
        }
        return "";
    }

    private synchronized void clearTable(TableModel model) {

        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                model.setValueAt("", i, j);
            }
        }
    }
    
     private synchronized void clearTables() {
        TableModel[] models = new TableModel[3];
        models[0] = this.usersTable.getModel();
        models[1] = this.rolesTable.getModel();
        models[2] = this.permissionsTable.getModel();
        
        TableModel model;
        for(int m=0; m<models.length; m++){
            model = models[m];
          clearTable(model);
        }
    }

    public void showOKOnlyMessageDialog(String message, Resources.Resource resc) {

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
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        permissionsTable = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jToolBar2 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        rolesTable = new javax.swing.JTable();
        jToolBar3 = new javax.swing.JToolBar();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        serverStatusButton = new javax.swing.JToggleButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        logPane = new javax.swing.JTextPane();
        loggingEnabledCheckbox = new javax.swing.JCheckBox();
        helpButton = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(0, 51, 102));

        jLabel1.setBackground(new java.awt.Color(0, 51, 102));
        jLabel1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
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

        jToolBar1.setBackground(new java.awt.Color(0, 51, 153));
        jToolBar1.setRollover(true);

        jButton5.setBackground(new java.awt.Color(0, 51, 153));
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("Add");
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doAddPermissionAction(evt);
            }
        });
        jToolBar1.add(jButton5);

        jButton6.setBackground(new java.awt.Color(0, 51, 153));
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("Edit");
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doEditPermissionAction(evt);
            }
        });
        jToolBar1.add(jButton6);

        jButton7.setBackground(new java.awt.Color(0, 51, 153));
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setText("Delete");
        jButton7.setFocusable(false);
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doDeletePermission(evt);
            }
        });
        jToolBar1.add(jButton7);

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

        jToolBar2.setBackground(new java.awt.Color(0, 51, 153));
        jToolBar2.setRollover(true);

        jButton1.setBackground(new java.awt.Color(0, 51, 153));
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

        jButton2.setBackground(new java.awt.Color(0, 51, 153));
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

        jToolBar3.setBackground(new java.awt.Color(0, 51, 153));
        jToolBar3.setRollover(true);

        jButton3.setBackground(new java.awt.Color(0, 51, 153));
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Add");
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doAddRoleAction(evt);
            }
        });
        jToolBar3.add(jButton3);

        jButton4.setBackground(new java.awt.Color(0, 51, 153));
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Revoke");
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doRevokeRoleAction(evt);
            }
        });
        jToolBar3.add(jButton4);

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Solr Staus: ");

        serverStatusButton.setBackground(new java.awt.Color(0, 0, 204));
        serverStatusButton.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        serverStatusButton.setForeground(new java.awt.Color(255, 255, 255));
        serverStatusButton.setText("Offline");

        logPane.setBackground(new java.awt.Color(255, 255, 255));
        logPane.setForeground(new java.awt.Color(0, 0, 0));
        jScrollPane5.setViewportView(logPane);

        loggingEnabledCheckbox.setBackground(new java.awt.Color(0, 51, 102));
        loggingEnabledCheckbox.setForeground(new java.awt.Color(255, 255, 255));
        loggingEnabledCheckbox.setSelected(true);
        loggingEnabledCheckbox.setText("Logging Enabled");
        loggingEnabledCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doToggleLoggingEnabled(evt);
            }
        });

        helpButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/cprassoc/solr/auth/forms/resources/help_sml.png"))); // NOI18N
        helpButton.setToolTipText("Get Help on this Topic");
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doLoadHelpContext(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane1))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6)
                                .addGap(4, 4, 4)
                                .addComponent(serverStatusButton)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(helpButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane5)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(37, 37, 37)
                                        .addComponent(loggingEnabledCheckbox)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(142, 142, 142))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(21, 21, 21)
                            .addComponent(jLabel2))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel6)
                                .addComponent(serverStatusButton))))
                    .addComponent(helpButton, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(loggingEnabledCheckbox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jMenu1.setText("File");

        jMenuItem5.setText("Load Config");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doLoadConfigAction(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuItem4.setText("Save as...");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSaveConfigAsAction(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuItem1.setText("Quit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doQuitAction(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Server");

        jMenuItem9.setText("Server Status");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doViewServerStatus(evt);
            }
        });
        jMenu2.add(jMenuItem9);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Tools");

        jMenuItem2.setText("Manage Properties");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doManagePropertiesAction(evt);
            }
        });
        jMenu3.add(jMenuItem2);

        jMenuItem3.setText("Export Current Config");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doExportSecurityAction(evt);
            }
        });
        jMenu3.add(jMenuItem3);

        jMenuItem6.setText("Push Config to Solr");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doPushConfigToSolrAction(evt);
            }
        });
        jMenu3.add(jMenuItem6);

        jMenuItem7.setText("Save a Version");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSaveAVersion(evt);
            }
        });
        jMenu3.add(jMenuItem7);

        jMenuItem8.setText("View Saved Versions");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doViewSavedVersions(evt);
            }
        });
        jMenu3.add(jMenuItem8);

        jMenuItem11.setText("Re-Index Permissions");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doReindexPermissions(evt);
            }
        });
        jMenu3.add(jMenuItem11);

        jMenuBar1.add(jMenu3);

        jMenu4.setText("Support");

        jMenuItem10.setText("Help");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doShowAllHelpTopics(evt);
            }
        });
        jMenu4.add(jMenuItem10);

        jMenuBar1.add(jMenu4);

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

    private void doQuitAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doQuitAction
        System.exit(0);
    }//GEN-LAST:event_doQuitAction

    private void doAddRoleAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doAddRoleAction
        AddRoleForm form = new AddRoleForm(this, true, securityJson.getAuthentication().getCredentials(), securityJson.getAuthorization().getUserRoles());
        form.setVisible(true);
    }//GEN-LAST:event_doAddRoleAction

    private void doDeleteUserConfirmAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doDeleteUserConfirmAction
        if (selectedUser == null || selectedUser.equals("")) {
            showOKOnlyMessageDialog("No user selected", Resources.Resource.warn);
        } else {
            showOKCancelMessageDialog("Are you sure you want to delete " + selectedUser + "? \n This action cannot be undone. ", SolrManagerAction.delete_user);
        }
    }//GEN-LAST:event_doDeleteUserConfirmAction

    private void doAddUserAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doAddUserAction
        AddUserDialog dialog = new AddUserDialog(this, true);
        dialog.setVisible(true);
        dialog.requestFocus();
    }//GEN-LAST:event_doAddUserAction

    private void doToggleLoggingEnabled(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doToggleLoggingEnabled
        if (this.loggingEnabledCheckbox.isSelected()) {
            Log.setLoggingEnabled(true);
        } else {
            Log.setLoggingEnabled(false);
        }
    }//GEN-LAST:event_doToggleLoggingEnabled

    private void doManagePropertiesAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doManagePropertiesAction
        SolrSecurityPropertyManagerFrame secframe = new SolrSecurityPropertyManagerFrame(SolrAuthManager.getProperties());
        secframe.setVisible(true);
    }//GEN-LAST:event_doManagePropertiesAction

    private void doExportSecurityAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doExportSecurityAction
        String json = securityJson.export();//JSON.encode(securityJson);
        File exportDir = new File("export");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        String exportFilePath = exportDir.getAbsolutePath() + File.separator + "security_json_" + new Date().toString() + ".json";
        Utils.writeBytesToFile(exportFilePath, json);
        File file = new File(exportFilePath);
        if (file.exists()) {
            showOKOnlyMessageDialog("File exported successfully", null);
        }
        Log.log(json);
    }//GEN-LAST:event_doExportSecurityAction

    private void doSaveConfigAsAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doSaveConfigAsAction
        try {
            JFileChooser fc = new JFileChooser();
            int retval = fc.showSaveDialog(this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                String filePath = fc.getSelectedFile().getAbsolutePath();
                String content = securityJson.export();
                Utils.writeBytesToFile(filePath, content);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_doSaveConfigAsAction

    private void doLoadConfigAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doLoadConfigAction
        JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));
        int retval = fc.showDialog(this, "Open Config");
        if (retval == JFileChooser.APPROVE_OPTION) {
            try {
                File fileToOpen = fc.getSelectedFile();
                if (fileToOpen.exists()) {
                    JSONObject obj = null;

                    String content = new String(IOUtils.readBytesFromStream(new FileInputStream(fileToOpen)));
                    JSONObject secu = new JSONObject(content);
                    if (secu.has("data")) {
                        obj = new JSONObject(secu.getString("data"));
                    } else {
                        obj = secu;
                    }
                    LinkedHashMap<String, Object> map = new LinkedHashMap(JsonHelper.jsonToMap(obj));
                    Log.log(content);
                    securityJson = new SecurityJson(map);
                    populateAuthorizationTable(securityJson.getAuthorization());
                    populateUserRolesTable(securityJson.getAuthorization());
                    populateAuthenticationTable(securityJson.getAuthentication());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_doLoadConfigAction

    private void doRevokeRoleAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doRevokeRoleAction
        if (this.rolesTable.getSelectedRow() > -1) {
            Log.log(getClass(), "Revoke: " + selectedRole);

            ArrayList<String> roles = new ArrayList();
            roles.add("null");
            String resp = SolrAuthActionController.addRole(selectedRoleUser, roles);

            if (getResponseStatus(getResponse(resp)) == OK_RESPONSE) {
                this.rolesTable.getModel().setValueAt("null", this.rolesTable.getSelectedRow(), 1);
                securityJson.getAuthorization().getUserRoles().remove(selectedRoleUser);
                clearTable(rolesTable.getModel());
                populateUserRolesTable(securityJson.getAuthorization());
            }
            //
        } else {
            Log.log(getClass(), "NOT Revoling.  No row selected: " + this.rolesTable.getSelectedRow());
            this.showOKOnlyMessageDialog("No Role Selected", Resources.Resource.warn);
        }
    }//GEN-LAST:event_doRevokeRoleAction

    private void doAddPermissionAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doAddPermissionAction
        ManagePermissionFrame form = new ManagePermissionFrame(this, true, securityJson, null);
        form.setVisible(true);
    }//GEN-LAST:event_doAddPermissionAction

    private void doEditPermissionAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doEditPermissionAction
        if (this.permissionsTable.getSelectedRow() > -1) {
            LinkedHashMap<String, Object> map = securityJson.getPermission(selectedPermission);
            ManagePermissionFrame form = new ManagePermissionFrame(this, true, securityJson, map);
            form.setVisible(true);
        }
    }//GEN-LAST:event_doEditPermissionAction

    private void doPushConfigToSolrAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doPushConfigToSolrAction

       pushToSolr(securityJson);
    }//GEN-LAST:event_doPushConfigToSolrAction

    private void doDeletePermission(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doDeletePermission
        if (!selectedPermission.equals("")) {
            int response;

            response = JOptionPane.showConfirmDialog(null, "You are about to delete the permission: '" + selectedPermission + "'.\nThis cannot be undone.  Are you sure you want to do this?");
            if (response == JOptionPane.YES_OPTION) {

                LinkedHashMap<String, Object> perm = securityJson.getPermission(selectedPermission);

                if (perm.get("index") != null) {
                    Object index = perm.get("index");
                    Log.log("Removing Permission with index: "+index.toString());
                    int permId = -1;
                    if (index instanceof Integer) {
                        permId = (Integer) index;
                    }

                    String result = SolrAuthActionController.deletePermission(selectedPermission, permId);
                    if (result.equals("")) {
                        this.showOKOnlyMessageDialog("Failed to delete permission.  No valid index found or provided. ", Resources.Resource.warn);
                    } else {
                        JSONObject resp = new JSONObject(result);
                        if (this.getResponseStatus(resp) == OK_RESPONSE) {
                            String authorization = SolrAuthActionController.SOLR.getAuthorization();
                            JSONObject authoJson = new JSONObject(authorization);
                            LinkedHashMap authoMap = new LinkedHashMap(JsonHelper.jsonToMap(authoJson));
                            Authorization auth = new Authorization(authoMap);
                            this.clearTable(this.permissionsTable.getModel());
                            securityJson.setAuthorization(auth);
                            this.populateAuthorizationTable(auth);
                            this.selectedPermission = "";
                            this.permissionsTable.changeSelection(0, 0, false, false);
                        }
                    }
                } else {
                     this.showOKOnlyMessageDialog("Failed to delete permission.  No valid index found or provided. ", Resources.Resource.warn);
                }

            }

        } else {
            this.showOKOnlyMessageDialog("No permission selectd. ", Resources.Resource.warn);
        }
    }//GEN-LAST:event_doDeletePermission

    private void doSaveAVersion(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doSaveAVersion
        AddVersionForm form = new AddVersionForm(this, true);
        form.setVisible(true);
    }//GEN-LAST:event_doSaveAVersion

    private void doViewSavedVersions(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doViewSavedVersions
        HistoryViewerDialog viewer = new HistoryViewerDialog(this, true, VERSIONS);
        viewer.setVisible(true);
    }//GEN-LAST:event_doViewSavedVersions

    private void doViewServerStatus(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doViewServerStatus
        ServerStatusDialog dialog = new ServerStatusDialog(this, true);
        dialog.setVisible(true);
    }//GEN-LAST:event_doViewServerStatus

    private void doLoadHelpContext(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doLoadHelpContext
        ContextualHelpDialog dialog = new ContextualHelpDialog(this, true, "main_window");
        dialog.setVisible(true);
    }//GEN-LAST:event_doLoadHelpContext

    private void doShowAllHelpTopics(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doShowAllHelpTopics
       ContextualHelpDialog dialog = new ContextualHelpDialog(this, true, null);
        dialog.setVisible(true);
    }//GEN-LAST:event_doShowAllHelpTopics

    private void doReindexPermissions(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doReindexPermissions
        
        ArrayList<LinkedHashMap<String,Object>> list = securityJson.getAuthorization().getPermissions();
        ArrayList<LinkedHashMap<String,Object>> newlist = new ArrayList<>();
        int index = 1;
        for(LinkedHashMap map: list){
            map.put("index", ""+index);
            index++;
            SolrAuthActionController.addOrEditPermission(map, true);
            newlist.add(map);
        }
        
        securityJson.getAuthorization().setPermissions(newlist);
        clearTable(this.permissionsTable.getModel());
        populateAuthorizationTable(securityJson.getAuthorization());
       
    }//GEN-LAST:event_doReindexPermissions

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
    private javax.swing.JButton helpButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JTextPane logPane;
    private javax.swing.JCheckBox loggingEnabledCheckbox;
    private javax.swing.JTable permissionsTable;
    private javax.swing.JTable rolesTable;
    private javax.swing.JToggleButton serverStatusButton;
    private javax.swing.JTable usersTable;
    // End of variables declaration//GEN-END:variables
}
