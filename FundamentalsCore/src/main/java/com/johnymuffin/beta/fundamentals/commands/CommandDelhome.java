package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;
import static com.johnymuffin.beta.fundamentals.util.Utils.getUUIDFromUsername;

public class CommandDelhome implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isPlayerAuthorized(commandSender,"fundamentals.delhome")) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("unavailable_to_console"));
            return true;
        }
        Player player = (Player) commandSender;
        String targetPlayerUsername;
        FundamentalsPlayer fundamentalsPlayer;
        String homeName;

        if (strings.length == 0) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("delhome_info"));
            return true;
        }
        else {
            if (strings[0].contains(":") && strings[0].length() > 1) {
                // User is requesting another user
                if (!isPlayerAuthorized(commandSender, "fundamentals.delhome.others")) {
                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
                    return true;
                }
                String[] homeNameParts = strings[0].split(":");
                if (homeNameParts[0].isEmpty()) {
                    String message = FundamentalsLanguage.getInstance().getMessage("home_empty_player_target");
                    commandSender.sendMessage(message);
                    return true;
                }
                if (homeNameParts.length > 1) {
                    // User is requesting to delete a specific home
                    homeName = homeNameParts[1];
                } else {
                    String message = FundamentalsLanguage.getInstance().getMessage("delhome_empty_player_home_target");
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
                fundamentalsPlayer = FundamentalsPlayerMap.getInstance().getPlayer(targetPlayerUUID);
            } else {
                // User is requesting their own homes
                fundamentalsPlayer = FundamentalsPlayerMap.getInstance().getPlayer(player);
                homeName = strings[0];
            }
        }

        if(!fundamentalsPlayer.doesHomeExist(homeName)) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("delhome_unknown_home"));
            return true;
        }
        if(fundamentalsPlayer.removeHome(homeName)) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("delhome_unknown_successful"));
        } else {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("delhome_unknown_unsuccessful"));
        }
        
        return true;
    }
}
