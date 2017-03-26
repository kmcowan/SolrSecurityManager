/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.forms;

import com.cprassoc.solr.auth.forms.html.HTMLResource;
import com.cprassoc.solr.auth.model.ContextualHelp;
import com.cprassoc.solr.auth.util.Log;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 *
 * @author kevin
 */
public class ContextualHelpDialog extends BaseDialog {

    String key = "";

    /**
     * Creates new form ContextualHelpDialog
     */
    public ContextualHelpDialog(java.awt.Frame parent, boolean modal, String key) {
        super(parent, modal);

        initComponents();
        init(key);
    }

    private void init(String key) {
        try {
            HTMLEditorKit kit = new HTMLEditorKit();
            HTMLDocument doc = new HTMLDocument();
            LinkedHashMap<String, String> map = null;
            boolean isTopical = false;
            if (key != null && !key.equals("")) {
                map = ContextualHelp.getInstance().getHelpContent(key);
            } else {
                map = ContextualHelp.getInstance().getHelpTopics("");
                isTopical = true;
                Log.log("Loading TOPICAL items...");
            }
            //   kit.read(new StringReader(map.get("content")), doc, 0);
            //  contentPane.setDocument(doc);
            kit.setStyleSheet(loadStyleSheet());

            contentPane.setContentType("text/html");

            titleField.setText(map.get("title"));
            Log.log(getClass(), "Title: " + map.get("title"));

            contentPane.setEditorKit(kit);
            contentPane.setEditable(false);
            if (!isTopical) {
                contentPane.setText(map.get("content"));
            } else {
                contentPane.setText(getTopicHTML(map));
            }

            contentPane.addHyperlinkListener(new HyperlinkListener() {
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    try {
                        Log.log("HyperLink Event activated: " + e.getEventType().toString());

                        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                            URL url = e.getURL();
                            Log.log("URL: " + url.toString());
                            String uri = url.toString();

                            if (uri.startsWith("file:")) {
                                String[] href = uri.split("://");
                                String nkey = href[1];
                                Log.log("Load File: " + nkey);
                                LinkedHashMap<String, String> map = ContextualHelp.getInstance().getHelpContent(nkey);
                                contentPane.setText(map.get("content"));
                                titleField.setText(map.get("title"));
                            } else if (uri.startsWith("http")) {
                                if (Desktop.isDesktopSupported()) {
                                    Desktop.getDesktop().browse(e.getURL().toURI());
                                }

                            }
                            /*else if (url.getProtocol().startsWith("resource:")) {
                            String[] href = url.getProtocol().split("://");
                            String nkey = href[1];
 
                            LinkedHashMap<String, String> map = ContextualHelp.getInstance().getHelpContent(nkey);
                            HTMLDocument doc = new HTMLDocument();
                         //   kit.read(new StringReader(map.get("content")), doc, 0);
                          //  contentPane.setDocument(doc);
                            contentPane.setText(map.get("content"));
                            titleField.setText(map.get("title"));
                        }*/

                        }
                    } catch (Exception e0) {
                        e0.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private URL getResourceAsURL(String qualifiedName) {
        return Thread.currentThread().getContextClassLoader().getResource(qualifiedName);
    }

    private String getTopicHTML(LinkedHashMap<String, String> map) {
        String html = "<html><head><title></title></head><body><div><ul>";
        Iterator<String> iter = map.keySet().iterator();
        String key, value;
        while (iter.hasNext()) {
            key = iter.next();
            value = map.get(key);
            if (!key.equals("title")) {
                html += "<li>" + value + "</li>";
            }
        }
        html += "</ul></div></body></html>";
        return html;
    }

    public StyleSheet loadStyleSheet() throws IOException {

        StyleSheet s = new StyleSheet();
        BufferedReader reader = new BufferedReader(new InputStreamReader(HTMLResource.class.getResourceAsStream("styles.css")));
        s.loadRules(reader, null);
        reader.close();

        return s;
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
        jButton1 = new javax.swing.JButton();
        titleField = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        contentPane = new javax.swing.JEditorPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 51, 102));

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Solr Security Manager Help");

        jButton1.setBackground(new java.awt.Color(0, 51, 153));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doOKAction(evt);
            }
        });

        titleField.setForeground(new java.awt.Color(0, 0, 0));

        contentPane.setContentType("text/html"); // NOI18N
        contentPane.setForeground(new java.awt.Color(0, 0, 0));
        jScrollPane2.setViewportView(contentPane);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(titleField)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE))
                        .addGap(0, 10, Short.MAX_VALUE)))
                .addGap(26, 26, 26))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addComponent(titleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
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
            java.util.logging.Logger.getLogger(ContextualHelpDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ContextualHelpDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ContextualHelpDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ContextualHelpDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ContextualHelpDialog dialog = new ContextualHelpDialog(new javax.swing.JFrame(), true, null);
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
    private javax.swing.JEditorPane contentPane;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField titleField;
    // End of variables declaration//GEN-END:variables
}
