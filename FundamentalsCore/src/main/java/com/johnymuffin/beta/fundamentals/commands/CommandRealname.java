package com.johnymuffin.beta.fundamentals.commands;

import com.earth2me.essentials.Essentials;
import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;

public class CommandRealname implements CommandExecutor {

    private Fundamentals plugin;

    public CommandRealname(Fundamentals plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isPlayerAuthorized(commandSender, "fundamentals.realname")) {
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("no_permission"));
            return true;
        }
        if (strings.length == 0) {
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("realname_info"));
            return true;
        }
        int found = 0;
        String arg = strings[0].toLowerCase();
        Essentials ess = Bukkit.getPluginManager().isPluginEnabled("Essentials") ?
                (Essentials) Bukkit.getPluginManager().getPlugin("Essentials") : null;
        for (Player p : Bukkit.getOnlinePlayers()) {
            FundamentalsPlayer fPlayer = plugin.getPlayerMap().getPlayer(p);
            if (ess != null && ess.getUser(p).isHidden()) {
                continue;
            }
            String nickname = fPlayer.getNickname() != null ?
                    fPlayer.getNickname().replaceAll("&([0-9a-f])", "").toLowerCase() : p.getName();
            if (!arg.equalsIgnoreCase(nickname) && !arg.equalsIgnoreCase(p.getName())) continue;
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("realname_successful")
                    .replace("%displayname%", fPlayer.getFullDisplayName())
                    .replace("%player%", p.getName()));
            found++;
        }
        if (found == 0)
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("realname_player_not_found")
                    .replace("%nickname%", arg));
        return true;
    }
}
