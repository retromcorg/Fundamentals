package com.johnymuffin.beta.fundamentals;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FundamentalPermission {

    public static boolean isPlayerAuthorized(final CommandSender commandSender, final String permission) {
        if (commandSender instanceof OfflinePlayer) {
            return false;
        }
        if (commandSender.isOp()) {
            return true;
        }
        if (commandSender.hasPermission(permission)) {
            return true;
        }

        return false;

    }

}
