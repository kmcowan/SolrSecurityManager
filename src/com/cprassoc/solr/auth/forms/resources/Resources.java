/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.forms.resources;

import java.awt.Image;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author kevin
 */
public class Resources {
    
    public static ImageIcon getResourceImage(String name){
        ImageIcon img = null;
        try{
         Image image = ImageIO.read(Resources.class.getResourceAsStream(name));
         img = new ImageIcon(image);
        }catch(Exception e){
            e.printStackTrace();
        }
        return img;
    }
    
    public static ImageIcon getNamedResourceIcon(Resource res){
        return getResourceImage(res.name()+".png");
    }
    
    public static enum Resource {
        info,
        add_document,
        arrow_right,
        cancel,
        delete_document,
        disk,
        disk_cd,
        group,
        help,
        home,
        permission_key,
        plus,
        save,
        user,
        warn,
        XC_security_icon_small
        
       
        
    }
}
