package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;
import static com.johnymuffin.beta.fundamentals.util.Utils.getSafeDestination;

public class CommandBack implements CommandExecutor {
    FundamentalsLanguage language = FundamentalsLanguage.getInstance();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!isPlayerAuthorized(commandSender, "fundamentals.back")) {
            commandSender.sendMessage(language.getMessage("no_permission"));
            return false;
        }
        if (args.length > 1) {
            commandSender.sendMessage(language.getMessage("back_proper_usage"));
            return false;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(language.getMessage("unavailable_to_console"));
            return false;
        }
        Player player = (Player) commandSender;

        FundamentalsPlayer fPlayer = FundamentalsPlayerMap.getInstance().getPlayer(player);

        Location lastTeleportLocation = fPlayer.getLastTeleportLocation();

        if (fPlayer.getLastTeleportLocation() == null) {
            commandSender.sendMessage(language.getMessage("no_last_teleport"));
            return true;
        }

        try {
            player.teleport(getSafeDestination(lastTeleportLocation));
        } catch (Exception e) {
            commandSender.sendMessage(language.getMessage("generic_error_player"));
            return false;
        }

        commandSender.sendMessage(language.getMessage("back_success"));
        return true;
    }
}
