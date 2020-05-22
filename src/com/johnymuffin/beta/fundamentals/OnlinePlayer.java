package com.johnymuffin.beta.fundamentals;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OnlinePlayer {
    private CommandSender replyTo = null;
    private long lastActivity = System.currentTimeMillis() / 1000l;
    private boolean isAFK = false;
    private Player player;

    public OnlinePlayer(final Player p, final Fundamentals plugin) {
        lastActivity = System.currentTimeMillis();
        player = p;


    }

    public void updateActivity() {
        lastActivity = System.currentTimeMillis() / 1000l;
        if(isAFK) {
            toggleAFK();
        }
    }


    //AFK
    public boolean isAFK() {
        return isAFK;
    }

    public void toggleAFK() {
        if (isAFK) {
            isAFK = false;
            Bukkit.broadcastMessage(player.getDisplayName() + " is no longer afk");

        } else {
            isAFK = true;
            Bukkit.broadcastMessage(player.getDisplayName() + " is now afk");
        }
    }

    public void setAFK(boolean b) {
        isAFK = b;
    }


    public void updateTimer() {
        System.out.println("Check");
        if (!isAFK && lastActivity + 60 * 5 < (System.currentTimeMillis() / 1000L)) {
            toggleAFK();
        }
    }
}
