package com.johnymuffin.beta.fundamentals.commands;

import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;
import static com.johnymuffin.beta.fundamentals.util.Utils.getPlayerName;
import static com.johnymuffin.beta.fundamentals.util.Utils.getUUIDFromUsername;
import static com.johnymuffin.beta.fundamentals.util.Utils.isInt;
import static com.johnymuffin.beta.fundamentals.util.Utils.sendLangFileMessage;
import static com.johnymuffin.beta.fundamentals.util.Utils.sendNewLinedMessage;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.api.EconomyAPI;
import com.johnymuffin.beta.fundamentals.api.FundamentalsAPI;
import com.johnymuffin.beta.fundamentals.banks.FundamentalsBank;
import com.johnymuffin.beta.fundamentals.events.BankCreationEvent;
import com.johnymuffin.beta.fundamentals.settings.BankManager;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;

public class CommandBank implements CommandExecutor {

    private Fundamentals plugin;

    public CommandBank(Fundamentals plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!isPlayerAuthorized(commandSender, "fundamentals.bank")) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("unavailable_to_console"));
            return true;
        }
        Player player = (Player) commandSender;

        if (strings.length == 0) {
            //commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("bank_info"));
            sendLangFileMessage(commandSender, "bank_info");
            return true;
        }

        if (strings.length >= 1) {
            if (strings[0].equalsIgnoreCase("list")) {
                HashMap<String, BankManager.AccessType> accessibleBanks = plugin.getBankManager().getAccessibleBanks(player.getUniqueId());
                commandSender.sendMessage(ChatColor.GRAY + "Bank List: ");
                for (String bankName : accessibleBanks.keySet()) {
                    commandSender.sendMessage(ChatColor.GRAY + bankName + " - " + accessibleBanks.get(bankName).name());
                }
                return true;
            } else if (strings[0].equalsIgnoreCase("new")) {
                if (!isPlayerAuthorized(commandSender, "fundamentals.bank.new")) {
                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
                    return true;
                }
                //Command argument failure
                if (strings.length == 1) {
                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("bank_new"));
                    return true;
                }
                //Actually trying to make an account
                String name = strings[1];
                if (!verifyBankName(name)) {
                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("bank_invalid_name"));
                    return true;
                }
                //Verify name isn't already in use
                if (plugin.getBanks().containsKey(name.toLowerCase())) {
                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("bank_already_exists"));
                    return true;
                }

                //Yeah, lets actually make an account.
                FundamentalsBank bank = new FundamentalsBank(name, player.getUniqueId(), new UUID[0], 0);
                plugin.getBanks().put(bank.getBankName().toLowerCase(), bank);

                BankCreationEvent bankCreationEvent = new BankCreationEvent(plugin, plugin.getPlayerMap().getPlayer(player), bank);
                Bukkit.getServer().getPluginManager().callEvent(bankCreationEvent);

                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("bank_created_successfully"));
                return true;
            } else if (strings[0].equalsIgnoreCase("delete")) {
                //Command argument failure
                if (strings.length == 1) {
                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("bank_delete"));
                    return true;
                }
                if (!plugin.getBanks().containsKey(strings[1].toLowerCase())) {
                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("bank_unknown"));
                    return true;
                }

                FundamentalsBank bank = plugin.getBanks().get(strings[1].toLowerCase());
                if (!bank.getBankOwner().equals(player.getUniqueId())) {
                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("bank_delete_unauthorized"));
                    return true;
                }

                //Transfer money out of account before closing - start
                EconomyAPI.EconomyResult economyResult = FundamentalsAPI.getEconomy().additionBalance(player.getUniqueId(), bank.getBalance());
                switch (economyResult) {
                    case error:
                    case userNotKnown:
                        commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("generic_error"));
                        return true;
                }

                bank.setBalance(0);
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("bank_withdraw_successfully"));
                //Transfer money out of account before closing - end

                plugin.getBanks().remove(strings[1].toLowerCase());
                sendLangFileMessage(commandSender, "bank_deleted_successfully");
                return true;
            }
            //Assume player is trying to get details of a bank account.
            if (!plugin.getBanks().containsKey(strings[0].toLowerCase())) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("bank_unknown"));
                return true;
            }

            FundamentalsBank bank = plugin.getBanks().get(strings[0].toLowerCase());

            //Bank Info
            if (strings.length == 1) {
                String bankInfo = FundamentalsLanguage.getInstance().getMessage("bank_account_info");
                bankInfo = bankInfo.replace("%var1%", bank.getBankName());
                bankInfo = bankInfo.replace("%var2%", String.valueOf((int) bank.getBalance()));
                bankInfo = bankInfo.replace("%var3%", getUsername(bank.getBankOwner()));
                //Generate list of users who have access - start
                String accessList = "";
                boolean firstEntry = true;
                for (UUID uuid : bank.getAccessList()) {
                    if (firstEntry) {
                        accessList = getUsername(uuid);
                        firstEntry = false;
                    } else {
                        accessList = accessList + ", " + getUsername(uuid);
                    }
                }
                //Generate list of users who have access - end
                bankInfo = bankInfo.replace("%var4%", accessList);
                //commandSender.sendMessage(bankInfo);
                sendNewLinedMessage(commandSender, bankInfo);
                return true;
            }

            if (strings[1].equalsIgnoreCase("add")) {
                if (strings.length == 2) {
                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("bank_info"));
                    return true;
                }
                //Check if player is the bank owner
                if (!player.getUniqueId().equals(bank.getBankOwner())) {
                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("bank_add_unauthorized"));
                    return true;
                }

                UUID uuid = getUUIDFromUsername(strings[2]);
                if (uuid == null) {
                    String error = FundamentalsLanguage.getInstance().getMessage("player_not_found_full");
                    error = error.replace("%username%", strings[2]);
                    commandSender.sendMessage(error);
                    return true;
                }

                UUID[] accessList = new UUID[bank.getAccessList().length + 1];
                int counter = 0;
                for (UUID accessUser : bank.getAccessList()) {
                    accessList[counter] = accessUser;
                    counter++;
                }

                accessList[bank.getAccessList().length] = uuid;
                bank.setAccessList(accessList);
                sendLangFileMessage(commandSender, "bank_add_successfully");
                return true;

            } else if (strings[1].equalsIgnoreCase("remove")) {
                if (strings.length == 2) {
                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("bank_info"));
                    return true;
                }
                //Check if player is the bank owner
                if (!player.getUniqueId().equals(bank.getBankOwner())) {
                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("bank_remove_unauthorized"));
                    return true;
                }

                UUID uuid = getUUIDFromUsername(strings[2]);
                if (uuid == null) {
                    String error = FundamentalsLanguage.getInstance().getMessage("player_not_found_full");
                    error = error.replace("%username%", strings[2]);
                    commandSender.sendMessage(error);
                    return true;
                }

                UUID[] accessList = new UUID[bank.getAccessList().length - 1];
                int counter = 0;
                for (UUID accessUser : bank.getAccessList()) {
                    if (accessUser.equals(uuid)) {
                        continue;
                    }
                    accessList[counter] = accessUser;
                    counter++;
                }

                bank.setAccessList(accessList);
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("bank_remove_successfully"));
                return true;
            } else if (strings[1].equalsIgnoreCase("deposit") || strings[1].equalsIgnoreCase("d")) {
                if (strings.length == 2) {
                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("bank_info"));
                    return true;
                }

                if (!isInt(strings[2].replaceAll("[^0-9\\.]", ""))) {
                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("bank_invalid_integer"));
                    return true;
                }
                double amount = Double.parseDouble(strings[2].replaceAll("[^0-9\\.]", ""));
                //Subtract money from user
                EconomyAPI.EconomyResult economyResult = FundamentalsAPI.getEconomy().subtractBalance(player.getUniqueId(), amount);
                switch (economyResult) {
                    case notEnoughFunds:
                        commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("pay_not_enough_funds"));
                        return true;
                    case error:
                    case userNotKnown:
                        commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("generic_error"));
                        return true;
                }

                bank.setBalance(bank.getBalance() + amount);
                plugin.debugLogger(Level.INFO, "Player " + player.getName() + " (" + player.getUniqueId() + ") has deposited $" + amount +
                        " into the bank account " + bank.getBankName(), 2);
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("bank_deposit_successfully"));
                return true;

            } else if (strings[1].equalsIgnoreCase("withdraw") || strings[1].equalsIgnoreCase("w")) {
                if (strings.length == 2) {
                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("bank_info"));
                    return true;
                }


                boolean authorized = false;
                if (player.getUniqueId().equals(bank.getBankOwner())) {
                    authorized = true;
                } else {
                    for (UUID uuidCheck : bank.getAccessList()) {
                        if (uuidCheck.equals(player.getUniqueId())) {
                            authorized = true;
                        }
                    }
                }

                if (!authorized) {
                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("bank_withdraw_unauthorized"));
                    return true;
                }

                if (!isInt(strings[2].replaceAll("[^0-9\\.]", ""))) {
                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("bank_invalid_integer"));
                    return true;
                }
                double amount = Double.parseDouble(strings[2].replaceAll("[^0-9\\.]", ""));

                if (bank.getBalance() < amount) {
                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("pay_not_enough_funds"));
                    return true;
                }

                bank.setBalance(bank.getBalance() - amount);

                //Subtract money from user
                EconomyAPI.EconomyResult economyResult = FundamentalsAPI.getEconomy().additionBalance(player.getUniqueId(), amount);
                switch (economyResult) {
                    case error:
                    case userNotKnown:
                        commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("generic_error"));
                        return true;
                }

                plugin.debugLogger(Level.INFO, "Player " + player.getName() + " (" + player.getUniqueId() + ") has withdrawn $" + amount +
                        " from the bank account " + bank.getBankName(), 2);
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("bank_withdraw_successfully"));
                return true;
            }
            if (strings.length == 2) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("bank_info"));
                return true;
            }

        }


        if (strings.length != 3) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("economy_info"));
            return true;
        }
        //Verify Player
        UUID giveTo = getUUIDFromUsername(strings[0]);
        if (giveTo == null) {
            String message = FundamentalsLanguage.getInstance().getMessage("player_not_found_full");
            message = message.replace("%username%", strings[0]);
            commandSender.sendMessage(message);
            return true;
        }
        //Get Amount
        double amount = Double.parseDouble(strings[2].replaceAll("[^0-9\\.]", ""));

        //Verify Type
        EconomyAPI.EconomyResult economyResult;
        if (strings[1].equalsIgnoreCase("set")) {
            economyResult = FundamentalsAPI.getEconomy().setBalance(giveTo, amount);
        } else if (strings[1].equalsIgnoreCase("give")) {
            economyResult = FundamentalsAPI.getEconomy().additionBalance(giveTo, amount);
        } else if (strings[1].equalsIgnoreCase("take")) {
            economyResult = FundamentalsAPI.getEconomy().subtractBalance(giveTo, amount);
        } else {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("economy_arg_1"));
            return true;
        }

        switch (economyResult) {
            case successful:
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("economy_result_successful"));
                return true;
            case error:
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("generic_error"));
                return true;
            case notEnoughFunds:
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("economy_result_nofunds"));
                return true;
            case userNotKnown:
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("generic_no_save_data"));
                return true;
        }
        return true;


    }

    public String getUsername(UUID uuid) {
        String username = getPlayerName(uuid);
        if (username == null) {
            username = "Unknown User";
        }
        return username;
    }

    public static boolean verifyBankName(String name) {
        return(
            Pattern.matches("^[a-zA-Z0-9]+$", name) &&
            name.length() <= 16
        );
    }
}