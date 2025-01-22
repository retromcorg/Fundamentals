package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
import com.johnymuffin.beta.fundamentals.playerdata.FundamentalsPlayerFile;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsConfig;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;
import static com.johnymuffin.beta.fundamentals.util.Utils.getUUIDFromUsername;
import static com.johnymuffin.beta.fundamentals.util.Utils.verifyHomeName;

public class CommandSetHome implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender.hasPermission("fundamentals.sethome") || commandSender.isOp())) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("unavailable_to_console"));
            return true;
        }
        Player player = (Player) commandSender;
        String targetPlayerUsername = "";
        FundamentalsPlayerFile fundamentalsPlayerFile = FundamentalsPlayerMap.getInstance().getPlayer(player);
        boolean viewingHomesFromAnotherPlayer = false;

        String homeName = "main";
        if (strings.length > 0) {
            if (strings[0].contains(":") && strings[0].length() > 1) {
                // User is requesting another user
                if (!isPlayerAuthorized(commandSender, "fundamentals.sethome.others")) {
                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
                    return true;
                }
                viewingHomesFromAnotherPlayer = true;
                String[] homeNameParts = strings[0].split(":");
                if (homeNameParts[0].isEmpty()) {
                    String message = FundamentalsLanguage.getInstance().getMessage("home_empty_player_target");
                    commandSender.sendMessage(message);
                    return true;
                }
                if (homeNameParts.length > 1) {
                    // User is requesting to set a specific home
                    homeName = homeNameParts[1];
                } else {
                    String message = FundamentalsLanguage.getInstance().getMessage("sethome_empty_player_home_target");
                    commandSender.sendMessage(message);
                    return true;
                }
                targetPlayerUsername = homeNameParts[0];
                UUID targetPlayerUUID = getUUIDFromUsername(targetPlayerUsername);
                if (!FundamentalsPlayerMap.getInstance().isPlayerKnown(targetPlayerUUID)) {
                    String message = FundamentalsLanguage.getInstance().getMessage("player_not_found_full");
                    message = message.replace("%username%", targetPlayerUsername);
                    commandSender.sendMessage(message);
                    return true;
                }
                fundamentalsPlayerFile = FundamentalsPlayerMap.getInstance().getPlayer(targetPlayerUUID);
            } else {
                // User is requesting to set their own homes
                homeName = strings[0];
            }
            if (!verifyHomeName(homeName)) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("sethome_invalid_name"));
                return true;
            }

        }
        //Check home limit
        int limit = Integer.parseInt(String.valueOf(FundamentalsConfig.getInstance().getConfigOption("settings.multiple-homes")));
        int homeCount = fundamentalsPlayerFile.getPlayerHomes().size();

        if (!(commandSender.hasPermission("fundamentals.sethome.unlimited") || commandSender.isOp())) {
            if (!(commandSender.hasPermission("fundamentals.sethome.multiple"))) {
                if (homeCount >= 1) {
                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("sethome_full"));
                    return true;
                }
            } else {
                if (homeCount > limit + 1) {
                    String msg = FundamentalsLanguage.getInstance().getMessage("sethome_limit_reached");
                    msg = msg.replaceAll("%var1%", String.valueOf(limit));
                    commandSender.sendMessage(msg);
                    return true;
                }
            }
        }
        if (fundamentalsPlayerFile.doesHomeExist(homeName)) {
            //Home already exists
            String msg = FundamentalsLanguage.getInstance().getMessage("sethome_already_exists");
            msg = msg.replaceAll("%var1%", homeName);
            commandSender.sendMessage(msg);
            return true;
        }
        fundamentalsPlayerFile.setPlayerHome(homeName, player.getLocation());

        String msg;
        if (!viewingHomesFromAnotherPlayer) {
            msg = FundamentalsLanguage.getInstance().getMessage("sethome_set_successfully");
            msg = msg.replaceAll("%var1%", homeName);
        }
        else {
            msg = FundamentalsLanguage.getInstance().getMessage("sethome_set_successfully_others");
            msg = msg.replaceAll("%var1%", homeName);
            msg = msg.replaceAll("%var2%", targetPlayerUsername);
        }
        commandSender.sendMessage(msg);
        return true;

    }
}
