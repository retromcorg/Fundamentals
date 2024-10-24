package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;

public class CommandList implements CommandExecutor {

    private Fundamentals plugin;

    public CommandList(Fundamentals plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isPlayerAuthorized(commandSender, "fundamentals.list")) {
            commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("no_permission"));
            return true;
        }
        boolean showHidden = isPlayerAuthorized(commandSender, "fundamentals.list.hidden");
        commandSender.sendMessage(plugin.getFundamentalsLanguageConfig().getMessage("list_online")
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().length))
                .replace("%maximum%", String.valueOf(Bukkit.getMaxPlayers())));

        List<String> list = new ArrayList<>();
        List<FundamentalsPlayer> players = new ArrayList<>();
        if (plugin.getFundamentalConfig().getConfigBoolean("settings.list.sort-groups")) {
            Map<String, List<FundamentalsPlayer>> groups = new TreeMap<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                FundamentalsPlayer fPlayer = plugin.getPlayerMap().getPlayer(p);
                String group = plugin.getPermissionsHook().getMainUserGroup(fPlayer.getUuid());
                List<FundamentalsPlayer> groupList = groups.get(group);
                if (groupList == null) groupList = new ArrayList<>();
                groupList.add(fPlayer);
                groups.put(group, groupList);
            }
            for (String group : groups.keySet()) {
                List<FundamentalsPlayer> groupList = groups.get(group);
                groupList.sort(Comparator.comparing(fPlayer -> fPlayer.getBukkitPlayer().getName()));
                players.addAll(groupList);
            }
        } else {
            for (Player p : Bukkit.getOnlinePlayers()) {
                players.add(plugin.getPlayerMap().getPlayer(p));
            }
            players.sort(Comparator.comparing(fPlayer -> fPlayer.getBukkitPlayer().getName()));
        }
        for (FundamentalsPlayer fPlayer : players) {
            boolean isHidden = Utils.isEssentialsHidden(fPlayer.getBukkitPlayer());
            if (isHidden && !showHidden) continue;
            StringBuilder sb = new StringBuilder();
            if (fPlayer.isAFK()) sb.append("§7[AFK]§f");
            if (isHidden) sb.append("§7[HIDDEN]§f");
            sb.append(fPlayer.getFullDisplayName());
            list.add(sb.toString());
        }
        commandSender.sendMessage("Connected players: " + String.join("§f, ", list));
        return true;
    }
}
