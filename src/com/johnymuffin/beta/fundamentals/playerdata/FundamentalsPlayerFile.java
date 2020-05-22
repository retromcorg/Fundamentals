package com.johnymuffin.beta.fundamentals.playerdata;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.johnymuffin.beta.fundamentals.simplejson.JSONObject;
import com.johnymuffin.beta.fundamentals.simplejson.parser.JSONParser;
import com.johnymuffin.beta.fundamentals.simplejson.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

import static com.johnymuffin.beta.fundamentals.playerdata.Util.sanitizeFileName;

public class FundamentalsPlayerFile {
    //File
    private boolean modified = false; //Has the file been modified, determines if the file should be saved on auto save
    private File playerDataFile; //Location of Player Data file
    private JSONObject jsonData; //Json Player Data Object
    //Fundamentals
    private Fundamentals plugin;
    //Player
    private UUID uuid;


    public FundamentalsPlayerFile(UUID uuid, Fundamentals plugin) {
        //Initialize Variables
        this.uuid = uuid;
        this.plugin = plugin;
        playerDataFile = new File(plugin.getDataFolder() + File.separator + "userdata" + File.separator + uuid.toString() + ".json");
        //Check for file existence, generate if file doesn't exist
        if(!playerDataFile.exists()) {
            playerDataFile.getParentFile().mkdirs();
            initializeData();
        } else {
            try {
                JSONParser parser = new JSONParser();
                jsonData = (JSONObject) parser.parse(new FileReader(playerDataFile));
            } catch (ParseException e) {
                plugin.logger(Level.WARNING, "Failed to Parse player data for " + uuid.toString() + " as it is most likely corrupt, resetting data.");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    protected void playerFileJoin(String username) {
        jsonData.put("username", username);
    }

    protected void playerFileQuit(String username) {
        jsonData.put("lastSeen", System.currentTimeMillis() / 1000L);
    }





    private void initializeData() {
        jsonData = new JSONObject();
        //Save player name in data file if known
        for(Player p : Bukkit.getServer().getOnlinePlayers()) {
            if(p.getUniqueId().equals(uuid)) {
                jsonData.put("username", p.getName());
                break;
            }
        }
        jsonData.put("uuid", uuid.toString());
        jsonData.put("firstJoin", System.currentTimeMillis() / 1000L);
        jsonData.put("lastSeen", System.currentTimeMillis() / 1000L);
        modified = true;
    }


    public void saveIfModified() {
        if(modified) {
            saveToJSON();
        }
    }

    private void saveToJSON() {
        try (FileWriter file = new FileWriter(playerDataFile)) { ;
            file.write(jsonData.toJSONString());
            file.flush();
            modified = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
