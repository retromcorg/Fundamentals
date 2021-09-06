package com.johnymuffin.beta.fundamentals.events;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.banks.FundamentalsBank;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;

public class BankCreationEvent extends FEconomyUpdateEvent {
    private FundamentalsBank bank;


    public BankCreationEvent(Fundamentals plugin, FundamentalsPlayer fundamentalsPlayer, FundamentalsBank bank) {
        super(plugin, fundamentalsPlayer);
        this.bank = bank;
    }

    public FundamentalsBank getBank() {
        return this.bank;
    }


}
