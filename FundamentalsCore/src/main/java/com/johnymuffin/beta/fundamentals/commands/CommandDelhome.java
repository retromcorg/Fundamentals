package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.johnymuffin.beta.fundamentals.util.Utils.getUUIDFromUsername;

public class CommandDelhome implements CommandExecutor {
    private final String PERMISSION_NODE = "fundamentals.delhome";
    private final String PERMISSION_NODE_OTHERS = "fundamentals.delhome.others";

    @Override
    public boolean onCommand(
        CommandSender sender,
        Command command,
        String label,
        String[] args
    ) {
        if(!validateCommandSender(sender))
            return true;

        Player senderPlayer = (Player) sender;
        boolean canDeleteOtherPlayersHomes = canDeleteOtherPlayersHomes(senderPlayer);

        switch(args.length) {
            case 0: {
                printUsage(senderPlayer, canDeleteOtherPlayersHomes);

                return true;
            }
            case 1: {
                String homeName = args[0];

                deleteSendersHome(senderPlayer, homeName);
                return true;
            }
            case 2: {
                if(!canDeleteOtherPlayersHomes) {
                    printUsage(senderPlayer, false);
                    return true;
                }

                String targetPlayerName = args[0];
                if(targetPlayerName.isEmpty()) {
                    sender.sendMessage(getMessage("home_empty_player_target"));
                    return true;
                }

                String homeName = args[1];
                deleteOtherPlayersHome(senderPlayer, targetPlayerName, homeName);

                return true;
            }
        }

        printUsage(senderPlayer, canDeleteOtherPlayersHomes);
        return true;
    }

    private boolean canUseCommand(CommandSender sender) {
        return (
            sender.hasPermission(PERMISSION_NODE) ||
            sender.isOp()
        );
    }

    private boolean validateCommandSender(CommandSender sender) {
        if (!canUseCommand(sender)) {
            sender.sendMessage(getMessage("no_permission"));
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(getMessage("unavailable_to_console"));
            return false;
        }

        return true;
    }

    private void printUsage(Player sender, boolean canDeleteOtherPlayersHomes) {
        sender.sendMessage(getMessage("delhome_usage"));
        if(canDeleteOtherPlayersHomes)
            sender.sendMessage(getMessage("delhome_usage_staff_extra"));
    }

    private boolean canDeleteOtherPlayersHomes(Player sender) {
        return (
            sender.hasPermission(PERMISSION_NODE_OTHERS) ||
            sender.isOp()
        );
    }

    private void deleteSendersHome(Player sender, String homeName) {
        FundamentalsPlayer targetPlayer = FundamentalsPlayerMap.getInstance().getPlayer(sender);

        String successMessage = getMessage("delhome_deleted_successfully");
        successMessage = successMessage.replace("%homeName%", homeName);

        deleteHome(sender, targetPlayer, homeName, successMessage);
    }

    private void deleteOtherPlayersHome(Player sender, String targetPlayerName, String homeName) {
        UUID targetPlayerUUID = getUUIDFromUsername(targetPlayerName);
        if(targetPlayerUUID == null) {
            String playerNotFoundMessage = getMessage("player_not_found_full");
            playerNotFoundMessage = playerNotFoundMessage.replace("%username%", targetPlayerName);
            sender.sendMessage(playerNotFoundMessage);

            return;
        }

        FundamentalsPlayer targetPlayer = FundamentalsPlayerMap.getInstance().getPlayer(targetPlayerUUID);

        String successMessage = getMessage("delhome_deleted_successfully_others");
        successMessage = successMessage.replaceAll("%homeName%", homeName);
        successMessage = successMessage.replaceAll("%targetPlayerName%", targetPlayerName);

        deleteHome(sender, targetPlayer, homeName, successMessage);
    }

    private void deleteHome(Player sender, FundamentalsPlayer targetPlayer, String homeName, String successMessage) {
        if(!targetPlayer.doesHomeExist(homeName)) {
            String homeNotFoundMessage = getMessage("home_not_on_record");
            homeNotFoundMessage = homeNotFoundMessage.replace("%homeName%", homeName);
            sender.sendMessage(homeNotFoundMessage);

            return;
        }

        if(targetPlayer.removeHome(homeName))
            sender.sendMessage(successMessage);
        else
            sender.sendMessage(getMessage("delhome_unsuccessful"));
    }

    private String getMessage(String key) {
        return FundamentalsLanguage.getInstance().getMessage(key);
    }
}
