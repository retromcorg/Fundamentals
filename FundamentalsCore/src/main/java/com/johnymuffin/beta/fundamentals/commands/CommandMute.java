package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;

public class CommandMute implements CommandExecutor {

    private Fundamentals plugin;

    public CommandMute(Fundamentals plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isPlayerAuthorized(commandSender,"fundamentals.mute")) {
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("no_permission"));
            return true;
        }
        if (strings.length == 0) {
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("mute_info"));
            return true;
        }
        UUID uuid = plugin.getPlayerCache().getUUIDFromUsername(strings[0]);
        if (uuid == null) {
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("player_not_found_full")
                    .replace("%username%", strings[0]));
            return true;
        }
        if (commandSender instanceof Player && uuid.equals(((Player) commandSender).getUniqueId())){
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("mute_self"));
            return true;
        }

        FundamentalsPlayer fPlayer = plugin.getPlayerMap().getPlayer(uuid);
        Player player = fPlayer.getBukkitPlayer();

        if (strings.length == 1 && fPlayer.isMuted()){
            fPlayer.setMuteTimer(null);

            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("mute_unmute_successful")
                        .replace("%player%", plugin.getPlayerCache().getUsernameFromUUID(fPlayer.getUuid())));
            if (player != null)
                player.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("mute_player_unmuted"));

            return true;
        }

        String time = Utils.getFullArg(strings, 1);
        long unixTime = System.currentTimeMillis();
        long duration;
        try {
            duration = Utils.parseDateDiff(time, true);
        } catch (Exception e){
            duration = -1;
        }

        fPlayer.setMuteTimer(duration);
        String formattedTime = Utils.formatDateDiff(unixTime, duration);
        commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("mute_successful")
                .replace("%player%", plugin.getPlayerCache().getUsernameFromUUID(fPlayer.getUuid()))
                .replace("%duration%", duration == -1 ? "permanently" : "for" + formattedTime));
        if (player != null)
            player.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("mute_player_muted")
                .replace("%duration%", duration == -1 ? "permanently" : "for" + formattedTime));
        return true;
    }
}
