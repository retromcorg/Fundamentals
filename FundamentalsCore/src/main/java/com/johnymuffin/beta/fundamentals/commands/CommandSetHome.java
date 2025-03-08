package com.johnymuffin.beta.fundamentals.commands;

import static com.johnymuffin.beta.fundamentals.util.Utils.getUUIDFromUsername;
import static com.johnymuffin.beta.fundamentals.util.Utils.verifyHomeName;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsConfig;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;

public class CommandSetHome implements CommandExecutor {
    private final String PERMISSION_NODE = "fundamentals.sethome";
    private final String PERMISSION_NODE_OTHERS = "fundamentals.sethome.others";
    private final String PERMISSION_NODE_UNLIMITED_HOMES = "fundamentals.sethome.unlimited";
    private final String PERMISSION_NODE_MULTIPLE_HOMES = "fundamentals.sethome.multiple";

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

    private boolean canSetOtherPlayersHomes(Player sender) {
        return (
            sender.hasPermission(PERMISSION_NODE_OTHERS) ||
            sender.isOp()
        );
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!validateCommandSender(sender))
            return true;

        Player senderPlayer = (Player) sender;
        boolean canSetOtherPlayersHomes = canSetOtherPlayersHomes(senderPlayer);

        switch(args.length) {
            case 0: {
                setSendersHome(senderPlayer, "main");

                return true;
            }
            case 1: {
                String homeName = args[0];
                setSendersHome(senderPlayer, homeName);

                return true;
            }
            case 2: {
                if(!canSetOtherPlayersHomes) {
                    printUsage(senderPlayer, false);
                    return true;
                }

                String targetPlayerName = args[0];
                if(targetPlayerName.isEmpty()) {
                    sender.sendMessage(getMessage("home_empty_player_target"));
                    return true;
                }

                String homeName = args[1];
                setOtherPlayersHome(senderPlayer, targetPlayerName, homeName);

                return true;
            }
        }

        return true;
    }

    private void printUsage(Player sender, boolean canSetOtherPlayersHomes) {
        sender.sendMessage(getMessage("sethome_usage"));
        if(canSetOtherPlayersHomes)
            sender.sendMessage(getMessage("sethome_usage_staff_extra"));
    }

    private void setSendersHome(Player sender, String homeName) {
        FundamentalsPlayer targetPlayer = FundamentalsPlayerMap.getInstance().getPlayer(sender);

        String successMessage = getMessage("sethome_set_successfully");
        successMessage = successMessage.replaceAll("%homeName%", homeName);

        setHome(sender, targetPlayer, homeName, successMessage);
    }

    private void setOtherPlayersHome(Player sender, String targetPlayerName, String homeName) {
        UUID targetPlayerUUID = getUUIDFromUsername(targetPlayerName);
        if(targetPlayerUUID == null) {
            String playerNotFoundMessage = getMessage("player_not_found_full");
            playerNotFoundMessage = playerNotFoundMessage.replace("%username%", targetPlayerName);
            sender.sendMessage(playerNotFoundMessage);

            return;
        }

        FundamentalsPlayer targetPlayer = FundamentalsPlayerMap.getInstance().getPlayer(targetPlayerUUID);

        String successMessage = getMessage("sethome_set_successfully_others");
        successMessage = successMessage.replaceAll("%homeName%", homeName);
        successMessage = successMessage.replaceAll("%targetPlayerName%", targetPlayerName);

        setHome(sender, targetPlayer, homeName, successMessage);
    }

    private void setHome(Player sender, FundamentalsPlayer targetPlayer, String homeName, String successMessage) {
        if(!canSetMoreHomes(sender, targetPlayer))
            return;

        if(!verifyHomeName(homeName)) {
            sender.sendMessage(getMessage("sethome_invalid_name"));
            return;
        }

        if(targetPlayer.doesHomeExist(homeName)) {
            String alreadyExistsMessage = getMessage("sethome_already_exists"); 
            alreadyExistsMessage = alreadyExistsMessage.replaceAll("%homeName%", homeName);
            sender.sendMessage(alreadyExistsMessage);

            return;
        }

        sender.sendMessage(successMessage);
        targetPlayer.setPlayerHome(homeName, sender.getLocation());
    }

    private boolean canSetInfiniteHomes(Player sender) {
        return (
            sender.hasPermission(PERMISSION_NODE_UNLIMITED_HOMES) ||
            sender.isOp()
        );
    }

    private boolean canSetMultipleHomes(Player sender, int homeCount) {
        // not checking for OP since the unlimited check already tests for that
        if(!sender.hasPermission(PERMISSION_NODE_MULTIPLE_HOMES))
            return false;

        int homeLimit = Integer.parseInt(
            String.valueOf(FundamentalsConfig.getInstance().getConfigOption("settings.multiple-homes"))
        );
        if(homeCount < homeLimit)
            return true;

        String homeLimitMessage = getMessage("sethome_limit_reached");
        homeLimitMessage = homeLimitMessage.replaceAll("%homeLimit%", String.valueOf(homeLimit));
        sender.sendMessage(homeLimitMessage);

        return false;
    }

    private boolean canSetMoreHomes(Player sender, FundamentalsPlayer targetPlayer) {
        if(canSetInfiniteHomes(sender))
            return true;

        int homeCount = targetPlayer.getPlayerHomes().size();
        if(canSetMultipleHomes(sender, homeCount))
            return true;
 
        // have no perms, so checking if they have their singular allowed home set
        if(homeCount >= 1) {
            sender.sendMessage(getMessage("sethome_full"));
            return false;
        }

        return true;
    }

    private String getMessage(String key) {
        return FundamentalsLanguage.getInstance().getMessage(key);
    }
}
