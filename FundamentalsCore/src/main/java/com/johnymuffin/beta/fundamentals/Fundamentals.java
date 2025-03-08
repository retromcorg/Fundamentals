package com.johnymuffin.beta.fundamentals;

import com.johnymuffin.beta.fundamentals.api.EconomyAPI;
import com.johnymuffin.beta.fundamentals.api.FundamentalsAPI;
import com.johnymuffin.beta.fundamentals.banks.FundamentalsBank;
import com.johnymuffin.beta.fundamentals.commands.*;
import com.johnymuffin.beta.fundamentals.hooks.permissions.JPermsHook;
import com.johnymuffin.beta.fundamentals.hooks.permissions.PermissionsHook;
import com.johnymuffin.beta.fundamentals.hooks.permissions.SuperPermsHook;
import com.johnymuffin.beta.fundamentals.listener.FundamentalsEntityListener;
import com.johnymuffin.beta.fundamentals.listener.FundamentalsPlayerListener;
import com.johnymuffin.beta.fundamentals.settings.*;
import com.johnymuffin.beta.fundamentals.task.InterestManager;
import com.johnymuffin.beta.fundamentals.util.FundamentalsDependencies;
import com.johnymuffin.beta.fundamentals.util.Utils;
//import com.johnymuffin.fundamentals.worldmanager.FundamentalsWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Fundamentals extends JavaPlugin {
    //Basic Plugin Info
    private static Fundamentals plugin;
    private Logger log;
    private String pluginName;
    private PluginDescriptionFile pdf;
    private int debugLevel = 3;
    //Hook Status
    //TODO: These should be migrated to the dependenciesMap system eventually.
    private boolean essentialsHook = false;
    private boolean discordCoreHook = false;

    private boolean worldManagerMultiWorldEconomy = false;
    private Long lastAutoSaveTime = System.currentTimeMillis() / 1000l;
    //API
    private EconomyAPI economyAPI;
    //Storage
    private PlayerCache playerCache;
    private EconomyCache economyCache;
    private HashMap<String, FundamentalsBank> banks = new HashMap<>();
    //Invsee Comamnd
    private HashMap<UUID, ItemStack[]> invSee = new HashMap<UUID, ItemStack[]>();
    private BankManager bankManager;
    //Dependency store
    private HashMap<FundamentalsDependencies, Boolean> dependenciesMap = new HashMap<>();

    private PermissionsHook[] permissionsHooks;
    private PermissionsHook permissionsHook;

//    private FundamentalsWorldManager fundamentalsWorldManager;

    private InterestManager interestManager;

    private double economySize = 0;

    private long lastEconomySizeUpdate = 0;

    @Override
    public void onEnable() {
        plugin = this;
        log = this.getServer().getLogger();
        pdf = this.getDescription();
        pluginName = pdf.getName();
        log.info("[" + pluginName + "] Is Loading, Version: " + pdf.getVersion());

        long startTimeUnix = System.currentTimeMillis() / 1000L;

        //Load Core Start
        this.logger(Level.INFO, "Initializing player data map");
        FundamentalsPlayerMap.getInstance(plugin);
        for (Player p : Bukkit.getOnlinePlayers()) {
            logger(Level.INFO, "Regenerating data for a player already online: " + p.getName());
            try {
                FundamentalsPlayerMap.getInstance(plugin).getPlayer(p);
            } catch (Exception e) {
                p.kickPlayer("");
            }
        }
        this.logger(Level.INFO, "Initializing settings map");
        FundamentalsConfig.getInstance(plugin);
        debugLevel = Integer.valueOf(String.valueOf(FundamentalsConfig.getInstance(plugin).getConfigOption("settings.debug-level")));
        this.logger(Level.INFO, "Setting console debug to " + debugLevel);

        this.logger(Level.INFO, "Initializing language map");
        FundamentalsLanguage.getInstance(plugin);

        this.logger(Level.INFO, "Initializing API");
        FundamentalsAPI.setFundamentals(plugin);
        economyAPI = new EconomyAPI(plugin);

        //Permissions Hook
        initializeHooks();

        this.logger(Level.INFO, "Initializing Player Cache");
        playerCache = new PlayerCache(plugin);

        this.logger(Level.INFO, "Initializing Economy Cache");
        economyCache = new EconomyCache(plugin);

        this.logger(Level.INFO, "Loading Fundamentals Banks");
        bankManager = new BankManager(plugin);
        int i = 0;
        for (FundamentalsBank bank : bankManager.getBanks()) {
            this.banks.put(bank.getBankName().toLowerCase(), bank);
            i = i + 1;
        }
        this.debugLogger(Level.INFO, "Loaded " + i + " banks into memory.", 1);


        //Listeners
        final FundamentalsPlayerListener fundamentalsPlayerListener = new FundamentalsPlayerListener(plugin);
        Bukkit.getPluginManager().registerEvents(fundamentalsPlayerListener, plugin);
        final FundamentalsEntityListener fundamentalsEntityListener = new FundamentalsEntityListener(plugin);
        Bukkit.getPluginManager().registerEvents(fundamentalsEntityListener, plugin);

        //Hooks
        if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
            essentialsHook = true;
            debugLogger(Level.INFO, "Essentials has been detected.", 1);
        }
        if (Bukkit.getPluginManager().isPluginEnabled("DiscordCore")) {
            discordCoreHook = true;
            debugLogger(Level.INFO, "Discord Core has been detected.", 1);
        }

        //Prefix Import Check
