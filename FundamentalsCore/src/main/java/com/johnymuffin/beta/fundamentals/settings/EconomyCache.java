package com.johnymuffin.beta.fundamentals.settings;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

    private int lowerLimit = 1; //The amount of money a player needs to have their economy information cached.

    public EconomyCache(Fundamentals plugin) {
        this.plugin = plugin;
        cacheFile = new File(plugin.getDataFolder() + File.separator + "cache" + File.separator + "economyCache.json");
        boolean isNew = false;
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
            isNew = true;
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

        //If EconomyCache is new loop thru all Fundamentals data files to save information
        if (isNew || memoryOnly) {
            plugin.debugLogger(Level.INFO, "Loading all player data to generate the economy cache. This might take awhile.", 1);
            if (memoryOnly) {
                plugin.debugLogger(Level.INFO, "Due to the economy cache being memory only this process will occur every start. Please rectify the issue if you want to prevent long loading times.", 1);
            }
            int players = 0;
            int totalPlayers = plugin.getPlayerMap().getKnownPlayers().size();
            long nextPrintStatus = (System.currentTimeMillis() / 1000) + 5;
            for (UUID uuid : plugin.getPlayerMap().getKnownPlayers()) {
                players++;
                //Skip players with unknown names because we don't want them in baltop
                //TODO: Create a better way for Baltop to exclude users without usernames in their datafile.
                if(plugin.getPlayerCache().getUsernameFromUUID(uuid) == null) {
                    continue;
                }
                saveRecord(uuid, plugin.getPlayerMap().getPlayer(uuid).getBalance());
                //Print progress so people don't think the server has hanged
                if ((System.currentTimeMillis() / 1000L) > nextPrintStatus) {
                    plugin.debugLogger(Level.INFO, players + "/" + totalPlayers + " loaded into the economy cache.", 1);
                    nextPrintStatus = (System.currentTimeMillis() / 1000) + 5;
                }
            }
            plugin.debugLogger(Level.INFO, "Loaded " + players + " players and saved their information into the economy cache.", 1);
        }

        //Run Cleanup
        economyCleanup();

    }

    public void saveRecord(UUID uuid, Double amount) {
        //Don't save records players with basically no money as it just clogs shit up.
        if (amount <= lowerLimit) {
            return;
        }
        economyCache.put(uuid.toString(), (int) Math.round(amount));
    }

    public TreeMap<Integer, UUID> getEconomyCache() {
        TreeMap<Integer, UUID> map = new TreeMap<>(Collections.reverseOrder());
        for (Iterator<Map.Entry> iterator = economyCache.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Integer> entry = iterator.next();
            map.put(entry.getValue(), UUID.fromString(entry.getKey()));
        }
        return map;
    }

    public int getPlayerBalance(UUID uuid) {
        return Integer.valueOf(String.valueOf( economyCache.getOrDefault(uuid.toString(), 0)));
    }

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

    public void economyCleanup() {
        int totalUsers = 0;
        int removed = 0;
        int converted = 0;

        for (Iterator<Map.Entry> iterator = economyCache.entrySet().iterator(); iterator.hasNext(); ) {
            totalUsers++;
            Map.Entry<String, Object> entry = iterator.next();
            //Remove if they are under the lowerLimit
            double balance = Double.valueOf(String.valueOf(entry.getValue()));
            if (balance <= lowerLimit) {
                removed++;
                iterator.remove();
                continue;
            }
            //Cast to an integer to ensure it is a nice value
            entry.setValue((int) Math.round(balance));
            converted++;
        }

        plugin.debugLogger(Level.INFO, "Economy Cache Cleanup iterated through " + totalUsers + " users and remove " +
                removed + " users as they didn't hit the economy limit of $" + lowerLimit + " and converted " + converted +
                " to ensure their values are in the correct format.", 2);

        saveJsonArray();
    }

}