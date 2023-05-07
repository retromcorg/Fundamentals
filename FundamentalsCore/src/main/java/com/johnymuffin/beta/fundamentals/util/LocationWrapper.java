package com.johnymuffin.beta.fundamentals.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationWrapper {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private String world;

    public LocationWrapper(double x, double y, double z, float yaw, float pitch, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.world = world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public String getWorld() {
        return world;
    }

    public Location getLocation() throws Exception {
        World world = Bukkit.getServer().getWorld(getWorld());
        return new Location(world, getX(), getY(), getZ(), getYaw(), getPitch());
    }
}