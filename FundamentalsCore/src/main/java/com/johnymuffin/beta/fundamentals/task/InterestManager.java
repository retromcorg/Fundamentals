package com.johnymuffin.beta.fundamentals.task;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.banks.FundamentalsBank;
import com.johnymuffin.beta.fundamentals.events.FEBalanceInterestEvent;
import com.johnymuffin.beta.fundamentals.util.Utils;
import org.bukkit.ChatColor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.logging.Level;

import static com.johnymuffin.beta.fundamentals.util.JsonReader.readAll;
import static com.johnymuffin.beta.fundamentals.util.Utils.round;

public class InterestManager {
    private Fundamentals fundamentals;

    public InterestManager(Fundamentals fundamentals) {
        this.fundamentals = fundamentals;
    }

    public void applyInterest() {
        UUID[] uuids = this.fundamentals.getPlayerMap().getKnownPlayers().toArray(new UUID[0]);

        String currentMonth = Utils.getCurrentMonth();
        double interestPayed = 0;
        int playersPaid = 0;
        double currentInterestRate = getCurrentInterestRate();

        int playersChecked = 0;

        for (UUID uuid : uuids) {
            //Print everytime 1000
            if (playersChecked % 1000 == 0) {
                this.fundamentals.debugLogger(Level.INFO, "Interest has been applied to " + playersChecked + "/" + uuids.length + " players.", 1);
            }
            playersChecked++;

            //Check player has played in the last 30 days
            long lastPlayedUnix = this.fundamentals.getPlayerMap().getPlayer(uuid).getLastSeen();

            //If player has not played in the last 30 days, skip them
            if (lastPlayedUnix < (System.currentTimeMillis() / 1000L) - 2592000L) {
                continue;
            }

            double originalBalance = fetchBalance(uuid, true);

            if (originalBalance == 0) {
                continue;
            }

            double interestPayment = this.calculateInterestPayment(currentInterestRate, originalBalance);

            FEBalanceInterestEvent FEBalanceInterestEvent = new FEBalanceInterestEvent(this.fundamentals, this.fundamentals.getPlayerMap().getPlayer(uuid), interestPayment, currentInterestRate);
            this.fundamentals.getServer().getPluginManager().callEvent(FEBalanceInterestEvent);

            if (FEBalanceInterestEvent.isCancelled()) {
                continue;
            }

            playersPaid++;

            double newInterestPayment = FEBalanceInterestEvent.getInterestGained();
            interestPayed += newInterestPayment;

            this.fundamentals.getPlayerMap().getPlayer(uuid).setBalance(originalBalance + newInterestPayment);

            String notification = ChatColor.GRAY + "You have been paid " + ChatColor.GREEN + "$" + round(newInterestPayment, 2) + ChatColor.GRAY + " in interest for " + ChatColor.GREEN + currentMonth + ChatColor.GRAY + " as you where active in the last 30 days.";
            this.fundamentals.getPlayerMap().getPlayer(uuid).addNotification(notification);
        }

        this.fundamentals.debugLogger(Level.INFO, "Interest has been applied to " + playersPaid + " players for " + currentMonth + ". Total interest payed: " + interestPayed, 1);
    }

    public Double fetchLiveInterestRate(String url, String apiKey, String country) throws IOException {
        //Fetch JSON from API with X-Api-Key header
        URL apiURL = new URL(url + country);
        HttpURLConnection connection = (HttpURLConnection) apiURL.openConnection();
        connection.setReadTimeout(5000);
        connection.setConnectTimeout(5000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("X-Api-Key", apiKey);
        connection.connect();

        int responseCode = connection.getResponseCode();

        if (responseCode != 200) {
            this.fundamentals.debugLogger(Level.WARNING, "Failed to fetch live interest rate from API. The following response code was returned: " + responseCode, 1);
            return null;
        }

        JSONObject response = null;

        // Read response
        InputStream is = connection.getInputStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONParser parser = new JSONParser();
            response = (JSONObject) parser.parse(jsonText);
        } catch (Exception exception) {
            this.fundamentals.debugLogger(Level.WARNING, "An exception occurred while reading the response from the API.", 1);
            exception.printStackTrace();
        } finally {
            is.close();
        }

        //If response is null, return
        if (response == null) {
            this.fundamentals.debugLogger(Level.WARNING, "Failed to fetch live interest rate from API. The response was null.", 1);
            return null;
        }

        JSONArray centralBanks = (JSONArray) response.get("central_bank_rates");

        //If centralBanks is null, return
        if (centralBanks == null) {
            this.fundamentals.debugLogger(Level.WARNING, "Failed to fetch live interest rate from API. The centralBanks array was null.", 1);
            return null;
        }

        JSONObject centralBank = (JSONObject) centralBanks.get(0);

        //If centralBank is null, return
        if (centralBank == null) {
            this.fundamentals.debugLogger(Level.WARNING, "Failed to fetch live interest rate from API. The centralBank object was null.", 1);
            return null;
        }

        String centralBankName = String.valueOf(centralBank.get("central_bank"));
        String countryName = String.valueOf(centralBank.get("country"));
        double interestRate = Double.parseDouble(String.valueOf(centralBank.get("rate_pct")));
        String lastUpdated = String.valueOf(centralBank.get("last_updated"));

        this.fundamentals.debugLogger(Level.INFO, "Successfully fetched live interest rate from API. Central bank: " + centralBankName + ", Country: " + countryName + ", Interest rate: " + interestRate + "%, Last updated: " + lastUpdated, 2);
        return interestRate;
    }

    public double getCurrentInterestRate() {
        return this.fundamentals.getFundamentalConfig().getConfigDouble("settings.interest.rate.value");
    }

    public void setInterestRate(double interestRate) {
        this.fundamentals.getFundamentalConfig().setProperty("settings.interest.rate.value", interestRate);
        this.fundamentals.getFundamentalConfig().save();
    }

    private double fetchBalance(UUID uuid, boolean includeBanks) {
        double balance = 0;

        //Player balance
        balance = balance + fundamentals.getPlayerMap().getPlayer(uuid).getBalance();

        if (includeBanks) {
            //Player bank balance
            for (FundamentalsBank bank : fundamentals.getBankManager().getBanks()) {
                if (bank.getBankOwner().equals(uuid)) {
                    balance = balance + bank.getBalance();
                }
            }
        }

        return balance;
    }

    //Calculate interest payment EG: 3.5% of 1000 = 35
    private static double calculateInterestPayment(double interestRate, double balance) {
        return (interestRate / 100) * balance;
    }

}
