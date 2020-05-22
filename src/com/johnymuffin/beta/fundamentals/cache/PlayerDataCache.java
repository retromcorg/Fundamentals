package com.johnymuffin.beta.fundamentals.cache;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.datafiles.FundamentalsPlayerData;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class PlayerDataCache {
    private static PlayerDataCache singleton;
    private HashMap<UUID, FundamentalsPlayerData> playerMap = new HashMap<UUID, FundamentalsPlayerData>();
    private Fundamentals plugin;

    private PlayerDataCache(Fundamentals plugin) {
        this.plugin = plugin;
    }

    public void loadPlayerDataSync() {
        //Get userdata folder
        File directory = new File(plugin.getDataFolder() + File.separator + "userdata" + File.separator);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        int playersLoaded = 0;
        long startTime = System.currentTimeMillis() / 1000L;
        plugin.logger(Level.INFO, "Loading player data.");
        for (File file : directory.listFiles()) {
            if (!file.isDirectory()) {
                if (getFileExtension(file).equalsIgnoreCase(".yml") || getFileExtension(file).equalsIgnoreCase("yml")) {
                    String sanitizedUUID = file.getName();
                    sanitizedUUID = sanitizedUUID.replaceAll(".yml", "");
                    //Confirm UUID
                    Pattern p = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
                    if (!p.matcher(sanitizedUUID).matches()) {
                        plugin.logger(Level.WARNING, "A playerdata file with a corrupt UUID has been found, skipping: \"" + sanitizedUUID + "\"");
                    } else {
                        UUID playerUUID = UUID.fromString(sanitizedUUID);
                        playerMap.put(playerUUID, getPlayerData(playerUUID, false));
                        playersLoaded++;
                    }
                }
            }
        }
        long finishTime = System.currentTimeMillis() / 1000L;
        int timeTaken = (int) (finishTime - startTime);
        plugin.logger(Level.INFO, "Loaded " + playersLoaded + " players into memory in " + timeTaken + " seconds");


    }


    public FundamentalsPlayerData getPlayerData(UUID uuid, boolean generate) {
        if (playerMap.containsKey(uuid)) {
            return playerMap.get(uuid);
        }
        if (!generate) {
            return null;
        }
        FundamentalsPlayerData player = new FundamentalsPlayerData(uuid, plugin);
        playerMap.put(uuid, player);
        return player;
    }

    public void serverShutdown() {
        //Save playerData files that have been modified
        for (UUID key : playerMap.keySet()) {
            playerMap.get(key).saveIfModified();
        }
        playerMap = null;
    }


    public static PlayerDataCache getInstance() {
        if (PlayerDataCache.singleton == null) {
            throw new RuntimeException("FundamentalsPlayerCache has not been given a instance of Fundamentals yet");
        }
        return PlayerDataCache.singleton;
    }

    public static PlayerDataCache getInstance(Fundamentals plugin) {
        if (PlayerDataCache.singleton == null) {
            PlayerDataCache.singleton = new PlayerDataCache(plugin);
        }
        return PlayerDataCache.singleton;


    }

    private String getFileExtension(File file) {
        //Credit https://stackoverflow.com/questions/3571223/how-do-i-get-the-file-extension-of-a-file-in-java
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }
}
