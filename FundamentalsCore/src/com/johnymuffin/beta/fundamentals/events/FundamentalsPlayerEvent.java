package com.johnymuffin.beta.fundamentals.events;

import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import org.bukkit.event.Event;

public class FundamentalsPlayerEvent extends FundamentalsEvent {
    protected FundamentalsPlayer player;


    protected FundamentalsPlayerEvent(final Type type, final FundamentalsPlayer player) {
        super(type);
    }

    public final FundamentalsPlayer getPlayer() {
        return player;

    }

}
