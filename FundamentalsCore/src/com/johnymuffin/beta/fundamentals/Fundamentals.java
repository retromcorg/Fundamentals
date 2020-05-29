package com.johnymuffin.beta.fundamentals;

import com.johnymuffin.beta.fundamentals.api.EconomyAPI;
import com.johnymuffin.beta.fundamentals.api.FundamentalsAPI;
import com.johnymuffin.beta.fundamentals.commands.*;
import com.johnymuffin.beta.fundamentals.listener.FundamentalsEntityListener;
import com.johnymuffin.beta.fundamentals.listener.FundamentalsPlayerListener;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsConfig;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
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
    private int debugLevel = 3;
    //Hook Status
    private boolean essentialsHook = false;
    private boolean discordCoreHook = false;
    private Long lastAutoSaveTime = System.currentTimeMillis() / 1000l;
    //API
    private EconomyAPI economyAPI;

    @Override
    public void onEnable() {
        plugin = this;
        log = this.getServer().getLogger();
        pdf = this.getDescription();
        pluginName = pdf.getName();
        log.info("[" + pluginName + "] Is Loading, Version: " + pdf.getVersion());

        //Load Core Start
        this.logger(Level.INFO, "Initializing player data map");
        FundamentalsPlayerMap.getInstance(plugin);
        for (Player p : Bukkit.getOnlinePlayers()) {
            logger(Level.INFO, "Regenerating data for a player already online: " + p.getName());
            FundamentalsPlayerMap.getInstance(plugin).getPlayer(p);
        }
        this.logger(Level.INFO, "Initializing settings map");
        FundamentalsConfig.getInstance(plugin);
        debugLevel = Integer.valueOf(String.valueOf(FundamentalsConfig.getInstance(plugin).getConfigOption("settings.debug-level")));
        this.logger(Level.INFO, "Setting console debug to " + debugLevel);

        this.logger(Level.INFO, "Initializing language map");
        FundamentalsLanguage.getInstance(plugin);

        this.logger(Level.INFO, "Initializing API");
        FundamentalsAPI.setFundamentals(plugin);
        economyAPI = new EconomyAPI(plugin);


        //Listeners
        final FundamentalsPlayerListener fundamentalsPlayerListener = new FundamentalsPlayerListener(plugin);
        Bukkit.getPluginManager().registerEvents(fundamentalsPlayerListener, plugin);
        final FundamentalsEntityListener fundamentalsEntityListener = new FundamentalsEntityListener(plugin);
        Bukkit.getPluginManager().registerEvents(fundamentalsEntityListener, plugin);

        //Hooks
        if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
            essentialsHook = true;
            debugLogger(Level.INFO, "Essentials has been detected.", 1);
        }
        if (Bukkit.getPluginManager().isPluginEnabled("DiscordCore")) {
            discordCoreHook = true;
            debugLogger(Level.INFO, "Discord Core has been detected.", 1);
        }
        long startTimeUnix = System.currentTimeMillis() / 1000L;


        //Commands
        Bukkit.getPluginCommand("heal").setExecutor(new CommandHeal());
        Bukkit.getPluginCommand("home").setExecutor(new CommandHome());
        Bukkit.getPluginCommand("afk").setExecutor(new CommandAFK());
        Bukkit.getPluginCommand("sethome").setExecutor(new CommandSetHome());
        Bukkit.getPluginCommand("delhome").setExecutor(new CommandDelhome());
        Bukkit.getPluginCommand("balance").setExecutor(new CommandBalance());
        Bukkit.getPluginCommand("economy").setExecutor(new CommandEconomy());
        Bukkit.getPluginCommand("pay").setExecutor(new CommandPay());
        Bukkit.getPluginCommand("god").setExecutor(new CommandGod());
        Bukkit.getPluginCommand("nickname").setExecutor(new CommandNickname());


        //Timer
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, () -> {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                int autoSavePeriod = Integer.valueOf(String.valueOf(FundamentalsConfig.getInstance(plugin).getConfigOption("settings.auto-save-time")));
                if (lastAutoSaveTime + autoSavePeriod < getUnix()) {
                    lastAutoSaveTime = getUnix();
                    debugLogger(Level.INFO, "Automatically saving data.", 2);
                    FundamentalsPlayerMap.getInstance().saveData();
                }
                FundamentalsPlayerMap.getInstance(plugin).runTimerTasks();
            });
        }, 20, 20 * 10);

        long endTimeUnix = System.currentTimeMillis() / 1000L;
        log.info("[" + pluginName + "] Has Loaded, loading took " + (int) (endTimeUnix - startTimeUnix) + " seconds.");

    }

    @Override
    public void onDisable() {
        FundamentalsPlayerMap.getInstance().serverShutdown();
    }

    public void logger(Level level, String message) {
        Bukkit.getLogger().log(level, "[" + pluginName + "] " + message);
    }

    public void debugLogger(Level level, String message, int debug) {
        if (debug <= debugLevel) {
            Bukkit.getLogger().log(level, "[" + pluginName + " Debug] " + message);
        }
    }

    public static Fundamentals getPlugin() {
        return plugin;
    }

    private Long getUnix() {
        return System.currentTimeMillis() / 1000l;
    }

    public EconomyAPI getEconomyAPI() {
        return economyAPI;
    }

    public FundamentalsPlayerMap getPlayerMap() {
        return FundamentalsPlayerMap.getInstance(plugin);
    }
}
