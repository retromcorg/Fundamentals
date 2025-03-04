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
            if (fPlayer.isAFK() || fPlayer.getFileGodModeStatus()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            FundamentalsPlayer fPlayer = plugin.getPlayerMap().getPlayer(player);
            if (fPlayer.isAFK() || fPlayer.getFileGodModeStatus()) {
                player.setFireTicks(0);
                player.setRemainingAir(player.getMaximumAir());
                event.setCancelled(true);
                return;
            }
        }

        if (!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent dmgEvent = (EntityDamageByEntityEvent) event;

        switch (event.getCause()) {
            case ENTITY_ATTACK: {
                if (dmgEvent.getDamager() == null || !(dmgEvent.getDamager() instanceof Player)) return;
                FundamentalsPlayer fPlayer = plugin.getPlayerMap().getPlayer((Player) dmgEvent.getDamager());
                if (fPlayer.isAFK()) event.setCancelled(true);
                break;
            }
            case PROJECTILE: {
                LivingEntity shooter = ((Projectile) dmgEvent.getDamager()).getShooter();
                if (!(shooter instanceof Player)) return;
                FundamentalsPlayer fPlayer = plugin.getPlayerMap().getPlayer((Player) shooter);
                if (fPlayer.isAFK()) event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            FundamentalsPlayer fPlayer = plugin.getPlayerMap().getPlayer((Player) event.getEntity());
            if (fPlayer.isAFK() || fPlayer.getFileGodModeStatus()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            FundamentalsPlayer fPlayer = plugin.getPlayerMap().getPlayer((Player) event.getTarget());
            if (fPlayer.isVanished()) {
                if (plugin.getFundamentalConfig().getConfigBoolean("settings.vanish-hidden-from-mobs")) {
                    event.setCancelled(true);
                }
            } else if (fPlayer.isAFK()) {
                event.setCancelled(true);
            }
        }
    }


}
