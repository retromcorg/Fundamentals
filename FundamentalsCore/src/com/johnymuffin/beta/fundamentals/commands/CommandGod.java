package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.johnymuffin.beta.fundamentals.util.CommandUtils.getPlayerFromString;
import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;

public class CommandGod implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isPlayerAuthorized(commandSender, "fundamentals.god")) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }

        if (strings.length > 0) {
            if (!isPlayerAuthorized(commandSender, "fundamentals.god.others")) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
                return true;
            }
            Player giveTo = getPlayerFromString(strings[0]);
            if (giveTo == null) {
                String message = FundamentalsLanguage.getInstance().getMessage("player_not_found_full");
                message = message.replace("%username%", strings[0]);
                commandSender.sendMessage(message);
                return true;
            }
            boolean currentGodmode = FundamentalsPlayerMap.getInstance().getPlayer(giveTo).getFileGodModeStatus();
            currentGodmode = !currentGodmode;
            FundamentalsPlayerMap.getInstance().getPlayer(giveTo).setFileGodModeStatus(currentGodmode);
            if (currentGodmode) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("god_enable"));
            } else {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("god_disable"));
            }

        } else {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("unavailable_to_console"));
                return true;
            }
            Player giveTo = (Player) commandSender;
            boolean currentGodmode = FundamentalsPlayerMap.getInstance().getPlayer(giveTo).getFileGodModeStatus();
            currentGodmode = !currentGodmode;
            FundamentalsPlayerMap.getInstance().getPlayer(giveTo).setFileGodModeStatus(currentGodmode);
            if (currentGodmode) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("god_enable"));
            } else {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("god_disable"));
            }
        }
        return true;


    }


}