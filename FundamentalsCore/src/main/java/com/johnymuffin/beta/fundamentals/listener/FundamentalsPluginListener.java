package com.johnymuffin.beta.fundamentals.listener;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.util.FundamentalsDependencies;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class FundamentalsPluginListener implements Listener {
    private Fundamentals plugin;

    public FundamentalsPluginListener(Fundamentals plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        String pluginName = event.getPlugin().getDescription().getName();
        if (pluginName.equals("FundamentalsItemCore")) {
            plugin.getDependenciesMap().put(FundamentalsDependencies.FUNDAMENTALS_ITEM_CORE, true);
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        String pluginName = event.getPlugin().getDescription().getName();
        if (pluginName.equals("FundamentalsItemCore")) {
            plugin.getDependenciesMap().put(FundamentalsDependencies.FUNDAMENTALS_ITEM_CORE, false);
        }
    }


}
