package com.johnymuffin.beta.fundamentals.events;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;

public class FundamentalsPlayerEvent extends FundamentalsEvent{
    private final FundamentalsPlayer fundamentalsPlayer;

    protected FundamentalsPlayerEvent(String eventName, Fundamentals plugin, FundamentalsPlayer fundamentalsPlayer) {
        super(eventName, plugin);
        this.fundamentalsPlayer = fundamentalsPlayer;
    }

    public FundamentalsPlayer getFundamentalsPlayer() {
        return fundamentalsPlayer;
    }
}
