package com.johnymuffin.beta.fundamentals;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandUtils {


    public static Player getPlayerFromString(String name) {
        List<Player> players = Bukkit.matchPlayer(name);
        if(players.size() == 1) {
            return players.get(0);
        }
        return null;
    }


}
