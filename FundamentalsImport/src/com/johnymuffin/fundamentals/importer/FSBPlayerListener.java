package com.johnymuffin.fundamentals.importer;

import com.earth2me.essentials.User;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.simplejson.JSONObject;
import com.johnymuffin.fundamentals.importer.essentials.EssentialsManager;
import com.johnymuffin.jstats.beta.JStats;
import com.johnymuffin.jstats.beta.PPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.net.URLEncoder;
import java.util.logging.Level;

import static com.johnymuffin.beta.fundamentals.util.JsonReader.readJsonFromUrl;

public class FSBPlayerListener implements Listener {
    private FundamentalsESSBridge plugin;
    private EssentialsManager essM;

    public FSBPlayerListener(FundamentalsESSBridge plugin) {
        this.plugin = plugin;
        this.essM = new EssentialsManager(plugin.getFundamentals());
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Monitor)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().isOnline()) {
            return;
        }

        User user = plugin.getEssentials().getUser(event.getPlayer()); //Essentials Player
        FundamentalsPlayer fundamentalsPlayer = plugin.getFundamentals().getPlayerMap().getPlayer(event.getPlayer()); //Fundamental Player


        //Essentials Start
        if (essM.doesPlayerExist(event.getPlayer().getName())) {
//            plugin.getFundamentals().debugLogger(Level.INFO, "Importing Essentials data for " + event.getPlayer(), 2);
            if (!Utils.doesImportEntryExist("essentials-homes", fundamentalsPlayer)) {
                essM.importHomes(event.getPlayer(), fundamentalsPlayer, plugin.getFundamentals());
                Utils.addImportEntry("essentials-homes", fundamentalsPlayer);
            }
            if (!Utils.doesImportEntryExist("essentials-money", fundamentalsPlayer)) {
                essM.importBalance(event.getPlayer(), fundamentalsPlayer, plugin.getFundamentals());
                Utils.addImportEntry("essentials-money", fundamentalsPlayer);
            }
        }
        //Essentials End



    }

}