//        if (this.getFundamentalConfig().getConfigBoolean("settings.import-prefixes-next-start")) {
//            this.debugLogger(Level.INFO, "Importing Player prefixes into their respective files", 2);
//            importPrefixes();
//            this.getFundamentalConfig().setProperty("settings.import-prefixes-next-start", false);
//            this.getFundamentalConfig().save();
//        }


        //Commands
        Bukkit.getPluginCommand("home").setExecutor(new CommandHome(plugin));
        Bukkit.getPluginCommand("homes").setExecutor(new CommandHomes(plugin));
        Bukkit.getPluginCommand("sethome").setExecutor(new CommandSetHome());
        Bukkit.getPluginCommand("delhome").setExecutor(new CommandDelhome());
        Bukkit.getPluginCommand("homesearch").setExecutor(new CommandHomeSearch(plugin));
        
        Bukkit.getPluginCommand("hat").setExecutor(new CommandHat());
        Bukkit.getPluginCommand("afk").setExecutor(new CommandAFK());
        Bukkit.getPluginCommand("balance").setExecutor(new CommandBalance());
        Bukkit.getPluginCommand("economy").setExecutor(new CommandEconomy());
//        Bukkit.getPluginCommand("heal").setExecutor(new CommandHeal());
//        Bukkit.getPluginCommand("pay").setExecutor(new CommandPay());
//        Bukkit.getPluginCommand("god").setExecutor(new CommandGod());
//        Bukkit.getPluginCommand("nickname").setExecutor(new CommandNickname());
//        Bukkit.getPluginCommand("invsee").setExecutor(new CommandInvsee(plugin));
//        Bukkit.getPluginCommand("clearinventory").setExecutor(new CommandClearInventory(plugin));
//        Bukkit.getPluginCommand("time").setExecutor(new CommandTime(plugin));
//        Bukkit.getPluginCommand("ptime").setExecutor(new CommandPTime(plugin));
        Bukkit.getPluginCommand("vanish").setExecutor(new CommandVanish(plugin));
        Bukkit.getPluginCommand("weather").setExecutor(new CommandWeather());
        Bukkit.getPluginCommand("bank").setExecutor(new CommandBank(plugin));
        Bukkit.getPluginCommand("balancetop").setExecutor(new CommandBalanceTop(plugin));
        Bukkit.getPluginCommand("fakequit").setExecutor(new CommandFakeQuit(plugin));
        //Timer
        //This async task is so TPS doesn't affect the timing of saving. This probably actually isn't needed TBH.
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, () -> {
            //Change back to main thread for all logic.
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                int autoSavePeriod = FundamentalsConfig.getInstance(plugin).getConfigInteger("settings.auto-save-time");
                if (lastAutoSaveTime + autoSavePeriod < getUnix()) {
                    lastAutoSaveTime = getUnix();
                    debugLogger(Level.INFO, "Automatically saving data.", 2);
                    saveData();
                }
                FundamentalsPlayerMap.getInstance(plugin).runTimerTasks();
            });
        }, 20, 20 * 10);

        long endTimeUnix = System.currentTimeMillis() / 1000L;

        //bstats metrics
        int pluginId = 20595; //Fundamentals Plugin ID
        Metrics metrics = new Metrics(plugin, pluginId);

        log.info("[" + pluginName + "] Has Loaded, loading took " + (int) (endTimeUnix - startTimeUnix) + " seconds.");

        //Multi-balance per world support enabled.
        //TODO: It is possible that the economy should be an additional plugin. I don't link the idea this plugin can depend on FundamentalsPerWorldManager which depends on FundamentalsCore
