package com.johnymuffin.beta.fundamentals.settings;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.HashMap;

public class FundamentalsLanguage extends Configuration {
    private static FundamentalsLanguage singleton = null;
    private HashMap<String, String> map;

    private FundamentalsLanguage(Fundamentals plugin) {
        super(new File(plugin.getDataFolder(), String.valueOf(FundamentalsConfig.getInstance(plugin).getConfigOption("settings.message-file"))));
        map = new HashMap<String, String>();
        loadDefaults();
        loadFile();
    }

    private void loadDefaults() {

        map.put("no_permission", "&4Sorry, you don't have permission for this command.");
        map.put("player_not_found_full", "&4Can't find a player called &9%username%");
        map.put("set_player_afk", "&4Set a player to AFK called &9%username%");

    }

    private void loadFile() {
        this.load();
        for (String key : map.keySet()) {
            if (this.getString(key) == null) {
                this.setProperty(key, map.get(key));
            } else {
                map.put(key, this.getString(key));
            }
        }
        this.save();
    }

    public String getMessage(String msg) {
        String loc = map.get(msg);
        if (loc != null) {
            return loc.replace("&", "\u00a7");
        }
        return msg;
    }


    public static FundamentalsLanguage getInstance() {
        if (FundamentalsLanguage.singleton == null) {
            throw new RuntimeException("A instance of Fundamentals hasn't been passed into FundamentalsLanguage yet.");
        }
        return FundamentalsLanguage.singleton;
    }

    public static FundamentalsLanguage getInstance(Fundamentals plugin) {
        if (FundamentalsLanguage.singleton == null) {
            FundamentalsLanguage.singleton = new FundamentalsLanguage(plugin);
        }
        return FundamentalsLanguage.singleton;
    }


}
