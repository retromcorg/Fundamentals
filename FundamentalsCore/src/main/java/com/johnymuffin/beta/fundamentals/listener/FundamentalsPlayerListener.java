package com.johnymuffin.beta.fundamentals.listener;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
import com.johnymuffin.beta.fundamentals.events.FEconomyUpdateEvent;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import com.projectposeidon.api.PoseidonUUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import static com.johnymuffin.beta.fundamentals.util.Utils.*;

public class FundamentalsPlayerListener implements Listener {

    private Fundamentals plugin;
    private Set<UUID> afkMovingPlayers = new HashSet<>();

    public FundamentalsPlayerListener(Fundamentals plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = Event.Priority.Lowest)
    public void onPlayerLogin(final PlayerLoginEvent event) {
        //Check if player is actually allowed to join
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return;
        }
        //Store user in PlayerCache
        plugin.getPlayerCache().updatePlayerProfile(event.getPlayer().getName(), plugin.getPermissionsHook().getMainUserPrefix(event.getPlayer().getUniqueId()), event.getPlayer().getUniqueId());
        FundamentalsPlayer fundamentalsPlayer = this.plugin.getPlayerMap().getPlayer(event.getPlayer().getUniqueId());
        fundamentalsPlayer.updateDisplayName(event.getPlayer());

    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Highest)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (event == null) {
            return;
        }
        if (!event.getPlayer().isOnline()) {
            return;
        }

        //Player Data File
        FundamentalsPlayer fPlayer = FundamentalsPlayerMap.getInstance().getPlayer(event.getPlayer());

        boolean fistJoin = !FundamentalsPlayerMap.getInstance().isPlayerKnown(event.getPlayer().getUniqueId());
        fPlayer.setFirstJoin(fistJoin);

        //Store balance in economy cache
        if (fPlayer.getBalance() > 1) {
            plugin.getEconomyCache().saveRecord(event.getPlayer().getUniqueId(), fPlayer.getBalance());
        }

        //Vanish Information
        updateVanishedPlayers();

        //Join message
