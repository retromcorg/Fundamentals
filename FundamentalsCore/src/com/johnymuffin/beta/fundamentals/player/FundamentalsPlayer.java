package com.johnymuffin.beta.fundamentals.player;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.banks.FundamentalsBank;
import com.johnymuffin.beta.fundamentals.playerdata.FundamentalsPlayerFile;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FundamentalsPlayer extends FundamentalsPlayerFile {
    //Fundamentals
    private Fundamentals plugin;
    //Player
    private UUID uuid;
    private long lastActivity = System.currentTimeMillis() / 1000l;
    private boolean isAFK = false;
    private boolean isFirstJoin = false;
    private long quitTime = 0L;
    private boolean fakeQuit = false;

    public FundamentalsPlayer(UUID uuid, Fundamentals plugin) {
        super(uuid, plugin);
        this.uuid = uuid;
        this.plugin = plugin;
        this.quitTime = System.currentTimeMillis() / 1000L;
    }


    public boolean isPlayerOnline() {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p.getUniqueId().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public Player getBukkitPlayer() {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p.getUniqueId().equals(uuid)) {
                return p;
            }
        }
        return null;
    }


    //AFK Logic
    public void updateActivity() {
        //Confirm Player Is Online
        if (!isPlayerOnline()) {
            return;
        }
        lastActivity = System.currentTimeMillis() / 1000l;
        if (isAFK) {
            toggleAFK();
        }
    }

    //AFK Start
    public boolean isAFK() {
        //Confirm Player Is Online
        if (!isPlayerOnline()) {
            return false;
        }
        return isAFK;
    }

    public void toggleAFK() {
        //Confirm Player Is Online
        if (!isPlayerOnline()) {
            return;
        }
        if (isAFK) {
            isAFK = false;
            String msg = FundamentalsLanguage.getInstance().getMessage("afk_toggle_off");
            msg = msg.replaceAll("%var1%", getBukkitPlayer().getDisplayName());
            Bukkit.broadcastMessage(msg);

        } else {
            isAFK = true;
            String msg = FundamentalsLanguage.getInstance().getMessage("afk_toggle_on");
            msg = msg.replaceAll("%var1%", getBukkitPlayer().getDisplayName());
            Bukkit.broadcastMessage(msg);
        }
    }

    public void setAFK(boolean b) {
        //Confirm Player Is Online
        if (!isPlayerOnline()) {
            return;
        }
        isAFK = b;
    }


    public void checkForAFK() {
        if (plugin.getFundamentalConfig().getConfigBoolean("settings.afk.enabled")) {
            if (!isAFK && lastActivity + plugin.getFundamentalConfig().getConfigInteger("settings.afk.time") < (System.currentTimeMillis() / 1000L)) {
                toggleAFK();
            }
            if (isAFK && plugin.getFundamentalConfig().getConfigBoolean("settings.afk.kick.enabled") && lastActivity + plugin.getFundamentalConfig().getConfigInteger("settings.afk.kick.time") < (System.currentTimeMillis() / 1000L)) {
                getBukkitPlayer().kickPlayer(FundamentalsLanguage.getInstance().getMessage("afk_kick_message"));

            }
        }
    }
    //AFK End

    public void playerJoinUpdate(String username) {
        this.playerFileJoin(username);
    }

    public void playerQuitUpdate(String username) {
        this.playerFileQuit(username);
        quitTime = System.currentTimeMillis() / 1000L;
    }

    //TODO This method is actually pretty intensive and a better system should be implemented when I have more time.
    public FundamentalsBank[] getAccessibleAccounts() {
        List<FundamentalsBank> accounts = new ArrayList<>();
        for (FundamentalsBank bank : plugin.getBanks().values()) {
            if (bank.getBankOwner().equals(this.uuid)) {
                accounts.add(bank);
                continue;
            }
            for (UUID access : bank.getAccessList()) {
                if (access.equals(this.uuid)) {
                    accounts.add(bank);
                    continue;
                }
            }
        }

        return (FundamentalsBank[]) accounts.toArray();
    }


    public long getQuitTime() {
        return quitTime;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setFirstJoin(boolean firstJoin) {
        isFirstJoin = firstJoin;
    }

    public boolean isFirstJoin() {
        return isFirstJoin;
    }

    public boolean isFakeQuit() {
        return fakeQuit;
    }

    public void setFakeQuit(boolean fakeQuit) {
        this.fakeQuit = fakeQuit;
    }
}

