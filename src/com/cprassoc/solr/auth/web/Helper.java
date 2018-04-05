package com.cprassoc.solr.auth.web;

import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;

import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
A generalized utility class that provides easy to operations

 */

public class Helper extends DataBean {

    public Helper() {
        super();
    }
    /*
     *         solrHostPort("solr.host.port"),
        solrInstallPath("solr.install.path"),
        isUsingSSL("solr.ssl.enabled"),
        zookeeperPort("solr.zookeeper.port"),
        defaultCollection("solr.default.collection"),
        solrAdminUser("solr.admin.user"),
        solrAdminPwd("solr.admin.pwd");
     */

    public static DataBean getDataBeanFromProperties(Properties props){
        DataBean result = new DataBean();
        
        if( props != null ){
            Enumeration propList = props.keys();
            
            while( propList.hasMoreElements() ){
                String entry = propList.nextElement().toString();
                String newEntry = entry.replaceAll("\\.","");
                
                result.setValue(newEntry,props.get(entry));
            }
        }
        
        return( result );
    }
    
    public static void concatLists(ArrayList<DataBean> dest, ArrayList<DataBean> src) {
        //for(int i = 0,size = src.size();i < size;i++)
        //     dest.add(src.get(i));

        dest.addAll(src);
    }

    private static Logger logger = LoggerFactory.getLogger(Helper.class);

    public static boolean DEBUG = true;

