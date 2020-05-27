package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;

public class CommandDelhome implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isPlayerAuthorized(commandSender,"fundamentals.delhome")) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("unavailable_to_console"));
            return true;
        }
        Player player = (Player) commandSender;
        FundamentalsPlayer fundamentalsPlayer = FundamentalsPlayerMap.getInstance().getPlayer(player);

        if (strings.length == 0) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("delhome_info"));
            return true;
        }
        String homeName = strings[0];
        if(!fundamentalsPlayer.doesHomeExist(homeName)) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("delhome_unknown_home"));
            return true;
        }
        if(fundamentalsPlayer.removeHome(homeName)) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("delhome_unknown_successful"));
        } else {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("delhome_unknown_unsuccessful"));
        }


        return true;
    }
}
