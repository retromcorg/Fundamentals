package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CommandWeather implements CommandExecutor {

    private Fundamentals plugin;

    public CommandWeather(Fundamentals plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player;
        List<World> worlds = new ArrayList<>();
        String worldarg;

        if (!(commandSender.hasPermission("fundamentals.weather") || commandSender.isOp())) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
            return true;
        }
        if (strings.length < 1) {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("weather_invalid_usage"));
            return true;
        }
        if (strings.length < 2) {
            player = (Player) commandSender;
            worlds.add(0, player.getWorld());
        }
        else {
            worldarg = strings[1];

            if (worldarg.equals("all")) {
                worlds = Bukkit.getWorlds();
            }
            else {
                    worlds.add(0, Bukkit.getWorld(worldarg));
            }
        }

        String arg = strings[0].toLowerCase();

        for (World world : worlds) {
            switch (arg) {
                case "clear":
                case "sun":
                    if (world.hasStorm()) {
                        world.setThundering(false);
                        world.setWeatherDuration(5);
                    }
                    break;
                case "storm":
                    if (!world.hasStorm()) {
                        world.setWeatherDuration(5);
                    }
                    world.setThundering(true);
                    break;
                case "rain":
                    if (!world.hasStorm()) {
                        world.setWeatherDuration(5);
                    }
                    world.setThundering(false);
                    break;
                default:
                    commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("weather_invalid_usage"));
                    return true;
            }
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("weather_set_weather")
                    .replace("%var1%", arg)
                    .replace("%var2%", world.getName())
            );
        }
        return true;
    }
}
