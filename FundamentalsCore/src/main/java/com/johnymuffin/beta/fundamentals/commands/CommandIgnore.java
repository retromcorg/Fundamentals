package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;

public class CommandIgnore implements CommandExecutor {

    private Fundamentals plugin;

    public CommandIgnore(Fundamentals plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isPlayerAuthorized(commandSender,"fundamentals.ignore")) {
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("no_permission"));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("unavailable_to_console"));
            return true;
        }
        if (strings.length == 0) {
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("ignore_info"));
            return true;
        }
        UUID uuid = plugin.getPlayerCache().getUUIDFromUsername(strings[0]);
        if (uuid == null) {
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("player_not_found_full")
                    .replace("%username%", strings[0]));
            return true;
        }
        if (uuid.equals(((Player) commandSender).getUniqueId())){
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("ignore_self"));
            return true;
        }
        FundamentalsPlayer fPlayer = plugin.getPlayerMap().getPlayer((Player) commandSender);
        if (fPlayer.getIgnoreList().contains(uuid)) {
            fPlayer.removeUserIgnore(uuid);
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("ignore_remove")
                    .replace("%player%", plugin.getPlayerCache().getUsernameFromUUID(uuid)));
        } else {
            fPlayer.addUserIgnore(uuid);
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("ignore_add")
                    .replace("%player%", plugin.getPlayerCache().getUsernameFromUUID(uuid)));
        }
        return true;
    }
}
