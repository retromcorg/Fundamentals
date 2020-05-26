package com.johnymuffin.beta.fundamentals.settings;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import org.bukkit.util.config.Configuration;

import java.io.File;

public class FundamentalsConfig extends Configuration {
    private static FundamentalsConfig singleton;

    private FundamentalsConfig(Fundamentals plugin) {
        super(new File(plugin.getDataFolder(), "core.yml"));
        this.reload();
    }

    private void write() {
        //Main
        generateConfigOption("config-version", 1);
        //Setting
        generateConfigOption("settings.message-file", "messages.yml");
        generateConfigOption("settings.auto-save-time", 300);
        generateConfigOption("settings.debug-level", 1);
        generateConfigOption("settings.multiple-homes", 5);
        generateConfigOption("settings.load-all-players-into-cache", false);


    }
    private void generateConfigOption(String key, Object defaultValue) {
        if (this.getProperty(key) == null) {
            this.setProperty(key, defaultValue);
        }
        final Object value = this.getProperty(key);
        this.removeProperty(key);
        this.setProperty(key, value);
    }

    public Object getConfigOption(String key) {
        return this.getProperty(key);
    }

    public Long getConfigLongOption(String key) {
        if(this.getConfigOption(key) == null) {
            return null;
        }
        return Long.valueOf(String.valueOf(this.getProperty(key)));
    }


    private boolean convertToNewAddress(String newKey, String oldKey) {
        if (this.getString(newKey) != null) {
            return false;
        }
        if (this.getString(oldKey) == null) {
            return false;
        }
        System.out.println("Converting Config: " + oldKey + " to " + newKey);
        Object value = this.getProperty(oldKey);
        this.setProperty(newKey, value);
        this.removeProperty(oldKey);
        return true;

    }



    private void reload() {
        this.load();
        this.write();
        this.save();
    }

    public static FundamentalsConfig getInstance() {
        if (FundamentalsConfig.singleton == null) {
            throw new RuntimeException("A instance of Fundamentals hasn't been passed into FundamentalsConfig yet.");
        }
        return FundamentalsConfig.singleton;
    }

    public static FundamentalsConfig getInstance(Fundamentals plugin) {
        if (FundamentalsConfig.singleton == null) {
            FundamentalsConfig.singleton = new FundamentalsConfig(plugin);
        }
        return FundamentalsConfig.singleton;
    }

}
