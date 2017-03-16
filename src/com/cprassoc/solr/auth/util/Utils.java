/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.util;

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
}
