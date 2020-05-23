package com.johnymuffin.beta.fundamentals;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Pattern;

public class CommandUtils {


    public static Player getPlayerFromString(String name) {
        List<Player> players = Bukkit.matchPlayer(name);
        if (players.size() == 1) {
            return players.get(0);
        }
        return null;
    }

    public static boolean verifyHomeName(String name) {
        return Pattern.matches("[a-zA-Z]+", name);
    }

    public static String formatColor(String s) {
        return s.replaceAll("(&([a-f0-9]))", "\u00A7$2");

    }


}
