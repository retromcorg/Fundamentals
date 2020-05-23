package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.johnymuffin.beta.fundamentals.CommandUtils.getPlayerFromString;

public class CommandAFK implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender.hasPermission("fundamentals.afk") || commandSender.isOp())) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }
        //Check if user is trying to heal another user
        if (strings.length > 0) {
            if (!(commandSender.hasPermission("fundamentals.afk.others") || commandSender.isOp())) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
                return true;
            }
            Player player = getPlayerFromString(strings[0]);
            if (player == null) {
                String message = FundamentalsLanguage.getInstance().getMessage("player_not_found_full");
                message = message.replaceAll("%username%", strings[0]);
                commandSender.sendMessage(message);
                return true;
            }
            FundamentalsPlayerMap.getInstance().getPlayer(player).toggleAFK();
            String message = FundamentalsLanguage.getInstance().getMessage("set_player_afk");
            message = message.replaceAll("%username%", player.getName());
            commandSender.sendMessage(message);
            return true;
        } else if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Sorry, console can't heal itself.");
            return true;
        }
        Player player = (Player) commandSender;
        FundamentalsPlayerMap.getInstance().getPlayer(player).toggleAFK();
        return true;
    }
}
