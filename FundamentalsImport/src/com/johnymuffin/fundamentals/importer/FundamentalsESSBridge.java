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
