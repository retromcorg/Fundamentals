package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
import com.johnymuffin.beta.fundamentals.playerdata.FundamentalsPlayerFile;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsConfig;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.johnymuffin.beta.fundamentals.util.CommandUtils.verifyHomeName;

public class CommandSetHome implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender.hasPermission("fundamentals.sethome") || commandSender.isOp())) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("unavailable_to_console"));
            return true;
        }
        Player player = (Player) commandSender;
        FundamentalsPlayerFile fundamentalsPlayerFile = FundamentalsPlayerMap.getInstance().getPlayer(player);

        String homeName = "main";
        if (strings.length > 0) {
            homeName = strings[0];
            if (!verifyHomeName(homeName)) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("sethome_invalid_name"));
                return true;
            }
        }
        //Check home limit
        int limit = Integer.valueOf(String.valueOf(FundamentalsConfig.getInstance().getConfigOption("settings.multiple-homes")));
        int homeCount = fundamentalsPlayerFile.getPlayerHomes().size();

        if (!(commandSender.hasPermission("fundamentals.sethome.unlimited") || commandSender.isOp())) {
            if (!(commandSender.hasPermission("fundamentals.sethome.multiple"))) {
                if (homeCount >= 1) {
                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("sethome_full"));
                    return true;
                }
            } else {
                if (homeCount < limit + 1) {
                    String msg = FundamentalsLanguage.getInstance().getMessage("sethome_limit_reached");
                    msg = msg.replaceAll("%var1%", String.valueOf(limit));
                    commandSender.sendMessage(msg);
                    return true;
                }
            }
        }
        if (fundamentalsPlayerFile.doesHomeExist(homeName)) {
            //Home already exists
            String msg = FundamentalsLanguage.getInstance().getMessage("sethome_already_exists");
            msg = msg.replaceAll("%var1%", homeName);
            commandSender.sendMessage(msg);
            return true;
        }
        fundamentalsPlayerFile.setPlayerHome(homeName, player.getLocation());

        String msg = FundamentalsLanguage.getInstance().getMessage("sethome_set_successfully");
        msg = msg.replaceAll("%var1%", homeName);
        commandSender.sendMessage(msg);
        return true;

    }
}
