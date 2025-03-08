package com.johnymuffin.beta.fundamentals.commands;

import static com.johnymuffin.beta.fundamentals.util.Utils.getPlayerName;
import static com.johnymuffin.beta.fundamentals.util.Utils.getSafeDestination;
import static com.johnymuffin.beta.fundamentals.util.Utils.getUUIDFromUsername;

import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;

public class CommandHome implements CommandExecutor {
    private final String PERMISSION_NODE = "fundamentals.home";
    private final String PERMISSION_NODE_OTHERS = "fundamentals.home.others";

    private Fundamentals plugin;

    public CommandHome(Fundamentals plugin) {
        this.plugin = plugin;
    }

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
        boolean canSeeOtherPlayerHomes = canSeeOtherPlayerHomes(senderPlayer);

        switch(args.length) {
            case 1: {
                String homeName = args[0];
                teleportToSendersHome(senderPlayer, homeName);

                return true;
            }
            case 2: {
                if (!canSeeOtherPlayerHomes) {
                    sender.sendMessage(getMessage("no_permission"));
                    return true;
                }

                String otherPlayer = args[0];
                if(otherPlayer.isEmpty()) {
                    String message = getMessage("home_empty_player_target");
                    sender.sendMessage(message);
                    
                    return true;
                }
                
                String homeName = args[1];
                teleportToOtherPlayerHome(senderPlayer, homeName, otherPlayer);
                
                return true;
            }
        }

        printUsage(senderPlayer, canSeeOtherPlayerHomes);
        return true;
    }

    private boolean canUseCommand(CommandSender sender) {
        return (
            sender.hasPermission(PERMISSION_NODE) ||
            sender.isOp()
        );
    }

    private boolean canSeeOtherPlayerHomes(CommandSender sender) {
        return (
            sender.hasPermission(PERMISSION_NODE_OTHERS) ||
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

    private void printUsage(Player sender, boolean canSeeOtherPlayersHomes) {
        sender.sendMessage(getMessage("home_usage"));
        if(canSeeOtherPlayersHomes)
            sender.sendMessage(getMessage("home_usage_staff_extra"));
    }

    private void teleportToSendersHome(Player sender, String homeName) {
        FundamentalsPlayer targetPlayer = FundamentalsPlayerMap.getInstance().getPlayer(sender);

        String successMessage = getMessage("home_teleport_successfully");
        successMessage = successMessage.replaceAll("%homeName%", homeName);
        
        teleportToHome(sender, targetPlayer, homeName, successMessage);
    }
    
    private void teleportToOtherPlayerHome(Player sender, String homeName, String targetPlayerName) {
        UUID targetPlayerUUID = getUUIDFromUsername(targetPlayerName);
        if(targetPlayerUUID == null) {
            String playerNotFoundMessage = getMessage("player_not_found_full");
            playerNotFoundMessage = playerNotFoundMessage.replace("%username%", targetPlayerName);
            sender.sendMessage(playerNotFoundMessage);

            return;
        }

        FundamentalsPlayer targetPlayer = FundamentalsPlayerMap.getInstance().getPlayer(targetPlayerUUID);
        
        String successMessage = getMessage("home_teleport_successfully_others");
        successMessage = successMessage.replaceAll("%targetPlayerName%", targetPlayerName);
        successMessage = successMessage.replaceAll("%homeName%", homeName);
        
        teleportToHome(sender, targetPlayer, homeName, successMessage);
    }

    private void teleportToHome(Player sender, FundamentalsPlayer targetPlayer, String homeName, String successMessage) {
        if(!validateTeleportToHome(sender, targetPlayer, homeName))
            return;

        Location home = targetPlayer.getPlayerHome(homeName);

        Location safeLocation = getSafeLocation(sender, home);
        if (safeLocation == null)
            return;
        
        sender.teleport(safeLocation);
        sender.sendMessage(successMessage);

        // uses UUID as a backup
        String targetName = getPlayerNameFromFundamentalsPlayer(targetPlayer);
        plugin.debugLogger(Level.INFO, sender.getName() + " has teleported to a home owned by " + targetName + " called " + homeName, 2);
    }

    private Location getSafeLocation(Player sender, Location destination) {
        try {
            return getSafeDestination(destination);
        } catch (Exception e) {
            String errorMessage = getMessage("generic_error_player");
            errorMessage = errorMessage.replaceAll("%var1%", e.getMessage());

            sender.sendMessage(errorMessage);
            return null;
        }
    }

    private boolean validateTeleportToHome(Player sender, FundamentalsPlayer targetPlayer, String homeName) {
        if (!targetPlayer.doesHomeExist(homeName)) {
            String homeNotFoundMessage = getMessage("home_not_on_record");
            homeNotFoundMessage = homeNotFoundMessage.replace("%homeName%", homeName);

            sender.sendMessage(homeNotFoundMessage);
            return false;
        }

        if (!targetPlayer.isHomeInValidWorld(homeName)) {
            sender.sendMessage(getMessage("home_in_invalid_world"));
            return false;
        }

        if (sender.isSleeping()) {
            sender.sendMessage(getMessage("home_is_sleeping"));
            return false;
        }

        return true;
    }

    private String getMessage(String key) {
        return FundamentalsLanguage.getInstance().getMessage(key);
    }

    private String getPlayerNameFromFundamentalsPlayer(FundamentalsPlayer player) {
        UUID uuid = player.getUuid();

        String name = getPlayerName(uuid);
        if(name == null)
            return uuid.toString();

        return name;
    }
}
