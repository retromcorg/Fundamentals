package com.johnymuffin.beta.fundamentals.listener;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
import com.projectposeidon.api.PoseidonUUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.UUID;
import java.util.logging.Level;

public class FundamentalsPlayerListener implements Listener {
    private Fundamentals plugin;

    public FundamentalsPlayerListener(Fundamentals plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = Event.Priority.Lowest)
    public void onPlayerLogin(final PlayerLoginEvent event) {
        //Check if player is actually allowed to join
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return;
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (event == null) {
            return;
        }
        if (!event.getPlayer().isOnline()) {
            return;
        }

        //Player Data File
        boolean fistJoin = !FundamentalsPlayerMap.getInstance().isPlayerKnown(event.getPlayer().getUniqueId());
        FundamentalsPlayerMap.getInstance().getPlayer(event.getPlayer());
        FundamentalsPlayerMap.getInstance().getPlayer(event.getPlayer()).setFirstJoin(fistJoin);

        //Online Player
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (!event.getPlayer().isOnline()) {
                return;
            }
            FundamentalsPlayerMap.getInstance().getPlayer(event.getPlayer()).updateActivity();
            FundamentalsPlayerMap.getInstance().getPlayer(event.getPlayer()).playerJoinUpdate(event.getPlayer().getName());

        }, 20L);

    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        FundamentalsPlayerMap.getInstance().getPlayer(event.getPlayer()).playerQuitUpdate(event.getPlayer().getName());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(final PlayerChatEvent event) {
        FundamentalsPlayerMap.getInstance().getPlayer(event.getPlayer()).updateActivity();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent event) {
        if (!((event.getFrom().getBlockX() == event.getTo().getBlockX()) && (event.getFrom().getBlockZ() == event.getTo().getBlockZ()))) {
            FundamentalsPlayerMap.getInstance().getPlayer(event.getPlayer()).updateActivity();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPreLogin(PlayerPreLoginEvent event) {
        UUID uuid = PoseidonUUID.getPlayerGracefulUUID(event.getName());
        //Ensure validity of player data before letting players join
        if(FundamentalsPlayerMap.getInstance().isPlayerKnown(uuid)) {
            try {
                FundamentalsPlayerMap.getInstance().getPlayer(uuid);
            } catch (Exception exception) {
                plugin.logger(Level.WARNING, "Error loading player data for " + uuid + ", disconnecting player. \n" + exception.getMessage());
                FundamentalsPlayerMap.getInstance().removePlayerFromMap(uuid);
                event.cancelPlayerLogin(ChatColor.RED + "Sorry, an error occurred reading your data. Please contact staff!");
            }
        }

    }


}