//        String message = plugin.getFundamentalConfig().getConfigString("settings.joinandleave.join-message");
//        message = message.replace("%player%", event.getPlayer().getName());
//        message = message.replace("%prefix%", getPrefix(fPlayer.getUuid()));
//        event.setJoinMessage(formatColor(message));

        // Hide Join Message if they are fakequit
        if (fPlayer.isFakeQuit()) {
            event.setJoinMessage(null);
            fPlayer.addNotification(plugin.getFundamentalsLanguageConfig().getMessage("fakequit_quit_on_join"));
        }

        //Online Player
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (!event.getPlayer().isOnline()) {
                return;
            }
            fPlayer.updateActivity();
            fPlayer.playerJoinUpdate(event.getPlayer().getName());

            //Nickname Start
            fPlayer.updateDisplayName();
            //Nickname End

            //Send Notifications
            String[] notifications = fPlayer.getNotifications();
            if (notifications != null) {
                for (String notification : notifications) {
                    event.getPlayer().sendMessage(formatColor(notification));
                }
                fPlayer.clearNotifications();
            }


        }, 20L);

        setEssentialsHidden(event.getPlayer(), fPlayer.isFakeQuit());

    }

    public String getPrefix(UUID uuid) {
        String prefix = plugin.getPermissionsHook().getMainUserPrefix(uuid);
        if (prefix == null) {
            prefix = "&f[&4Unknown&f]";
        }
        return prefix;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        FundamentalsPlayer fPlayer = FundamentalsPlayerMap.getInstance().getPlayer(event.getPlayer());

        fPlayer.playerQuitUpdate();
        //Don't print quit message if player is fakequit
        if (fPlayer.isFakeQuit()) {
            event.setQuitMessage(null);
        }

        //Quit message
//        String message = plugin.getFundamentalConfig().getConfigString("settings.joinandleave.leave-message");
//        message = message.replace("%player%", event.getPlayer().getName());
//        message = message.replace("%prefix%", getPrefix(event.getPlayer().getUniqueId()));
//        event.setQuitMessage(formatColor(message));

    }

    @EventHandler(ignoreCancelled = true)
    public void onPLayerKick(PlayerKickEvent event) {
        FundamentalsPlayerMap.getInstance().getPlayer(event.getPlayer()).playerQuitUpdate();

        //Kick message
//        String message = plugin.getFundamentalConfig().getConfigString("settings.joinandleave.kick-message");
//        message = message.replace("%player%", event.getPlayer().getName());
//        message = message.replace("%prefix%", getPrefix(event.getPlayer().getUniqueId()));
//        event.setLeaveMessage(formatColor(message));

    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Low)
    public void onPlayerChat(final PlayerChatEvent event) {
        Player player = event.getPlayer();
        FundamentalsPlayer fundamentalsPlayer = plugin.getPlayerMap().getPlayer(player);


        fundamentalsPlayer.updateActivity(); //Update AFK timer
        fundamentalsPlayer.updateDisplayName(); //Update Nickname

        //Nickname Start
//        String displayName = fundamentalsPlayer.getNickname();
//        if (displayName != null) {
//            if (player.hasPermission("fundamentals.nickname.color") || player.isOp()) {
//                displayName = formatColor(displayName + "&f");
//            }
//            player.setDisplayName("~" + displayName);
//        }
        //Nickname End


    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        UUID uuid = event.getPlayer().getUniqueId();

        //Run nickname color check
        if(command.toLowerCase().startsWith("/nick") || command.toLowerCase().startsWith("/nickname")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    FundamentalsPlayer fundamentalsPlayer = plugin.getPlayerMap().getPlayer(uuid);
                    fundamentalsPlayer.updateDisplayName();

                }
            }, 5L);
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent event) {
        if(!plugin.getPlayerMap().getPlayer(event.getPlayer().getUniqueId()).isAFK()) return;
        if(event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;
        if(!event.getPlayer().hasPermission("fundamentals.afk") && !event.getPlayer().isOp()){
            plugin.getPlayerMap().getPlayer(event.getPlayer().getUniqueId()).updateActivity();
            return;
        }
        Location location = event.getFrom();
        location.setPitch(event.getTo().getPitch());
        location.setYaw(event.getTo().getYaw());
        if(event.getFrom().getY() > event.getTo().getY()){
            location.setY(event.getTo().getY());
        }else{
            location.setY(event.getFrom().getY());
        }
        event.getPlayer().teleport(location);
        if(!afkMovingPlayers.contains(event.getPlayer().getUniqueId())){
            afkMovingPlayers.add(event.getPlayer().getUniqueId());
            event.getPlayer().sendMessage(FundamentalsLanguage.getInstance().getMessage("moving_while_afk"));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                    () -> afkMovingPlayers.remove(event.getPlayer().getUniqueId()), 100);

        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event){
        if(plugin.getPlayerMap().getPlayer(event.getPlayer().getUniqueId()).isAFK()){
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPreLogin(PlayerPreLoginEvent event) {
        UUID uuid = PoseidonUUID.getPlayerGracefulUUID(event.getName());
        //Ensure validity of player data before letting players join
        if (FundamentalsPlayerMap.getInstance().isPlayerKnown(uuid)) {
            try {
                FundamentalsPlayerMap.getInstance().getPlayer(uuid);
            } catch (Exception exception) {
                plugin.logger(Level.WARNING, "Error loading player data for " + uuid + ", disconnecting player. \n" + exception.getMessage());
                FundamentalsPlayerMap.getInstance().removePlayerFromMap(uuid);
                event.cancelPlayerLogin(ChatColor.RED + "Sorry, an error occurred reading your data. Please contact staff!");
            }
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemDrop(PlayerDropItemEvent event) {
        if (plugin.isPlayerInvSee(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(FundamentalsLanguage.getInstance().getMessage("invsee_deny"));
            event.setCancelled(true);
            return;
        }

        if (plugin.getPlayerMap().getPlayer(event.getPlayer()).isVanished()) {
            event.getPlayer().sendMessage(FundamentalsLanguage.getInstance().getMessage("vanish_deny"));
            event.setCancelled(true);
            return;
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemPickup(PlayerPickupItemEvent event) {
        if (plugin.isPlayerInvSee(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (plugin.getPlayerMap().getPlayer(event.getPlayer()).isVanished()) {
            event.setCancelled(true);
            return;
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (plugin.isPlayerInvSee(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(FundamentalsLanguage.getInstance().getMessage("invsee_deny"));
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        //Vanish
        if (plugin.getPlayerMap().getPlayer(event).isVanished()) {
            if (plugin.getFundamentalConfig().getConfigBoolean("settings.vanish.block-interactions")) {
                if (event.getAction() == Action.PHYSICAL && event.hasBlock()) {
                    if (event.getMaterial() == Material.WOOD_PLATE || event.getMaterial() == Material.STONE_PLATE) {
                        event.setCancelled(true);
                    }
                } else if (event.getMaterial() == Material.STONE_BUTTON) {
                    event.getPlayer().sendMessage(FundamentalsLanguage.getInstance().getMessage("vanish_deny"));
                    event.setCancelled(true);
                }
            }
        }


        //Invsee - Block opening chests
        if (plugin.isPlayerInvSee(event.getPlayer().getUniqueId())) {
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }
            Block clickedBlock = event.getClickedBlock();
            Location location = clickedBlock.getLocation();
            CraftWorld craftWorld = (CraftWorld) clickedBlock.getWorld();
            CraftBlock craftBlock = new CraftBlock((CraftChunk) craftWorld.getChunkAt(location), location.getBlockX(), location.getBlockY(), location.getBlockZ());
            craftBlock.setTypeId(craftWorld.getBlockTypeIdAt(location));
            if (craftBlock.getState() instanceof org.bukkit.block.ContainerBlock) {
                event.getPlayer().sendMessage(FundamentalsLanguage.getInstance().getMessage("invsee_deny"));
                event.setCancelled(true);
            }
            return;
        }


    }

    @EventHandler(ignoreCancelled = true)
    public void onEconomyUpdate(FEconomyUpdateEvent economyUpdateEvent) {

    }


}
