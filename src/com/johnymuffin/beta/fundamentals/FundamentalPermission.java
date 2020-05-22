package com.johnymuffin.beta.fundamentals;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class FundamentalPermission {

    public static boolean isPlayerAuthorized(final Player p, final String permission) {
        if (p instanceof OfflinePlayer) {
            return false;
        }
        if (p.isOp()) {
            return true;
        }
        if (p.hasPermission(permission)) {
            return true;
        }

        return false;

    }

}
