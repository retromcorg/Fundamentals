package com.johnymuffin.beta.fundamentals.events;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import org.bukkit.event.Event;

public class FundamentalsEvent extends Event {
    private final Fundamentals plugin;

    protected FundamentalsEvent(String eventName, Fundamentals plugin) {
        super(eventName);
        this.plugin = plugin;
    }

    public Fundamentals getPlugin() {
        return plugin;
    }
}
