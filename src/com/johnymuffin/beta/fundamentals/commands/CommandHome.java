package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static com.johnymuffin.beta.fundamentals.CommandUtils.formatColor;

public class CommandHome implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender.hasPermission("fundamentals.home") || commandSender.isOp())) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Sorry, console can't run this command.");
            return true;
        }
        Player player = (Player) commandSender;
        FundamentalsPlayer fundamentalsPlayer = FundamentalsPlayerMap.getInstance().getPlayer(player);

        if (strings.length == 0) {
            //List Homes
            ArrayList<String> homes = fundamentalsPlayer.getPlayerHomes();
            if (homes.size() == 0) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("&6Sorry, you have no homes on record. Please set one with /sethome"));
                return true;
            }
            String msg = "&6Home List: ";
            for (String homeName : homes) {
                if (fundamentalsPlayer.isHomeInValidWorld(homeName)) {
                    msg = msg + "&a" + homeName + "&6, ";
                } else {
                    msg = msg + "&4" + homeName + "&6, ";
                }
            }
            msg = msg.substring(0, msg.length() - 1);
            commandSender.sendMessage(formatColor(msg));
            return true;
        } else {
            String homeName = strings[0];
            if (!fundamentalsPlayer.doesHomeExist(homeName)) {
                commandSender.sendMessage(formatColor("&6We couldn't find a home with the name \"&b" + strings[0] + "&6\". Do /home to get a list of homes"));
                return true;
            }
            if(!fundamentalsPlayer.isHomeInValidWorld(homeName)) {
                commandSender.sendMessage(formatColor("&4Sorry, your home is in a invalid world"));
                return true;
            }
            Location home = fundamentalsPlayer.getPlayerHome(strings[0]);
            player.teleport(home);
            commandSender.sendMessage(formatColor("&6You have been teleported to &b" + strings[0]));
            return true;
        }


    }
}
