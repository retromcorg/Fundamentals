package com.johnymuffin.beta.fundamentals.commands;

import static com.johnymuffin.beta.fundamentals.util.Utils.getUUIDFromUsername;
import static com.johnymuffin.beta.fundamentals.util.Utils.verifyHomeName;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;

public class CommandRenameHome implements CommandExecutor {
    @SuppressWarnings("FieldCanBeLocal")
    private final String PERMISSION_NODE = "fundamentals.renamehome";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!validateCommandSender(sender))
            return true;

        if (args.length != 2) {
            sender.sendMessage(getMessage("renamehome_usage"));
            return true;
        }

        Player player = (Player) sender;
        String oldName = args[0];
        String newName = args[1];

        if (!verifyHomeName(newName)) {
            player.sendMessage(getMessage("sethome_invalid_name"));
            return true;
        }

        FundamentalsPlayer fPlayer = FundamentalsPlayerMap.getInstance().getPlayer(player);

        if (!fPlayer.doesHomeExist(oldName)) {
            String notFound = getMessage("home_not_on_record");
            notFound = notFound.replace("%homeName%", oldName);
            player.sendMessage(notFound);
            return true;
        }

        if (fPlayer.doesHomeExist(newName)) {
            String exists = getMessage("sethome_already_exists");
            exists = exists.replace("%homeName%", newName);
            player.sendMessage(exists);
            return true;
        }

        // rename operation
        fPlayer.renameHome(oldName, newName);
        String success = getMessage("renamehome_success");
        success = success.replace("%oldName%", oldName).replace("%newName%", newName);
        player.sendMessage(success);

        return true;
    }

    private boolean validateCommandSender(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(getMessage("unavailable_to_console"));
            return false;
        }

        if (!canUseCommand(sender)) {
            sender.sendMessage(getMessage("no_permission"));
            return false;
        }

        return true;
    }

    private boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission(PERMISSION_NODE) || sender.isOp();
    }

    @SuppressWarnings("deprecation")
    private String getMessage(String key) {
        return FundamentalsLanguage.getInstance().getMessage(key);
    }
}
