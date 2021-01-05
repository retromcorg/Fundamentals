package com.johnymuffin.beta.fundamentals.listener;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class FundamentalsEntityListener implements Listener {
    private Fundamentals plugin;

    public FundamentalsEntityListener(Fundamentals plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        if (event.getEntity() instanceof Player && plugin.getPlayerMap().getPlayer((Player) event.getEntity()).getFileGodModeStatus()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && plugin.getPlayerMap().getPlayer((Player) event.getEntity()).getFileGodModeStatus()) {
            final Player player = (Player) event.getEntity();
            player.setFireTicks(0);
            player.setRemainingAir(player.getMaximumAir());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && plugin.getPlayerMap().getPlayer((Player) event.getEntity()).getFileGodModeStatus()) {
            event.setCancelled(true);
        }
    }


}
