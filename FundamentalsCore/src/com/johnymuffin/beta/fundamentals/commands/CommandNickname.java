package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.johnymuffin.beta.fundamentals.util.CommandUtils.formatColor;
import static com.johnymuffin.beta.fundamentals.util.CommandUtils.getPlayerFromString;
import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;

public class CommandNickname implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isPlayerAuthorized(commandSender, "fundamentals.nickname")) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("unavailable_to_console"));
            return true;
        }
        Player player = (Player) commandSender;
        FundamentalsPlayer fundamentalsPlayer = FundamentalsPlayerMap.getInstance().getPlayer(player);


        if (strings.length == 0) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("nickname_info"));
            return true;
        }

        //Player is trying to nick themself
        if (strings.length == 1) {
            if (strings[0].equalsIgnoreCase("off") || strings[0].equalsIgnoreCase(player.getName())) {
                fundamentalsPlayer.setNickname(null);
                player.setDisplayName(player.getName());
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("nickname_remove"));
                return true;
            }
            fundamentalsPlayer.setNickname(strings[0]);
            String message = FundamentalsLanguage.getInstance().getMessage("nickname_set");
            message = message.replace("%var1%", formatColor(strings[0]));
            commandSender.sendMessage(message);
            return true;
        }
        //Player is trying to nick another player
        if (strings.length > 1) {
            //Check permission to nick
            if (!isPlayerAuthorized(commandSender, "fundamentals.nickname.others")) {
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
            FundamentalsPlayer target = FundamentalsPlayerMap.getInstance().getPlayer(giveTo);

            if (strings[1].equalsIgnoreCase("off") || strings[1].equalsIgnoreCase(giveTo.getName())) {
                target.setNickname(null);
                giveTo.setDisplayName(giveTo.getName());
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("nickname_remove_others"));
                return true;
            }

            target.setNickname(strings[1]);
            String message = FundamentalsLanguage.getInstance().getMessage("nickname_set_others");
            message = message.replace("%var1%", giveTo.getName());
            message = message.replace("%var2%", formatColor(strings[1]));
            commandSender.sendMessage(message);
            return true;

        }


        return true;
    }


}