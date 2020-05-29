package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
import com.johnymuffin.beta.fundamentals.api.EconomyAPI;
import com.johnymuffin.beta.fundamentals.api.FundamentalsAPI;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import com.projectposeidon.api.PoseidonUUID;
import com.projectposeidon.api.UUIDType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.johnymuffin.beta.fundamentals.CommandUtils.getPlayerFromString;
import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;

public class CommandPay implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isPlayerAuthorized(commandSender, "fundamentals.balance")) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("unavailable_to_console"));
            return true;
        }
        if (strings.length != 2) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("pay_info"));
            return true;
        }
        Player player = (Player) commandSender;
        FundamentalsPlayer fundamentalsPlayer = FundamentalsPlayerMap.getInstance().getPlayer(player);
        String username = strings[0];
        double amount = Double.parseDouble(strings[1].replaceAll("[^0-9\\.]", ""));
        UUID uuid = null;
        Player giveTo = getPlayerFromString(username);
        if (giveTo != null) {
            uuid = giveTo.getUniqueId();
        } else {
            UUIDType uuidType = PoseidonUUID.getPlayerUUIDCacheStatus(username);
            if (uuidType == UUIDType.ONLINE) {
                uuid = PoseidonUUID.getPlayerUUIDFromCache(username, true);
            } else if (uuidType == UUIDType.OFFLINE) {
                uuid = PoseidonUUID.getPlayerUUIDFromCache(username, false);
            }
        }
        if (uuid == null) {
            String message = FundamentalsLanguage.getInstance().getMessage("player_not_found_full");
            message = message.replace("%username%", username);
            commandSender.sendMessage(message);
            return true;
        }
        if (uuid.equals(player.getUniqueId())) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("pay_can't_pay_self"));
            return true;
        }

        //Subtract money from user
        EconomyAPI.EconomyResult economyResult = FundamentalsAPI.getEconomy().subtractBalance(player.getUniqueId(), amount);
        switch (economyResult) {
            case notEnoughFunds:
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("pay_not_enough_funds"));
                return true;
            case error:
            case userNotKnown:
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("generic_error"));
                return true;
        }


        //Move the actual money
        economyResult = FundamentalsAPI.getEconomy().additionBalance(uuid, amount);
        switch (economyResult) {
            case userNotKnown:
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("generic_no_save_data"));
                return true;
            case error:
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("generic_error"));
                FundamentalsAPI.getEconomy().additionBalance(player.getUniqueId(), amount);
                return true;
            case successful:
                String message = FundamentalsLanguage.getInstance().getMessage("pay_successful");
                message = message.replace("%var1%", strings[1]);
                message = message.replace("%var2%", username);
                commandSender.sendMessage(message);
                return true;
        }


        return true;
    }


}