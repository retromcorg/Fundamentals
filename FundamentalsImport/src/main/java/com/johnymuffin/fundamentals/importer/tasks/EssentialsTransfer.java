package com.johnymuffin.fundamentals.importer.tasks;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.fundamentals.importer.essentials.EssentialsManager;

import java.util.ArrayList;

public class EssentialsTransfer {

    private String newUsername;
    private String oldUsername;
    private Fundamentals fundamentals;
    private FundamentalsPlayer fundamentalsPlayer;
    private ArrayList<String> transferDebug;


    public EssentialsTransfer(String newUsername, String oldUsername, Fundamentals fundamentals, FundamentalsPlayer fundamentalsPlayer, ArrayList<String> transferDebug) {
        this.newUsername = newUsername;
        this.oldUsername = oldUsername;
        this.fundamentals = fundamentals;
        this.transferDebug = transferDebug;
        this.fundamentalsPlayer = fundamentalsPlayer;
    }

    public void runTransfer() {
        EssentialsManager essentialsManager = new EssentialsManager(fundamentals);
        essentialsManager.importHomes(oldUsername, fundamentalsPlayer, fundamentals);
        essentialsManager.importBalance(oldUsername,fundamentalsPlayer, fundamentals, false);


    }
}
