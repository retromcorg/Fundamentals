package com.johnymuffin.beta.fundamentals.settings;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.HashMap;

public class FundamentalsLanguage extends Configuration {
    private static FundamentalsLanguage singleton = null;
    private HashMap<String, String> map;

    private FundamentalsLanguage(Fundamentals plugin) {
        super(new File(plugin.getDataFolder(), String.valueOf(FundamentalsConfig.getInstance(plugin).getConfigOption("settings.message-file"))));
        map = new HashMap<String, String>();
        loadDefaults();
        if(!plugin.getFundamentalConfig().getConfigBoolean("settings.default-language-file.enabled")) {
            loadFile();
        }
    }

    private void loadDefaults() {
        //General Stuff
        map.put("no_permission", "&4Sorry, you don't have permission for this command.");
        map.put("unavailable_to_console", "&4Sorry, console can't run this command.");
        map.put("player_not_found_full", "&4Can't find a player called &9%username%");
        map.put("generic_error", "&4Sorry, an error occurred running that command, please contact staff!");
        map.put("generic_error_player", "&4Sorry, an error occurred:&f %var1%");
        map.put("generic_no_save_data", "&4Sorry, Fundamentals has no information on that player.");
        map.put("generic_invalid_world", "&cSorry, a world with that name couldn't be located");
        //AFK
        map.put("set_player_afk", "&4Set a player to AFK called &9%username%");
        map.put("afk_toggle_off", "%var1% is no longer afk");
        map.put("afk_toggle_on", "%var1% is now afk");
        map.put("afk_kick_message", "&4You have been kicked for inactivity");
        map.put("commencing_afk", "&7You will be marked as AFK in 3 seconds. Do not move or take damage.");
        map.put("afk_cancelled", "&cPending AFK request cancelled.");
        map.put("already_requesting_afk", "&cSlow down, you are already requesting AFK!");
        map.put("moving_while_afk", "&cPlease type /afk to un-AFK!");
        //Hat
        map.put("hat_air", "&cWhy would you want air on your head?");
        map.put("hat_dropping", "Was unable to put the old hat away, dropping it at your feet");
        map.put("hat_success", "Enjoy your new hat!");
        map.put("hat_invalid", "&cYou can't put that item on your head silly!");
        //Home
        map.put("home_not_on_record", "&7Home &b%homeName% &7unknown. Run &b/homes &7for a list of homes");
        map.put("home_in_invalid_world", "&7Home is in an invalid world.");
        map.put("home_is_sleeping", "&7Can't teleport to a home while sleeping.");
        map.put("home_empty_player_target", "&7The target players name can't be empty.");
        map.put("home_teleport_successfully", "&7Teleported to home &b%homeName%&7.");
        map.put("home_teleport_successfully_others", "&7Teleported to &b%targetPlayerName%&7's home &b%homeName%&7.");
        map.put("home_usage", "&7Usage: &b/home <home_name>");
        map.put("home_usage_staff_extra", "&b/home <player> <home_name>");
        map.put("home_usage_staff_extra_backport", "&b/home <player>:[home_name]");
        //Homes
        map.put("homes_non_recorded", "&7No homes on record. Please set one with &b/sethome&7.");
        map.put("homes_non_recorded_others", "&b%targetPlayerName%&7 has no homes on record.");
        map.put("homes_usage", "&7Usage: &b/homes");
        map.put("homes_usage_staff", "&7Usage: &b/homes [player]");
        map.put("homes_page_count", "&7=======================[ &b%pageToView%&3/&b%pageCount% &7]=======================");
        //Homesearch
        map.put("homesearch_invalid_radius", "Please enter a valid number for radius.");
        map.put("homesearch_usage", "Usage: /homesearch <username> <radius>");
        map.put("homesearch_no_homes_found", "&3No homes found within a &6%radius% &3block radius.");
        map.put("homesearch_found_homes", "&3Found &6%count% &3home(s) in a &6%radius% &3block radius:");
        map.put("homesearch_found_entries", "&b%player% &8- &7%homes%");
        map.put("homesearch_too_many_results", "&cToo many results to display! &7Use a smaller radius.");
        //Sethome
        map.put("sethome_invalid_name", "&7Only alphanumeric characters can be used in a home name: &bA-Z&7,&b0-9&7.");
        map.put("sethome_full", "&7Home already set.");
        map.put("sethome_limit_reached", "&7Home limit of &b%homeLimit% &7homes reached.");
        map.put("sethome_already_exists", "&7Home &b%homeName%&7 already exists. Delete it with &b/delhome %homeName%&7.");
        map.put("sethome_set_successfully", "&7Home &b%homeName% &7set.");
        map.put("sethome_set_successfully_others", "&b%targetPlayerName%&7's home &b%homeName% &7has been set.");
        map.put("sethome_usage", "&7Usage: &b/sethome <home_name>");
        map.put("sethome_usage_staff_extra", "&b/sethome <player> <home_name>");
        //Delhome
        map.put("delhome_unsuccessful", "&cAn error was encountered when deleting that home. Please contact staff!");
        map.put("delhome_deleted_successfully", "&7Home &b%homeName% &7deleted.");
        map.put("delhome_deleted_successfully_others", "&b%targetPlayerName%&7's home &b%homeName% &7has been deleted.");
        map.put("delhome_usage", "&7Usage: &b/delhome <home_name>");
        map.put("delhome_usage_staff_extra", "&b/delhome <player> <home_name>");
        //Balance
        map.put("balance_successful", "&6Balance: $%var1%");
        //Baltop
        map.put("balancetop_invalid_integer", "&4Please provide an integer as the page number.");
        map.put("balancetop_too_high", "&4That page doesn't exist as it is too high.");
        map.put("balancetop_too_low", "&4That page doesn't exist as it is too low. The first page number is 1.");
        //Pay
        map.put("pay_info", "&4You haven't provided enough arguments, /pay (username) (amount)");
        map.put("pay_can't_pay_self", "&4Sorry, you can't send money to yourself!");
        map.put("pay_not_enough_funds", "&4Sorry, you don't have enough funds to send that much money!");
        map.put("pay_successful", "&6You have sent $%var1% to %var2%");
        //Transfer
        map.put("transfer_info", "&4You haven't provided enough arguments, /transfer (old_username) (new_username)");
        map.put("transfer_unknown_uuid", "&4The UUID for the new username is unknown. Please get the player to join the server before issuing this command.");
        map.put("transfer_unknown_player", "&4The new username doesn't have a Fundamentals player data file. Please get the player to join the server before issuing this command.");
        map.put("transfer_complete", "&6The transfer has been scheduled.");
        //Economy
        map.put("economy_info", "&4You haven't provided enough arguments, /economy (username) (set|give|take) (amount)");
        map.put("economy_arg_1", "&4Please specify set, give or take");
        map.put("economy_result_successful", "&bThe balance has been modified successfully");
        map.put("economy_result_nofunds", "&4Sorry, that user doesn't have that much money to take. Maybe try set instead?");
        //God Mode
        map.put("god_enable", "&6Godmode has been enabled");
        map.put("god_disable", "&6Godmode has been disabled");
        //Nickname
        map.put("nickname_info", "&4Please use /nick <player> [nickname:off]");
        map.put("nickname_remove", "&6Your nickname has been removed");
        map.put("nickname_set", "&6Your nickname has been set to &f%var1%");
        map.put("nickname_set_others", "&6The nickname for %var1% has been set to &f%var2%");
        map.put("nickname_remove_others", "&4You have removed a nickname");
        //InvSee
        map.put("invsee_deny", "&cSorry, you can't do that while you are in InvSee");
        map.put("invsee_info", "&4You haven't provided enough arguments, /invsee (username)");
        map.put("invsee_disable", "&bYou have disabled InvSee");
        map.put("invsee_already", "&4Sorry, you are already in InvSee. Please do /invsee to disable");
        map.put("invsee_enable", "&bYou have enabled InvSee for %var1%");
        //Vanish
        map.put("vanish_deny", "&cSorry, you can't do that while you are in vanish");
        map.put("vanish_successful_other_enabled", "&bEnabled vanish for another player.");
        map.put("vanish_successful_other_disabled", "&4Disabled vanish for another player.");
        map.put("vanish_disable", "&4Vanish Disabled.");
        map.put("vanish_enable", "&7Vanish Enabled.");
        //Weather
        map.put("weather_invalid_usage", "&6Invalid usage: &a/weather <clear|sun|rain|storm> [worldname|all]");
        map.put("weather_set_weather", "&7Weather has been set to %var1% in %var2%");
        //Clear Inventory
        map.put("clearinventory_notice", "&9Your inventory has been cleared");
        map.put("clearinventory_successfully", "&9Inventory cleared successfully");
        //Time Command
        map.put("time_info", "/time [day|night|dawn|17:30|4pm|4000ticks] [worldname|all]");
         map.put("time_change_successful", "The time has been changed to %var1% in %var2%");

        // PTime Command
        map.put("ptime_info", "/ptime [reset|off|day|night|dawn|17:30|4pm|4000ticks] [player]");
        map.put("ptime_change_successful", "The time has been changed to %var1% for %var2%");

        //Bank Stuff
        map.put("bank_info", "&7Fundamentals Banking System\n&8/bank list - List your accounts\n&8/bank new (name) - Make account\n&8/bank delete (name) - Delete account\n&8/bank (name) - Show bank details\n&8/bank (name) (deposit/withdraw) (amount)\n&8/bank (name) (add/remove) (username/uuid)");
        map.put("bank_unknown", "&4Sorry, that bank account is unknown.");
        map.put("bank_account_info", "&7Bank Account Name: &6%var1%\n&7Amount: &6$%var2%\n&7Owner: &6%var3%\n&7Access List: &6%var4%");
        map.put("bank_new", "&4You haven't provided enough arguments, /bank new (name)");
        map.put("bank_delete", "&4You haven't provided enough arguments, /bank delete (name)");
        map.put("bank_edit_access", "&4You haven't provided enough arguments, /bank (name) (add/remove) (username/uuid)");
        map.put("bank_withdraw", "&4You haven't provided enough arguments, /bank (name) (deposit/withdraw) (amount)");
        map.put("bank_invalid_name", "&4Bank names must be no more than 16 characters, and use any of {A-Z,a-z,0-9}");
        map.put("bank_already_exists", "&4A bank with that name already exists. Please try another name.");
        map.put("bank_created_successfully", "&6Your bank account has been created successfully.");
        map.put("bank_deleted_successfully", "&4Your bank account has been deleted successfully.");
        map.put("bank_delete_unauthorized", "&4Sorry, you cannot delete this bank account as you are not the owner.");
        map.put("bank_add_unauthorized", "&4Sorry, you cannot add to this bank account as you are not the owner.");
        map.put("bank_remove_unauthorized", "&4Sorry, you cannot remove users from the bank account as you are not the owner.");
        map.put("bank_add_successfully", "&6The user has been added successfully.\n&4PLEASE NOTE, Staff will not be responsible nor will assist for any theft regarding a bank account. Only add people you trust to your account.");
        map.put("bank_remove_successfully", "&6The user has been removed successfully.");
        map.put("bank_deposit_successfully", "&6You have successfully deposited money.");
        map.put("bank_withdraw_successfully", "&6You have successfully withdrawn money.");
        map.put("bank_withdraw_unauthorized", "&4Sorry, you cannot withdraw from the bank account as you are not the owner or authorized.");
        map.put("bank_invalid_integer", "&4Sorry, please specify a number without decimal places.");

        //FakeQuit
        map.put("fakequit_quit_on_join", "&cYou were fake quit, so your join message was hidden.");
        map.put("fakequit_disabled", "&cFakeQuit has been disabled.");
        map.put("fakequit_enabled", "&cFakeQuit has been enabled.");
        map.put("fakequit_vanish_enabled", "&cvanish has been enabled as you fake quit.");


    }

    private void loadFile() {
        this.load();
        for (String key : map.keySet()) {
            if (this.getString(key) == null) {
                this.setProperty(key, map.get(key));
            } else {
                map.put(key, this.getString(key));
            }
        }
        this.save();
    }

    public String getMessage(String msg) {
        String loc = map.get(msg);
        if (loc != null) {
            return loc.replace("&", "\u00a7");
        }
        return msg;
    }

    @Deprecated
    public static FundamentalsLanguage getInstance() {
        if (FundamentalsLanguage.singleton == null) {
            throw new RuntimeException("A instance of Fundamentals hasn't been passed into FundamentalsLanguage yet.");
        }
        return FundamentalsLanguage.singleton;
    }

    @Deprecated
    public static FundamentalsLanguage getInstance(Fundamentals plugin) {
        if (FundamentalsLanguage.singleton == null) {
            FundamentalsLanguage.singleton = new FundamentalsLanguage(plugin);
        }
        return FundamentalsLanguage.singleton;
    }


}