    public static void writeLog(int level, String message) {
        if (DEBUG) {
            if (message == null) {
                message = "null message";
            }

            //System.out.println(message);
            if( level == 1 ){
            	logger.info(message);
            }
            else if( level == 2 ){
            	logger.debug(message);
            }
            else
            	logger.info(message);
            
            if (false) {
                try {
                    //java.io.FileOutputStream errorLog = new java.io.FileOutputStream("C:\\temp\\rest.txt",true);
                    //java.io.FileOutputStream errorLog = new java.io.FileOutputStream("/tmp/rest.txt",true);
                    java.io.FileOutputStream errorLog =
                        new java.io.FileOutputStream(File.pathSeparator.equalsIgnoreCase(";") ? "\\temp\\rest.txt" :
                                                     "/tmp/rest.txt", true);

                    java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.OutputStreamWriter(errorLog));

                    bw.write(message, 0, message.length());
                    bw.write("\n");
                    bw.close();

                    errorLog.close();
                } catch (java.io.IOException e) {
                    System.out.println(e.toString());
                }
            }
        }
    }


    public static int parseInt(String v) {
        int result = -1;

        try {
            result = Integer.parseInt(v);
        } catch (Exception e) {

        }
        return (result);
    }
    
    public static short parseShort(String v) {
        short result = -1;

        try {
            result = Short.parseShort(v);
        } catch (Exception e) {

        }
        return (result);
    }

    //sh 08-16-1017    No Item   Trim elements parsed from input string.
    public static ArrayList<String> splitFields(String sSrc, String sDelim) {
        ArrayList<String> retval = new ArrayList<String>();
        int nd = sDelim.length();
        int ns = sSrc.length();
        int i = 0;
        int j = 0;
        boolean bf = false;
        char q = '\"';

        if (sDelim.length() != 0 && ns != 0) {
            while (j + nd <= ns) {
                if (sSrc.charAt(j) == q) {
                    bf = !bf;

                }
                if (sSrc.substring(j, j + nd).equals(sDelim) && !bf) {
                    retval.add(sSrc.substring(i, j));
                    j += nd;
                    i = j;
                } else {
                    j++;
                }
            }
            retval.add(sSrc.substring(i).trim());
        }

        return retval;
    }
  //sh 08-16-1017    No Item   Trim elements parsed from input string.  This affects SQLHelper.IN clauses
    public static ArrayList<String> simpleSplitFields(String sSrc, String sDelim) {
        ArrayList<String> retval = new ArrayList<String>();
        int nd = sDelim.length();
        int ns = sSrc.length();
        int i = 0;
        int j = 0;
        boolean bf = false;
        char q = '\"';

        if (sDelim.length() != 0) {
            while (j + nd <= ns) {
                if (sSrc.substring(j, j + nd).equals(sDelim)) {
                    retval.add(sSrc.substring(i, j));
                    j += nd;
                    i = j;
                } else {
                    j++;
                }
            }
            retval.add(sSrc.substring(i).trim());
        }

        return retval;
    }

    public static DataBean readFromRESTURL(String url) {
        String json = readFromURL(url);


        return (parseJSON(json));
    }

    public static String readFromURL(String url) {
        StringBuffer result = new StringBuffer();
        try {
            URL u = new URL(url);
            InputStream in = u.openStream();
            InputStreamReader uin = new InputStreamReader(in);
            BufferedReader fin = new BufferedReader(uin);

            String buffer;
            while ((buffer = fin.readLine()) != null) {
                result.append(buffer);

            }

            uin.close();
            in.close();
        } catch (IOException e) {
            result.append("Error: " + e.toString() + " " + e.getMessage());
        }
        return (result.toString());
    }


    public static ArrayList<DataBean> cloneList(ArrayList<DataBean> src) {
        ArrayList<DataBean> result = new ArrayList<DataBean>(src.size());

        for (int i = 0, size = src.size(); i < size; i++)
            result.add(src.get(i));

        return (result);
    }
    public static void readAllParametersFromURI(DataBean result,URI requestedUri) {
       readAllParametersFromURI(result,requestedUri.getRawQuery());
    }
    
    public static void readAllParametersFromURI(DataBean result,String query) {
        try {
            if( query != null ){
                 String pairs[] = query.split("[&]");
        
                 for (String pair : pairs) {
                     String param[] = pair.split("[=]");
        
                     String key = null;
                     String value = null;
                     if (param.length > 0) {
                         key = URLDecoder.decode(param[0],
                             System.getProperty("file.encoding"));
                     }
        
                     if (param.length > 1) {
                         value = URLDecoder.decode(param[1],System.getProperty("file.encoding"));
                     }
        
                     if (result.isValid(key)) {
                         Object obj = result.getValue(key);
                         result.addToCollection(key,obj);
                         result.removeValue(key);
                         result.addToCollection(key,value);
                     } else {
                         result.setValue(key, value);
                     }
                 }
            }
        }
        catch(Exception e){
            result.setValue("_error",e.toString());
        }
    }

    public static DataBean readAllParameters(HttpExchange ex) {
        DataBean result = new DataBean();
        try {
            URI requestedUri = ex.getRequestURI();
            readAllParametersFromURI(result,requestedUri);
        }
        catch(Exception e){
            result.setValue("_error",e.toString());
        }

        return (result);
    }
    
    public static DataBean readAnyParameters(HttpExchange ex) {
        DataBean result = readAllJSONParameters(ex);

        try {
            URI requestedUri = ex.getRequestURI();
            readAllParametersFromURI(result,requestedUri);
        }
        catch(Exception e){
            result.setValue("_error",e.toString());
        }

        return (result);
    }
    
    public static DataBean readPostParameters(HttpExchange ex) {
        DataBean result = new DataBean();
        
        try {
            /*String line;
            InputStream is = ex.getRequestBody();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            StringBuffer buffer = new StringBuffer();
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }*/
            InputStreamReader isr = new InputStreamReader(ex.getRequestBody(),"utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query;
            while( (query = br.readLine()) != null ){
                readAllParametersFromURI(result,query);
            }
            
            /*InputStream in = ex.getRequestBody();
            ByteArrayOutputStream _out = new ByteArrayOutputStream();
            byte[] buf = new byte[2048];
            int read = 0;
            
            while ((read = in.read(buf)) != -1) {
                _out.write(buf, 0, read);
            }
            String query = _out.toString();
            readAllParametersFromURI(result,query);*/
            

            URI requestedUri = ex.getRequestURI();
            readAllParametersFromURI(result,requestedUri);
        }
        catch(Exception e){
            result.setValue("_error",e.toString());
        }

        return (result);
    }

    public static String readPut(HttpExchange ex) {
        StringBuffer result = new StringBuffer();

        try {
            InputStream in = ex.getRequestBody();
            DataInputStream din = new DataInputStream(in);
            int dSize = in.available();
            int size;
            int blockSize = 1024;

            byte buffer[] = new byte[blockSize];


            int i;
            for (;;) {
                size = din.read(buffer, 0, blockSize);

                for (i = 0; i < size; i++) {
                    result.append((char) buffer[i]);

                }
                if (size < 0) {
                    break;
                }
            }
        } catch (Exception e) {
            writeLog(1, "Read Error: " + e.toString());

        }

        return (result.toString());
    }

    public static DataBean parseJSON(String json) {
        DataBean result = new DataBean();

        try {
            JSONObject obj = new JSONObject(json);
            result = parseJSON(obj);
        } catch (Exception e) {
            result.setValue("error", "parse error: " + e.toString());
            //writeLog(1, result.getString("error"));
        }

        return (result);
    }


    public static DataBean parseJSON(JSONObject json) throws Exception {

        DataBean result = new DataBean();
        //writeLog(1,"internal parse:");

        String fNames[] = JSONObject.getNames(json);
        for (int i = 0; i < fNames.length; i++) {
            Object currentObject = ((JSONObject) json).get(fNames[i]);
            //writeLog(1,"fcolumn: " + fNames[i] + " instance of: " + currentObject.getClass().getName());

            if (currentObject instanceof String || currentObject instanceof JSONString ||
                currentObject instanceof Integer || currentObject instanceof Float || currentObject instanceof Double ||
                currentObject instanceof Boolean) {
                result.setValue(fNames[i], currentObject.toString());
                //writeLog(1,"column: " + fNames[i] + " value: " + result.getString(fNames[i]));
            } else if (currentObject instanceof JSONArray) {

                JSONArray currentArray = (JSONArray) currentObject;
                for (int j = 0, jsize = currentArray.length(); j < jsize; j++) {
                    if (currentArray.get(j) instanceof String || currentArray.get(j) instanceof JSONString) {
                        result.addToCollection(fNames[i], currentArray.get(j).toString());
                        //writeLog(1,"column: " + fNames[i] + " value: " + result.getString(fNames[i]));
                    } else
                        result.addToCollection(fNames[i], parseJSON((JSONObject) currentArray.get(j)));
                }
            } else if (currentObject instanceof JSONObject) {
                result.setStructure(fNames[i], parseJSON((JSONObject) currentObject));
            }

        }
        
        return (result);
    }

    public static ArrayList parseJsonArray(String json){
        DataBean tempStructure = parseJSON("{\"list\":" + json + "}");
        
        ArrayList result = tempStructure.getCollection("list");
        
        return( result );
    }
    
    public static DataBean readAllJSONParameters(HttpExchange ex) {
        String jsonData = Helper.readPut(ex);
        writeLog(1, "read all json data: " + jsonData.getClass().getName() + " json: " + jsonData);
        if (jsonData.getClass().getName().endsWith("String"))
            return (parseJSON(jsonData));
        else
            return (parseJSON(jsonData));
    }

    public static DataBean readJSONFromString(String jsonData) {
        DataBean result = new DataBean();


        try {
            writeLog(1, "parseing json");
            JSONObject obj = new JSONObject(jsonData);
            String fNames[] = obj.getNames(obj);

            for (int i = 0; i < fNames.length; i++) {
                result.setValue(fNames[i], (String) obj.get(fNames[i]));
            }
        } catch (Exception e) {
            writeLog(2, "json parse failed: " + e.toString());
        }
        writeLog(1, "after parsing json: " + result.toString());
        return (result);
    }

    public static int indexOf(ArrayList<DataBean> list, String column, String value) {
        int result = -1;

        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).getString(column).equalsIgnoreCase(value)) {
                result = i;

                break;
            }

        }

        return (result);
    }


    public static int indexOf(ArrayList<DataBean> list, String column1, String value1, String column2, String value2) {
        int result = -1;

        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).getString(column1).equalsIgnoreCase(value1) &&
                list.get(i).getString(column2).equalsIgnoreCase(value2)) {
                result = i;

                break;
            }

        }

        return (result);
    }


    public static DataBean getParameters(HttpServletRequest req) {
        DataBean result = new DataBean();

        Enumeration parameterNames = req.getParameterNames();

        if (parameterNames != null) {
            String tStr;
            String vStr;
            while (parameterNames.hasMoreElements()) {
                tStr = parameterNames.nextElement().toString();
                vStr = req.getParameter(tStr);

                if (vStr != null) {
                    result.setValue(tStr, vStr);
                }
            }
        }

        return (result);
    }

    public static ArrayList getParameterList(HttpServletRequest req) {
        ArrayList result = new ArrayList();

        Enumeration parameterNames = req.getParameterNames();

        if (parameterNames != null) {
            String tStr;
            String vStr;
            while (parameterNames.hasMoreElements()) {
                tStr = parameterNames.nextElement().toString();
                vStr = req.getParameter(tStr);

                if (vStr != null) {
                    DataBean tmp = new DataBean();
                    tmp.setValue("name", tStr);
                    tmp.setValue("value", vStr);

                    result.add(tmp);
                }
            }
        }

        return (result);
    }

    public static ArrayList getColumnsFromEnumeration(Iterator columns) {
        ArrayList result = new ArrayList();

        String tKey;

        while (columns.hasNext()) {

            tKey = columns.next().toString();
            result.add(tKey);

        }


        return (result);
    }
    
    public static String toJSON(DataBean target) {
        
            StringBuilder result = new StringBuilder("{");
            Iterator columNameList = target.getColumnNames();

            String tKey;
            boolean hasColumns = false;
            if (columNameList != null) {
                int k = 0;
                while (columNameList.hasNext()) {
                    tKey = columNameList.next().toString();

                    if (k++ > 0)
                        result.append(",");
                    result.append("\"");
                    result.append(tKey);
                    result.append("\":\"");
                    result.append(StringEscapeUtils.escapeJson(target.getString(tKey)));
                    result.append("\"");
                    if (!hasColumns)
                        hasColumns = true;
                }

            }

            if (target.getCollections() != null) {
                Iterator cKeys = target.getCollections().keySet().iterator();
                String cName;

                if (hasColumns) {
                    result.append(",");
                }

                if (!hasColumns)
                    hasColumns = true;
                

                ArrayList subList;
                DataBean subEntry;
                int counter = 0;
                while (cKeys.hasNext()) {
                    cName = cKeys.next().toString();

                    subList = target.getCollection(cName);
                    
                    if( subList != null ){
                        if( counter++ > 0 ){
                            result.append(",");
                        }
                        result.append("\"");
                        result.append(cName);
                        result.append("\":[");
    
                        for (int j = 0; j < subList.size(); j++) {
                            if (j > 0) {
                                result.append(",");
                            }
//                            writeLog(1,"name : " + cName + " class: " + subList.get(j).getClass().getName());
//                            writeLog(1,"item: " + subList.get(j).toString());
                            if (subList.get(j) instanceof DataBean ) {
                                subEntry = (DataBean) subList.get(j);
    
                                result.append(toJSON(subEntry));
                            } else {
                                result.append("\"");
                                result.append(subList.get(j).toString());
                                result.append("\"");
                            }
                        }
    
                        result.append("]");
                    }

                }
            }


            if (target.getStructures() != null) {
                Iterator cKeys = target.getStructures().keySet().iterator();
                String cName;


                DataBean subEntry;

                while (cKeys.hasNext()) {
                    cName = cKeys.next().toString();

                    subEntry = target.getStructure(cName);
                    
                    if (hasColumns) {
                        result.append(",");
                    }
                    else {
                        hasColumns = true;
                    }
                    result.append("\"");
                    result.append(cName);
                    result.append("\":");
                    result.append(toJSON(subEntry));
                }
            }

            result.append("}");

            return (result.toString());
        }
        
        public static String toJsonArray(ArrayList list) {
            StringBuilder result = new StringBuilder("[");

            if (list != null) {
                
                for(int i = 0,size = list.size();i < size;i++){
                    if (i > 0) {
                        result.append(",");
                    }

                    if (list.get(i) instanceof DataBean) {
                        DataBean subEntry = (DataBean) list.get(i);

                        result.append(toJSON(subEntry));
                    } else {
                        result.append("\"");
                        result.append(StringEscapeUtils.escapeJson(list.get(i).toString()));
                        result.append("\"");
                    }
                }
                
                
            }


            

            result.append("]");

            return (result.toString());
        }


    public static void main(String[] args) {

        System.exit(0);

    }
    
    public static String getCookie(HttpServletRequest request, String cookieName) {
        String result = null;

        try {
            //Cookie[] cookieList = (Cookie[])getPortletRequest().getCookies();
            Cookie[] cookieList = request.getCookies();

            if (cookieList != null) {
                for (int i = cookieList.length - 1; i >= 0; i--) {
                    writeLog(1,
                             "cookie name: " + cookieList[i].getName() + " " + cookieList[i].getPath() + " max age: " +
                             cookieList[i].getMaxAge() + " domain: " + cookieList[i].getDomain() + " path: " +
                             cookieList[i].getPath() + " value: " + cookieList[i].getValue());

                    if (cookieList[i].getName().equalsIgnoreCase(cookieName)) {
                        result = cookieList[i].getValue();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            writeLog(1, "get cookie error : " + e.toString());
        }

        return (result);
    }

    public static void setCookie(DataBean args, HttpServletResponse response, String cookieName, String value) {
        setCookie(args, response, cookieName, value, args.getString("expires", "600000"));
    }

    public static void setCookie(DataBean args, HttpServletResponse response, String cookieName, String value,
                                 String maxAge) {
        Cookie cookie = new Cookie(cookieName, value);
        cookie.setMaxAge(Integer.parseInt(maxAge));
        cookie.setPath(args.getString("path", "/"));
        response.addCookie(cookie);
    }
    
    public static String writeTextFile(String file,String text) {

		String result = null;
		
		BufferedWriter bw = null;
		FileWriter fw = null;

		try {

			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write(text);
		} catch (IOException e) {
			result = e.toString();
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				result = ex.toString();
				ex.printStackTrace();
			}
		}

		return( result );
	}
    
    public static long parseLong(String longStr){
    	long result = -1;

        try {
            result = Long.parseLong(longStr);
        } catch (Exception e) {

        }
        
        return (result);
    }
    
   
    public static String ISOTIMESTAMPFORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    
    
    public static String formatDate(Date value,String outFormat){
    	String result = value.toString();
    	
        try{
        	java.text.SimpleDateFormat out = new java.text.SimpleDateFormat(outFormat);
            
        	result = out.format(value);
        }
        catch(Exception e){
            writeLog(1,"date parse error: " + e.toString() + " " + value + " output format: " + outFormat);
        }
    	
    	return( result );
    }

}
/*end DataStructure*/
