package com.johnymuffin.fundamentals.importer;

import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.discordcore.DiscordCore;
import com.johnymuffin.fundamentals.importer.essentials.EssentialsManager;
import com.johnymuffin.fundamentals.importer.tasks.LWCTransfer;
import com.johnymuffin.fundamentals.importer.tasks.TownyTransfer;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.logging.Level;

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

        FundamentalsPlayer fundamentalsPlayer = plugin.getFundamentals().getPlayerMap().getPlayer(event.getPlayer()); //Fundamental Player


        //Essentials Start
        if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
            if (essM.doesPlayerExist(event.getPlayer().getName())) {
//            plugin.getFundamentals().debugLogger(Level.INFO, "Importing Essentials data for " + event.getPlayer(), 2);
                if (!Utils.doesImportEntryExist("essentials-homes", fundamentalsPlayer)) {
                    essM.importHomes(event.getPlayer().getName(), fundamentalsPlayer, plugin.getFundamentals());
                    Utils.addImportEntry("essentials-homes", fundamentalsPlayer);
                }
                if (!Utils.doesImportEntryExist("essentials-money", fundamentalsPlayer)) {
                    essM.importBalance(event.getPlayer().getName(), fundamentalsPlayer, plugin.getFundamentals(), true);
                    Utils.addImportEntry("essentials-money", fundamentalsPlayer);
                }
            }
        }
        //Essentials End


        //Towny & LWC Changer

        //if (fundamentalsPlayer.getInformation("last-username") != null && !String.valueOf(fundamentalsPlayer.getInformation("last-username")).equals(event.getPlayer().getName())) {
        if (fundamentalsPlayer.getInformation("last-username") != null && !String.valueOf(fundamentalsPlayer.getInformation("last-username")).equalsIgnoreCase(event.getPlayer().getName())) {
            String newUsername = event.getPlayer().getName();
            String oldUsername = String.valueOf(fundamentalsPlayer.getInformation("last-username"));

            plugin.getFundamentals().debugLogger(Level.INFO, "[Account Transfer] Player " + oldUsername + " ("
                    + event.getPlayer().getUniqueId().toString() + ") has changed their username to " + newUsername, 2);


            ArrayList<String> debugTransfer = new ArrayList<String>();

            //Run Towny Transfer
            if (Bukkit.getPluginManager().isPluginEnabled("Towny")) {
                try {
                    (new TownyTransfer(newUsername, oldUsername, plugin.getFundamentals(), fundamentalsPlayer, debugTransfer)).runTransfer();
                } catch (Exception exception) {
                    plugin.getFundamentals().debugLogger(Level.WARNING, "An error occurred attempting to run the Towny transfer", 2);
                    debugTransfer.add("An error occurred attempting to run the Towny transfer");
                    exception.printStackTrace();
                }
            } else {
                debugTransfer.add("Towny is not enabled");
            }

            //Run LWC Transfer
            if (Bukkit.getPluginManager().isPluginEnabled("LWC")) {
                try {
                    (new LWCTransfer(newUsername, oldUsername, plugin.getFundamentals(), fundamentalsPlayer, debugTransfer)).runTransfer();
                } catch (Exception exception) {
                    plugin.getFundamentals().debugLogger(Level.INFO, "An error occurred attempting to run the LWC transfer", 2);
                    debugTransfer.add("An error occurred attempting to run the LWC transfer");
                    exception.printStackTrace();
                }
            } else {
                debugTransfer.add("LWC is not enabled");
            }


            for (String debug : debugTransfer) {
                plugin.getFundamentals().debugLogger(Level.INFO, "[Account Transfer] " + debug, 2);
            }

            //TODO: fix this lazy shit
            try {
                if (Bukkit.getServer().getPluginManager().isPluginEnabled("DiscordCore") && plugin.getFundamentals().getFundamentalConfig().getConfigBoolean("settings.fundamentals-importer.discordDebugEnabled")) {
                    DiscordCore discordCore = (DiscordCore) Bukkit.getServer().getPluginManager().getPlugin("DiscordCore");
                    String channelID = plugin.getFundamentals().getFundamentalConfig().getConfigString("settings.fundamentals-importer.discordChannelID");
                    discordCore.getDiscordBot().discordSendToChannel(channelID, "**Automatic Account Transfer Task**");
                    discordCore.getDiscordBot().discordSendToChannel(channelID, "**Old Username: **" + oldUsername + " **New Username: **" + newUsername);
                    discordCore.getDiscordBot().discordSendToChannel(channelID, "**UUID: **" + event.getPlayer().getUniqueId());
                    for (String debug : debugTransfer) {
                        discordCore.getDiscordBot().discordSendToChannel(channelID, "- " + debug);
                    }

                }
            } catch (Exception e) {
                plugin.getFundamentals().debugLogger(Level.WARNING, "Failed to send transfer debug to Discord", 2);
                e.printStackTrace();
            }

        }
        fundamentalsPlayer.saveInformation("last-username", event.getPlayer().getName());


    }

}
