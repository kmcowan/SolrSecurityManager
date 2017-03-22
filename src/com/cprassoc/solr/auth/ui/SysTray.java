/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.ui;

import com.cprassoc.solr.auth.Frameable;
import com.cprassoc.solr.auth.forms.resources.Resources;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Timer;
import javax.swing.Icon;
import javax.swing.JFrame;

/**
 *
 * @author kevin
 */
public class SysTray extends JFrame implements Runnable {

    
    private static TrayIcon icon = null;
    private static Frameable view = null;
    private static Timer sysTimer = null;
    
    public SysTray(Frameable frame){
        this.view = frame;
        init();
    }
    
     private void init(){
        sysTimer = new Timer();
        this.setTitle("Solr Auth Manager");
        if (icon == null) {
                icon = new TrayIcon(SysTray.getImage(),
                        "Solr Auth Manager", SysTray.createPopupMenu());
            }
    }
     
     public void run(){
         init();
     }

    public static void displayMessage(String msg) {
        try {
            
            icon.displayMessage("Solr Auth Manager", msg, TrayIcon.MessageType.INFO);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static PopupMenu createPopupMenu() {

        PopupMenu menu = new PopupMenu();

        try {

            //EXIT
            MenuItem exit = new MenuItem("Exit");

            exit.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {

                    System.exit(0);

                }
            });
            menu.add(exit);

            // SHOW
            MenuItem show = new MenuItem("Show");
            show.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        if (getView() != null) {
                            getView().getFrame().setVisible(true);
                        }
                    } catch (Exception e0) {
                        e0.printStackTrace();
                    }

                }
            });

            if (view != null) {
                menu.add(show);
            }

            // STATS
            MenuItem stats = new MenuItem("Status");

            stats.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    String msg = "";
                    //   msg +=  "Status: "+ view.getFileMonitor().getMonitorState().name();
                    //   msg += " \n Hit Count: "+(FileMonitor.getHitCount());
                    //   msg += " \n Running For: "+view.getFileMonitor().getWatch().getFriendlyElapsedTime();

                    getIcon().displayMessage("Solr Auth Manager", msg, TrayIcon.MessageType.NONE);

                }
            });
            menu.add(stats);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return menu;

    }

    private static Image getImage() throws HeadlessException {

        Icon defaultIcon = Resources.getResourceImage("permission_key.png");

        Image img = new BufferedImage(defaultIcon.getIconWidth(),
                defaultIcon.getIconHeight(),
                BufferedImage.TYPE_4BYTE_ABGR);

        defaultIcon.paintIcon(new Panel(), img.getGraphics(), 0, 0);

        return img;

    }

    /**
     * @return the icon
     */
    public static TrayIcon getIcon() {
        return icon;
    }

    /**
     * @return the view
     */
    public static Frameable getView() {
        return view;
    }

    /**
     * @param aView the view to set
     */
    public static void setView(Frameable aView) {
        view = aView;
    }
}
