package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import com.johnymuffin.beta.fundamentals.util.Utils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;
import static com.johnymuffin.beta.fundamentals.util.Utils.*;

public class CommandHome implements CommandExecutor {
    private Fundamentals plugin;

    public CommandHome(Fundamentals plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender.hasPermission("fundamentals.home") || commandSender.isOp())) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Sorry, console can't run this command.");
            return true;
        }
        Player player = (Player) commandSender;
        String homeName = null;
        FundamentalsPlayer targetPlayer;


        if (strings.length == 0) {
            targetPlayer = FundamentalsPlayerMap.getInstance().getPlayer(player);
            homeName = null;
        } else {
            String[] homeNameParts = strings[0].split(":");
            if (strings[0].contains(":")) {
                //User is requesting another user
                if (!isPlayerAuthorized(commandSender, "fundamentals.home.others")) {
                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
                    return true;
                }
                String targetPlayerUsername;
                if (homeNameParts.length > 1) {
                    //User is requesting to teleport to a specific home
                    homeName = homeNameParts[1];
                    targetPlayerUsername = homeNameParts[0];
                } else {
                    //User is requesting a list of homes from another user
                    homeName = null;
                    targetPlayerUsername = homeNameParts[0];
                }
                UUID targetPlayerUUID = getUUIDFromUsername(targetPlayerUsername);
                if (!FundamentalsPlayerMap.getInstance().isPlayerKnown(targetPlayerUUID)) {
                    String message = FundamentalsLanguage.getInstance().getMessage("player_not_found_full");
                    message = message.replace("%username%", targetPlayerUsername);
                    commandSender.sendMessage(message);
                    return true;
                }
                targetPlayer = FundamentalsPlayerMap.getInstance().getPlayer(targetPlayerUUID);
            } else {
                //User is requesting their own homes
                targetPlayer = FundamentalsPlayerMap.getInstance().getPlayer(player);
                homeName = strings[0];
            }
        }


        if (homeName == null) {
            //No home specified, list homes
            ArrayList<String> homes = targetPlayer.getPlayerHomes();
            if (homes.size() == 0) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("home_non_recorded"));
                return true;
            }
            String msg = "&6Home List: ";
            for (String hn : homes) {
                if (targetPlayer.isHomeInValidWorld(hn)) {
                    msg = msg + "&a" + hn + "&6, ";
                } else {
                    msg = msg + "&4" + hn + "&6, ";
                }
            }
            msg = msg.substring(0, msg.length() - 2);
            commandSender.sendMessage(formatColor(msg));
            return true;
        } else {
            if (!targetPlayer.doesHomeExist(homeName)) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("home_not_on_record"));
                return true;
            }
            if (!targetPlayer.isHomeInValidWorld(homeName)) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("home_in_invalid_world"));
                return true;
            }
            Location home = targetPlayer.getPlayerHome(homeName);
            Location safeLocation;
            try {
                safeLocation = Utils.getSafeDestination(home);
            } catch (Exception e) {
                String msg = FundamentalsLanguage.getInstance().getMessage("generic_error_player");
                msg = msg.replaceAll("%var1%", e.getMessage());
                commandSender.sendMessage(msg);
                return true;
            }
            player.teleport(safeLocation);
            String msg = FundamentalsLanguage.getInstance().getMessage("home_teleport_successfully");
            msg = msg.replaceAll("%var1%", homeName);
            commandSender.sendMessage(msg);
            //Username
            String target = getPlayerName(targetPlayer.getUuid());
            if (target == null) {
                target = targetPlayer.getUuid().toString();
            }
            plugin.debugLogger(Level.INFO, player.getName() + " has teleported to a home owned by " + target + " called " + homeName, 2);
            return true;
        }


    }

}
