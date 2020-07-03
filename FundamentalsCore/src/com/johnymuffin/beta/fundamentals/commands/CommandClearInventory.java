package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.johnymuffin.beta.fundamentals.util.Utils.getPlayerFromString;
import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;

public class CommandClearInventory implements CommandExecutor {
    private Fundamentals plugin;
    private FundamentalsLanguage lang;

    public CommandClearInventory(Fundamentals plugin) {
        this.plugin = plugin;
        this.lang = plugin.getFundamentalsLanguageConfig();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isPlayerAuthorized(commandSender, "fundamentals.clearinventory")) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }

        if (strings.length > 0) {
            if (!isPlayerAuthorized(commandSender, "fundamentals.clearinventory.others")) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
                return true;
            }
            Player giveTo = getPlayerFromString(strings[0]);
            if (giveTo == null) {
                String message = FundamentalsLanguage.getInstance().getMessage("player_not_found_full");
                message = message.replace("%username%", strings[0]);
                commandSender.sendMessage(message);
                return true;
            }
            giveTo.getInventory().clear(); //Clear Inventory
            giveTo.sendMessage(lang.getMessage("clearinventory_notice"));
            commandSender.sendMessage(lang.getMessage("clearinventory_successfully"));
        } else {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("unavailable_to_console"));
                return true;
            }
            Player giveTo = (Player) commandSender;
            giveTo.getInventory().clear(); //Clear Inventory
            giveTo.sendMessage(lang.getMessage("clearinventory_notice"));
        }
        return true;


    }


}