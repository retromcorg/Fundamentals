//package com.johnymuffin.beta.fundamentals.settings;
//
//import com.johnymuffin.beta.fundamentals.Fundamentals;
//import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
//import com.johnymuffin.beta.fundamentals.simplejson.JSONObject;
//import com.johnymuffin.beta.fundamentals.simplejson.parser.JSONParser;
//import com.johnymuffin.beta.fundamentals.simplejson.parser.ParseException;
//
//import java.io.File;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.UUID;
//import java.util.logging.Level;
//
//public class UUIDCache {
//    private Fundamentals plugin;
//    private JSONObject usernameUUIDCache;
//    private File cacheFile;
//    private boolean memoryOnly = false;
//    private HashMap<UUID, String> uuidMap = new HashMap<>();
//
//    public UUIDCache(Fundamentals plugin) {
//        this.plugin = plugin;
//        cacheFile = new File(plugin.getDataFolder(), "UUIDCache.json");
//        boolean isNew = false;
//        if (!cacheFile.exists()) {
//            cacheFile.getParentFile().mkdirs();
//            try {
//                FileWriter file = new FileWriter(cacheFile);
//                plugin.debugLogger(Level.INFO, "Generating UUIDCache.json file", 1);
//                usernameUUIDCache = new JSONObject();
//                file.write(usernameUUIDCache.toJSONString());
//                file.flush();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            isNew = true;
//        }
//
//        try {
//            plugin.debugLogger(Level.INFO, "Reading UUIDCache.json file", 1);
//            JSONParser parser = new JSONParser();
//            usernameUUIDCache = (JSONObject) parser.parse(new FileReader(cacheFile));
//        } catch (ParseException e) {
//            plugin.logger(Level.WARNING, "UUIDCache.json file is corrupt, resetting file: " + e + " : " + e.getMessage());
//            usernameUUIDCache = new JSONObject();
//        } catch (Exception e) {
//            plugin.logger(Level.WARNING, "UUIDCache.json file is corrupt, changing to memory only mode.");
//            memoryOnly = true;
//            usernameUUIDCache = new JSONObject();
//        }
//
//        //If UUIDCache is new loop thru all Fundamentals data files to save information
//        if (isNew || memoryOnly) {
//            plugin.debugLogger(Level.INFO, "Loading all player data to generate the uuid cache. This might take awhile.", 1);
//            if (memoryOnly) {
//                plugin.debugLogger(Level.INFO, "Due to the economy cache being memory only this process will occur every start. Please rectify the issue if you want to prevent long loading times.", 1);
//            }
//            int players = 0;
//            int totalPlayers = plugin.getPlayerMap().getKnownPlayers().size();
//            int namedPlayers = 0;
//            long nextPrintStatus = (System.currentTimeMillis() / 1000) + 5;
//            for (UUID uuid : plugin.getPlayerMap().getKnownPlayers()) {
//                FundamentalsPlayer player = plugin.getPlayerMap().getPlayer(uuid);
//                if (player.getInformation("username") != null) {
//                    addUser(String.valueOf(player.getInformation("username")), uuid);
//                    namedPlayers++;
//                }
//                players++;
//                //Print progress so people don't think the server has hanged
//                if ((System.currentTimeMillis() / 1000L) > nextPrintStatus) {
//                    plugin.debugLogger(Level.INFO, players + "/" + totalPlayers + " loaded into the UUIDCache.", 1);
//                    nextPrintStatus = (System.currentTimeMillis() / 1000) + 5;
//                }
//            }
//            plugin.debugLogger(Level.INFO, "Loaded " + namedPlayers + " players names into the cache out of " + totalPlayers +
//                    ". " + (totalPlayers - namedPlayers) + " couldn't be loaded as their player data file didn't contain their username.", 1);
//        }
//        saveData();
//
//        for (Object usernameString : usernameUUIDCache.keySet()) {
//            uuidMap.put(UUID.fromString((String) usernameUUIDCache.get(usernameString)), (String) usernameString);
//        }
//        plugin.debugLogger(Level.INFO, "Generating UUID to Username memory map from UUIDCache.", 2);
//
//
//    }
//
//    public void addUser(String username, UUID uuid) {
//        usernameUUIDCache.put(username.toLowerCase(), uuid.toString());
//        uuidMap.put(uuid, username);
//    }
//
//    public UUID getUUIDFromUsername(String username) {
//        username = username.toLowerCase();
//        if (usernameUUIDCache.containsKey(username)) {
//            return UUID.fromString(String.valueOf(usernameUUIDCache.get(username)));
//        }
//        return null;
//    }
//
//    public String getUsernameFromUUID(UUID uuid) {
//        return uuidMap.get(uuid);
//    }
//
//
//    public void saveData() {
//        saveJsonArray();
//    }
//
//    private void saveJsonArray() {
//        if (memoryOnly) {
//            return;
//        }
//        try (FileWriter file = new FileWriter(cacheFile)) {
//            plugin.debugLogger(Level.INFO, "Saving UUIDCache.json", 1);
//            file.write(usernameUUIDCache.toJSONString());
//            file.flush();
//        } catch (IOException e) {
//            plugin.logger(Level.WARNING, "Error saving UUIDCache.json: " + e + " : " + e.getMessage());
//        }
//    }
//
//}
