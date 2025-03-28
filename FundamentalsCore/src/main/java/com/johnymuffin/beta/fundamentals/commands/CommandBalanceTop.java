package com.johnymuffin.beta.fundamentals.commands;

import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;
import static com.johnymuffin.beta.fundamentals.util.Utils.formatColor;
import static com.johnymuffin.beta.fundamentals.util.Utils.isInt;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;

public class CommandBalanceTop implements CommandExecutor {

    private Fundamentals plugin;

    public CommandBalanceTop(Fundamentals plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isPlayerAuthorized(commandSender, "fundamentals.balancetop")) {
            commandSender.sendMessage(getMessage("no_permission"));
            return true;
        }

        if (strings.length == 0) {
            printPage(commandSender, 0);
        } else if (strings.length > 0) {
            if (!isInt(strings[0])) {
                commandSender.sendMessage(getMessage("balancetop_invalid_integer"));
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
            commandSender.sendMessage(getMessage("balancetop_too_low"));
            return;
        }
        if (pageNumber >= ((int) economyCacheSize / 10)) { // It has to be greater then or equal because technically the first page is zero.
            commandSender.sendMessage(getMessage("balancetop_too_high"));
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
                //System.out.println("Unknown User: " + entry.getValue());

                //Skipping the unknown user
                continue;
            } else {
                String prefix = plugin.getPlayerCache().getUserPrefix(entry.getValue());
                if (prefix != null) {
                    username = formatColor(prefix + "&f" + username);
                }
            }
            commandSender.sendMessage(ChatColor.WHITE + "" + (index - startingPoint) + ". " + username +
                    ", $" + entry.getKey());
            index++;
        }
        //Print economy size on final page with $ for the economy size
        if(pageNumber == 0) {
            commandSender.sendMessage(ChatColor.GOLD + "Economy Size: " + ChatColor.WHITE + formatEconomySize());
        }

        commandSender.sendMessage(ChatColor.GOLD + "Next Page: " + ChatColor.GRAY + "/baltop " + (pageNumber + 2));
    }

    private String getMessage(String key) {
        return FundamentalsLanguage.getInstance().getMessage(key);
    }

    private String formatEconomySize() {
        final int groupSize = 1000;

        double economySize = plugin.getEconomySize();

        if (economySize < groupSize)
            return "$" + String.valueOf(economySize);

        economySize /= groupSize;

        String[] units = {"K", "M", "B", "T"};
        int unitIndex = 0;

        while(economySize > groupSize) {
            economySize /= groupSize;
            unitIndex++;
        }

        String suffix = units[unitIndex];

        BigDecimal rounded = new BigDecimal(economySize);
        rounded = rounded.round(new MathContext(4, RoundingMode.HALF_UP));
        rounded = rounded.stripTrailingZeros();

        String output = "$" + String.valueOf(rounded) + suffix;
        return output;
    }
}
