//package com.johnymuffin.fundamentals.importer.commands;
//
//import com.johnymuffin.beta.fundamentals.Fundamentals;
//import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
//import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
//import com.johnymuffin.fundamentals.importer.FundamentalsESSBridge;
//import com.projectposeidon.api.PoseidonUUID;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//
//import java.util.UUID;
//
//import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;
//
//public class CommandTransfer implements CommandExecutor {
//    private FundamentalsESSBridge fundamentalsImporter;
//
//    @Override
//    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
//        if (!isPlayerAuthorized(commandSender, "fundamentals.transfer")) {
//            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
//            return true;
//        }
//        if (strings.length != 2) {
//            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("transfer_info"));
//            return true;
//        }
//        String oldUsername = strings[0];
//        String newUsername = strings[1];
//
//        //Check if the new UUID is known. It is required to transfer the information of the player.
//        UUID uuid = PoseidonUUID.getPlayerMojangUUID(newUsername);
//        if(uuid == null) {
//            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("transfer_unknown_uuid"));
//            return true;
//        }
//        //Check if player is known by Fundamentals.
//        if(!FundamentalsPlayerMap.getInstance().isPlayerKnown(uuid)) {
//            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("transfer_unknown_player"));
//            return true;
//        }
//
//
//    }
//}
