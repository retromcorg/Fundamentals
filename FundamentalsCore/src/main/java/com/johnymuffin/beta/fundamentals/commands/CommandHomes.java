package com.johnymuffin.beta.fundamentals.commands;

import org.bukkit.entity.Player;

import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import com.johnymuffin.beta.fundamentals.*;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.settings.*;

import java.util.Collections;
import java.util.Arrays;
import java.util.ArrayList;

import static com.johnymuffin.beta.fundamentals.util.Utils.formatColor;

public class CommandHomes implements CommandExecutor {
    private Fundamentals plugin;

    public CommandHomes(Fundamentals plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender.hasPermission("fundamentals.homes") || commandSender.isOp())) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Sorry, console can't run this command.");
            return true;
        }

        int page;

        if (strings.length == 0) {
            page = 1;
        }
        else {
            try {
                // check if the home page number provided by the user is 0
                if (Integer.parseInt(strings[0]) != 0) {
                    page = Integer.parseInt(strings[0]);
                } else {
                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("homes_invalid_usage"));
                    return true;
                }
            } catch (NumberFormatException e) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("homes_invalid_usage"));
                return true;
            }
        }
        FundamentalsPlayer fPlayer = FundamentalsPlayerMap.getInstance().getPlayer((Player) commandSender);
        ArrayList<String> homeList = fPlayer.getPlayerHomes();
        Collections.sort(homeList);

        if (homeList.size() == 0) {
            //no entries in home list
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("home_non_recorded"));
            return true;
        }
        int homesPerPage = FundamentalsConfig.getInstance().getConfigInteger("settings.homes-per-page");
        int pageCount = (int) Math.ceil((double) homeList.size() / homesPerPage);

        if (page <= pageCount && page > 0) {
            String message = FundamentalsLanguage.getInstance().getMessage("homes_list");
            message = message.replace("%var1%", String.valueOf(page));
            message = message.replace("%var2%", String.valueOf(pageCount));
            commandSender.sendMessage(message);

            String homes = "";
            for (int i = page * homesPerPage - homesPerPage; (i < page * homesPerPage) && i < homeList.size() && homeList.get(i) != null; i++) {
                String home = homeList.get(i);
                if (fPlayer.isHomeInValidWorld(home)) {
                    homes = homes + "&a" + home + "&6, ";
                } else {
                    homes = homes + "&4" + home + "&6, ";
                }
            }
            homes = homes.substring(0, homes.length() - 2);
            commandSender.sendMessage(formatColor(homes));
        } else {
            // specified page count too high or invalid
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("homes_invalid_usage"));
            return true;
        }

        return true;
    }
}
