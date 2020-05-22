package com.johnymuffin.beta.fundamentals.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.johnymuffin.beta.fundamentals.CommandUtils.getPlayerFromString;

public class CommandHeal implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender.hasPermission("fundamentals.heal") || commandSender.isOp())) {
            commandSender.sendMessage(ChatColor.RED + "Sorry, you don't have permission for this command.");
            return true;
        }
        //Check if user is trying to heal another user
        if (strings.length > 0) {
            if (!(commandSender.hasPermission("fundamentals.heal.others") || commandSender.isOp())) {
                commandSender.sendMessage(ChatColor.RED + "Sorry, you don't have permission for this command.");
                return true;
            }
            Player player = getPlayerFromString(strings[0]);
            if (player == null) {
                commandSender.sendMessage(ChatColor.RED + "Can't find a player called " + ChatColor.BLUE + strings[0]);
                return true;
            }
            healPlayer(player);
            commandSender.sendMessage(ChatColor.RED + "Healed a player called " + ChatColor.BLUE + player.getName());
            return true;
        } else if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Sorry, console can't heal itself.");
            return true;
        }
        Player player = (Player) commandSender;
        healPlayer(player);
        return true;
    }


    public void healPlayer(Player player) {
        if (!player.isDead()) {
            player.setHealth(20);
            player.sendMessage(ChatColor.YELLOW + "You have been healed");
        }


    }
}
