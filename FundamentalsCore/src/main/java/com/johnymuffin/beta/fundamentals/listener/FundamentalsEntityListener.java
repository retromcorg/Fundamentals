package com.johnymuffin.beta.fundamentals.listener;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;

public class FundamentalsEntityListener implements Listener {
    private Fundamentals plugin;

    public FundamentalsEntityListener(Fundamentals plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        if (event.getEntity() instanceof Player) {
            FundamentalsPlayer fPlayer = plugin.getPlayerMap().getPlayer((Player) event.getEntity());
            if(fPlayer.isAFK() || fPlayer.getFileGodModeStatus()){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            FundamentalsPlayer fPlayer = plugin.getPlayerMap().getPlayer((Player) event.getEntity());
            if(fPlayer.isAFK() || fPlayer.getFileGodModeStatus()){
                final Player player = (Player) event.getEntity();
                player.setFireTicks(0);
                player.setRemainingAir(player.getMaximumAir());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            FundamentalsPlayer fPlayer = plugin.getPlayerMap().getPlayer((Player) event.getEntity());
            if(fPlayer.isAFK() || fPlayer.getFileGodModeStatus()){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            if (plugin.getPlayerMap().getPlayer((Player) event.getTarget()).isVanished()) {
                if(plugin.getFundamentalConfig().getConfigBoolean("settings.vanish-hidden-from-mobs")) {
                    event.setCancelled(true);
                }
            }
        }
    }


}
