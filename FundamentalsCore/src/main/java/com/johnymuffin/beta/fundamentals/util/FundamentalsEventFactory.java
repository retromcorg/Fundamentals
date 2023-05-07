package com.johnymuffin.beta.fundamentals.util;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.events.FEconomyUpdateEvent;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import org.bukkit.Bukkit;

public class FundamentalsEventFactory {

    public static FEconomyUpdateEvent callEconomyUpdateEvent(Fundamentals fundamentals, FundamentalsPlayer fPlayer) {
        FEconomyUpdateEvent fEconomyUpdateEvent = new FEconomyUpdateEvent(fundamentals, fPlayer);
        Bukkit.getServer().getPluginManager().callEvent(fEconomyUpdateEvent);
        return fEconomyUpdateEvent;
    }


}