//        if (getFundamentalConfig().getConfigBoolean("settings.per-world-economy.enabled")) {
//            log.info("[" + pluginName + "] Will attempt to initialize per-world support when the server has finished loading.");
//            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//                @Override
//                public void run() {
//                    if (!Bukkit.getPluginManager().isPluginEnabled("FundamentalsWorldManager")){
//                        debugLogger(Level.WARNING, "The FundamentalsWorldManager plugin couldn't be located. Disabling per-world economy support.", 0);
//                    } else {
//                        worldManagerMultiWorldEconomy = true;
//                        fundamentalsWorldManager = (FundamentalsWorldManager) Bukkit.getPluginManager().getPlugin("FundamentalsWorldManager");
//                        debugLogger(Level.INFO, "FundamentalsWorldManager has been successfully loaded and hooked.", 0);
//                    }
//                }
//            });
//        }

        //Interest Manager
        interestManager = new InterestManager(plugin);
        initializeInterest();
    }

    private void initializeInterest() {
        boolean interestEnabled = this.getFundamentalConfig().getConfigBoolean("settings.interest.enabled");

        if (!interestEnabled) {
            return;
        }

        boolean useLiveInterest = this.getFundamentalConfig().getConfigBoolean("settings.interest.rate.use-live-interest-rate.enabled");
        if (useLiveInterest) {
            long lastInterestFetch = this.getFundamentalConfig().getConfigLong("settings.interest.rate.use-live-interest-rate.last-updated.value");

            //If the last time we fetched the interest rate was more than 24 hours ago, fetch it again.
            if (lastInterestFetch + 86400 < (System.currentTimeMillis() / 1000L)) {
                fetchLiveInterest();
            } else {
                debugLogger(Level.INFO, "Live interest rate is enabled, but the interest rate was fetched less than 24 hours ago. Using cached interest rate.", 2);
            }
        }

        //Check if interest has been payed this month.
        String lastPayedMonth = this.getFundamentalConfig().getConfigString("settings.interest.rate.last-month-applied.value");
        String currentMonth = Utils.getCurrentMonth();

        if (!lastPayedMonth.equalsIgnoreCase(currentMonth)) {
            //Interest hasn't been payed this month, so pay it.
            debugLogger(Level.INFO, "Interest hasn't been payed this month. Paying interest. Please note this is a blocking operation and may hang temporarily.", 1);
            interestManager.applyInterest();
            this.getFundamentalConfig().setProperty("settings.interest.rate.last-month-applied.value", currentMonth);
            this.getFundamentalConfig().save();
        } else {
            debugLogger(Level.INFO, "Interest has already been payed this month. Skipping interest payment.", 2);
        }
    }

    private void fetchLiveInterest() {
        //Attempt to fetch live interest rates from the internet.
        final String url = "https://api.api-ninjas.com/v1/interestrate?name=";
        final String apiKey = this.getFundamentalConfig().getConfigString("settings.interest.rate.use-live-interest-rate.api-key.value");

        if (apiKey.equalsIgnoreCase("API_KEY_HERE")) {
            this.debugLogger(Level.WARNING, "You have not set your API key for the interest rate API. Please set it in the config.yml file.", 1);
            this.debugLogger(Level.WARNING, "Disabling live interest rate.", 1);
            this.getFundamentalConfig().setProperty("settings.interest.rate.use-live-interest-rate.enabled", false);
            this.getFundamentalConfig().save();
            return;
        }

        final String country = this.getFundamentalConfig().getConfigString("settings.interest.rate.use-live-interest-rate.country.value");
        boolean useLiveInterestRate = this.getFundamentalConfig().getConfigBoolean("settings.interest.rate.use-live-interest-rate.enabled");
        if (!useLiveInterestRate) {
            this.debugLogger(Level.INFO, "Live interest rate is disabled, using static interest rate: " + interestManager.getCurrentInterestRate(), 2);
            return;
        } else {
            this.debugLogger(Level.INFO, "Live interest rate is enabled, attempting to fetch live interest rate.", 2);
            Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
                try {
                    Double interestRate = interestManager.fetchLiveInterestRate(url, apiKey, country);

                    if (interestRate == null) {
                        debugLogger(Level.WARNING, "Failed to fetch live interest rate, using static interest rate: " + interestManager.getCurrentInterestRate(), 1);
                        return;
                    }

                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        interestManager.setInterestRate(interestRate);
                        this.debugLogger(Level.INFO, "Live interest rate has been fetched and set to: " + interestManager.getCurrentInterestRate() + "% based on " + country + ".", 1);
                        getFundamentalConfig().setProperty("settings.interest.rate.use-live-interest-rate.last-updated.value", (System.currentTimeMillis() / 1000L));
                        getFundamentalConfig().save();
                    });
                } catch (IOException e) {
                    debugLogger(Level.WARNING, "Failed to fetch live interest rate, using static interest rate: " + interestManager.getCurrentInterestRate(), 1);
                    e.printStackTrace();
                }
            });
        }
    }

    //This method is case-insensitive.
    public UUID getPlayerUUID(String playerName) {
        UUID uuid = playerCache.getUUIDFromUsername(playerName);
        if (uuid == null) {
            uuid = Utils.getUUIDFromUsername(playerName);
        }
        return uuid;
    }

    public void saveData() {
        FundamentalsPlayerMap.getInstance().saveData(); // Save player data files that have been modified.

        // Save Fundamentals Banks
        FundamentalsBank[] banks = new FundamentalsBank[this.banks.size()];
        int i = 0;
        for (String bankName : this.banks.keySet()) {
            banks[i] = this.banks.get(bankName);
            i = i + 1;
        }
        bankManager.saveBanks(banks);
        bankManager.saveIfModified();
    }

    @Override
    public void onDisable() {
        //Force Disable InvSee Start
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (invSee.containsKey(p.getUniqueId())) {
                p.getInventory().setContents(invSee.get(p.getUniqueId()));
                logger(Level.INFO, "Restored the inventory for " + p.getName() + " as they where using InvSee on plugin disable.");
            }
        }
        //Force Disable InvSee End

        //Save Player Data
        saveData();

        // Save caches
        playerCache.saveData();
        economyCache.saveData();
    }

    public void logger(Level level, String message) {
        Bukkit.getLogger().log(level, "[" + pluginName + "] " + message);
    }

    public void debugLogger(Level level, String message, int debug) {
        if (debug <= debugLevel) {
            Bukkit.getLogger().log(level, "[" + pluginName + " Debug] " + message);
        }
    }

    public static Fundamentals getPlugin() {
        return plugin;
    }

    private Long getUnix() {
        return System.currentTimeMillis() / 1000l;
    }

    public EconomyAPI getEconomyAPI() {
        return economyAPI;
    }

    public FundamentalsPlayerMap getPlayerMap() {
        return FundamentalsPlayerMap.getInstance(plugin);
    }

    public FundamentalsConfig getFundamentalConfig() {
        return FundamentalsConfig.getInstance(plugin);
    }

    public FundamentalsLanguage getFundamentalsLanguageConfig() {
        return FundamentalsLanguage.getInstance(plugin);
    }

    //InvSee Start
    public boolean isPlayerInvSee(UUID uuid) {
        return invSee.containsKey(uuid);
    }

    public boolean enableInvSee(UUID uuid, ItemStack[] itemStacks) {
        if (invSee.containsKey(uuid)) {
            return false;
        }
        invSee.put(uuid, itemStacks);
        return true;
    }

    public ItemStack[] disableInvSee(UUID uuid) {
        if (invSee.containsKey(uuid)) {
            ItemStack[] itemStacks = invSee.get(uuid);
            invSee.remove(uuid);
            return itemStacks;
        }
        return null;
    }

    public PlayerCache getPlayerCache() {
        return playerCache;
    }

    public EconomyCache getEconomyCache() {
        return economyCache;
    }

    public HashMap<String, FundamentalsBank> getBanks() {
        return banks;
    }

    public HashMap<FundamentalsDependencies, Boolean> getDependenciesMap() {
        return dependenciesMap;
    }

    public BankManager getBankManager() {
        return bankManager;
    }


    public PermissionsHook getPermissionsHook() {
        if (this.permissionsHook != null) {
            return this.permissionsHook;
        }
        String preferredHook = plugin.getFundamentalConfig().getConfigString("settings.preferred-permissions-hook");
        for (PermissionsHook permissionsHook : permissionsHooks) {
            if (permissionsHook.isHookEnabled() && permissionsHook.getHookName().equalsIgnoreCase(preferredHook)) {
                plugin.debugLogger(Level.INFO, "Preferred hook (" + preferredHook + ") was located and will be used", 2);
                this.permissionsHook = permissionsHook;
                return permissionsHook;
            }
        }
        //Yes, this is technically inefficient but I am lazy and the list will at maximum have 10 entries. I have chosen to reloop as I want to grab the highest priority plugin easily.
        for (PermissionsHook permissionsHook : permissionsHooks) {
            if (permissionsHook.isHookEnabled()) {
                plugin.debugLogger(Level.INFO, "Hook (" + preferredHook + ") was located and will be used", 2);
                this.permissionsHook = permissionsHook;
                return permissionsHook;
            }
        }

        throw new RuntimeException("A Permissions Hook couldn't be found. This shouldn't be possible as the SuperPerms hook should always be available.");
    }


    //InvSee End
    public void initializeHooks() {
        permissionsHooks = new PermissionsHook[2];
        permissionsHooks[0] = new JPermsHook(plugin);
        permissionsHooks[1] = new SuperPermsHook(plugin);


        for (PermissionsHook permissionsHook : permissionsHooks) {
            if (permissionsHook instanceof Listener) {
                Bukkit.getServer().getPluginManager().registerEvents((Listener) permissionsHook, plugin);
            }
        }

    }

    public boolean isWorldManagerMultiWorldEconomy() {
        return worldManagerMultiWorldEconomy;
    }

