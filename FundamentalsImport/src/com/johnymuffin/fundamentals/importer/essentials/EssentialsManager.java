package com.johnymuffin.fundamentals.importer.essentials;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.util.LocationWrapper;
import com.johnymuffin.fundamentals.importer.PluginDataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.logging.Level;

public class EssentialsManager implements PluginDataManager {
    private File essentialsFolder;
    private Fundamentals plugin;

    public EssentialsManager(Fundamentals fundamentals) {
        essentialsFolder = new File(fundamentals.getDataFolder().getParentFile(), "Essentials" + File.separator + "userdata");
        this.plugin = fundamentals;
    }


    private File getPlayerFile(String username) {
        return new File(essentialsFolder, sanitizeFileName(username) + ".yml");
    }

    public EssentialsPlayerData getPlayerData(String username) {
        if (!doesPlayerExist(username)) {
            throw new RuntimeException("Trying to get a player data file that doesn't exist.");
        }

        try {
            return new EssentialsPlayerData(username, getPlayerFile(username), this);
        } catch (Exception e) {
            throw new RuntimeException("Error reading Player Data file");
        }

    }

    public boolean doesPlayerExist(String username) {
        return getPlayerFile(username).exists();
    }

    public Fundamentals getFundamentals() {
        return plugin;
    }

    //Essentials Code Start: com.earth2me.essentials.Util
    private static String sanitizeFileName(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]", "_");
    }
    //Essentials Code End

    //Import Logic Start
    public void importHomes(Player player, FundamentalsPlayer fPlayer, Fundamentals fundamentals) {
        //Check if player exists
        if (!doesPlayerExist(player.getName())) {
            fundamentals.debugLogger(Level.INFO, "Can't import data for " + player.getName() + " as they don't have any Essentials player data", 3);
            return;
        }
        EssentialsPlayerData ePlayer = getPlayerData(player.getName());
        //Loop through all homes
        int importCount = 0;
        System.out.println(ePlayer.getHomes().toString());
        for (String home : ePlayer.getHomes()) {
            LocationWrapper locationWrapper = ePlayer.getHome(home);
            if (locationWrapper == null) {
                fundamentals.debugLogger(Level.WARNING, "Failed to import home " + home + " for player " + player.getName(), 1);
                player.sendMessage(ChatColor.RED + "Failed to import home " + ChatColor.GOLD + home + ChatColor.RED + ", contact staff if you need help.");
                continue;
            }
            if (Bukkit.getServer().getWorld(locationWrapper.getWorld()) == null) {
                fundamentals.debugLogger(Level.WARNING, "Failed to import home " + home + " for player " + player.getName() + " as it is in an invalid world.", 2);
                player.sendMessage(ChatColor.RED + "Your home " + ChatColor.GOLD + home + ChatColor.RED + " couldn't be imported as it is in an invalid world.");
                continue;
            }
            if (!fPlayer.doesHomeExist(home)) {
                //Home name doesn't exist
                Location location = new Location(Bukkit.getServer().getWorld(locationWrapper.getWorld()), locationWrapper.getX(), locationWrapper.getY(), locationWrapper.getZ(), locationWrapper.getYaw(), locationWrapper.getPitch());
                fPlayer.setPlayerHome(home, location);
                importCount = importCount + 1;
            } else {
                //Home name already exists
                fundamentals.debugLogger(Level.INFO, "Failed to import home  " + home + " for " + player.getName() + " as it already exists", 2);
                player.sendMessage(ChatColor.RED + "Failed to import home " + ChatColor.GOLD + home + ChatColor.RED + " as it already exists.");
            }
        }
        if (importCount > 0) {
            player.sendMessage(ChatColor.BLUE + "Imported " + importCount + " homes");
        }
        fundamentals.debugLogger(Level.INFO, "Imported " + importCount + " homes for " + player.getName(), 2);

    }

    public void importBalance(Player player, FundamentalsPlayer fPlayer, Fundamentals fundamentals) {
        //Check if player exists
        if (!doesPlayerExist(player.getName())) {
            fundamentals.debugLogger(Level.INFO, "Can't import data for " + player.getName() + " as they don't have any Essentials player data", 3);
            return;
        }
        EssentialsPlayerData ePlayer = getPlayerData(player.getName());
        fPlayer.setBalance(ePlayer.getMoney());
        player.sendMessage(ChatColor.BLUE + "Imported balance of $" + fPlayer.getBalance());
        fundamentals.debugLogger(Level.INFO, "Imported the balance of $" + fPlayer.getBalance() + " for " + player.getName(), 2);
    }

    //Import Logic End


}
