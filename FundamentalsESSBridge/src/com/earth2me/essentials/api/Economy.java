package com.earth2me.essentials.api;

import com.johnymuffin.beta.fundamentals.util.CommandUtils;
import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.api.EconomyAPI;
import com.johnymuffin.beta.fundamentals.api.FundamentalsAPI;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Instead of using this api directly, we recommend to use the register plugin:
 * http://bit.ly/RegisterMethod
 */
public final class Economy {
    private Economy() {
    }

    private static final Logger logger = Logger.getLogger("Minecraft");
    private static Fundamentals ess;


    public static void setEss(Fundamentals plugin) {
        ess = plugin;
    }

//    private static void createNPCFile(String name) {
//        File folder = new File(ess.getDataFolder(), "userdata");
//        if (!folder.exists()) {
//            folder.mkdirs();
//        }
//
//        UUID uuid = CommandUtils.getUUIDFromUsername(name);
//        if (uuid == null) {
//            throw new RuntimeException("A the UUID for the user " + name + " isn't known.");
//        }
//        ess.getPlayerMap().getPlayer(uuid);
//    }

    /**
     * Returns the balance of a user
     *
     * @param name Name of the user
     * @return balance
     * @throws UserDoesNotExistException
     */
    public static double getMoney(String name) throws UserDoesNotExistException {
        UUID uuid = CommandUtils.getUUIDFromUsername(name);
        if (uuid == null) {
            throw new UserDoesNotExistException(name);
        }

        EconomyAPI.BalanceWrapper balanceWrapper = FundamentalsAPI.getEconomy().getBalance(uuid);
        if (balanceWrapper.getEconomyResult() == EconomyAPI.EconomyResult.successful) {
            return balanceWrapper.getBalance();
        } else {
            throw new UserDoesNotExistException(name);
        }
    }

    /**
     * Sets the balance of a user
     *
     * @param name    Name of the user
     * @param balance The balance you want to set
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     */
    public static void setMoney(String name, double balance) throws UserDoesNotExistException, NoLoanPermittedException {
        UUID uuid = CommandUtils.getUUIDFromUsername(name);

        if (balance < 0.0) {
            throw new NoLoanPermittedException();
        }
        if (uuid == null) {
            throw new UserDoesNotExistException(name);
        }
        EconomyAPI.EconomyResult economyResult = FundamentalsAPI.getEconomy().setBalance(uuid, balance);
        if (economyResult != EconomyAPI.EconomyResult.successful) {
            throw new UserDoesNotExistException(name);
        }
        ess.debugLogger(Level.INFO, "The balance of " + name + " has been set to " + balance, 2);
    }

    /**
     * Adds money to the balance of a user
     *
     * @param name   Name of the user
     * @param amount The money you want to add
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     */
    public static void add(String name, double amount) throws UserDoesNotExistException, NoLoanPermittedException {
        double result = getMoney(name) + amount;
        setMoney(name, result);
    }

    /**
     * Substracts money from the balance of a user
     *
     * @param name   Name of the user
     * @param amount The money you want to substract
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     */
    public static void subtract(String name, double amount) throws UserDoesNotExistException, NoLoanPermittedException {
        double result = getMoney(name) - amount;
        setMoney(name, result);
    }

    /**
     * Divides the balance of a user by a value
     *
     * @param name  Name of the user
     * @param value The balance is divided by this value
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     */
    public static void divide(String name, double value) throws UserDoesNotExistException, NoLoanPermittedException {
        double result = getMoney(name) / value;
        setMoney(name, result);
    }

    /**
     * Multiplies the balance of a user by a value
     *
     * @param name  Name of the user
     * @param value The balance is multiplied by this value
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     */
    public static void multiply(String name, double value) throws UserDoesNotExistException, NoLoanPermittedException {
        double result = getMoney(name) * value;
        setMoney(name, result);
    }

    /**
     * Resets the balance of a user to the starting balance
     *
     * @param name Name of the user
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     */
    public static void resetBalance(String name) throws UserDoesNotExistException, NoLoanPermittedException {
        setMoney(name, 0D);
    }

    /**
     * @param name   Name of the user
     * @param amount The amount of money the user should have
     * @return true, if the user has more or an equal amount of money
     * @throws UserDoesNotExistException If a user by that name does not exists
     */
    public static boolean hasEnough(String name, double amount) throws UserDoesNotExistException {
        return amount <= getMoney(name);
    }

    /**
     * @param name   Name of the user
     * @param amount The amount of money the user should have
     * @return true, if the user has more money
     * @throws UserDoesNotExistException If a user by that name does not exists
     */
    public static boolean hasMore(String name, double amount) throws UserDoesNotExistException {
        return amount < getMoney(name);
    }

    /**
     * @param name   Name of the user
     * @param amount The amount of money the user should not have
     * @return true, if the user has less money
     * @throws UserDoesNotExistException If a user by that name does not exists
     */
    public static boolean hasLess(String name, double amount) throws UserDoesNotExistException {
        return amount > getMoney(name);
    }

    /**
     * Test if the user has a negative balance
     *
     * @param name Name of the user
     * @return true, if the user has a negative balance
     * @throws UserDoesNotExistException If a user by that name does not exists
     */
    public static boolean isNegative(String name) throws UserDoesNotExistException {
        return getMoney(name) < 0.0;
    }

    /**
     * Formats the amount of money like all other Essentials functions.
     * Example: $100000 or $12345.67
     *
     * @param amount The amount of money
     * @return Formatted money
     */
    public static String format(double amount) {
        return formatCurrency(amount);
    }

    private static String formatCurrency(final double value) {
        String str = "$" + df.format(value);
        if (str.endsWith(".00")) {
            str = str.substring(0, str.length() - 3);
        }
        return str;
    }

    private static DecimalFormat df = new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance(Locale.US));

    /**
     * Test if a player exists to avoid the UserDoesNotExistException
     *
     * @param name Name of the user
     * @return true, if the user exists
     */
    public static boolean playerExists(String name) {
        UUID uuid = CommandUtils.getUUIDFromUsername(name);
        if (uuid == null) {
            return false;
        }
        EconomyAPI.BalanceWrapper balanceWrapper = FundamentalsAPI.getEconomy().getBalance(uuid);
        if (balanceWrapper.getEconomyResult() == EconomyAPI.EconomyResult.successful) {
            return true;
        }
        return false;
    }

    /**
     * Test if a player is a npc
     *
     * @param name Name of the player
     * @return true, if it's a npc
     * @throws UserDoesNotExistException
     */
    public static boolean isNPC(String name) throws UserDoesNotExistException {
        return false;
    }

    /**
     * Creates dummy files for a npc, if there is no player yet with that name.
     *
     * @param name Name of the player
     * @return true, if a new npc was created
     */
    public static boolean createNPC(String name) {
        return false;
    }

    /**
     * Deletes a user, if it is marked as npc.
     *
     * @param name Name of the player
     * @throws UserDoesNotExistException
     */
    public static void removeNPC(String name) throws UserDoesNotExistException {
        return;
    }
}
