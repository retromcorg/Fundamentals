//package com.johnymuffin.beta.fundamentals.commands;
//
//import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
//import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//
//import static com.johnymuffin.beta.fundamentals.util.CommandUtils.getPlayerFromString;
//import static com.johnymuffin.beta.fundamentals.FundamentalPermission.isPlayerAuthorized;
//
//public class CommandTeleport implements CommandExecutor {
//    @Override
//    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
//        if (!isPlayerAuthorized(commandSender, "fundamentals.teleport")) {
//            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
//            return true;
//        }
//
//
//        switch (strings.length) {
//            case 0:
//                //Not Enough Args
//            case 1: //Teleporting to a player
//                Player teleportTo = getPlayerFromString(strings[0]);
//                if (teleportTo == null) {
//                    String message = FundamentalsLanguage.getInstance().getMessage("player_not_found_full");
//                    message = message.replace("%username%", strings[0]);
//                    commandSender.sendMessage(message);
//                    return true;
//                }
//                //Teleport Player
//            case 2: //Teleport one player to another
//                if (!isPlayerAuthorized(commandSender, "fundamentals.teleport.others")) {
//                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
//                    return true;
//                }
//                //Get Players
//                Player player1 = getPlayerFromString(strings[0]);
//                Player player2 = getPlayerFromString(strings[1]);
//
//                if (player1 == null) {
//                    String message = FundamentalsLanguage.getInstance().getMessage("player_not_found_full");
//                    message = message.replace("%username%", strings[0]);
//                    commandSender.sendMessage(message);
//                    return true;
//                }
//                if (player2 == null) {
//                    String message = FundamentalsLanguage.getInstance().getMessage("player_not_found_full");
//                    message = message.replace("%username%", strings[1]);
//                    commandSender.sendMessage(message);
//                    return true;
//                }
//
//                player1.teleport(player2.getLocation());
//
//
//            case 3: //Teleport to a set of cords
//            case 4: //Teleport a player to a set of cords
//
//        }
//
//
//        if (strings.length > 0) {
//            if (!isPlayerAuthorized(commandSender, "fundamentals.god.others")) {
//                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
//                return true;
//            }
//            Player giveTo = getPlayerFromString(strings[0]);
//            if (giveTo == null) {
//                String message = FundamentalsLanguage.getInstance().getMessage("player_not_found_full");
//                message = message.replace("%username%", strings[0]);
//                commandSender.sendMessage(message);
//                return true;
//            }
//            boolean currentGodmode = FundamentalsPlayerMap.getInstance().getPlayer(giveTo).getFileGodModeStatus();
//            currentGodmode = !currentGodmode;
//            FundamentalsPlayerMap.getInstance().getPlayer(giveTo).setFileGodModeStatus(currentGodmode);
//            if (currentGodmode) {
//                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("god_enable"));
//            } else {
//                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("god_disable"));
//            }
//
//        } else {
//            if (!(commandSender instanceof Player)) {
//                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("unavailable_to_console"));
//                return true;
//            }
//            Player giveTo = (Player) commandSender;
//            boolean currentGodmode = FundamentalsPlayerMap.getInstance().getPlayer(giveTo).getFileGodModeStatus();
//            currentGodmode = !currentGodmode;
//            FundamentalsPlayerMap.getInstance().getPlayer(giveTo).setFileGodModeStatus(currentGodmode);
//            if (currentGodmode) {
//                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("god_enable"));
//            } else {
//                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("god_disable"));
//            }
//        }
//        return true;
//
//
//    }