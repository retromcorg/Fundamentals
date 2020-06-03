package com.johnymuffin.fundamentals.pexbridge;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FundamentalsPexBridge extends JavaPlugin {
    //Basic Plugin Info
    private static FundamentalsPexBridge plugin;
    private Logger log;
    private String pluginName;
    private PluginDescriptionFile pdf;
    //Fundamentals
    private Fundamentals fundamentals;
    //Pex
    private PermissionsEx permissionsEx;

    @Override
    public void onEnable() {
        plugin = this;
        log = this.getServer().getLogger();
        pdf = this.getDescription();
        pluginName = pdf.getName();
        log.info("[" + pluginName + "] Is Loading, Version: " + pdf.getVersion());

        //Check if plugins are enabled
        if(!Bukkit.getPluginManager().isPluginEnabled("Fundamentals")) {
            log.log(Level.WARNING, "Fundamentals couldn't be detected, shutting down");
            Bukkit.getPluginManager().disablePlugin(plugin);

        }
        if(!Bukkit.getPluginManager().isPluginEnabled("PermissionsEx")) {
            log.log(Level.WARNING, "PermissionsEx couldn't be detected, shutting down");
            Bukkit.getPluginManager().disablePlugin(plugin);

        }



        //Hook Fundamentals
        log.info("[" + pluginName + "] Hooking into the core Fundamentals plugin");
        fundamentals = (Fundamentals) Bukkit.getServer().getPluginManager().getPlugin("Fundamentals");
        fundamentals.debugLogger(Level.INFO, fundamentals.getDescription().getName() + " has been hooked successfully", 2);
        //Hook PermissionsEx
        log.info("[" + pluginName + "] Hooking into the PermissionsEX");
        permissionsEx = (PermissionsEx) Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx");
        fundamentals.debugLogger(Level.INFO, permissionsEx.getDescription().getName() + " has been hooked successfully", 2);

        //Initialize Listeners
        final FEBPlayerListener febPlayerListener = new FEBPlayerListener(plugin);
        Bukkit.getPluginManager().registerEvents(febPlayerListener, plugin);


    }

    @Override
    public void onDisable() {
        log.info("[" + pluginName + "] Is Disabling");
    }

    public Fundamentals getFundamentals() {
        return fundamentals;
    }
}
