package com.johnymuffin.fundamentals.essbridge;

import com.earth2me.essentials.User;
import com.johnymuffin.beta.fundamentals.api.FundamentalsAPI;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class FSBPlayerListener implements Listener {
    private FundamentalsESSBridge plugin;

    public FSBPlayerListener(FundamentalsESSBridge plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (!event.getPlayer().isOnline()) {
                return;
            }
            User user = plugin.getEssentials().getUser(event.getPlayer()); //Essentials Player
            FundamentalsPlayer fundamentalsPlayer = plugin.getFundamentals().getPlayerMap().getPlayer(event.getPlayer()); //Fundamental Player

            //Home Import Start
            List<String> homes = user.getHomes();
            int importCount = 0;
            for (String key : homes) {
                try {
                    Location homeLocation = user.getHome(key);
                    if (!fundamentalsPlayer.doesHomeExist(key)) {
                        fundamentalsPlayer.setPlayerHome(key, homeLocation);
                    }
                    importCount = importCount + 1;
                } catch (Exception e) {

                }
            }
            event.getPlayer().sendMessage("Imported " + importCount + "/" + homes.size() + ". Homes in an invalid would will not be imported. If this import failed please contact staff!");
            //Home Import End

            //Balance Import Start
            FundamentalsAPI.getEconomy().setBalance(event.getPlayer().getUniqueId(), user.getMoney());
            //Balance Import End

        }, 5L);
    }

}
