package com.johnymuffin.beta.fundamentals.playerdata;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.simplejson.JSONArray;
import com.johnymuffin.beta.fundamentals.simplejson.JSONObject;
import com.johnymuffin.beta.fundamentals.simplejson.parser.JSONParser;
import com.johnymuffin.beta.fundamentals.simplejson.parser.ParseException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import static com.johnymuffin.beta.fundamentals.util.Utils.verifyHomeName;

public class FundamentalsPlayerFile {
    //File
    private boolean modified = false; //Has the file been modified, determines if the file should be saved on auto save
    private File playerDataFile; //Location of Player Data file
    private JSONObject jsonData; //Json Player Data Object
    //Fundamentals
    private Fundamentals plugin;
    //Player
    private UUID uuid;
    private Double playerBalance = 0D;

    public FundamentalsPlayerFile(UUID uuid, Fundamentals plugin) {
        //Initialize Variables
        this.uuid = uuid;
        this.plugin = plugin;
        playerDataFile = new File(plugin.getDataFolder() + File.separator + "userdata" + File.separator + uuid.toString() + ".json");
        //Check for file existence, generate if file doesn't exist
        if (!playerDataFile.exists()) {
            playerDataFile.getParentFile().mkdirs();
            initializeData();
        } else {
            try {
                JSONParser parser = new JSONParser();
                jsonData = (JSONObject) parser.parse(new FileReader(playerDataFile));
            } catch (ParseException e) {
                plugin.logger(Level.WARNING, "Failed to Parse player data for " + uuid.toString() + " as it is most likely corrupt, resetting data.");
                throw new RuntimeException("Parse Exception: " + e + " - " + e.getMessage());
//                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    protected void playerFileJoin(String username) {
        if (jsonData.get("username") != username) {
            jsonData.put("username", username);
            modified = true;
        }
    }

    protected void playerFileQuit(String username) {
        jsonData.put("lastSeen", System.currentTimeMillis() / 1000L);
    }

    //Economy Start

    //This balance function is only used if multi-world economy is disabled.
    public Double getBalance() {
        if (!jsonData.containsKey("balance")) {
            return 0.00D;
        }
        return Double.valueOf(String.valueOf(jsonData.get("balance")));
    }

    //This balance function is only used if multi-world economy is disabled.
    public void setBalance(Double amount) {
        modified = true;
        jsonData.put("balance", amount);
        plugin.getEconomyCache().saveRecord(uuid, amount); //Save the balance for any economy transaction that might occur, even if the player is offline.
    }

    //Economy End

    //Nickname Start
    public void setNickname(String nickname) {
        modified = true;
        if (nickname == null || nickname.isEmpty()) {
            jsonData.remove("nickname");
            return;
        }
        jsonData.put("nickname", nickname);
    }

    public String getNickname() {
        if (jsonData.get("nickname") == null) {
            return null;
        }
        return String.valueOf(jsonData.get("nickname"));

    }
    //Nickname End

    //Vanish Start
    public boolean isVanished() {
        if (jsonData.get("vanished") == null) {
            return false;
        }
        return Boolean.valueOf(String.valueOf(jsonData.get("vanished")));
    }

    public void setVanished(boolean vanished) {
        modified = true;
        jsonData.put("vanished", vanished);
    }

    //Vanish End

    //Ignores start
    public void addUserIgnore(UUID uuid) {
        JSONArray ignores;
        if (jsonData.containsKey("ignore")) {
            ignores = (JSONArray) jsonData.get("ignore");
        } else {
            ignores = new JSONArray();
        }
        if (!ignores.contains(uuid)) {
            ignores.add(uuid);
            jsonData.put("ignore", ignores);
        }
    }

    public void removeUserIgnore(UUID uuid) {
        JSONArray ignores;
        if (!jsonData.containsKey("ignore")) {
            return;
        }
        ignores = (JSONArray) jsonData.get("ignore");
        if (ignores.contains(uuid)) {
            ignores.remove(uuid);
        }
    }

    public List<String> getIgnoreList() {
        List<String> list = new ArrayList<String>();
        if (jsonData.containsKey("ignore")) {
            JSONArray jsonArray = (JSONArray) jsonData.get("ignores");
            for (int i = 0; i < jsonArray.size(); i++) {
                list.add(String.valueOf(jsonArray.get(i)));
            }
        }
        return list;
    }

    //Ignores End

    //Homes Start
    public boolean doesHomeExist(String name) {
        name = name.toLowerCase();
        return getPlayerHomeJsonData(name) != null;
    }


    public Location getPlayerHome(String name) {
        name = name.toLowerCase();
        JSONObject home = getPlayerHomeJsonData(name);
        //Verify World
        String worldName = String.valueOf(home.get("world"));
        if (Bukkit.getServer().getWorld(worldName) == null) {
            return null;
        }
        //Create Location
        World world = Bukkit.getWorld(worldName);
        Double x = Double.valueOf(String.valueOf(home.get("x")));
        Double y = Double.valueOf(String.valueOf(home.get("y")));
        Double z = Double.valueOf(String.valueOf(home.get("z")));
        Float yaw = Float.valueOf(String.valueOf(home.get("yaw")));
        Float pitch = Float.valueOf(String.valueOf(home.get("pitch")));

        Location location = new Location(world, x, y, z, yaw, pitch);
        return location;
    }

    public boolean isHomeInValidWorld(String name) {
        name = name.toLowerCase();
        JSONObject homeData = getPlayerHomeJsonData(name);
        if (homeData == null) {
            return false;
        }
        String worldName = String.valueOf(homeData.get("world"));
        return Bukkit.getServer().getWorld(worldName) != null;

    }


    public boolean removeHome(String name) {
        name = name.toLowerCase();
        //Check home name is valid
        if (!verifyHomeName(name)) {
            return false;
        }
        //Check if user has set any homes
        if (jsonData.get("homes") == null) {
            return false;
        }
        //Get homes object
        JSONObject homes = (JSONObject) jsonData.get("homes");
        //Check if home exists
        if (homes.get(name) == null) {
            return false;
        }
        //Remove home object
        homes.remove(name);
        //Save home object to master json data
        jsonData.put("homes", homes);
        modified = true;
        return true;
    }

    public boolean setPlayerHome(String name, Location location) {
        name = name.toLowerCase();
//        if (!verifyHomeName(name)) {
//            return false;
//        }
        JSONObject homes;
        if (jsonData.get("homes") == null) {
            homes = new JSONObject();
        } else {
            homes = (JSONObject) jsonData.get("homes");
        }
        JSONObject home = new JSONObject();
        home.put("world", location.getWorld().getName());
        home.put("x", Double.valueOf(String.valueOf(location.getX())));
        home.put("y", Double.valueOf(String.valueOf(location.getY())));
        home.put("z", Double.valueOf(String.valueOf(location.getZ())));
        home.put("yaw", Float.valueOf(String.valueOf(location.getYaw())));
        home.put("pitch", Float.valueOf(String.valueOf(location.getPitch())));
        homes.put(name, home);
        jsonData.put("homes", homes);
        modified = true;
        return true;

    }

    public ArrayList<String> getPlayerHomes() {
        ArrayList<String> homesList = new ArrayList<String>();
        if (jsonData.get("homes") == null) {
            return homesList;
        }
        //Get homes object
        JSONObject homes = (JSONObject) jsonData.get("homes");
        for (Object key : homes.keySet()) {
            if (key instanceof String) {
                homesList.add(String.valueOf(key));
            }
        }
        return homesList;
    }

    private JSONObject getPlayerHomeJsonData(String name) {
        name = name.toLowerCase();
        //Check home name is valid
        if (!verifyHomeName(name)) {
            return null;
        }
        //Check if user has set any homes
        if (jsonData.get("homes") == null) {
            return null;
        }
        //Get homes object
        JSONObject homes = (JSONObject) jsonData.get("homes");
        //Check if home exists
        if (homes.get(name) == null) {
            return null;
        }
        JSONObject home = (JSONObject) homes.get(name);
        return home;

    }


    //Homes End

    //God Mode Start
    public boolean getFileGodModeStatus() {
        return (boolean) jsonData.getOrDefault("god", false);
    }

    public void setFileGodModeStatus(boolean status) {
        modified = true;
        jsonData.put("god", status);
    }

    //God Mode End


    private void initializeData() {
        jsonData = new JSONObject();
        //Save player name in data file if known
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p.getUniqueId().equals(uuid)) {
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
        if (modified) {
            saveToJSON();
        }
    }

    //Raw Storage Start
    public void saveInformation(String fieldName, Object value) {
        modified = true;
        jsonData.put(fieldName.toLowerCase(), value);
    }

    public Object getInformation(String fieldName) {
        return jsonData.get(fieldName.toLowerCase());
    }
    //Raw Storage End

    //Mute Start
    public void setMuteTimer(Long expiry) {
        if (expiry == null) {
            jsonData.remove("mute");
        } else {
            jsonData.put("mute", expiry);
        }
    }

    public Long getMuteStatus() {
        if (jsonData.containsKey("mute")) {
            return Long.valueOf(String.valueOf(jsonData.get("mute")));
        }
        return null;
    }

    //Mute End


    private void saveToJSON() {
        try (FileWriter file = new FileWriter(playerDataFile)) {
            file.write(jsonData.toJSONString());
            file.flush();
            modified = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
