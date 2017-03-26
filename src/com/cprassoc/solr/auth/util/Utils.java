/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.util;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author kevin
 */
public class Utils {

    public static String streamToString(InputStream in) {
        String result = "";
        try {
            StringWriter writer = new StringWriter();
            IOUtils.copy(in, writer, "UTF-8");
            result = writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
  

    public static String writeBytesToFile(String filePath, String content) {
        String path = "";
        try {
            File file = new File(filePath);
            FileWriter fw = new FileWriter(file);
            Log.log(Utils.class, "write file: "+filePath);
            fw.write(content);
          //  IOUtils.write(content, fw);
          //  IOUtils.write
            path = file.getAbsolutePath();
            fw.close();

        } catch (Exception e) {
               e.printStackTrace();
        }
        return path;
    }
    
    public static String mapKeysToString(Map map){
        Iterator<String> iter = map.keySet().iterator();
        String result = "";
        int row = 0;
        String key,value;
        Object obj;
        while(iter.hasNext()){
            key = iter.next();
            obj = map.get(key);
            if(obj instanceof String){
            value = (String)map.get(key);
            } else {
                value = obj.toString();
            }
            if(row > 0){
                result += ",";
            }
            result += key;
            row++;
        }
        return result;
    }
    
        public static String mapValuesToString(Map map){
        Iterator<String> iter = map.keySet().iterator();
        String result = "";
        int row = 0;
        String key;
        Object value;
        while(iter.hasNext()){
            key = iter.next();
            value =  map.get(key);
            if(row > 0){
                result += ",";
            }
            if(value instanceof String || value instanceof ArrayList){
                result += value.toString();
            } else if(value instanceof Map){
                result += mapValuesToString((Map)value);
            }
           // result += value;
            row++;
        }
        return result;
    }
    
    public static String mapKeysAndValuesToString(Map map){
          Iterator<String> iter = map.keySet().iterator();
        String result = "";
        int row = 0;
        String key,value;
        while(iter.hasNext()){
            key = iter.next();
            value = (String)map.get(key);
            if(row > 0){
                result += ",";
            }
            result += key+":"+value;
            row++;
        }
        return result;
    }
}
