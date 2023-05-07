package com.johnymuffin.fundamentals.importer;

import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import org.json.simple.JSONArray;

public class Utils {

    public static void addImportEntry(String type, FundamentalsPlayer player) {
        JSONArray data;
        if (player.getInformation("importer") != null) {
            data = (JSONArray) player.getInformation("importer");
        } else {
            data = new JSONArray();
        }
        if (!data.contains(type)) {
            data.add(type);
        }
        player.saveInformation("importer", data);
    }

    public static boolean doesImportEntryExist(String type, FundamentalsPlayer player) {
        if (player.getInformation("importer") != null) {
            return ((JSONArray) player.getInformation("importer")).contains(type);
        }
        return false;
    }
}
