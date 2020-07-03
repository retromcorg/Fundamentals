package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.api.EconomyAPI;
import com.johnymuffin.beta.fundamentals.api.FundamentalsAPI;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import com.projectposeidon.api.PoseidonUUID;
import com.projectposeidon.api.UUIDType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.johnymuffin.beta.fundamentals.util.Utils.getPlayerFromString;
import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;

public class CommandBalance implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isPlayerAuthorized(commandSender, "fundamentals.balance")) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }
        if (strings.length == 0) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("unavailable_to_console"));
                return true;
            }
            Player player = (Player) commandSender;
            printBalance(player.getName(), commandSender, FundamentalsAPI.getEconomy().getBalance(player.getUniqueId()));
        } else if (strings.length > 0) {
            if (!isPlayerAuthorized(commandSender, "fundamentals.balance.others")) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
                return true;
            }
            Player player = getPlayerFromString(strings[0]);
            UUID playerUUID = null;
            if (player != null) {
                playerUUID = player.getUniqueId();
            } else {
                UUIDType uuidType = PoseidonUUID.getPlayerUUIDCacheStatus(strings[0]);
                if (uuidType == UUIDType.ONLINE) {
                    playerUUID = PoseidonUUID.getPlayerUUIDFromCache(strings[0], true);
                } else if (uuidType == UUIDType.OFFLINE) {
                    playerUUID = PoseidonUUID.getPlayerUUIDFromCache(strings[0], false);
                }
            }
            //Did we get a UUID?
            if (playerUUID == null) {
                String message = FundamentalsLanguage.getInstance().getMessage("player_not_found_full");
                message = message.replace("%username%", strings[0]);
                commandSender.sendMessage(message);
            } else {
                printBalance(strings[0], commandSender, FundamentalsAPI.getEconomy().getBalance(playerUUID));
            }
        }
        return true;
    }

    private void printBalance(String playerName, CommandSender commandSender, EconomyAPI.BalanceWrapper balanceWrapper) {
        if (balanceWrapper.getEconomyResult() == EconomyAPI.EconomyResult.successful) {
            String message = FundamentalsLanguage.getInstance().getMessage("balance_successful");
            message = message.replace("%var1%", String.valueOf(balanceWrapper.getBalance()));
            commandSender.sendMessage(message);
        } else if (balanceWrapper.getEconomyResult() == EconomyAPI.EconomyResult.userNotKnown) {
            String message = FundamentalsLanguage.getInstance().getMessage("player_not_found_full");
            message = message.replace("%username%", playerName);
            commandSender.sendMessage(message);
        } else {
            String message = FundamentalsLanguage.getInstance().getMessage("generic_error");
            commandSender.sendMessage(message);
        }
    }


}

