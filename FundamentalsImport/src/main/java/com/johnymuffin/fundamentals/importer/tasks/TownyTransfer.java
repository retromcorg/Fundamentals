package com.johnymuffin.fundamentals.importer.tasks;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.palmergames.bukkit.towny.EmptyTownException;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.Bukkit;

import java.util.ArrayList;

public class TownyTransfer {
    private String newUsername;
    private String oldUsername;
    private Fundamentals fundamentals;
    private FundamentalsPlayer fundamentalsPlayer;
    private ArrayList<String> transferDebug;


    public TownyTransfer(String newUsername, String oldUsername, Fundamentals fundamentals, FundamentalsPlayer fundamentalsPlayer, ArrayList<String> transferDebug) {
        this.newUsername = newUsername;
        this.oldUsername = oldUsername;
        this.fundamentals = fundamentals;
        this.transferDebug = transferDebug;
        this.fundamentalsPlayer = fundamentalsPlayer;
    }


    public void runTransfer() throws TownyException, EmptyTownException {
        Towny towny = (Towny) Bukkit.getPluginManager().getPlugin("Towny");

        //Old Resident
        Resident oldResident = towny.getTownyUniverse().getResident(oldUsername);
        //New Resident
        Resident newResident = towny.getTownyUniverse().getResident(newUsername);

        //Add user to new town and sort out mayor
        if (oldResident.hasTown()) {
            boolean isMayor = oldResident.isMayor();
            if (newResident.hasTown())
                newResident.getTown().removeResident(newResident); //Remove the new user from any town
            newResident.setTown(oldResident.getTown()); //Add the new user back to their old town
            oldResident.getTown().addResident(newResident); //
            transferDebug.add("Added to the town of " + newResident.getTown().getName() + " as a resident.");
            if (isMayor) {
                newResident.getTown().setMayor(newResident);
                transferDebug.add("Assigned as mayor of " + newResident.getTown().getName());
            }
        }

        //Transfer ownership of nations
        if (oldResident.hasNation() && oldResident.isKing()) {
            oldResident.getTown().getNation().setKing(newResident);
            transferDebug.add("Ownership of the nation " + newResident.getTown().getNation().getName() + " transferred.");
        }

        oldResident.clear(); //Delete data for old Towny resident in case someone else uses that username in the future.
        transferDebug.add("Cleared Towny data for old username " + oldUsername);

    }


}
