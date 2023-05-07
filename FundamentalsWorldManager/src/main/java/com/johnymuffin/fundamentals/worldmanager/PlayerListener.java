package com.johnymuffin.fundamentals.worldmanager;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;
import java.util.logging.Level;

import static com.johnymuffin.beta.fundamentals.util.Utils.getPlayerFromUUID;

public class PlayerListener implements Listener {
    private FundamentalsWorldManager plugin;
    private Fundamentals fundamentals;

    public PlayerListener(FundamentalsWorldManager plugin) {
        this.plugin = plugin;
        this.fundamentals = plugin.getFundamentals();

    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Monitor)
    public void onWorldChangeEvent(PlayerTeleportEvent event) {
        fundamentals.debugLogger(Level.INFO, "A teleport for " + event.getPlayer().getName() + " has been detected.", 1);
//        String currentWorld = event.getFrom().getWorld().getName();
//        String newWorld = event.getTo().getWorld().getName();

//        String currentWorldGroup =

        //Inventory
        plugin.savePlayerInventory(event.getPlayer(), event.getFrom().getWorld().getName()); //Save current inventory
        //TODO: Save armour
        event.getPlayer().getInventory().clear(); //Clear inventory
        //TODO: Clear armour
        plugin.setPlayerInventory(event.getPlayer(), event.getTo().getWorld().getName()); //Set inventory for new world
        //TODO: Set armour
        event.getPlayer().updateInventory(); //Send inventory updates to client

    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Monitor)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        plugin.savePlayerInventory(event.getPlayer(), event.getPlayer().getWorld().getName());
        event.getPlayer().getInventory().clear(); //Clear inventory of a player when they quit
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Monitor)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Player player = getPlayerFromUUID(uuid);
            if(player == null || !player.isOnline()) {
                this.plugin.getFundamentals().debugLogger(Level.WARNING, "Cannot set inventory for " + uuid.toString() + " because they are not online.", 1);
                return;
            }
            plugin.setPlayerInventory(event.getPlayer(), event.getPlayer().getLocation().getWorld().getName());
            event.getPlayer().updateInventory();
        }, 1L);
    }

}
