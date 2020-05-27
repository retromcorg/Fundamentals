package com.johnymuffin.beta.fundamentals.api;

import com.johnymuffin.beta.fundamentals.Fundamentals;

public class FundamentalsAPI {
    private static Fundamentals fundamentals;


    private FundamentalsAPI() {
    }

    public static EconomyAPI getEconomy() {
        return fundamentals.getEconomyAPI();
    }


    public static void setFundamentals(Fundamentals plugin) {
        if (fundamentals == null) {
            fundamentals = plugin;
        } else {
            throw new RuntimeException("Cannot override Fundamentals in Singleton");
        }
    }
}
