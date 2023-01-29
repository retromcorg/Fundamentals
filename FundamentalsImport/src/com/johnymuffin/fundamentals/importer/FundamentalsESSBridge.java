package com.johnymuffin.fundamentals.importer;

import com.earth2me.essentials.Essentials;
import com.johnymuffin.beta.fundamentals.Fundamentals;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class FundamentalsESSBridge extends JavaPlugin {
    //Basic Plugin Info
    private static FundamentalsESSBridge plugin;
    private Logger log;
    private String pluginName;
    private PluginDescriptionFile pdf;
    private Essentials essentials;
    private Fundamentals fundamentals;


    @Override
    public void onEnable() {
        plugin = this;
        log = this.getServer().getLogger();
        pdf = this.getDescription();
        pluginName = pdf.getName();
        log.info("[" + pluginName + "] Is Loading, Version: " + pdf.getVersion());

        essentials = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
        fundamentals = (Fundamentals) Bukkit.getServer().getPluginManager().getPlugin("Fundamentals");

        Bukkit.getServer().getPluginManager().registerEvents(new FSBPlayerListener(plugin), plugin);

        //Generate some settings YAAAAAAAAAAA
        fundamentals.getFundamentalConfig().generateConfigOption("settings.fundamentals-importer.discordDebugEnabled", false);
        fundamentals.getFundamentalConfig().generateConfigOption("settings.fundamentals-importer.discordChannelID", "0");


        fundamentals.getFundamentalConfig().generateConfigOption("settings.fundamentals-importer.importEssentialsHomes.enabled", true);
        fundamentals.getFundamentalConfig().generateConfigOption("settings.fundamentals-importer.importEssentialsMoney.enabled", true);


        //Automatic Transfer
        fundamentals.getFundamentalConfig().generateConfigOption("settings.fundamentals-importer.auto-transfer.enabled", true);
        fundamentals.getFundamentalConfig().generateConfigOption("settings.fundamentals-importer.auto-transfer.info", "When a player changes their username, this will automatically transfer their data to the new username.");

        fundamentals.getFundamentalConfig().generateConfigOption("settings.fundamentals-importer.auto-transfer.towny.enabled", true);
        fundamentals.getFundamentalConfig().generateConfigOption("settings.fundamentals-importer.auto-transfer.towny.info", "When a player changes their username, this will automatically transfer their towny data to the new username.");

        fundamentals.getFundamentalConfig().generateConfigOption("settings.fundamentals-importer.auto-transfer.lwc.enabled", true);
        fundamentals.getFundamentalConfig().generateConfigOption("settings.fundamentals-importer.auto-transfer.lwc.info", "When a player changes their username, this will automatically transfer their LWC data to the new username.");


        //Save the config
        fundamentals.getFundamentalConfig().save();


    }


    @Override
    public void onDisable() {

    }

    public Essentials getEssentials() {
        return essentials;
    }

    public Fundamentals getFundamentals() {
        return fundamentals;
    }


}
