package com.johnymuffin.beta.fundamentals;

import org.bukkit.command.CommandSender;

public class FundamentalPermission {

    public static boolean isPlayerAuthorized(final CommandSender commandSender, final String permission) {
        if (commandSender.isOp()) {
            return true;
        }
        if (commandSender.hasPermission(permission)) {
            return true;
        }

        return false;

    }

}
