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
import java.util.logging.Level;

import static com.johnymuffin.beta.fundamentals.util.Utils.getPlayerFromUUID;

public class FundamentalsPlayer extends FundamentalsPlayerFile {
    //Fundamentals
    private Fundamentals plugin;
    //Player
    private UUID uuid;
    private long lastActivity = System.currentTimeMillis() / 1000l;
    private boolean isAFK = false;
    private boolean pendingAFKRequest = false;
    private boolean isFirstJoin = false;
    private long quitTime = 0L;
    private boolean fakeQuit = false;

    public FundamentalsPlayer(UUID uuid, Fundamentals plugin) {
        super(uuid, plugin);
        this.uuid = uuid;
        this.plugin = plugin;
        this.quitTime = System.currentTimeMillis() / 1000L;
    }

    public Player getBukkitPlayer() {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p.getUniqueId().equals(uuid)) {
                return p;
            }
        }
        return null;
    }

    //Multiworld support hook
    @Deprecated
    public Double getBalance() {
        if (this.plugin.isWorldManagerMultiWorldEconomy()) {
            String username = this.plugin.getPlayerCache().getUsernameFromUUID(uuid);
            if (username == null) username = "Unknown Player";

            String world; //The world to lookup

            //Get world of player if they are online and assume it.
            Player player = getPlayerFromUUID(uuid);
            if (player != null) {
                world = player.getWorld().getName();
                this.plugin.debugLogger(Level.WARNING, "Something called getBalance() for " + username + ". Assuming the world " + world + " as that is the one the player is in.", 2);
            } else {
                //Fallback to offline world
                world = this.plugin.getFundamentalConfig().getConfigString("settings.per-world-economy.fallback.value");
                this.plugin.debugLogger(Level.WARNING, "Fall back to default world " + world + " for getBalance() " + username + ". This isn't recommended and can be exploited.", 1);
            }


            return getBalance(world);


        } else {
            return super.getBalance();
        }

    }

    public Double getBalance(String worldName) {
        if (this.plugin.isWorldManagerMultiWorldEconomy()) {
            String worldGroup = this.plugin.getFundamentalsWorldManager().getWorldGroup(worldName);

            if (getInformation("balance." + worldGroup) == null) {
                return 0.00D;
            }
            return Double.valueOf(String.valueOf(getInformation("balance." + worldGroup)));
        }

        return super.getBalance();
    }


    @Deprecated
    public void setBalance(Double amount) {
        if (this.plugin.isWorldManagerMultiWorldEconomy()) {
            String username = this.plugin.getPlayerCache().getUsernameFromUUID(uuid);
            if (username == null) username = "Unknown Player";

            String world; //The world to lookup

            //Get world of player if they are online and assume it.
            Player player = getPlayerFromUUID(uuid);
            if (player != null) {
                world = player.getWorld().getName();
                this.plugin.debugLogger(Level.WARNING, "Something called setBalance() for " + username + ". Assuming the world " + world + " as that is the one the player is in.", 2);
            } else {
                //Fallback to offline world
                world = this.plugin.getFundamentalConfig().getConfigString("settings.per-world-economy.fallback.value");
                this.plugin.debugLogger(Level.WARNING, "Fall back to default world " + world + " for setBalance() " + username + ". This isn't recommended and can be exploited.", 1);
            }

            setBalance(amount, world);


        } else {
            super.setBalance(amount);
        }
    }

    public void updateDisplayName() {
        Player player = getPlayerFromUUID(uuid);
        updateDisplayName(player);
    }

    public void updateDisplayName(Player player) {
        if (player != null) {
            String displayName = player.getDisplayName();

            if (player.hasPermission("fundamentals.nickname.color") || player.isOp()) {
                displayName = displayName.replace('&', '§');
                displayName = displayName + "§f";
            }
            player.setDisplayName(displayName);
//            System.out.println("Updating display name for " + player.getName() + " to " + displayName + ". (" + player.getUniqueId() + ")");
        }
    }


    public void setBalance(Double amount, String worldName) {
        if (this.plugin.isWorldManagerMultiWorldEconomy()) {
            String worldGroup = this.plugin.getFundamentalsWorldManager().getWorldGroup(worldName);
            this.saveInformation("balance." + worldGroup, amount);
        } else {
            super.setBalance(amount);
        }
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

    public void setAFKRequestStatus(boolean status){
        pendingAFKRequest = status;
    }

    public boolean isRequestingAFK(){
        return pendingAFKRequest;
    }
    //AFK End

    public void playerJoinUpdate(String username) {
        this.playerFileJoin(username);
    }

    public void playerQuitUpdate() {
        this.setLastSeen();
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

