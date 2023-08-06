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
        generateConfigOption("settings.afk.enabled", true);
        generateConfigOption("settings.afk.time", 60 * 5);
        generateConfigOption("settings.afk.kick.enabled", true);
        generateConfigOption("settings.afk.kick.time", 60 * 25);
//        generateConfigOption("settings.import.player-stats-url", "https://example.com/player/stats?username=");
        generateConfigOption("settings.vanish.block-interactions", true);
        generateConfigOption("settings.vanish-hidden-from-mobs", true);
        generateConfigOption("settings.preferred-permissions-hook", "JPerms");
//        generateConfigOption("settings.import-prefixes-next-start", true);


        generateConfigOption("settings.per-world-economy.info", "This setting enables per-world economy. FundamentalsWorldManager must be installed for this feature to function.");
        generateConfigOption("settings.per-world-economy.enabled", true);
        generateConfigOption("settings.per-world-economy.fallback.value", "world");

        generateConfigOption("settings.default-language-file.info", "Don't load the saved language file, instead use the specified language in the class.");
        generateConfigOption("settings.default-language-file.enabled", true);

        generateConfigOption("settings.joinandleave.join-message", "%prefix% &c%player% &bJoined the game.");
        generateConfigOption("settings.joinandleave.leave-message", "%prefix% &c%player% &bLeft the game.");
        generateConfigOption("settings.joinandleave.kick-message", "%prefix% &c%player% &bWas kicked from the game.");


        //Interest Settings

        generateConfigOption("settings.interest.info", "This setting enables interest for players balances and banks.");
        generateConfigOption("settings.interest.enabled", false);

        generateConfigOption("settings.interest.rate.info", "This setting is the interest rate for players balances and banks. EG: 5.5% = 0.055");
        generateConfigOption("settings.interest.rate.value", 5.5);

        generateConfigOption("settings.interest.rate.last-month-applied.info", "The last month the interest rate was applied.");
        generateConfigOption("settings.interest.rate.last-month-applied.value", "_JANUARY");

        generateConfigOption("settings.interest.rate.use-live-interest-rate.info", "This setting will automatically update the interest rate from API Ninja.");
        generateConfigOption("settings.interest.rate.use-live-interest-rate.enabled", false);

        generateConfigOption("settings.interest.rate.use-live-interest-rate.last-updated.value", 0L);
        generateConfigOption("settings.interest.rate.use-live-interest-rate.last-updated.info", "This setting is the last time (unix timestamp) the interest rate was updated from API Ninja.");

        generateConfigOption("settings.interest.rate.use-live-interest-rate.api-key.info", "This setting is the API key for API Ninja.");
        generateConfigOption("settings.interest.rate.use-live-interest-rate.api-key.value", "API_KEY_HERE");

        generateConfigOption("settings.interest.rate.use-live-interest-rate.country.info", "This setting is the country for API Ninja.");
        generateConfigOption("settings.interest.rate.use-live-interest-rate.country.value", "Australia");




    }

    public void generateConfigOption(String key, Object defaultValue) {
        if (this.getProperty(key) == null) {
            this.setProperty(key, defaultValue);
        }
        final Object value = this.getProperty(key);
        this.removeProperty(key);
        this.setProperty(key, value);
    }


    //Getters Start
    public Object getConfigOption(String key) {
        return this.getProperty(key);
    }

    public String getConfigString(String key) {
        return String.valueOf(getConfigOption(key));
    }

    public Integer getConfigInteger(String key) {
        return Integer.valueOf(getConfigString(key));
    }

    public Long getConfigLong(String key) {
        return Long.valueOf(getConfigString(key));
    }

    public Double getConfigDouble(String key) {
        return Double.valueOf(getConfigString(key));
    }

    public Boolean getConfigBoolean(String key) {
        return Boolean.valueOf(getConfigString(key));
    }


    //Getters End


    public Long getConfigLongOption(String key) {
        if (this.getConfigOption(key) == null) {
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

    @Deprecated
    public static FundamentalsConfig getInstance() {
        if (FundamentalsConfig.singleton == null) {
            throw new RuntimeException("A instance of Fundamentals hasn't been passed into FundamentalsConfig yet.");
        }
        return FundamentalsConfig.singleton;
    }

    @Deprecated
    public static FundamentalsConfig getInstance(Fundamentals plugin) {
        if (FundamentalsConfig.singleton == null) {
            FundamentalsConfig.singleton = new FundamentalsConfig(plugin);
        }
        return FundamentalsConfig.singleton;
    }

}
