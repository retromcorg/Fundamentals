package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import com.johnymuffin.beta.fundamentals.util.TimeTickConverter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;
import static com.johnymuffin.beta.fundamentals.util.Utils.getPlayerFromString;

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
        }

        else if (strings.length == 1) {

            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("unavailable_to_console"));
                return true;
            }

            Player player = ( Player ) commandSender;
            setPlayerTime(player, strings[0]);
            return true;

        }

        else if(strings.length == 2) {
            if (!(commandSender.hasPermission("fundamentals.ptime.others") || commandSender.isOp())) {
                commandSender.sendMessage(ChatColor.RED + "Sorry, you don't have permission for this command.");
                return true;
            } else {
                Player player = getPlayerFromString(strings[1]);

                if (player == null) {
                    commandSender.sendMessage(ChatColor.RED + "Can't find a player called " + ChatColor.BLUE + strings[0]);
                    return true;
                }
                else {
                    setPlayerTime(player, strings[0]);
                    commandSender.sendMessage( "Time set for " + player.getName());
                    return true;
                }
            }
        }
        return true;
    }

    public void setPlayerTime(Player player, String time) {
        Long ticks;
        if(time.equalsIgnoreCase("reset") || time.equalsIgnoreCase("off")) {
            ticks = player.getLocation().getWorld().getTime();
            player.setPlayerTime(ticks, true);

            String playerSetUntime = lang.getMessage("ptime_change_successful");

            playerSetUntime = playerSetUntime.replace("%var1%", TimeTickConverter.format24(ticks));
            playerSetUntime = playerSetUntime.replace("%var2%", player.getName());

            player.sendMessage(playerSetUntime);
        }
        else {
            try {
                ticks = TimeTickConverter.parse(time);
                player.setPlayerTime(ticks, true);

                String playerSetUntime = lang.getMessage("ptime_change_successful");

                playerSetUntime = playerSetUntime.replace("%var1%", TimeTickConverter.format24(ticks));
                playerSetUntime = playerSetUntime.replace("%var2%", player.getName());

                player.sendMessage(playerSetUntime);
            } catch (NumberFormatException e) {
                player.sendMessage(lang.getMessage("ptime_info"));
            }
        }
    }

}