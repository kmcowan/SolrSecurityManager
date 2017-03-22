/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.model;

import java.util.Date;

/**
 *
 * @author kevin
 */
public class SavedVersion {
    
    private String title = "";
    private String description = "";
    private Date date = null;
    private SecurityJson seucrityJson = null;
    
    public SavedVersion(){}
    
    public SavedVersion(String title, String description, Date date, SecurityJson secu){
        this.title = title;
        this.description = description;
        this.date = date;
        this.seucrityJson = secu;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the seucrityJson
     */
    public SecurityJson getSeucrityJson() {
        return seucrityJson;
    }

    /**
     * @param seucrityJson the seucrityJson to set
     */
    public void setSeucrityJson(SecurityJson seucrityJson) {
        this.seucrityJson = seucrityJson;
    }
    
}
