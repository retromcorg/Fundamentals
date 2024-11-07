package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;

public class CommandSeen implements CommandExecutor {

    private Fundamentals plugin;

    public CommandSeen(Fundamentals plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isPlayerAuthorized(commandSender, "fundamentals.seen")) {
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("no_permission"));
            return true;
        }
        if (strings.length == 0) {
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("seen_info"));
            return true;
        }
        List<Player> matches = Bukkit.matchPlayer(strings[0]);
        if (matches.isEmpty()) {
            UUID uuid = plugin.getPlayerCache().getUUIDFromUsername(strings[0]);
            if (uuid == null) {
                commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("player_not_found_full")
                        .replace("%username%", strings[0]));
                return true;
            }
            FundamentalsPlayer fPlayer = plugin.getPlayerMap().getPlayer(uuid);
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("seen_offline")
                    .replace("%player%", plugin.getPlayerCache().getUsernameFromUUID(uuid))
                    .replace("%time%", Utils.formatDateDiff(fPlayer.getLastSeen()*1000, System.currentTimeMillis())));
        } else {
            FundamentalsPlayer fPlayer = plugin.getPlayerMap().getPlayer(matches.get(0));
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("seen_online")
                    .replace("%displayname%", fPlayer.getFullDisplayName())
                    .replace("%time%", Utils.formatDateDiff(fPlayer.getLoginTime(), System.currentTimeMillis())));
        }
        return true;
    }
}
