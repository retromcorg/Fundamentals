package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;
import static com.johnymuffin.beta.fundamentals.util.Utils.*;

public class CommandBroadcast implements CommandExecutor {

    private Fundamentals plugin;

    public CommandBroadcast(Fundamentals plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isPlayerAuthorized(commandSender, "fundamentals.broadcast")) {
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("no_permission"));
            return true;
        }
        if (strings.length == 0) {
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("broadcast_info"));
            return true;
        }
        String message = getFullArg(strings, 0);
        String broadcast = formatColor(plugin.getFundamentalConfig().getConfigString("settings.chat.broadcast-format")
                .replace("{message}", message));
        Bukkit.broadcastMessage(broadcast);
        return true;
    }
}
