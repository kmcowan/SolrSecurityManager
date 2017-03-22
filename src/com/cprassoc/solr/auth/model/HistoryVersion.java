/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.model;

import com.cprassoc.solr.auth.util.Log;
import com.cprassoc.solr.auth.util.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

/**
 *
 * @author kevin
 */
public class HistoryVersion {

    private LinkedHashMap<String, SavedVersion> history = null;
    private final File historyDir = new File("history");

    public HistoryVersion() {
        init();
    }

    private void init() {
        history = new LinkedHashMap<>();
        try {
            if (!historyDir.exists()) {
                historyDir.mkdirs();
            } else {
                FilenameFilter fileNameFilter = new FilenameFilter() {

                    @Override
                    public boolean accept(File dir, String name) {
                        if (name.contains(".json")) {

                            return true;
                        }

                        return false;
                    }
                };
                File[] files = historyDir.listFiles(fileNameFilter);
                File file;
                String json;
                JSONObject jsonObj;
                for (int i = 0; i < files.length; i++) {
                    file = files[i];
                    json = Utils.streamToString(new FileInputStream(file));
                    jsonObj = new JSONObject(json);
                    String key = jsonObj.getString("key");
                    JSONObject data = new JSONObject(jsonObj.getString("data"));
                    String title = jsonObj.getString("title");
                    String desc = jsonObj.getString("description");
                    Date date = new Date(jsonObj.getString("date"));

                    SecurityJson secu = new SecurityJson(data);
                    SavedVersion version = new SavedVersion(title, desc, date, secu);
                    Log.log(getClass(), "Adding Version: "+version.getTitle());
                    this.history.put(key, version);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String saveVersion(String title, String description, SecurityJson json) {
        String uuid = UUID.randomUUID().toString();
        try {
            JSONObject obj = new JSONObject();
            obj.put("key", uuid);
            obj.put("title", title);
            obj.put("description", description);
            obj.put("data", json.export());
            obj.put("date", new Date().toString());

            File file = new File(historyDir.getAbsolutePath() + File.separator + uuid + ".json");
            FileUtils.writeByteArrayToFile(file, obj.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            uuid = null;
        }

        return uuid;
    }

    /**
     * @return the history
     */
    public LinkedHashMap<String, SavedVersion> getHistory() {
        return history;
    }
}
