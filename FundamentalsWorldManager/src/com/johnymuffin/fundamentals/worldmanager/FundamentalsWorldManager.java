package com.johnymuffin.fundamentals.worldmanager;

import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.projectposeidon.api.PoseidonUUID;
import com.projectposeidon.johnymuffin.UUIDManager;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class FundamentalsWorldManager extends JavaPlugin {
    //Basic Plugin Info
    private static FundamentalsWorldManager plugin;
    private Logger log;
    private String pluginName;
    private PluginDescriptionFile pdf;
    private Fundamentals fundamentals;
//    private WorldGroup[] worldGroups;


    @Override
    public void onEnable() {
        plugin = this;
        log = this.getServer().getLogger();
        pdf = this.getDescription();
        pluginName = pdf.getName();
        log.info("[" + pluginName + "] Is Loading, Version: " + pdf.getVersion());

        fundamentals = (Fundamentals) Bukkit.getServer().getPluginManager().getPlugin("Fundamentals");

        //Generate config options
//        fundamentals.getFundamentalConfig().generateConfigOption("settings.world-manager.use-world-groups.default", "This group can't be specified manually. Any worlds that aren't apart of another group are apart of this.");
//        fundamentals.getFundamentalConfig().generateConfigOption("settings.world-manager.use-world-groups.1.worlds", "world1,world1_nether,world2");
//        fundamentals.getFundamentalConfig().generateConfigOption("settings.world-manager.use-world-groups.1.name", "Group 1 - Blue");
//        fundamentals.getFundamentalConfig().generateConfigOption("settings.world-manager.use-world-groups.2.worlds", "skylands,world3_nether,world3");
//        fundamentals.getFundamentalConfig().generateConfigOption("settings.world-manager.use-world-groups.2.name", "Group 2 - Orange");
//        fundamentals.getFundamentalConfig().generateConfigOption("settings.world-manager.use-world-groups.enable", false);
//        fundamentals.getFundamentalConfig().generateConfigOption("settings.world-manager.use-world-groups.info", "The world groups with each group having the ability to have different balances, inventories and etc.");
        fundamentals.getFundamentalConfig().generateConfigOption("settings.world-manager.world.world.group", "World Group 1");
        fundamentals.getFundamentalConfig().generateConfigOption("settings.world-manager.world.world_nether.group", "World Group 1");
        fundamentals.getFundamentalConfig().generateConfigOption("settings.world-manager.world.world2.group", "World Group 2");
        fundamentals.getFundamentalConfig().generateConfigOption("settings.world-manager.world.world2_nether.group", "World Group 2");

        fundamentals.getFundamentalConfig().save();

        PlayerListener playerListener = new PlayerListener(plugin);
        Bukkit.getPluginManager().registerEvents(playerListener, plugin);


        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                inventoryConversionProcess(Bukkit.getWorlds().get(0));
                balanceConversionProcess(Bukkit.getWorlds().get(0));
            }
        });


    }

    @Override
    public void onDisable() {

    }

    public Fundamentals getFundamentals() {
        return fundamentals;
    }


    public void setPlayerInventory(Player player, String world) {
        player.getInventory().setContents(getPlayerWorldInventory(player.getUniqueId(), world).getContents());
    }

    public void savePlayerInventory(Player player, String world) {
        savePlayerInventory(player, world, player.getInventory());
    }

    public void savePlayerInventory(Player player, String world, Inventory inventory) {
        FundamentalsPlayer fundamentalsPlayer = fundamentals.getPlayerMap().getPlayer(player.getUniqueId());
        String serializedInventory = Utility.InventoryToString(inventory);
        String worldGroup = getWorldGroup(world);
        fundamentalsPlayer.saveInformation("fundamentals-world-manager." + worldGroup + ".inventory", serializedInventory);
    }

    public void savePlayerInventory(UUID player, String world, Inventory inventory) {
        FundamentalsPlayer fundamentalsPlayer = fundamentals.getPlayerMap().getPlayer(player);
        String serializedInventory = Utility.InventoryToString(inventory);
        String worldGroup = getWorldGroup(world);
        fundamentalsPlayer.saveInformation("fundamentals-world-manager." + worldGroup + ".inventory", serializedInventory);
    }

    public String getWorldGroup(String world) {
        String worldGroup = fundamentals.getFundamentalConfig().getConfigString("settings.world-manager.world." + world + ".group");
        if(worldGroup == null) {
            worldGroup = "Default World Group";
            fundamentals.debugLogger(Level.WARNING,"A inventory group for the world " + world + " couldn't be found. Using " + worldGroup + ".",2);
        }
        return worldGroup;
    }

    public Inventory getPlayerWorldInventory(UUID uuid, String world) {
        FundamentalsPlayer fundamentalsPlayer = fundamentals.getPlayerMap().getPlayer(uuid);
        String worldGroup = getWorldGroup(world);
        Object serializedInventoryRaw = fundamentalsPlayer.getInformation("fundamentals-world-manager." + worldGroup + ".inventory");
        if(serializedInventoryRaw == null) {
            return new CraftInventory(null);
        }
        String serializedInventory = (String) serializedInventoryRaw;
        return Utility.StringToInventory(serializedInventory);

    }

    public void balanceConversionProcess(World world) {
        for(UUID uuid : fundamentals.getPlayerMap().getKnownPlayers()) {
//            ((WatchDogThread) WatchDogThread.currentThread()).tickUpdate(); //Does this work
            FundamentalsPlayer player = fundamentals.getPlayerMap().getPlayer(uuid);
            if(player.getInformation("balance") == null) {
                continue; //Skip player
            }

            String playerName = PoseidonUUID.getPlayerUsernameFromUUID(uuid);
            if(playerName == null) {
                playerName = fundamentals.getPlayerCache().getUsernameFromUUID(uuid);
            }
            if(playerName == null) {
                playerName = "Unknown Player";
            }

            Double balance = Double.valueOf(String.valueOf(player.getInformation("balance")));
            player.setBalance(balance, world.getName()); //Get the old balance amount and set it with the world.
            fundamentals.debugLogger(Level.INFO, "Converted the balance of $"+ balance + " to multi-world for " + playerName + " (" + uuid.toString() + ").", 1);
        }
    }

    public void inventoryConversionProcess(World world) {
        fundamentals.debugLogger(Level.INFO, "Loading all players that exist in world " + world.getName(), 1);

        File worldPlayerFolder = new File(world.getName(), "players");
        if(!worldPlayerFolder.exists()) {
            fundamentals.debugLogger(Level.WARNING, "Unable to find a player data folder for the world " + world.getName(), 0);
        }

        Pattern uuidPattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
        for (String string : worldPlayerFolder.list()) {
//            ((WatchDogThread) WatchDogThread.currentThread()).tickUpdate(); //Does this work
            if (!string.endsWith(".dat")) {
                fundamentals.debugLogger(Level.WARNING, "Skipping a file called " + string + " as it is an unknown file type.", 2);
                continue;
            }

            String sanitizedUUID = string.replaceAll(".dat", "");
            if(!uuidPattern.matcher(sanitizedUUID).matches()) {
                fundamentals.debugLogger(Level.WARNING, "A non UUID based player data file has been found called " + sanitizedUUID + " and will be skipped.", 2);
                continue;
            }
            UUID uuid = UUID.fromString(sanitizedUUID);

//            String playerName = fundamentals.getPlayerCache().getUsernameFromUUID(uuid);
            //Try Poseidon UUID Cache
            String playerName = PoseidonUUID.getPlayerUsernameFromUUID(uuid);
            if(playerName == null) {
                //Try Fundamentals cache
                playerName = fundamentals.getPlayerCache().getUsernameFromUUID(uuid);
                if(playerName == null) {
                    fundamentals.debugLogger(Level.WARNING, "Unable to find username for UUID " + uuid.toString() + ". Skipping player.", 1);
                    continue;
                }
                //Fundamentals knows the player
                boolean offlineUUID = uuid.equals(UUIDManager.generateOfflineUUID(playerName));
                UUIDManager.getInstance().receivedUUID(playerName, uuid, (System.currentTimeMillis() / 1000L) + 30, !offlineUUID); //Save the info to the cache for 30 seconds for player data loading.
                fundamentals.debugLogger(Level.WARNING, "Fundamentals fallback check has been able to find the username " + playerName + " for " + uuid.toString(), 1);
            }

            MinecraftServer server = ((CraftServer)Bukkit.getServer()).getServer();
            EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), playerName, new ItemInWorldManager(server.getWorldServer(0)));
            CraftPlayer craftPlayer = (entity == null) ? null : (CraftPlayer) entity.getBukkitEntity();
            if(craftPlayer != null) {
                craftPlayer.loadData();
            }
            savePlayerInventory(uuid, world.getName(), craftPlayer.getInventory());
            fundamentals.debugLogger(Level.INFO, "Saved " + playerName + " as serialized data.", 1);

        }





    }

}
