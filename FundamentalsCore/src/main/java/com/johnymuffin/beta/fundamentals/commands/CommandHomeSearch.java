package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.johnymuffin.beta.fundamentals.util.Utils.*;

public class CommandHomeSearch implements CommandExecutor {
    private Fundamentals plugin;

    public CommandHomeSearch(Fundamentals plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender.hasPermission("fundamentals.homesearch") || commandSender.isOp())) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("unavailable_to_console"));
            return true;
        }

        Player player = (Player) commandSender;
        FundamentalsPlayer targetPlayer;
        int range;

        if (strings.length == 0) {
            // No arguments, show usage
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("homesearch_usage"));
            return true;
        }

        // Command format: /homesearch <radius>
        if (strings.length == 1) {
            try {
                range = Integer.parseInt(strings[0]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("homesearch_usage"));
                return true;
            }

            targetPlayer = FundamentalsPlayerMap.getInstance().getPlayer(player);
            searchHomesInRadius(player, null, range);
            return true;
        }

        // Command format: /homesearch <username> <radius>
        if (strings.length == 2) {
            String username = strings[0];
            UUID targetPlayerUUID = getUUIDFromUsername(username);

            if (targetPlayerUUID == null || !FundamentalsPlayerMap.getInstance().isPlayerKnown(targetPlayerUUID)) {
                String msg = FundamentalsLanguage.getInstance().getMessage("player_not_found_full");
                msg = msg.replaceAll("%username%", username);
                commandSender.sendMessage(msg);
                return true;
            }

            try {
                range = Integer.parseInt(strings[1]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("homesearch_invalid_radius"));
                return true;
            }

            targetPlayer = FundamentalsPlayerMap.getInstance().getPlayer(targetPlayerUUID);
            searchHomesInRadius(player, targetPlayer, range);
            return true;
        }

        // If more than 2 arguments
        commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("homesearch_usage"));
        return true;
    }

    private void searchHomesInRadius(Player player, FundamentalsPlayer targetPlayer, int radius) {
        Location playerLocation = player.getLocation();

        ArrayList<String> homes = new ArrayList<>();
        HashMap<String, ArrayList<String>> homesByOwner = new HashMap<>();

        // when searching for one specific player
        if (targetPlayer != null) {
            homes = targetPlayer.getPlayerHomes();
            processPlayerHomes(targetPlayer, homes, playerLocation, radius, homesByOwner);
        } else { // search through every player on the server
            ArrayList<UUID> knownPlayers = FundamentalsPlayerMap.getInstance().getKnownPlayers();

            for (UUID uuid : knownPlayers) {
                FundamentalsPlayer currentPlayer = FundamentalsPlayerMap.getInstance().getPlayer(uuid);
                homes = currentPlayer.getPlayerHomes();
                processPlayerHomes(currentPlayer, homes, playerLocation, radius, homesByOwner);
            }
        }

        // Display results
        if (homesByOwner.isEmpty()) {
            String msg = FundamentalsLanguage.getInstance().getMessage("homesearch_no_homes_found");
            msg = msg.replaceAll("%radius%", String.valueOf(radius));
            player.sendMessage(msg);
        } else {
            int totalHomes = homesByOwner.values().stream().mapToInt(ArrayList::size).sum();
            String msg = FundamentalsLanguage.getInstance().getMessage("homesearch_found_homes");
            msg = msg.replaceAll("%count%", String.valueOf(totalHomes));
            msg = msg.replaceAll("%radius%", String.valueOf(radius));
            player.sendMessage(msg);

            // Display homes grouped by player
            for (Map.Entry<String, ArrayList<String>> entry : homesByOwner.entrySet()) {
                String ownerName = entry.getKey();
                ArrayList<String> ownerHomes = entry.getValue();
                String homeList = String.join(", ", ownerHomes);

                msg = FundamentalsLanguage.getInstance().getMessage("homesearch_found_entries");
                msg = msg.replaceAll("%player%", ownerName);
                msg = msg.replaceAll("%homes%", homeList);
                player.sendMessage(msg);
            }
        }
    }

    // Helper method to process homes for a player and group them by owner
    private void processPlayerHomes(FundamentalsPlayer player, ArrayList<String> homes, Location playerLocation, int radius, HashMap<String, ArrayList<String>> homesByOwner) {
        for (String homeName : homes) {
            if (!player.isHomeInValidWorld(homeName)) {
                continue;
            }

            Location homeLocation = player.getPlayerHome(homeName);

            // only show homes in the same dimension as the player who ran the command
            if (!homeLocation.getWorld().equals(playerLocation.getWorld())) {
                continue;  // Skip this home if they are in different dimensions
            }

            double distance = homeLocation.distance(playerLocation);

            if (distance <= radius) {
                String ownerName = getPlayerName(player.getUuid());

                if (ownerName == null){
                    continue; // skip over null player names
                }

                homesByOwner.putIfAbsent(ownerName, new ArrayList<>());
                homesByOwner.get(ownerName).add(homeName);
            }
        }
    }

}
