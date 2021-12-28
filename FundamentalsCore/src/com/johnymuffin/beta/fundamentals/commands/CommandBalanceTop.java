package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;
import static com.johnymuffin.beta.fundamentals.util.Utils.formatColor;
import static com.johnymuffin.beta.fundamentals.util.Utils.isInt;

public class CommandBalanceTop implements CommandExecutor {

    private Fundamentals plugin;

    private String balanceTop;
    private Long cacheTime;

    public CommandBalanceTop(Fundamentals plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isPlayerAuthorized(commandSender, "fundamentals.balancetop")) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }

        if (strings.length == 0) {
            printPage(commandSender, 0);
        } else if (strings.length > 0) {
            if (!isInt(strings[0])) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("balancetop_invalid_integer"));
                return true;
            }
            int pageNumber = Integer.parseInt(strings[0]);
            printPage(commandSender, pageNumber - 1);
        }
        return true;
    }

    //First Page Number Is Actually 0
    private void printPage(CommandSender commandSender, int pageNumber) {
        int startingPoint = pageNumber * 10 - 1; //Subtract 1 because mappos start at 0
        int economyCacheSize = plugin.getEconomyCache().getEconomyCache().size();
        if (pageNumber < 0) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("balancetop_too_low"));
            return;
        }
        if (pageNumber >= ((int) economyCacheSize / 10)) { // It has to be greater then or equal because technically the first page is zero.
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("balancetop_too_high"));
            return;
        }
        commandSender.sendMessage(ChatColor.AQUA + "Balancetop " + ChatColor.GOLD + "Page " + (pageNumber + 1) + "/" + ((int) economyCacheSize / 10));
        int index = 0;
        for (Iterator<Map.Entry<Integer, UUID>> iterator = plugin.getEconomyCache().getEconomyCache().entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<Integer, UUID> entry = iterator.next();
            //Skip until reaching the usernames for that page
            if (index <= startingPoint) {
                index++;
                continue;
            }
            //Break loop after 10 names are printed
            if (index > (startingPoint + 10)) {
                index++;
                break;
            }
            String username = plugin.getPlayerCache().getUsernameFromUUID(entry.getValue());
            if (username == null) {
                username = "Unknown User";
                System.out.println("Unknown User: " + entry.getValue());
            } else {
                String prefix = plugin.getPlayerCache().getUserPrefix(entry.getValue());
                if (prefix != null) {
                    username = formatColor(prefix + " &f" + username);
                }
            }
            commandSender.sendMessage(ChatColor.WHITE + "" + (index - startingPoint) + ". " + username +
                    ", $" + entry.getKey());
            index++;
        }
        commandSender.sendMessage(ChatColor.GOLD + "Next Page: " + ChatColor.GRAY + "/baltop " + (pageNumber + 2));


    }


}
