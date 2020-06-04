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
        loadFile();
    }

    private void loadDefaults() {
        //General Stuff
        map.put("no_permission", "&4Sorry, you don't have permission for this command.");
        map.put("unavailable_to_console", "&4Sorry, console can't run this command.");
        map.put("player_not_found_full", "&4Can't find a player called &9%username%");
        map.put("generic_error", "&4Sorry, an error occurred running that command, please contact staff!");
        map.put("generic_no_save_data", "&4Sorry, Fundamentals has no information on that player.");
        //AFK
        map.put("set_player_afk", "&4Set a player to AFK called &9%username%");
        map.put("afk_toggle_off", "%var1% is no longer afk");
        map.put("afk_toggle_on", "%var1% is now afk");
        map.put("afk_kick_message", "&4You have been kicked for inactivity");
        //Home
        map.put("home_non_recorded", "&6Sorry, you have no homes on record. Please set one with /sethome");
        map.put("home_not_on_record", "&6Sorry, we couldn't find a home with that name. Do /home for a list of homes");
        map.put("home_in_invalid_world", "&4Sorry, your home is in an invalid world.");
        map.put("home_teleport_successfully", "&6You have been teleported to your home &b%var1%");
        //Sethome
        map.put("sethome_invalid_name", "&4Only alphanumeric characters can be used in a home name, A-Z,0-9");
        map.put("sethome_full", "&4Sorry, you are already have a home set");
        map.put("sethome_limit_reached", "&6Sorry, you are already at your limit of &4%var1% &6homes.");
        map.put("sethome_already_exists", "&6Sorry, a home with the name &b%var1%&6 already exists. Please delete the existing home before creating another with the same name.");
        map.put("sethome_set_successfully", "&6Your home &b%var1% &6has been set.");
        //Delhome
        map.put("delhome_info", "&6Please specify a home name \"/delhome (home)\". A list of homes can be obtained with /home.");
        map.put("delhome_unknown_home", "&6Sorry, we couldn't find a home with that name. Do /home to get a list of homes.");
        map.put("delhome_unknown_successful", "&6You have successfully deleted a home");
        map.put("delhome_unknown_unsuccessful", "&4Sorry, an error was encountered when deleting that home. Please contact staff!");
        //Balance
        map.put("balance_successful", "&6Balance: $%var1%");
        //Pay
        map.put("pay_info", "&4You haven't provided enough arguments, /pay (username) (amount)");
        map.put("pay_can't_pay_self", "&4Sorry, you can't send money to yourself!");
        map.put("pay_not_enough_funds", "&4Sorry, you don't have enough funds to send that much money!");
        map.put("pay_successful", "&6You have sent $%var1% to %var2%");
        //Economy
        map.put("economy_info", "&4You haven't provided enough arguments, /economy (username) (set|give|take) (amount)");
        map.put("economy_arg_1", "&4Please specify set, give or take");
        map.put("economy_result_successful", "&bThe balance has been modified successfully");
        map.put("economy_result_nofunds", "&4Sorry, that user doesn't have that much money to take. Maybe try set instead?");
        //God Mode
        map.put("god_enable", "&6Godmode has been enabled");
        map.put("god_disable", "&6Godmode has been disabled");
        //Nickname
        map.put("nickname_info", "&4You haven't used this command correctly, /nick (nickname)");
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
        //Clear Inventory
        map.put("clearinventory_notice", "&9Your inventory has been cleared");
        map.put("clearinventory_successfully", "&9Inventory cleared successfully");


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


    public static FundamentalsLanguage getInstance() {
        if (FundamentalsLanguage.singleton == null) {
            throw new RuntimeException("A instance of Fundamentals hasn't been passed into FundamentalsLanguage yet.");
        }
        return FundamentalsLanguage.singleton;
    }

    public static FundamentalsLanguage getInstance(Fundamentals plugin) {
        if (FundamentalsLanguage.singleton == null) {
            FundamentalsLanguage.singleton = new FundamentalsLanguage(plugin);
        }
        return FundamentalsLanguage.singleton;
    }


}
