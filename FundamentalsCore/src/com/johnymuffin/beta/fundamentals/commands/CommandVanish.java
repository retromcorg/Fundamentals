package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;
import static com.johnymuffin.beta.fundamentals.util.Utils.*;

public class CommandVanish implements CommandExecutor {
    private Fundamentals plugin;

    public CommandVanish(Fundamentals plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isPlayerAuthorized(commandSender, "fundamentals.vanish")) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("unavailable_to_console"));
            return true;
        }
        Player player = (Player) commandSender;
        if (strings.length == 0) {
            toggleVanish(plugin.getPlayerMap().getPlayer(player));
            return true;
        }

        if (strings.length > 0) {
            UUID uuid = getUUIDFromUsername(strings[0]);
            if (uuid == null || !plugin.getPlayerMap().isPlayerKnown(uuid)) {
                String message = FundamentalsLanguage.getInstance().getMessage("player_not_found_full");
                message = message.replace("%username%", strings[0]);
                commandSender.sendMessage(message);
                return true;
            }
            FundamentalsPlayer fTarget = plugin.getPlayerMap().getPlayer(uuid);
            toggleVanish(fTarget);
            if (fTarget.isVanished()) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("vanish_successful_other_enabled"));
            } else {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("vanish_successful_other_disabled"));
            }
            return true;

        }
        return true;
    }

    public void toggleVanish(FundamentalsPlayer fPlayer) {
        if (fPlayer.isVanished()) {
            fPlayer.setVanished(false);
            if (fPlayer.getBukkitPlayer() != null) {
                fPlayer.getBukkitPlayer().sendMessage(FundamentalsLanguage.getInstance().getMessage("vanish_disable"));
            }
        } else {
            fPlayer.setVanished(true);
            if (fPlayer.getBukkitPlayer() != null) {
                fPlayer.getBukkitPlayer().sendMessage(FundamentalsLanguage.getInstance().getMessage("vanish_enable"));
            }
        }
        updateVanishedPlayers();
    }


}