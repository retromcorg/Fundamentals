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

    @Deprecated
    public BalanceWrapper getBalance(UUID uuid) {
        return getBalance(uuid, null);
    }

    public BalanceWrapper getBalance(UUID uuid, String world) {
        if (!plugin.getPlayerMap().isPlayerKnown(uuid)) {
            return new BalanceWrapper(0D, EconomyResult.userNotKnown);
        }

        try {
            if (world == null) {
                return new BalanceWrapper(plugin.getPlayerMap().getPlayer(uuid).getBalance(), EconomyResult.successful); //Fallback if world isn't provided
            } else {
                return new BalanceWrapper(plugin.getPlayerMap().getPlayer(uuid).getBalance(world), EconomyResult.successful);
            }
        } catch (Exception e) {
            Fundamentals.getPlugin().logger(Level.WARNING, "An error occurred trying to change the balance of " + uuid.toString() + ": " + e.getMessage());
            return new BalanceWrapper(0D, EconomyResult.error);
        }
    }

    @Deprecated
    public EconomyResult subtractBalance(UUID uuid, double amount) {
        return subtractBalance(uuid, amount, null);
    }

    public EconomyResult subtractBalance(UUID uuid, double amount, String world) {
        if (!plugin.getPlayerMap().isPlayerKnown(uuid)) {
            return EconomyResult.userNotKnown;
        }

        try {
            FundamentalsPlayer player = plugin.getPlayerMap().getPlayer(uuid);
            double balance = player.getBalance() - amount;
            if (balance < 0) {
                return EconomyResult.notEnoughFunds;
            }
            if (world == null) {
                player.setBalance(balance);
            } else {
                player.setBalance(balance, world);
            }
            FundamentalsEventFactory.callEconomyUpdateEvent(plugin, player); //Call event so other plugins can update
            return EconomyResult.successful;
        } catch (Exception e) {
            Fundamentals.getPlugin().logger(Level.WARNING, "An error occurred trying to change the balance of " + uuid.toString() + ": " + e.getMessage());
            return EconomyResult.error;
        }

    }

    @Deprecated
    public EconomyResult additionBalance(UUID uuid, double amount) {
        return additionBalance(uuid, amount, null);
    }

    public EconomyResult additionBalance(UUID uuid, double amount, String world) {
        if (!plugin.getPlayerMap().isPlayerKnown(uuid)) {
            return EconomyResult.userNotKnown;
        }

        try {
            FundamentalsPlayer player = plugin.getPlayerMap().getPlayer(uuid);
            if (world == null) {
                double balance = player.getBalance() + amount;
                player.setBalance(balance);
            } else {
                double balance = player.getBalance(world) + amount;
                player.setBalance(balance, world);
            }
            FundamentalsEventFactory.callEconomyUpdateEvent(plugin, player); //Call event so other plugins can update
            return EconomyResult.successful;
        } catch (Exception e) {
            Fundamentals.getPlugin().logger(Level.WARNING, "An error occurred trying to change the balance of " + uuid.toString() + ": " + e.getMessage());
            return EconomyResult.error;
        }

    }

    @Deprecated
    public EconomyResult setBalance(UUID uuid, double amount) {
        return setBalance(uuid, amount, null);
    }

    public EconomyResult setBalance(UUID uuid, double amount, String world) {
        if (!plugin.getPlayerMap().isPlayerKnown(uuid)) {
            return EconomyResult.userNotKnown;
        }

        try {
            FundamentalsPlayer player = plugin.getPlayerMap().getPlayer(uuid);
            if (world == null) {
                player.setBalance(amount);
            } else {
                player.setBalance(amount, world);
            }
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
