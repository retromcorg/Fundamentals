package com.johnymuffin.fundamentals.pexbridge;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.Permission;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.logging.Level;

public class FEBPlayerListener implements Listener {
    private FundamentalsPexBridge plugin;

    public FEBPlayerListener(FundamentalsPexBridge plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        //Placeholder, check if username is different
        Player player = event.getPlayer();
        FundamentalsPlayer fundamentalsPlayer = plugin.getFundamentals().getPlayerMap().getPlayer(player);

        String rawUsername = String.valueOf(fundamentalsPlayer.getInformation(plugin.getDescription().getName(), "username"));
        //Is user known
        if (rawUsername == null) {
            fundamentalsPlayer.saveInformation(plugin.getDescription().getName(), "username", player.getName());
            return;
        }

        if(rawUsername.equalsIgnoreCase(player.getName())) {
            //Username is the same


            return;
        }


        String oldUsername = "";
        String newUsername = player.getName();

        PermissionUser newUser = PermissionsEx.getPermissionManager().getUser(newUsername);
        PermissionUser oldUser = PermissionsEx.getPermissionManager().getUser(oldUsername);
        if (oldUser == null) {
            plugin.getFundamentals().logger(Level.WARNING, "The old username " + oldUsername + " isn't known in the Pex database. Aborting permissions transfer.");
            return;
        }

        //Change groups
        newUser.setGroups(oldUser.getGroups()); //Transfer Groups

        //Transfer Prefixes and Suffixes
        for (World world : Bukkit.getServer().getWorlds()) {
            //Move Prefix
            String prefix = oldUser.getPrefix(world.getName());
            if (prefix != null) {
                newUser.setPrefix(prefix, world.getName());
            }
            //Move Suffix
            String suffix = oldUser.getSuffix(world.getName());
            if (suffix != null) {
                newUser.setPrefix(suffix, world.getName());
            }
        }

        //Delete stats for old user
        PermissionsEx.getPermissionManager().resetUser(oldUsername);


    }


}
