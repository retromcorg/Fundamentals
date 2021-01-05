package com.johnymuffin.beta.fundamentals.settings;

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
    private JSONObject usernameUUIDCache;
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
                usernameUUIDCache = new JSONObject();
                file.write(usernameUUIDCache.toJSONString());
                file.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            plugin.debugLogger(Level.INFO, "Reading UUIDCache.json file", 1);
            JSONParser parser = new JSONParser();
            usernameUUIDCache = (JSONObject) parser.parse(new FileReader(cacheFile));
        } catch (ParseException e) {
            plugin.logger(Level.WARNING, "UUIDCache.json file is corrupt, resetting file: " + e + " : " + e.getMessage());
            usernameUUIDCache = new JSONObject();
        } catch (Exception e) {
            plugin.logger(Level.WARNING, "UUIDCache.json file is corrupt, changing to memory only mode.");
            memoryOnly = true;
            usernameUUIDCache = new JSONObject();
        }


    }

    public void addUser(String username, UUID uuid) {
        usernameUUIDCache.put(username.toLowerCase(), uuid.toString());
    }

    public UUID getUUIDFromUsername(String username) {
        username = username.toLowerCase();
        if (usernameUUIDCache.containsKey(username)) {
            return UUID.fromString(String.valueOf(usernameUUIDCache.get(username)));
        }
        return null;
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
            file.write(usernameUUIDCache.toJSONString());
            file.flush();
        } catch (IOException e) {
            plugin.logger(Level.WARNING, "Error saving UUIDCache.json: " + e + " : " + e.getMessage());
        }
    }

}
