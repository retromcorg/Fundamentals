package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.johnymuffin.beta.fundamentals.util.CommandUtils.getPlayerFromString;
import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;

public class CommandInvsee implements CommandExecutor {
    private Fundamentals plugin;

    public CommandInvsee(Fundamentals plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isPlayerAuthorized(commandSender, "fundamentals.invsee")) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("unavailable_to_console"));
            return true;
        }
        Player player = (Player) commandSender;
        if (strings.length == 0) {
            if (!plugin.isPlayerInvSee(player.getUniqueId())) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("invsee_info"));
                return true;
            }

            player.getInventory().setContents(plugin.disableInvSee(player.getUniqueId()));
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("invsee_disable"));
            return true;

        }

        if (strings.length > 0) {
            //Check if user is already in InvSee
            if (plugin.isPlayerInvSee(player.getUniqueId())) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("invsee_already"));
                return true;
            }
            Player giveTo = getPlayerFromString(strings[0]);
            if (giveTo == null) {
                String message = FundamentalsLanguage.getInstance().getMessage("player_not_found_full");
                message = message.replace("%username%", strings[0]);
                commandSender.sendMessage(message);
                return true;
            }
            plugin.enableInvSee(player.getUniqueId(), player.getInventory().getContents());
            player.getInventory().setContents(giveTo.getInventory().getContents());

            String message = FundamentalsLanguage.getInstance().getMessage("invsee_enable");
            message = message.replace("%var1%", giveTo.getName());
            commandSender.sendMessage(message);
            return true;

        }
        return true;


    }


}