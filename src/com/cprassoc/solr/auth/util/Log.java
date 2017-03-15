/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.util;

/**
 *
 * @author kevin
 */
public class Log {
    
    private static void logMessage(String message){
        System.out.println(message);
    }
    
    public static void log(String message){
       logMessage(message);
    }
    
    public static void log(Class clsName, String message){
       logMessage("["+clsName.getSimpleName()+"] "+message);
    }
}
