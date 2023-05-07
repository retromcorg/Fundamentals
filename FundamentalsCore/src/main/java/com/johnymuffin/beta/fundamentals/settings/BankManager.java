package com.johnymuffin.beta.fundamentals.settings;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.banks.FundamentalsBank;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class BankManager {
    //File
    private boolean modified = false; //Has the file been modified, determines if the file should be saved on auto save
    private File playerDataFile; //Location of Player Data file
    private JSONObject jsonData; //Json Player Data Object
    //Fundamentals
    private Fundamentals plugin;
    //Player

    public BankManager(Fundamentals plugin) {
        //Initialize Variables
        this.plugin = plugin;
        playerDataFile = new File(plugin.getDataFolder() + File.separator + "banks.json");
        //Check for file existence, generate if file doesn't exist
        if (!playerDataFile.exists()) {
            playerDataFile.getParentFile().mkdirs();
            initializeData();
        } else {
            try {
                JSONParser parser = new JSONParser();
                jsonData = (JSONObject) parser.parse(new FileReader(playerDataFile));
            } catch (ParseException e) {
                plugin.logger(Level.WARNING, "Failed to Parse bank accounts as it is most likely corrupt, resetting data.");
                initializeData();
                throw new RuntimeException("Parse Exception: " + e + " - " + e.getMessage());
//                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public FundamentalsBank[] getBanks() {
        JSONArray banks = (JSONArray) jsonData.get("banks");
        FundamentalsBank[] fundamentalsBanks = new FundamentalsBank[banks.size()];
        int i = 0;
        for (Object rawBankInfo : banks) {
            fundamentalsBanks[i] = new FundamentalsBank((JSONObject) rawBankInfo);
            i = i + 1;
        }
        return fundamentalsBanks;
    }

    public HashMap<String, AccessType> getAccessibleBanks(UUID uuid) {
        HashMap<String, AccessType> accessibleBanks = new HashMap<>();
        //Iterate thru banks to generate list
        for (FundamentalsBank bank : plugin.getBanks().values()) {
            if (bank.getBankOwner().equals(uuid)) {
                accessibleBanks.put(bank.getBankName(), AccessType.OWNER);
                continue;
            }
            if (Arrays.asList(bank.getAccessList()).contains(uuid)) {
                accessibleBanks.put(bank.getBankName(), AccessType.MEMBER);
                continue;
            }
        }
        return accessibleBanks;
    }

    public void saveBanks(FundamentalsBank[] banks) {
        JSONArray banksArray = new JSONArray();
        for (FundamentalsBank bank : banks) {
            banksArray.add(bank.getJSONObject());
        }
        jsonData.put("banks", banksArray);
        modified = true;

    }


    private void initializeData() {
        jsonData = new JSONObject();
        jsonData.put("version", 1);
        jsonData.put("banks", new JSONArray());
        modified = true;
    }

    public void saveIfModified() {
        if (modified) {
            saveToJSON();
        }
    }

    private void saveToJSON() {
        try (FileWriter file = new FileWriter(playerDataFile)) {
            file.write(jsonData.toJSONString());
            file.flush();
            modified = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public enum AccessType {
        OWNER,
        MEMBER,
    }

}