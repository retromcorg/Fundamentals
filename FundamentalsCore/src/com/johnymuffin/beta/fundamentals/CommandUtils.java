package com.johnymuffin.beta.fundamentals;

import com.projectposeidon.api.PoseidonUUID;
import com.projectposeidon.api.UUIDType;
import org.bukkit.Bukkit;
import org.bukkit.entity.FallingSand;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class CommandUtils {


    public static Player getPlayerFromString(String name) {
        List<Player> players = Bukkit.matchPlayer(name);
        if (players.size() == 1) {
            return players.get(0);
        }
        return null;
    }

    public static UUID getUUIDFromUsername(String name) {
        Player player = getPlayerFromString(name);
        if(player != null) {
            return player.getUniqueId();
        }
        //Search Poseidon Cache
        UUIDType uuidType = PoseidonUUID.getPlayerUUIDCacheStatus(name);
        switch (uuidType) {
            case ONLINE:
                return PoseidonUUID.getPlayerUUIDFromCache(name, true);
            case OFFLINE:
                return PoseidonUUID.getPlayerUUIDFromCache(name, false);
        }
        return null;

    }


    public static boolean verifyHomeName(String name) {
        return Pattern.matches("^[a-zA-Z0-9]+$", name);
    }

    public static String formatColor(String s) {
        return s.replaceAll("(&([a-f0-9]))", "\u00A7$2");

    }


}
