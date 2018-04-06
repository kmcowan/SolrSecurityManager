/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.util;

import com.cprassoc.solr.auth.web.handlers.model.Handler;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author kevin
 */
public class Utils {

    private static final String OS = System.getProperty("os.name").toLowerCase();

    public static byte[] streamToBytes(InputStream in) {
        byte result[] = new byte[0];
        try {
            result = new byte[in.available()];
            in.read(result);
        } catch (Exception e) {
           // e.printStackTrace();
        }
        return result;
    }
    
    public static String streamToString(InputStream in) {
        String result = "";
        try {
            StringWriter writer = new StringWriter();
            IOUtils.copy(in, writer, "UTF-8");
            result = writer.toString();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return result;
    }

    public static String writeBytesToFile(String filePath, String content) {
        String path = "";
        try {
            File file = new File(filePath);
            FileWriter fw = new FileWriter(file);
            Log.log(Utils.class, "write file: " + filePath);
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

    public static String mapKeysToString(Map map) {
        Iterator<String> iter = map.keySet().iterator();
        String result = "";
        int row = 0;
        String key, value;
        Object obj;
        while (iter.hasNext()) {
            key = iter.next();
            obj = map.get(key);
            if (obj instanceof String) {
                value = (String) map.get(key);
            } else {
                value = obj.toString();
            }
            if (row > 0) {
                result += ",";
            }
            result += key;
            row++;
        }
        return result;
    }

    public static String mapValuesToString(Map map) {
        Iterator<String> iter = map.keySet().iterator();
        String result = "";
        int row = 0;
        String key;
        Object value;
        while (iter.hasNext()) {
            key = iter.next();
            value = map.get(key);
            if (row > 0) {
                result += ",";
            }
            if (value instanceof String || value instanceof ArrayList) {
                result += value.toString();
            } else if (value instanceof Map) {
                result += mapValuesToString((Map) value);
            }
            // result += value;
            row++;
        }
        return result;
    }

    public static ArrayList<String> mapValuesToList(Map map) {
        Iterator<String> iter = map.keySet().iterator();
        ArrayList<String> result = new ArrayList<>();

        int row = 0;
        String key;
        Object value;
        while (iter.hasNext()) {
            key = iter.next();
            value = map.get(key);

            if (value instanceof String || value instanceof ArrayList) {
                result.add(value.toString());
            } else if (value instanceof Map) {
                result.add(mapValuesToString((Map) value));
            }
            // result += value;
            row++;
        }
        return result;
    }

    public static String mapKeysAndValuesToString(Map map) {
        Iterator<String> iter = map.keySet().iterator();
        String result = "";
        int row = 0;
        String key, value;
        while (iter.hasNext()) {
            key = iter.next();
            value = (String) map.get(key);
            if (row > 0) {
                result += ",";
            }
            result += key + ":" + value;
            row++;
        }
        return result;
    }

    public static boolean isWindows() {

        return (OS.contains("win"));

    }

    public static boolean isMac() {

        return (OS.contains("mac"));

    }

    public static boolean isUnix() {

        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));

    }

    public static boolean isSolaris() {

        return (OS.contains("sunos"));

    }
    
    public  static boolean isHTML(byte[] bytes){
        if(new String(bytes).toLowerCase().contains("<html>")){
            return true;
        }
        return false;
    }
    
    public static Handler getHandler(String className){
        Handler handler = null;
        try{
             // Create a new JavaClassLoader

            ClassLoader classLoader = Utils.class.getClassLoader();

             

            // Load the target class using its binary name

            Class loadedMyClass = classLoader.loadClass(className);

             

            System.out.println("Loaded class name: " + loadedMyClass.getName());

             

            // Create a new instance from the loaded class

            Constructor constructor = loadedMyClass.getConstructor();

            Object myClassObject = constructor.newInstance();
            handler = (Handler)myClassObject;

         
        }catch(Exception e){
            e.printStackTrace();
        }
        return handler;
    }
}
