package com.johnymuffin.beta.fundamentals.hooks.permissions;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class SuperPermsHook implements PermissionsHook{

    public SuperPermsHook(Fundamentals plugin) {
    }

    public Plugin getPermissionsPlugin() {
        return null;
    }

    public boolean doesUserHavePermission(UUID uuid, String permission) {
        return false;
    }

    public boolean doesUserHavePermission(String username, String permission) {
        return false;
    }

    public String[] getUserGroups(String username) {
        return new String[0];
    }

    public String[] getUserGroups(UUID uuid) {
        return new String[0];
    }

    public String getMainUserGroup(String username) {
        return null;
    }

    public String getMainUserGroup(UUID uuid) {
        return null;
    }

    public String[] getUserPrefixes(String username) {
        return new String[0];
    }

    public String[] getUserPrefixes(UUID uuid) {
        return new String[0];
    }

    public String getMainUserPrefix(String username) {
        return null;
    }

    public String getMainUserPrefix(UUID uuid) {
        return null;
    }

    public boolean isHookEnabled() {
        return false;
    }

    public String getHookName() {
        return "SuperPerms";
    }
}
