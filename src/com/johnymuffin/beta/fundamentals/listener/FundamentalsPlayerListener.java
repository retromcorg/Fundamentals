package com.johnymuffin.beta.fundamentals.listener;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.OnlinePlayer;
import com.johnymuffin.beta.fundamentals.cache.OnlinePlayers;
import com.johnymuffin.beta.fundamentals.cache.PlayerDataCache;
import com.johnymuffin.beta.fundamentals.datafiles.FundamentalsPlayerData;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class FundamentalsPlayerListener implements Listener {
    private Fundamentals plugin;
    private PlayerDataCache fundamentalsPlayerCache;

    public FundamentalsPlayerListener(Fundamentals plugin) {
        this.plugin = plugin;
        //FundamentalsPlayerCache = PlayerDataCache.getInstance();
    }

    @EventHandler(priority = Event.Priority.Lowest)
    public void onPlayerLogin(final PlayerLoginEvent event) {
        //Check if player is actually allowed to join
        if(event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return;
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if(event == null) {
            return;
        }
        if(!event.getPlayer().isOnline()) {
            return;
        }

        //Player Data File
        //FundamentalsPlayerData fundamentalsPlayerData = PlayerDataCache.getInstance().getPlayerData(event.getPlayer().getUniqueId(), true);
        //Online Player
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if(!event.getPlayer().isOnline()) {
                return;
            }
            OnlinePlayer onlinePlayer = OnlinePlayers.getInstance().getPlayer(event.getPlayer());
            onlinePlayer.updateActivity();

        }, 20l);

    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(final PlayerChatEvent event) {
        //Update activity
        OnlinePlayers.getInstance().getPlayer(event.getPlayer()).updateActivity();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent event) {
        if (!((event.getFrom().getBlockX() == event.getTo().getBlockX()) && (event.getFrom().getBlockZ() == event.getTo().getBlockZ()))) {
            OnlinePlayers.getInstance().getPlayer(event.getPlayer()).updateActivity();
        }
    }


}
