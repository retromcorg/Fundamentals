package com.johnymuffin.beta.fundamentals.importer;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.projectposeidon.api.PoseidonUUID;
import com.projectposeidon.johnymuffin.UUIDManager;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class UUIDFetcherWrapper {
    private List<String> knownPlayers;
    private HashMap<String, UUID> uuidResults = new HashMap<String, UUID>();
    private Fundamentals plugin;
    private long lastNotification;
    private int online = 0;
    private int offline = 0;
    private int errored = 0;
    private int cache = 0;

    public UUIDFetcherWrapper(List<String> knownPlayers, Fundamentals plugin) {
        lastNotification = 0L;
        this.knownPlayers = knownPlayers;
        this.plugin = plugin;

    }

    public HashMap<String, UUID> call() throws Exception {
        for (String username : this.knownPlayers) {
            UUID uuid = PoseidonUUID.getPlayerMojangUUID(username);
            if (uuid != null) {
                online++;
                uuidResults.put(username, uuid);
                continue;
            }
            long unixTime = (System.currentTimeMillis() / 1000L);
            //Check for cracked users
            uuid = UUIDManager.getInstance().getUUIDFromUsername(username, false, unixTime);
            if (uuid != null) {
                offline++;
                uuidResults.put(username, null);
                continue;
            }
            unixTime = unixTime + 1382400;
            try {
                uuid = getUUID(username);
                if (uuid != null) {
                    cache++;
                    UUIDManager.getInstance().receivedUUID(username, uuid, unixTime, true);
                } else {
                    offline++;
                    UUIDManager.getInstance().receivedUUID(username, UUIDManager.generateOfflineUUID(username), unixTime, false);
                }
                uuidResults.put(username, uuid);
            } catch (Exception e) {
                errored++;
                plugin.logger(Level.WARNING, "Unable to fetch UUID for " + username + " due to a Mojang error, " + e + " : " + e.getMessage());
            }

            if (lastNotification + 30 < System.currentTimeMillis() / 1000L) {
                lastNotification = System.currentTimeMillis() / 1000L;
                int total = offline + online + errored;
                plugin.logger(Level.INFO, "UUID Convert Progress: " + total + "/" + this.knownPlayers.size() + ", Cache: " + cache + ", Online: " + online + ", Offline: " + offline + ", Errored: " + errored);
            }

            Thread.sleep(300);
        }
        int total = offline + online + errored + cache;
        plugin.logger(Level.INFO, "UUID Convert Complete: " + total + "/" + this.knownPlayers.size() + ", Cache: " + cache + ", Online: " + online + ", Offline: " + offline + ", Errored: " + errored);
        return uuidResults;

    }


    private UUID getUUID(String username) throws ParseException, IOException {
        JSONObject jsonObject = readJsonFromUrl("https://api.minetools.eu/uuid/" + username);
        if (String.valueOf(jsonObject.get("status")).equalsIgnoreCase("ERR")) {
            return null;
        } else {
            return UUID.fromString(insertDashUUID(String.valueOf(jsonObject.get("id"))));
        }


    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static String insertDashUUID(String uuid) {
        StringBuilder sb = new StringBuilder(uuid);
        sb.insert(8, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(13, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(18, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(23, "-");

        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, ParseException {

        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonText);
            return json;
        } finally {
            is.close();
        }
    }


}
