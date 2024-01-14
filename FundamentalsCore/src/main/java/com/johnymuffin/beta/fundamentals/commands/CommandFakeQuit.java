package com.johnymuffin.beta.fundamentals.commands;

import com.earth2me.essentials.Essentials;
import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;
import static com.johnymuffin.beta.fundamentals.util.Utils.setEssentialsHidden;
import static com.johnymuffin.beta.fundamentals.util.Utils.updateVanishedPlayers;

public class CommandFakeQuit implements CommandExecutor {

    private Fundamentals plugin;

    public CommandFakeQuit(Fundamentals plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isPlayerAuthorized(commandSender, "fundamentals.fakequit")) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }

        //If arguments are provided, assume that is target player
        Player targetPlayer;
        if (strings.length > 0) {
            targetPlayer = Bukkit.getPlayer(strings[0]);
            if (targetPlayer == null) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("player_not_found_full").replace("%username%", strings[0]));
                return true;
            }
        } else {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("unavailable_to_console"));
                return true;
            }
            targetPlayer = (Player) commandSender;
        }

        //Toggle fakequit
        boolean currentStatus = plugin.getPlayerMap().getPlayer(targetPlayer).isFakeQuit();

        FundamentalsPlayer fundamentalsPlayer = plugin.getPlayerMap().getPlayer(targetPlayer);

        if (currentStatus) {
            plugin.getPlayerMap().getPlayer(targetPlayer).setFakeQuit(false);
            setEssentialsHidden(targetPlayer, false);
            targetPlayer.sendMessage(FundamentalsLanguage.getInstance().getMessage("fakequit_disabled"));
        } else {
            plugin.getPlayerMap().getPlayer(targetPlayer).setFakeQuit(true);
            setEssentialsHidden(targetPlayer, true);
            targetPlayer.sendMessage(FundamentalsLanguage.getInstance().getMessage("fakequit_enabled"));

            //Enable Vanish if it isn't already
            if (!fundamentalsPlayer.isVanished()) {
                fundamentalsPlayer.setVanished(true);
                targetPlayer.sendMessage(FundamentalsLanguage.getInstance().getMessage("fakequit_vanish_enabled"));
            }

        }

        String joinOrQuitMessage = !currentStatus ? plugin.getFundamentalConfig().getConfigString("settings.player.quit-message.value") : plugin.getFundamentalConfig().getConfigString("settings.player.join-message.value");
        joinOrQuitMessage = joinOrQuitMessage.replace("{name}", targetPlayer.getName());
        joinOrQuitMessage = joinOrQuitMessage.replace("{prefix}", plugin.getPermissionsHook().getMainUserPrefix(targetPlayer.getUniqueId()));
        Bukkit.broadcastMessage(joinOrQuitMessage.replace("&", "\u00a7"));
        updateVanishedPlayers(); //Update vanished players
        return true;
    }


}