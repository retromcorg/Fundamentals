package com.johnymuffin.beta.fundamentals.playerdata;

public class Util {
    public static String sanitizeFileName(String name)
    {
        return name.toLowerCase().replaceAll("[^a-z0-9]", "_");
    }
}
