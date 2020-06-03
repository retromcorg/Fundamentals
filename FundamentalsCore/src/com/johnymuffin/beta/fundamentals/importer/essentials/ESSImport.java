package com.johnymuffin.beta.fundamentals.importer.essentials;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.importer.UUIDFetcherWrapper;
import com.projectposeidon.johnymuffin.UUIDManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class ESSImport {
    private Fundamentals plugin;
    private List<String> knownPlayers = new ArrayList<>();

    public ESSImport(Fundamentals plugin) {
        this.plugin = plugin;
        plugin.logger(Level.INFO, "Starting Essentials user data import, this is a blocking task so expect the server to freeze for awhile.");
        File essentialsFolder = new File(plugin.getDataFolder().getParentFile(), "Essentials" + File.separator + "userdata");
        if (!essentialsFolder.exists()) {
            plugin.logger(Level.WARNING, "An Essentials player data folder couldn't be detected, aborting import");
            return;
        }
        plugin.logger(Level.INFO, "Scanning directory for player names");
        for (String username : essentialsFolder.list()) {
            if (!username.endsWith(".yml")) {
                plugin.logger(Level.WARNING, username + "isn't a valid player data file, skipping");
                continue;
            }
            knownPlayers.add(username.replace(".yml", ""));
//            break;
        }
        plugin.logger(Level.INFO, "Scanning directory complete");
        plugin.logger(Level.INFO, "Starting UUID lookup task");
        HashMap<String, UUID> response = null;
        try {
            response = new UUIDFetcherWrapper(knownPlayers, plugin).call();
        } catch (Exception e) {
            plugin.logger(Level.SEVERE, "UUID Fetcher failed, killing import task. " + e + ": " + e.getMessage());
            return;
        }
        long unixTime = (System.currentTimeMillis() / 1000L) + 1382400;
        for (String key : response.keySet()) {
            plugin.logger(Level.INFO, key + " : " + response.get(key));
        }


    }
}
