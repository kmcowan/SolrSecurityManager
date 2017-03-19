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
}
