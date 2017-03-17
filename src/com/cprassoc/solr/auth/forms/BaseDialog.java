/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.forms;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import javax.swing.JDialog;

/**
 *
 * @author kevin
 */
public abstract class BaseDialog extends JDialog {

    public BaseDialog(Frame frm, boolean modal){
        super(frm, modal);
    }
    protected void center() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
        this.setLocation(x, y);
    }
}
