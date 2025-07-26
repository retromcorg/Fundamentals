// File: CommandMoveHome.java
package com.johnymuffin.beta.fundamentals.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;

public class CommandMoveHome implements CommandExecutor {
    @SuppressWarnings("FieldCanBeLocal")
    private final String PERMISSION_NODE = "fundamentals.sethome";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(getMessage("unavailable_to_console"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission(PERMISSION_NODE) && !player.isOp()) {
            player.sendMessage(getMessage("no_permission"));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("Usage: /movehome <home>");
            return true;
        }

        String homeName = args[0];
        FundamentalsPlayer fPlayer = FundamentalsPlayerMap.getInstance().getPlayer(player);

        if (!fPlayer.doesHomeExist(homeName)) {
            String notFound = getMessage("home_not_on_record").replace("%homeName%", homeName);
            player.sendMessage(notFound);
            return true;
        }

        fPlayer.setPlayerHome(homeName, player.getLocation());
        String moved = getMessage("movehome_set_successfully").replace("%homeName%", homeName);
        player.sendMessage(moved);
        return true;
    }

    @SuppressWarnings("deprecation")
    private String getMessage(String key) {
        return FundamentalsLanguage.getInstance().getMessage(key);
    }
}
