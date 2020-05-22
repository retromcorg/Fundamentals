package com.johnymuffin.beta.fundamentals.datafiles;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.UUID;
import java.util.logging.Level;

public class FundamentalsPlayerData {
    private Fundamentals plugin;
    private File dataFile;
    private JSONObject data;
    private UUID uuid;
    private boolean modified = false;

    public FundamentalsPlayerData(UUID uuid, Fundamentals plugin) {
        this.plugin = plugin;
        this.uuid = uuid;
        dataFile = new File(plugin.getDataFolder() + File.separator + "userdata" + File.separator + uuid.toString() + ".json");
        if(!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            initializeData();
        }
        try {
            JSONParser parser = new JSONParser();
            data = (JSONObject) parser.parse(new FileReader(dataFile));
        } catch (ParseException e) {
            plugin.logger(Level.WARNING, "Failed to Parse player data for " + uuid.toString() + " as it is most likely corrupt, resetting data.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeData() {
        try (FileWriter file = new FileWriter(dataFile)) {
            data = new JSONObject();
            //Save player name for user if they are online
            for(Player p : Bukkit.getServer().getOnlinePlayers()) {
                if(p.getUniqueId().equals(uuid)) {
                    data.put("username", p.getName());
                    break;
                }
            }
            data.put("uuid", uuid.toString());
            file.write(data.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void saveIfModified() {
        if(modified) {
            saveToJSON();
        }
    }


    private void saveToJSON() {
        try (FileWriter file = new FileWriter(dataFile)) { ;
            file.write(data.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
