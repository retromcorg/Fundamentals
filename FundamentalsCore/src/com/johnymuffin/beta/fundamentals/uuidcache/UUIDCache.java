package com.johnymuffin.beta.fundamentals.uuidcache;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.simplejson.JSONArray;
import com.johnymuffin.beta.fundamentals.simplejson.JSONObject;
import com.johnymuffin.beta.fundamentals.simplejson.parser.JSONParser;
import com.johnymuffin.beta.fundamentals.simplejson.parser.ParseException;

import java.io.*;
import java.util.UUID;
import java.util.logging.Level;

public class UUIDCache {
    private Fundamentals plugin;
    private JSONArray UUIDCacheArray;
    private File cacheFile;
    private boolean memoryOnly = false;

    public UUIDCache(Fundamentals plugin) {
        this.plugin = plugin;
        cacheFile = new File(plugin.getDataFolder(), "UUIDCache.json");
        if (!cacheFile.exists()) {
            cacheFile.getParentFile().mkdirs();
            try {
                FileWriter file = new FileWriter(cacheFile);
                plugin.debugLogger(Level.INFO, "Generating UUIDCache.json file", 1);
                UUIDCacheArray = new JSONArray();
                file.write(UUIDCacheArray.toJSONString());
                file.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            plugin.debugLogger(Level.INFO, "Reading UUIDCache.json file", 1);
            JSONParser parser = new JSONParser();
            UUIDCacheArray = (JSONArray) parser.parse(new FileReader(cacheFile));
        } catch (ParseException e) {
            plugin.logger(Level.WARNING, "UUIDCache.json file is corrupt, resetting file: " + e + " : " + e.getMessage());
            UUIDCacheArray = new JSONArray();
        } catch (Exception e) {
            plugin.logger(Level.WARNING, "UUIDCache.json file is corrupt, changing to memory only mode.");
            memoryOnly = true;
            UUIDCacheArray = new JSONArray();
        }


    }

    public void addUser(String username, UUID uuid) {
        username = username.toLowerCase();
        //Remove preexisting cache for username
        removeInstanceOfUsername(username);
        JSONObject tmp = new JSONObject();
        tmp.put("username", username);
        tmp.put("uuid", uuid.toString());
        UUIDCacheArray.add(tmp);
    }

    public UUID getUUIDFromUsername(String username) {
        for (int i = 0; i < UUIDCacheArray.size(); i++) {
            JSONObject tmp = (JSONObject) UUIDCacheArray.get(i);
            if (((String) tmp.get("username")).equalsIgnoreCase(username)) {
                return UUID.fromString(String.valueOf(tmp.get("uuid")));
            }
        }
        return null;
    }


    private void removeInstanceOfUsername(String username) {
        boolean stillRemoving = true;
        while (stillRemoving) {
            boolean removedEntry = false;
            for (int i = 0; i < UUIDCacheArray.size(); i++) {
                JSONObject tmp = (JSONObject) UUIDCacheArray.get(i);
                if (((String) tmp.get("username")).equalsIgnoreCase(username)) {
                    UUIDCacheArray.remove(i);
                    removedEntry = true;
                    break;
                }
            }
            if (!removedEntry) {
                stillRemoving = false;
            }
        }
    }


    public void saveData() {
        saveJsonArray();
    }

    private void saveJsonArray() {
        if (memoryOnly) {
            return;
        }
        try (FileWriter file = new FileWriter(cacheFile)) {
            plugin.debugLogger(Level.INFO, "Saving UUIDCache.json", 1);
            file.write(UUIDCacheArray.toJSONString());
            file.flush();
        } catch (IOException e) {
            plugin.logger(Level.WARNING, "Error saving UUIDCache.json: " + e + " : " + e.getMessage());
        }
    }


}
