package com.earth2me.essentials;

import com.earth2me.essentials.api.Economy;
import com.johnymuffin.beta.fundamentals.Fundamentals;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Essentials extends JavaPlugin {
    //Basic Plugin Info
    private static Essentials plugin;
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


        Fundamentals fundamentals = (Fundamentals) Bukkit.getPluginManager().getPlugin("Fundamentals");
        fundamentals.logger(Level.INFO, "Essentials Economy Bridge is loading");
        Economy.setEss(fundamentals);



    }

    @Override
    public void onDisable() {

    }

}
