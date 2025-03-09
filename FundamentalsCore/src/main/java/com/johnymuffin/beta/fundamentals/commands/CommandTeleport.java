//package com.johnymuffin.beta.fundamentals.commands;
//
//import com.johnymuffin.beta.fundamentals.Fundamentals;
//import org.bukkit.Bukkit;
//import org.bukkit.Location;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//
//public class CommandTeleport implements CommandExecutor {
//
//    private Fundamentals plugin;
//
//    public CommandTeleport(Fundamentals plugin) {
//        this.plugin = plugin;
//    }
//
//    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//        // Check if sender has teleport permission (customize as needed)
//        if (!sender.hasPermission("fundamentals.teleport")) {
//            sender.sendMessage("You do not have permission to use this command.");
//            return true;
//        }
//
//        try {
//            switch (args.length) {
//                case 1: // /tp <targetPlayerName>
//                    return teleportToPlayer(sender, args[0]);
//
//                case 3: // /tp <x> <y> <z>
//                    return teleportToCoordinates(sender, args);
//
//                case 4: // /tp <targetPlayerName> <x> <y> <z>
//                    return teleportPlayerToCoordinates(sender, args[0], args[1], args[2], args[3]);
//
//                case 2: // /tp <targetPlayerName> <destinationPlayerName>
//                    return teleportPlayerToPlayer(sender, args[0], args[1]);
//
//                default:
//                    sender.sendMessage("Usage: /tp <player> [destinationPlayer] or /tp [x y z] or /tp <player> [x y z]");
//                    return true;
//            }
//        } catch (Exception e) {
//            sender.sendMessage("An error occurred while executing the command.");
//            return true;
//        }
//    }
//
//    private boolean teleportToCoordinates(CommandSender sender, String[] args) {
//        if (!(sender instanceof Player)) {
//            sender.sendMessage("Only players can use this command.");
//            return true;
//        }
//
//        Player player = (Player) sender;
//        Location loc = new Location(player.getWorld(), Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]));
//        player.teleport(loc);
//        sender.sendMessage("Teleported to coordinates.");
//        return true;
//    }
//
//    private boolean teleportPlayerToCoordinates(CommandSender sender, String playerName, String x, String y, String z) {
//        Player player = Bukkit.getPlayer(playerName);
//        if (player == null) {
//            Bukkit.getServer().getConsoleSender().sendMessage("Player not found.");
//
//            return true;
//        }
//
//        Location loc = new Location(player.getWorld(), Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z));
//        player.teleport(loc);
//        Bukkit.getServer().getConsoleSender().sendMessage("Teleported " + playerName + " to coordinates.");
//        return true;
//    }
//
//    private boolean teleportToPlayer(CommandSender sender, String targetPlayerName) {
//        if (!(sender instanceof Player)) {
//            sender.sendMessage("Only players can use this command.");
//            return true;
//        }
//
//        Player player = (Player) sender;
//        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
//        if (targetPlayer == null) {
//            sender.sendMessage("Target player not found.");
//            return true;
//        }
//
//        player.teleport(targetPlayer.getLocation());
//        sender.sendMessage("Teleported to " + targetPlayerName + ".");
//        return true;
//    }
//
//    private boolean teleportPlayerToPlayer(CommandSender sender, String playerName, String destinationPlayerName) {
//        Player player = Bukkit.getPlayer(playerName);
//        Player destinationPlayer = Bukkit.getPlayer(destinationPlayerName);
//        if (player == null || destinationPlayer == null) {
//            Bukkit.getServer().getConsoleSender().sendMessage("Player not found.");
//            return true;
//        }
//
//        player.teleport(destinationPlayer.getLocation());
//        Bukkit.getServer().getConsoleSender().sendMessage("Teleported " + playerName + " to " + destinationPlayerName + ".");
//        return true;
//    }
//}
