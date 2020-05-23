package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
import com.johnymuffin.beta.fundamentals.playerdata.FundamentalsPlayerFile;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsConfig;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.johnymuffin.beta.fundamentals.CommandUtils.formatColor;
import static com.johnymuffin.beta.fundamentals.CommandUtils.verifyHomeName;

public class CommandSetHome implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender.hasPermission("fundamentals.sethome") || commandSender.isOp())) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Sorry, console can't run this command.");
            return true;
        }
        Player player = (Player) commandSender;
        FundamentalsPlayerFile fundamentalsPlayerFile = FundamentalsPlayerMap.getInstance().getPlayer(player);

        String homeName = "main";
        if (strings.length > 0) {
            homeName = strings[0];
            if (!verifyHomeName(homeName)) {
                player.sendMessage(formatColor("&4Only letters can be used in home names A-Z"));
                return true;
            }
        }
        //Check home limit
        int limit = Integer.valueOf(String.valueOf(FundamentalsConfig.getInstance().getConfigOption("settings.multiple-homes")));
        int homeCount = fundamentalsPlayerFile.getPlayerHomes().size();

        if (!(commandSender.hasPermission("fundamentals.sethome.unlimited") || commandSender.isOp())) {
            if (!(commandSender.hasPermission("fundamentals.sethome.multiple"))) {
                if (homeCount >= 1) {
                    commandSender.sendMessage(formatColor("&4Sorry, you are already have a home set"));
                    return true;
                }
            } else {
                if (homeCount < limit + 1) {
                    commandSender.sendMessage(formatColor("&6Sorry, you are already at your limit of &4" + limit + " &6homes"));
                    return true;
                }
            }
        }
        if (fundamentalsPlayerFile.doesHomeExist(homeName)) {
            //Home already exists
            player.sendMessage(formatColor("&6Sorry, home \"&b" + homeName + "&6\" already exists. Please delete the existing home before creating a new one with the same name"));
            return true;
        }
        fundamentalsPlayerFile.setPlayerHome(homeName, player.getLocation());
        commandSender.sendMessage(formatColor("&6Your home \"&b" + homeName + "\" &6has been set"));
        return true;

    }
}
