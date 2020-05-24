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
        //General Stuff
        map.put("no_permission", "&4Sorry, you don't have permission for this command.");
        map.put("unavailable_to_console", "&4Sorry, console can't run this command.");
        map.put("player_not_found_full", "&4Can't find a player called &9%username%");
        //AFK
        map.put("set_player_afk", "&4Set a player to AFK called &9%username%");
        //Home
        map.put("home_non_recorded", "&6Sorry, you have no homes on record. Please set one with /sethome");
        map.put("home_not_on_record", "&6Sorry, we couldn't find a home with that name. Do /home for a list of homes");
        map.put("home_in_invalid_world", "&4Sorry, your home is in an invalid world.");
        map.put("home_teleport_successfully", "&6You have been teleported to your home &b%var1%");
        //Delhome
        map.put("delhome_info", "&6Please specify a home name \"/delhome (home)\". A list of homes can be obtained with /home.");
        map.put("delhome_unknown_home", "&6Sorry, we couldn't find a home with that name. Do /home to get a list of homes.");
        map.put("delhome_unknown_successful", "&6You have successfully deleted a home");
        map.put("delhome_unknown_unsuccessful", "&4Sorry, an error was encountered when deleting that home. Please contact staff!");

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