//    public FundamentalsWorldManager getFundamentalsWorldManager() {
//        return fundamentalsWorldManager;
//    }



    public double getEconomySize() {
        //If the economy size was updated less than 5 minutes ago, return the cached value.
        if (lastEconomySizeUpdate + 300 > (System.currentTimeMillis() / 1000L)) {
            return economySize;
        }

        double economySize = 0;

        //Add all player balances to the economy size.
        for (UUID uuid : getPlayerMap().getKnownPlayers()) {
            economySize = economySize + economyCache.getPlayerBalance(uuid);
        }

        //Add all bank balances to the economy size.
        for (FundamentalsBank bank : banks.values()) {
            economySize = economySize + bank.getBalance();
        }

        this.economySize = economySize;
        this.lastEconomySizeUpdate = System.currentTimeMillis() / 1000L;
        return economySize;
    }



//    public void importPrefixes() {
//        int players = 0;
//        int totalPlayers = plugin.getPlayerMap().getKnownPlayers().size();
//        int skippedPlayers = 0;
//        long nextPrintStatus = (System.currentTimeMillis() / 1000) + 5;
//        for (UUID uuid : plugin.getPlayerMap().getKnownPlayers()) {
//            FundamentalsPlayer player = plugin.getPlayerMap().getPlayer(uuid);
//            String prefix = this.getPermissionsHook().getMainUserPrefix(uuid);
//            if (prefix == null) {
//                skippedPlayers++;
//                continue;
//            }
//            player.saveInformation("prefix", prefix);
//            player.saveIfModified();
//            players++;
//            //Print progress so people don't think the server has hanged
//            if ((System.currentTimeMillis() / 1000L) > nextPrintStatus) {
//                plugin.debugLogger(Level.INFO, (players + skippedPlayers) + "/" + totalPlayers + " have had their prefixed saved into their PlayerData file.", 1);
//                nextPrintStatus = (System.currentTimeMillis() / 1000) + 5;
//            }
//        }
//        plugin.debugLogger(Level.INFO, "Saved the prefixes of " + players + " into their player data file out of " + totalPlayers + " players."
//                + skippedPlayers + " couldn't be loaded as the permission plugin returned null for their UUID.", 1);
//
//    }

}
