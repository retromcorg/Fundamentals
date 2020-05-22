package com.johnymuffin.beta.fundamentals.player;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.playerdata.FundamentalsPlayerFile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FundamentalPlayer extends FundamentalsPlayerFile {
    //Fundamentals
    private Fundamentals plugin;
    //Player
    private UUID uuid;
    private long lastActivity = System.currentTimeMillis() / 1000l;
    private boolean isAFK = false;
    private boolean isFirstJoin = false;

    public FundamentalPlayer(UUID uuid, Fundamentals plugin) {
        super(uuid, plugin);
        this.uuid = uuid;
        this.plugin = plugin;
    }



    public boolean isPlayerOnline() {
        for(Player p : Bukkit.getServer().getOnlinePlayers()) {
            if(p.getUniqueId().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public Player getBukkitPlayer() {
        for(Player p : Bukkit.getServer().getOnlinePlayers()) {
            if(p.getUniqueId().equals(uuid)) {
                return p;
            }
        }
        return null;
    }



    //AFK Logic
    public void updateActivity() {
        //Confirm Player Is Online
        if(!isPlayerOnline()) {
            return;
        }
        lastActivity = System.currentTimeMillis() / 1000l;
        if(isAFK) {
            toggleAFK();
        }
    }

    //AFK Start
    public boolean isAFK() {
        //Confirm Player Is Online
        if(!isPlayerOnline()) {
            return false;
        }
        return isAFK;
    }

    public void toggleAFK() {
        //Confirm Player Is Online
        if(!isPlayerOnline()) {
            return;
        }
        if (isAFK) {
            isAFK = false;
            Bukkit.broadcastMessage(getBukkitPlayer().getDisplayName() + " is no longer afk");

        } else {
            isAFK = true;
            Bukkit.broadcastMessage(getBukkitPlayer().getDisplayName() + " is now afk");
        }
    }

    public void setAFK(boolean b) {
        //Confirm Player Is Online
        if(!isPlayerOnline()) {
            return;
        }
        isAFK = b;
    }


    public void updateTimer() {
        if (!isAFK && lastActivity + 60 * 5 < (System.currentTimeMillis() / 1000L)) {
            toggleAFK();
        }
    }
    //AFK End

    protected void playerJoinUpdate(String username) {
        this.playerFileJoin(username);
    }

    protected void playerQuitUpdate(String username) {
        this.playerFileQuit(username);
    }

    public void setFirstJoin(boolean firstJoin) {
        isFirstJoin = firstJoin;
    }

    public boolean isFirstJoin() {
        return isFirstJoin;
    }
}
