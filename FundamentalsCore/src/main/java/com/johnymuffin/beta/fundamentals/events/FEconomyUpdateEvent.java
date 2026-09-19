package com.johnymuffin.beta.fundamentals.events;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import org.bukkit.event.HandlerList;

public class FEconomyUpdateEvent extends FundamentalsPlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public FEconomyUpdateEvent(Fundamentals plugin, FundamentalsPlayer fundamentalsPlayer) {
        super("FundamentalsEconomyUpdateEvent", plugin, fundamentalsPlayer);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
