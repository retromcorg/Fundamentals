package com.johnymuffin.beta.fundamentals.hooks.permissions;

import org.bukkit.plugin.Plugin;

import java.util.UUID;

public interface PermissionsHook {

    public Plugin getPermissionsPlugin();

    public boolean doesUserHavePermission(UUID uuid, String permission);

    @Deprecated
    public boolean doesUserHavePermission(String username, String permission);

    @Deprecated
    public String[] getUserGroups(String username);

    public String[] getUserGroups(UUID uuid);

    @Deprecated
    public String getMainUserGroup(String username);

    public String getMainUserGroup(UUID uuid);

    @Deprecated
    public String[] getUserPrefixes(String username);

    public String[] getUserPrefixes(UUID uuid);

    @Deprecated
    public String getMainUserPrefix(String username);

    public String getMainUserPrefix(UUID uuid);

    public boolean isHookEnabled();

    public String getHookName();

}
