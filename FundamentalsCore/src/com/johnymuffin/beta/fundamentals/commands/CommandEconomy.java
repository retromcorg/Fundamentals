package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.api.EconomyAPI;
import com.johnymuffin.beta.fundamentals.api.FundamentalsAPI;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

import static com.johnymuffin.beta.fundamentals.CommandUtils.getUUIDFromUsername;
import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;

public class CommandEconomy implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isPlayerAuthorized(commandSender, "fundamentals.economy")) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }
        if (strings.length != 3) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("economy_info"));
            return true;
        }
        //Verify Player
        UUID giveTo = getUUIDFromUsername(strings[0]);
        if (giveTo == null) {
            String message = FundamentalsLanguage.getInstance().getMessage("player_not_found_full");
            message = message.replaceAll("%username%", strings[0]);
            commandSender.sendMessage(message);
            return true;
        }
        //Get Amount
        double amount = Double.parseDouble(strings[2].replaceAll("[^0-9\\.]", ""));

        //Verify Type
        EconomyAPI.EconomyResult economyResult;
        if (strings[1].equalsIgnoreCase("set")) {
            economyResult = FundamentalsAPI.getEconomy().setBalance(giveTo, amount);
        } else if (strings[1].equalsIgnoreCase("give")) {
            economyResult = FundamentalsAPI.getEconomy().additionBalance(giveTo, amount);
        } else if (strings[1].equalsIgnoreCase("take")) {
            economyResult = FundamentalsAPI.getEconomy().subtractBalance(giveTo, amount);
        } else {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("economy_arg_1"));
            return true;
        }

        switch (economyResult) {
            case successful:
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("economy_result_successful"));
                return true;
            case error:
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("generic_error"));
                return true;
            case notEnoughFunds:
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("economy_result_nofunds"));
                return true;
            case userNotKnown:
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("generic_no_save_data"));
                return true;
        }
        return true;


    }


}