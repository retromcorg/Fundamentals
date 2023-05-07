package com.johnymuffin.beta.fundamentals.settings;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerCache {
    private Fundamentals plugin;
    private JSONObject playerCacheJSON;
    private File cacheFile;
    private boolean memoryOnly = false;
    private HashMap<UUID, String> uuidToUsernameMap = new HashMap<>();
    private HashMap<String, UUID> usernameToUUIDMap = new HashMap<>();

    public PlayerCache(Fundamentals plugin) {
        this.plugin = plugin;
        cacheFile = new File(plugin.getDataFolder() + File.separator + "cache" + File.separator + "playerCache.json");
        boolean isNew = false;
        if (!cacheFile.exists()) {
            cacheFile.getParentFile().mkdirs();
            try {
                FileWriter file = new FileWriter(cacheFile);
                plugin.debugLogger(Level.INFO, "Generating playerCache.json file", 1);
                playerCacheJSON = new JSONObject();
                file.write(playerCacheJSON.toJSONString());
                file.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            isNew = true;
        }

        try {
            plugin.debugLogger(Level.INFO, "Reading playerCache.json file", 1);
            JSONParser parser = new JSONParser();
            playerCacheJSON = (JSONObject) parser.parse(new FileReader(cacheFile));
        } catch (ParseException e) {
            plugin.logger(Level.WARNING, "playerCache.json file is corrupt, resetting file: " + e + " : " + e.getMessage());
            playerCacheJSON = new JSONObject();
        } catch (Exception e) {
            plugin.logger(Level.WARNING, "playerCache.json file is corrupt, changing to memory only mode.");
            memoryOnly = true;
            playerCacheJSON = new JSONObject();
        }

        //If UUIDCache is new loop thru all Fundamentals data files to save information
        if (isNew || memoryOnly) {
            plugin.debugLogger(Level.INFO, "Loading all player data to generate the PlayerCache.json. This might take awhile.", 1);
            if (memoryOnly) {
                plugin.debugLogger(Level.INFO, "Due to the economy cache being memory only this process will occur every start. Please rectify the issue if you want to prevent long loading times.", 1);
            }
            int players = 0;
            int totalPlayers = plugin.getPlayerMap().getKnownPlayers().size();
            int namedPlayers = 0;
            long nextPrintStatus = (System.currentTimeMillis() / 1000) + 5;
            for (UUID uuid : plugin.getPlayerMap().getKnownPlayers()) {
                FundamentalsPlayer player = plugin.getPlayerMap().getPlayer(uuid);
                if (player.getInformation("username") != null) {
                    String prefix = this.plugin.getPermissionsHook().getMainUserPrefix(uuid);
                    if (prefix == null) {
                        updatePlayerProfile(String.valueOf(player.getInformation("username")), uuid);
                    } else {
                        updatePlayerProfile(String.valueOf(player.getInformation("username")), prefix, uuid);
                    }
                    namedPlayers++;
                }
                players++;
                //Print progress so people don't think the server has hanged
                if ((System.currentTimeMillis() / 1000L) > nextPrintStatus) {
                    plugin.debugLogger(Level.INFO, players + "/" + totalPlayers + " loaded into the PlayerCache.", 1);
                    nextPrintStatus = (System.currentTimeMillis() / 1000) + 5;
                }
            }
            plugin.debugLogger(Level.INFO, "Loaded " + namedPlayers + " players names into the cache out of " + totalPlayers +
                    ". " + (totalPlayers - namedPlayers) + " couldn't be loaded as their player data file didn't contain their username.", 1);
        }
        saveData();

        //Generate in memory maps
        plugin.debugLogger(Level.INFO, "Generating in memory HashMaps for PlayerCache.", 2);

        for (Object uuid : playerCacheJSON.keySet()) {
            String playerUsername = (String) ((JSONObject) playerCacheJSON.get(uuid)).get("username");
            UUID playerUUID = UUID.fromString((String) uuid);
            uuidToUsernameMap.put(playerUUID, playerUsername);
            usernameToUUIDMap.put(playerUsername.toLowerCase(), playerUUID);
        }


    }

    public void updatePlayerProfile(String username, UUID uuid) {
        JSONObject userEntry = (JSONObject) playerCacheJSON.getOrDefault(uuid.toString(), new JSONObject());
        userEntry.put("username", username);
        playerCacheJSON.put(uuid.toString(), userEntry);

        uuidToUsernameMap.put(uuid, username);
        usernameToUUIDMap.put(username.toLowerCase(), uuid);
    }

    public void updatePlayerProfile(String username, String prefix, UUID uuid) {
        JSONObject userEntry = (JSONObject) playerCacheJSON.getOrDefault(uuid.toString(), new JSONObject());
        userEntry.put("username", username);
        userEntry.put("prefix", prefix);
        playerCacheJSON.put(uuid.toString(), userEntry);

        uuidToUsernameMap.put(uuid, username);
        usernameToUUIDMap.put(username.toLowerCase(), uuid);
    }

    public UUID getUUIDFromUsername(String username) {
        return usernameToUUIDMap.getOrDefault(username.toLowerCase(), null);
    }

    public String getUserPrefix(UUID uuid) {
        JSONObject userEntry = (JSONObject) playerCacheJSON.getOrDefault(uuid.toString(), new JSONObject());
        if(userEntry.get("prefix") == null) {
            return null;
        }
        return (String) userEntry.get("prefix");
    }

    public String getUsernameFromUUID(UUID uuid) {
        return uuidToUsernameMap.getOrDefault(uuid, null);
    }


    public void saveData() {
        saveJsonArray();
    }

    private void saveJsonArray() {
        if (memoryOnly) {
            return;
        }
        try (FileWriter file = new FileWriter(cacheFile)) {
            plugin.debugLogger(Level.INFO, "Saving PlayerCache.json", 1);
            file.write(playerCacheJSON.toJSONString());
            file.flush();
        } catch (IOException e) {
            plugin.logger(Level.WARNING, "Error saving PlayerCache.json: " + e + " : " + e.getMessage());
        }
    }

}
