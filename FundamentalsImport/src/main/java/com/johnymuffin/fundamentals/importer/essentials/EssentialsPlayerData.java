package com.johnymuffin.fundamentals.importer.essentials;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.util.LocationWrapper;
import com.johnymuffin.fundamentals.importer.PlayerData;
import com.johnymuffin.fundamentals.importer.PluginDataManager;
import com.johnymuffin.fundamentals.importer.YAMLConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EssentialsPlayerData extends YAMLConfig implements PlayerData {
    private File file;
    private PluginDataManager pluginDataManager;
    private Fundamentals plugin;

    public EssentialsPlayerData(String Username, File file, PluginDataManager pluginDataManager) {
        super(file);
        this.file = file;
        this.pluginDataManager = pluginDataManager;
        this.plugin = pluginDataManager.getFundamentals();
        if(doesPlayerExist()) {
            this.load();
        }
    }

    public Double getMoney() {
        if (this.getConfigOption("money") != null) {
            return this.getConfigDouble("money");
        }
        return 0D;
    }

    //Essentials Code Start: com.earth2me.essentials.UserData
    private Map<String, Object> _getHomes() {
        Object o = this.getProperty("homes");

        if (o instanceof Map) {
            return (Map<String, Object>) o;
        } else {
            return new HashMap<String, Object>();
        }

    }

    public List<String> getHomes() {
        List<String> list = new ArrayList(_getHomes().keySet());
        return list;

    }

    public LocationWrapper getHome(String name) {
        LocationWrapper location = null;
        String path = "homes." + name;
        //First Check
        location = getHomeMethod1(path);
        if (location == null) {
            location = getHomeMethod2(name);
        }
        return location;
    }

    private LocationWrapper getHomeMethod1(String path) {
        final String worldName = getString((path == null ? "" : path + ".") + "world");
        if (worldName == null || worldName.isEmpty()) {
            return null;
        }
        return new LocationWrapper(getDouble((path == null ? "" : path + ".") + "x", 0),
                getDouble((path == null ? "" : path + ".") + "y", 0),
                getDouble((path == null ? "" : path + ".") + "z", 0),
                (float) getDouble((path == null ? "" : path + ".") + "yaw", 0),
                (float) getDouble((path == null ? "" : path + ".") + "pitch", 0), worldName);
    }

    private LocationWrapper getHomeMethod2(String name) {
        try {
            LocationWrapper loc = getHomeMethod1("homes." + getHomes().get(Integer.parseInt(name)));
            return loc;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean getGodModeEnabled() {
        return this.getBoolean("godmode", false);
    }

    public String getNickname() {
        return this.getString("nickname");
    }

    //Essentials Code End


    @Override
    public boolean doesPlayerExist() {
        return file.exists();
    }

}
