package com.johnymuffin.fundamentals.importer;

import com.johnymuffin.beta.fundamentals.Fundamentals;

public interface PluginDataManager {

    public PlayerData getPlayerData(String username);

    public boolean doesPlayerExist(String username);

    public Fundamentals getFundamentals();

}
