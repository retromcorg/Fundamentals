package com.johnymuffin.beta.fundamentals.listener;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

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
                return;
            }
        }
        if(event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)){
            EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
            if(damageEvent.getDamager() != null && damageEvent.getDamager() instanceof Player){
                FundamentalsPlayer fPlayer = plugin.getPlayerMap().getPlayer((Player) damageEvent.getDamager());
                if(fPlayer.isAFK()){
                    event.setCancelled(true);
                }
            }
        } else if(event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)){
            EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
            LivingEntity shooter = ((Projectile) damageEvent.getDamager()).getShooter();
            if(shooter instanceof Player){
                FundamentalsPlayer fPlayer = plugin.getPlayerMap().getPlayer((Player) shooter);
                if(fPlayer.isAFK()){
                    event.setCancelled(true);
                }
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
            FundamentalsPlayer fPlayer = plugin.getPlayerMap().getPlayer((Player) event.getTarget());
            if(fPlayer.isVanished()){
                if(plugin.getFundamentalConfig().getConfigBoolean("settings.vanish-hidden-from-mobs")) {
                    event.setCancelled(true);
                }
            } else if(fPlayer.isAFK()){
                event.setCancelled(true);
            }
        }
    }


}
