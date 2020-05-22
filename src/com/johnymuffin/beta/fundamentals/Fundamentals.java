package com.johnymuffin.beta.fundamentals;

import com.johnymuffin.beta.fundamentals.cache.OnlinePlayers;
import com.johnymuffin.beta.fundamentals.cache.PlayerDataCache;
import com.johnymuffin.beta.fundamentals.commands.CommandAFK;
import com.johnymuffin.beta.fundamentals.commands.CommandHeal;
import com.johnymuffin.beta.fundamentals.listener.FundamentalsPlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Fundamentals extends JavaPlugin {
    //Basic Plugin Info
    private static Fundamentals plugin;
    private Logger log;
    private String pluginName;
    private PluginDescriptionFile pdf;
    //Hook Status
    private boolean essentialsHook = false;
    private boolean discordCoreHook = false;


    @Override
    public void onEnable() {
        plugin = this;
        log = this.getServer().getLogger();
        pdf = this.getDescription();
        pluginName = pdf.getName();
        log.info("[" + pluginName + "] Is Loading, Version: " + pdf.getVersion());
        //Load Core
        OnlinePlayers.getInstance();
        for(Player p: Bukkit.getOnlinePlayers()) {
            logger(Level.INFO, "Regenerating data for a player already online: " + p.getName());
            OnlinePlayers.getInstance().getPlayer(p);
        }
        //Listeners
        final FundamentalsPlayerListener fundamentalsPlayerListener = new FundamentalsPlayerListener(plugin);
        Bukkit.getPluginManager().registerEvents(fundamentalsPlayerListener, plugin);


//        PlayerDataCache.getInstance(plugin); //Initialize Player Map
//        PlayerDataCache.getInstance().loadPlayerDataSync();

        //Hooks
        if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
            essentialsHook = true;
            logger(Level.INFO, "Essentials has been detected.");
        }
        if (Bukkit.getPluginManager().isPluginEnabled("DiscordCore")) {
            discordCoreHook = true;
            logger(Level.INFO, "Discord Core has been detected.");
        }
        long startTimeUnix = System.currentTimeMillis() / 1000L;


        //Commands
        Bukkit.getPluginCommand("heal").setExecutor(new CommandHeal());
        Bukkit.getPluginCommand("afk").setExecutor(new CommandAFK());


        //Timer
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, () -> {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                OnlinePlayers.getInstance().runTimerTasks();
            });
        }, 20, 20 * 10);

        long endTimeUnix = System.currentTimeMillis() / 1000L;
        log.info("[" + pluginName + "] Has Loaded, loading took " + (int) (endTimeUnix - startTimeUnix) + " seconds.");
    }

    @Override
    public void onDisable() {
        PlayerDataCache.getInstance(plugin).serverShutdown();
    }

    public void logger(Level level, String message) {
        Bukkit.getLogger().log(level, "[" + pluginName + "] " + message);
    }

    public static Fundamentals getPlugin() {
        return plugin;
    }
}
