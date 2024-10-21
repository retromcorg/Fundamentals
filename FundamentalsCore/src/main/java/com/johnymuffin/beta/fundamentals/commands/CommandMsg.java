package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;
import static com.johnymuffin.beta.fundamentals.util.Utils.*;

public class CommandMsg implements CommandExecutor {

    private Fundamentals plugin;

    public CommandMsg(Fundamentals plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isPlayerAuthorized(commandSender, "fundamentals.msg")) {
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("no_permission"));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("unavailable_to_console"));
            return true;
        }
        if (strings.length < 2) {
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("msg_info"));
            return true;
        }

        List<Player> matches = Bukkit.matchPlayer(strings[0].trim());
        FundamentalsPlayer fPlayer = plugin.getPlayerMap().getPlayer((Player) commandSender);

        if (fPlayer.isMuted()) {
            String mutedMessage = plugin.getFundamentalsLanguageConfig().getMessage("mute_player_chat")
                    .replace("%duration%", fPlayer.getMuteStatus() == -1 ? "permanently" : "for" + formatDateDiff(fPlayer.getMuteStatus()));
            commandSender.sendMessage(mutedMessage);
            return true;
        }
        if (matches.isEmpty()) {
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("player_not_found_full")
                    .replace("%username%", strings[0]));
            return true;
        }

        Player recipient = matches.get(0);
        if (fPlayer.getIgnoreList().contains(recipient.getUniqueId())) {
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("msg_player_ignored")
                    .replace("%player%", recipient.getName()));
            return true;
        }
        String message = getFullArg(strings, 1);
        if (isPlayerAuthorized(commandSender, "fundamentals.chat.color")) {
            message = formatColor(message);
        }

        FundamentalsPlayer fRecipient = plugin.getPlayerMap().getPlayer(recipient);
        String send = formatColor(plugin.getFundamentalConfig().getConfigString("settings.chat.msg-send-format"))
                .replace("{displayname}", fRecipient.getFullDisplayName())
                .replace("{message}", message);
        commandSender.sendMessage(send);
        fPlayer.setReplyTo(recipient);

        if (!fRecipient.getIgnoreList().contains(fPlayer.getUuid())) {
            String receive = formatColor(plugin.getFundamentalConfig().getConfigString("settings.chat.msg-receive-format"))
                    .replace("{displayname}", fPlayer.getFullDisplayName())
                    .replace("{message}", message);
            recipient.sendMessage(receive);
            fRecipient.setReplyTo((Player) commandSender);
        }
        return true;
    }
}
