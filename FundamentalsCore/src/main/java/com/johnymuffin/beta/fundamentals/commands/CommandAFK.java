package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.johnymuffin.beta.fundamentals.util.Utils.getPlayerFromString;

public class CommandAFK implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender.hasPermission("fundamentals.afk") || commandSender.isOp())) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }
        //Check if user is trying to heal another user
        if (strings.length > 0) {
            if (!(commandSender.hasPermission("fundamentals.afk.others") || commandSender.isOp())) {
                commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
                return true;
            }
            Player player = getPlayerFromString(strings[0]);
            if (player == null) {
                String message = FundamentalsLanguage.getInstance().getMessage("player_not_found_full");
                message = message.replace("%username%", strings[0]);
                commandSender.sendMessage(message);
                return true;
            }
            FundamentalsPlayerMap.getInstance().getPlayer(player).toggleAFK();
            String message = FundamentalsLanguage.getInstance().getMessage("set_player_afk");
            message = message.replace("%username%", player.getName());
            commandSender.sendMessage(message);
            return true;
        } else if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("unavailable_to_console"));
            return true;
        }
        Player player = (Player) commandSender;
        if(FundamentalsPlayerMap.getInstance().getPlayer(player.getUniqueId()).isRequestingAFK()){
            player.sendMessage(FundamentalsLanguage.getInstance().getMessage("already_requesting_afk"));
            return true;
        }
        if(!FundamentalsPlayerMap.getInstance().getPlayer(player.getUniqueId()).isAFK()){
            new ScheduleAFK(player);
        }else{
            FundamentalsPlayerMap.getInstance().getPlayer(player).toggleAFK();
        }
        return true;
    }

    class ScheduleAFK implements Runnable{

        private Player player;
        private CheckConditions checker;
        private int taskId = -1;

        private ScheduleAFK(Player player){
            this.player = player;
            FundamentalsPlayerMap.getInstance().getPlayer(player.getUniqueId()).setAFKRequestStatus(true);
            player.sendMessage(FundamentalsLanguage.getInstance().getMessage("commencing_afk"));
            taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(Fundamentals.getPlugin(), this, 60);
            checker = new CheckConditions(player, this);

        }

        @Override
        public void run(){
            FundamentalsPlayerMap.getInstance().getPlayer(player.getUniqueId()).toggleAFK();
            FundamentalsPlayerMap.getInstance().getPlayer(player.getUniqueId()).setAFKRequestStatus(false);
            Bukkit.getScheduler().cancelTask(checker.taskId);
        }
    }

    class CheckConditions implements Runnable{

        private Player player;
        private int health;
        private double posX;
        private double posY;
        private double posZ;

        private ScheduleAFK task;
        private int taskId = -1;

        private CheckConditions(Player player, ScheduleAFK task){
            this.player = player;
            health = player.getHealth();
            Location location = player.getLocation();
            posX = roundPos(location.getX());
            posY = roundPos(location.getY());
            posZ = roundPos(location.getZ());
            this.task = task;
            taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Fundamentals.getPlugin(), this, 0, 1);
        }

        @Override
        public void run(){
            if(player.getHealth() < health || roundPos(player.getLocation().getX()) != posX
                    || roundPos(player.getLocation().getY()) != posY || roundPos(player.getLocation().getZ()) != posZ){
                FundamentalsPlayerMap.getInstance().getPlayer(player.getUniqueId()).setAFKRequestStatus(false);
                Bukkit.getScheduler().cancelTask(task.taskId);
                Bukkit.getScheduler().cancelTask(taskId);
                player.sendMessage(FundamentalsLanguage.getInstance().getMessage("afk_cancelled"));
            }
        }
        private double roundPos(double d){
            return Math.round(d*10)/10f;
        }
    }
}
