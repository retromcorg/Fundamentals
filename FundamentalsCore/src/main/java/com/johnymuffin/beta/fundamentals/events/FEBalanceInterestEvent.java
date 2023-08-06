package com.johnymuffin.beta.fundamentals.events;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import org.bukkit.event.Cancellable;

public class FEBalanceInterestEvent extends FEconomyUpdateEvent implements Cancellable {
    private double originalInterestGained;
    private double interestGained;
    private double interestRate;

    private boolean cancelled = false;


    public FEBalanceInterestEvent(Fundamentals plugin, FundamentalsPlayer fundamentalsPlayer, double interestGained, double interestRate) {
        super(plugin, fundamentalsPlayer);
        this.originalInterestGained = interestGained;
        this.interestGained = interestGained;
        this.interestRate = interestRate;
    }


    public double getInterestGained() {
        return this.interestGained;
    }

    public void setInterestGained(double interestGained) {
        this.interestGained = interestGained;
    }

    public double getOriginalInterestGained() {
        return this.originalInterestGained;
    }

    public double getInterestRate() {
        return this.interestRate;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
