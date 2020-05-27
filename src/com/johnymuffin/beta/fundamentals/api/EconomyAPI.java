package com.johnymuffin.beta.fundamentals.api;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;

import java.util.UUID;
import java.util.logging.Level;

public class EconomyAPI {
    Fundamentals plugin;

    public EconomyAPI(Fundamentals plugin) {
        this.plugin = plugin;
    }

    public EconomyResult subtractBalance(UUID uuid, double amount) {
        if (!plugin.getPlayerMap().isPlayerKnown(uuid)) {
            return EconomyResult.userNotKnown;
        }

        try {
            FundamentalsPlayer player = plugin.getPlayerMap().getPlayer(uuid);
            double balance = player.getBalance() - amount;
            if (balance < 0) {
                return EconomyResult.notEnoughFunds;
            }
            player.setBalance(balance);
            return EconomyResult.successful;
        } catch (Exception e) {
            Fundamentals.getPlugin().logger(Level.WARNING, "An error occurred trying to change the balance of " + uuid.toString() + ": " + e.getMessage());
            return EconomyResult.error;
        }

    }

    public EconomyResult additionBalance(UUID uuid, double amount) {
        if (!plugin.getPlayerMap().isPlayerKnown(uuid)) {
            return EconomyResult.userNotKnown;
        }

        try {
            FundamentalsPlayer player = plugin.getPlayerMap().getPlayer(uuid);
            double balance = player.getBalance() + amount;
            player.setBalance(balance);
            return EconomyResult.successful;
        } catch (Exception e) {
            Fundamentals.getPlugin().logger(Level.WARNING, "An error occurred trying to change the balance of " + uuid.toString() + ": " + e.getMessage());
            return EconomyResult.error;
        }

    }

    public enum EconomyResult {
        successful,
        notEnoughFunds,
        error,
        userNotKnown,

    }

}
