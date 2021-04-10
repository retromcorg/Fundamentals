package com.johnymuffin.beta.fundamentals.settings;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.simplejson.JSONObject;
import com.johnymuffin.beta.fundamentals.simplejson.parser.JSONParser;
import com.johnymuffin.beta.fundamentals.simplejson.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class EconomyCache {
    private Fundamentals plugin;
    private JSONObject economyCache;
    private File cacheFile;
    private boolean memoryOnly = false;

    public EconomyCache(Fundamentals plugin) {
        this.plugin = plugin;
        cacheFile = new File(plugin.getDataFolder(), "economyCache.json");
        if (!cacheFile.exists()) {
            cacheFile.getParentFile().mkdirs();
            try {
                FileWriter file = new FileWriter(cacheFile);
                plugin.debugLogger(Level.INFO, "Generating economyCache.json file", 1);
                economyCache = new JSONObject();
                file.write(economyCache.toJSONString());
                file.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            plugin.debugLogger(Level.INFO, "Reading economyCache.json file", 1);
            JSONParser parser = new JSONParser();
            economyCache = (JSONObject) parser.parse(new FileReader(cacheFile));
        } catch (ParseException e) {
            plugin.logger(Level.WARNING, "economyCache.json file is corrupt, resetting file: " + e + " : " + e.getMessage());
            economyCache = new JSONObject();
        } catch (Exception e) {
            plugin.logger(Level.WARNING, "economyCache.json file is corrupt, changing to memory only mode.");
            memoryOnly = true;
            economyCache = new JSONObject();
        }


    }

    public void saveRecord(UUID uuid, Double amount) {
        economyCache.put(uuid.toString(), amount);
    }

//    public TreeMap<Integer, UUID> getEconomyCache() {
//        TreeMap<Integer, UUID> map = new TreeMap<>();
//        for (Object uuid : economyCache.keySet()) {
//            map.put((int) economyCache.get((UUID) uuid), (UUID) uuid);
//        }
////        map = map.
//
//    }

    public void saveData() {
        saveJsonArray();
    }

    private void saveJsonArray() {
        if (memoryOnly) {
            return;
        }
        try (FileWriter file = new FileWriter(cacheFile)) {
            plugin.debugLogger(Level.INFO, "Saving economyCache.json", 1);
            file.write(economyCache.toJSONString());
            file.flush();
        } catch (IOException e) {
            plugin.logger(Level.WARNING, "Error saving economyCache.json: " + e + " : " + e.getMessage());
        }
    }

}