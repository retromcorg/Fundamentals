package com.johnymuffin.beta.fundamentals.hooks.permissions;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.jperms.beta.JohnyPerms;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.UUID;
import java.util.logging.Level;

public class JPermsHook implements PermissionsHook, Listener {
    private Fundamentals fundamentals;
    private boolean enabled;
    private JohnyPerms plugin;

    public JPermsHook(Fundamentals fundamentals) {
        this.fundamentals = fundamentals;
        enabled = false;
        //Check if plugin is installed
        for (Plugin plugin1 : Bukkit.getPluginManager().getPlugins()) {
            if (plugin1.isEnabled() && plugin1 instanceof JohnyPerms) {
                fundamentals.debugLogger(Level.INFO, plugin1.getDescription().getName() + " (" + plugin1.getDescription().getVersion() + ") Has been hooked successfully.", 1);
                enabled = true;
                this.plugin = (JohnyPerms) plugin1;
                break;
            }
        }
    }

    @EventHandler
    public void onPluginLoadEvent(PluginEnableEvent event) {
        if (event.getPlugin() instanceof JohnyPerms) {
            fundamentals.debugLogger(Level.INFO, event.getPlugin().getDescription().getName() + " (" + event.getPlugin().getDescription().getVersion() + ") Has been hooked successfully.", 1);
            enabled = true;
            plugin = (JohnyPerms) event.getPlugin();
        }
    }

    @EventHandler
    public void onPluginDisableEvent(PluginDisableEvent event) {
        if (event.getPlugin() instanceof JohnyPerms) {
            fundamentals.debugLogger(Level.INFO, event.getPlugin().getDescription().getName() + " (" + event.getPlugin().getDescription().getVersion() + ") Has been unhooked as the plugin has been disabled.", 1);
            enabled = false;
            plugin = null;
        }
    }


    public Plugin getPermissionsPlugin() {
        return this.plugin;
    }

    public boolean doesUserHavePermission(UUID uuid, String permission) {
        return JohnyPerms.getJPermsAPI().getUser(uuid).hasPermission(permission);
    }

    public boolean doesUserHavePermission(String username, String permission) {
        UUID uuid = plugin.getJpuuidCache().getUUIDFromUsername(username);
        if(uuid == null) {
            return false;
        }
        return doesUserHavePermission(uuid, permission);
    }

    public String[] getUserGroups(String username) {
        UUID uuid = plugin.getJpuuidCache().getUUIDFromUsername(username);
        if(uuid == null) {
            return null;
        }
        return getUserGroups(uuid);
    }

    public String[] getUserGroups(UUID uuid) {
        String[] groups = new String[1];
        groups[0] = JohnyPerms.getJPermsAPI().getUser(uuid).getGroup().getName();
        return groups;
    }

    public String getMainUserGroup(String username) {
        String[] groups = getUserGroups(username);
        if(groups == null) {
            return null;
        }
        return groups[0];
    }

    public String getMainUserGroup(UUID uuid) {
        String[] groups = getUserGroups(uuid);
        if(groups == null) {
            return null;
        }
        return groups[0];
    }

    public String[] getUserPrefixes(String username) {
        UUID uuid = plugin.getJpuuidCache().getUUIDFromUsername(username);
        if(uuid == null) {
            return null;
        }
        return getUserPrefixes(uuid);
    }

    public String[] getUserPrefixes(UUID uuid) {
        String mainPrefix = getMainUserPrefix(uuid);
        if(mainPrefix == null) {
            return null;
        }
        String[] prefixes = new String[1];
        prefixes[0] = mainPrefix;
        return prefixes;
    }

    public String getMainUserPrefix(String username) {
        UUID uuid = plugin.getJpuuidCache().getUUIDFromUsername(username);
        if(uuid == null) {
            return null;
        }
        return getMainUserPrefix(uuid);
    }

    public String getMainUserPrefix(UUID uuid) {
        return JohnyPerms.getJPermsAPI().getUser(uuid).getGroup().getPrefix();
    }

    public boolean isHookEnabled() {
        return enabled;
    }

    public String getHookName() {
        return "JPerms";
    }
}
