package com.johnymuffin.fundamentals.importer;

import com.earth2me.essentials.User;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.simplejson.JSONObject;
import com.johnymuffin.fundamentals.importer.essentials.EssentialsManager;
import com.johnymuffin.jstats.beta.JStats;
import com.johnymuffin.jstats.beta.PPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.net.URLEncoder;
import java.util.logging.Level;

import static com.johnymuffin.beta.fundamentals.util.JsonReader.readJsonFromUrl;

public class FSBPlayerListener implements Listener {
    private FundamentalsESSBridge plugin;
    private EssentialsManager essM;

    public FSBPlayerListener(FundamentalsESSBridge plugin) {
        this.plugin = plugin;
        this.essM = new EssentialsManager(plugin.getFundamentals());
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Monitor)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().isOnline()) {
            return;
        }

        User user = plugin.getEssentials().getUser(event.getPlayer()); //Essentials Player
        FundamentalsPlayer fundamentalsPlayer = plugin.getFundamentals().getPlayerMap().getPlayer(event.getPlayer()); //Fundamental Player


        //Essentials Start
        if (essM.doesPlayerExist(event.getPlayer().getName())) {
//            plugin.getFundamentals().debugLogger(Level.INFO, "Importing Essentials data for " + event.getPlayer(), 2);
            if (!Utils.doesImportEntryExist("essentials-homes", fundamentalsPlayer)) {
                essM.importHomes(event.getPlayer(), fundamentalsPlayer, plugin.getFundamentals());
                Utils.addImportEntry("essentials-homes", fundamentalsPlayer);
            }
            if (!Utils.doesImportEntryExist("essentials-money", fundamentalsPlayer)) {
                essM.importBalance(event.getPlayer(), fundamentalsPlayer, plugin.getFundamentals());
                Utils.addImportEntry("essentials-money", fundamentalsPlayer);
            }
        }
        //Essentials End


        //Statistics Start
        if (!Utils.doesImportEntryExist("jstats-stats", fundamentalsPlayer)) {
            if (Bukkit.getPluginManager().isPluginEnabled("JStats")) {
                //Fetch Data - Async
                final String username = event.getPlayer().getName();
                JStats jStats = (JStats) Bukkit.getPluginManager().getPlugin("JStats");
                //Make sure JStats knows the player
                String serverURL = plugin.getFundamentals().getFundamentalConfig().getConfigString("settings.import.player-stats-url");
                if (serverURL == null) {
                    plugin.getFundamentals().logger(Level.WARNING, "No stats import URL present.");
                    return;
                }
                Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
                    Exception exception = null;
                    JSONObject playerData = null;
                    try {
                        String url = serverURL + URLEncoder.encode(username, "UTF-8");
                        playerData = readJsonFromUrl(url);
                    } catch (Exception e) {
                        exception = e;
                    }
                    //Final instances or something
                    Exception finalException = exception;
                    JSONObject finalPlayerData = playerData;
                    //Handle results Async
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        if (finalException != null) {
                            plugin.getFundamentals().logger(Level.WARNING, "An error occured trying to import stats for " + username + ": " + finalException + ": " + finalException.getMessage());
                            return;
                        }
                        if (finalPlayerData == null) {
                            plugin.getFundamentals().logger(Level.WARNING, "Stats import data for " + username + " is null.");
                            return;
                        }

                        PPlayer jStatsProfile = jStats.getPlayerMap().get(event.getPlayer().getUniqueId());
                        //Make sure stats profile isn't null
                        if (jStatsProfile == null) {
                            plugin.getFundamentals().logger(Level.INFO, "No JStats profile for " + event.getPlayer().getName() + "could be found, skipping import check.");
                            return;
                        }
                        if (Boolean.valueOf(String.valueOf(finalPlayerData.get("found")))) {
                            //Player Found
                            Long firstJoin = Long.valueOf(String.valueOf(finalPlayerData.get("firstJoin")));
                            if (firstJoin != null) jStatsProfile.setFirstJoin(firstJoin);
                            Integer joinCount = Integer.valueOf(String.valueOf(finalPlayerData.get("joinCount")));
                            if (joinCount != null) jStatsProfile.setJoinCount(jStatsProfile.getJoinCount() + joinCount);
                            Integer creatureKills = Integer.valueOf(String.valueOf(finalPlayerData.get("creatureKills")));
                            if (creatureKills != null)
                                jStatsProfile.setCreaturesKilled(jStatsProfile.getCreaturesKilled() + creatureKills);
                            Integer blocksPlaced = Integer.valueOf(String.valueOf(finalPlayerData.get("blocksPlaced")));
                            if (blocksPlaced != null)
                                jStatsProfile.setBlocksPlaced(jStatsProfile.getBlocksPlaced() + blocksPlaced);
                            Integer blocksDestroyed = Integer.valueOf(String.valueOf(finalPlayerData.get("blocksDestroyed")));
                            if (blocksDestroyed != null)
                                jStatsProfile.setBlocksDestroyed(jStatsProfile.getBlocksDestroyed() + blocksDestroyed);
                            Integer metersTraveled = Integer.valueOf(String.valueOf(finalPlayerData.get("metersTraveled")));
                            if (metersTraveled != null)
                                jStatsProfile.setMetersTraveled(jStatsProfile.getMetersTraveled() + metersTraveled);
                            Integer playerDeaths = Integer.valueOf(String.valueOf(finalPlayerData.get("playerDeahts")));
                            if (playerDeaths != null)
                                jStatsProfile.setPlayerDeaths(jStatsProfile.getPlayerDeaths() + playerDeaths);
                            Integer playTime = Integer.valueOf(String.valueOf(finalPlayerData.get("playtime")));
                            if (playTime != null) jStatsProfile.setPlayTime(jStatsProfile.getPlayTime() + playTime);
                            plugin.getFundamentals().logger(Level.INFO, "Imported stats for " + username);
                        }
                        //Add Import Status
                        Utils.addImportEntry("jstats-stats", fundamentalsPlayer);
                    });


                });
            }
        }
        //Statistics End


    }

}
