package com.johnymuffin.beta.fundamentals.events;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import org.bukkit.event.Event;

public class FEconomyUpdateEvent extends FundamentalsPlayerEvent {

    public FEconomyUpdateEvent(Fundamentals plugin, FundamentalsPlayer fundamentalsPlayer) {
        super("FundamentalsEconomyUpdateEvent", plugin, fundamentalsPlayer);
    }

}
