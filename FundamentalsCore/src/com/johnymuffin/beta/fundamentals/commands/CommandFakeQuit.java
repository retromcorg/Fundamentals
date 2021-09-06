//package com.johnymuffin.beta.fundamentals.commands;
//
//import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//
//import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;
//
//public class CommandFakeQuit implements CommandExecutor {
//
//    @Override
//    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
//        if (!isPlayerAuthorized(commandSender, "fundamentals.fakequit")) {
//            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
//            return true;
//        }
//
//        if (!(commandSender instanceof Player)) {
//            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("unavailable_to_console"));
//            return true;
//        }
//
//
//    }
//
//
//}