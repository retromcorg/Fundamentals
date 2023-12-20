package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import com.johnymuffin.beta.fundamentals.util.TimeTickConverter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;

public class CommandPTime implements CommandExecutor {
    private Fundamentals plugin;
    private FundamentalsLanguage lang;

    public CommandPTime(Fundamentals plugin) {
        this.plugin = plugin;
        this.lang = plugin.getFundamentalsLanguageConfig();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isPlayerAuthorized(commandSender, "fundamentals.ptime")) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }


        if (strings.length == 0) {
            commandSender.sendMessage(lang.getMessage("ptime_info"));
            return true;
        } else if (strings.length == 1) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("unavailable_to_console"));
                return true;
            }
            Long ticks;
            try {
                ticks = TimeTickConverter.parse(strings[0]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(lang.getMessage("ptime_info"));
                return true;
            }
            Player player = (Player) commandSender;
            player.setPlayerTime(ticks, true);


            String playerSetTime = lang.getMessage("ptime_change_successful");

            playerSetTime = playerSetTime.replace("%var1%",  TimeTickConverter.format24(ticks));
            playerSetTime = playerSetTime.replace("%var2%", player.getName());

            commandSender.sendMessage(playerSetTime);
            return true;
        }

        return true;
    }

}