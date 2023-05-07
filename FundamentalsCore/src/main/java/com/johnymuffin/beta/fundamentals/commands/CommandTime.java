package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import com.johnymuffin.beta.fundamentals.util.TimeTickConverter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;

public class CommandTime implements CommandExecutor {
    private Fundamentals plugin;
    private FundamentalsLanguage lang;

    public CommandTime(Fundamentals plugin) {
        this.plugin = plugin;
        this.lang = plugin.getFundamentalsLanguageConfig();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isPlayerAuthorized(commandSender, "fundamentals.time")) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }


        if (strings.length == 0) {
            commandSender.sendMessage(lang.getMessage("time_info"));
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
                commandSender.sendMessage(lang.getMessage("time_info"));
                return true;
            }
            Player player = (Player) commandSender;
            player.getLocation().getWorld().setTime(ticks);
            commandSender.sendMessage(lang.getMessage("time_change_successful"));
            return true;
        } else {
            Long ticks;
            try {
                ticks = TimeTickConverter.parse(strings[0]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(lang.getMessage("time_info"));
                return true;
            }
            if (strings[1].equalsIgnoreCase("all")) {
                for (World w : Bukkit.getWorlds()) {
                    w.setTime(ticks);
                }
            } else {
                World world = Bukkit.getWorld(strings[1]);
                if (world == null) {
                    commandSender.sendMessage(lang.getMessage("generic_invalid_world"));
                    return true;
                }
                world.setTime(ticks);
                commandSender.sendMessage(lang.getMessage("time_change_successful"));
                return true;
            }


        }

        return true;
    }

}