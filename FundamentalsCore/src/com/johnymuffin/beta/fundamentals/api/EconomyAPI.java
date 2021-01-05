package com.johnymuffin.beta.fundamentals.api;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.util.FundamentalsEventFactory;

import java.util.UUID;
import java.util.logging.Level;

public class EconomyAPI {
    Fundamentals plugin;

    public EconomyAPI(Fundamentals plugin) {
        this.plugin = plugin;
    }

    public BalanceWrapper getBalance(UUID uuid) {
        if (!plugin.getPlayerMap().isPlayerKnown(uuid)) {
            return new BalanceWrapper(0D, EconomyResult.userNotKnown);
        }

        try {
            return new BalanceWrapper(plugin.getPlayerMap().getPlayer(uuid).getBalance(), EconomyResult.successful);
        } catch (Exception e) {
            Fundamentals.getPlugin().logger(Level.WARNING, "An error occurred trying to change the balance of " + uuid.toString() + ": " + e.getMessage());
            return new BalanceWrapper(0D, EconomyResult.error);
        }

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
            FundamentalsEventFactory.callEconomyUpdateEvent(plugin, player); //Call event so other plugins can update
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
            FundamentalsEventFactory.callEconomyUpdateEvent(plugin, player); //Call event so other plugins can update
            return EconomyResult.successful;
        } catch (Exception e) {
            Fundamentals.getPlugin().logger(Level.WARNING, "An error occurred trying to change the balance of " + uuid.toString() + ": " + e.getMessage());
            return EconomyResult.error;
        }

    }

    public EconomyResult setBalance(UUID uuid, double amount) {
        if (!plugin.getPlayerMap().isPlayerKnown(uuid)) {
            return EconomyResult.userNotKnown;
        }

        try {
            FundamentalsPlayer player = plugin.getPlayerMap().getPlayer(uuid);
            player.setBalance(amount);
            FundamentalsEventFactory.callEconomyUpdateEvent(plugin, player); //Call event so other plugins can update
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

    public class BalanceWrapper {
        private final double balance;
        private final EconomyResult economyResult;

        public BalanceWrapper(double balance, EconomyResult result) {
            this.balance = balance;
            this.economyResult = result;
        }

        public double getBalance() {
            return balance;
        }

        public EconomyResult getEconomyResult() {
            return economyResult;
        }
    }

}
