package com.johnymuffin.beta.fundamentals.cache;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.OnlinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class OnlinePlayers {
    private static OnlinePlayers singleton;
    private Fundamentals plugin;
    private HashMap<Player, OnlinePlayer> playerData = new HashMap<Player, OnlinePlayer>();

    private OnlinePlayers() {
        plugin = Fundamentals.getPlugin();

    }


    public OnlinePlayer getPlayer(Player player) {
        if (!player.isOnline()) {
            throw new RuntimeException(player.getName() + " isn't currently online");
        }
        if (!playerData.containsKey(player)) {
            playerData.put(player, new OnlinePlayer(player, plugin));
        }
        return playerData.get(player);
    }

    public boolean removePlayer(Player player) {
        if (player.isOnline()) {
            return false;
        }
        if (!playerData.containsKey(player)) {
            return false;
        }
        playerData.remove(player);
        return true;
    }

    public void runTimerTasks() {
        playerData.forEach((k,v) -> v.updateTimer());
    }


    public static OnlinePlayers getInstance() {
        if (OnlinePlayers.singleton == null) {
            OnlinePlayers.singleton = new OnlinePlayers();
        }
        return OnlinePlayers.singleton;
    }

}
