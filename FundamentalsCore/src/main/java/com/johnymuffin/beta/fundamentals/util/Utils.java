package com.johnymuffin.beta.fundamentals.util;

import com.earth2me.essentials.Essentials;
import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import com.projectposeidon.api.PoseidonUUID;
import com.projectposeidon.api.UUIDType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.regex.Pattern;

public class Utils {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
    
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String getCurrentMonth() {
        // Get the current date
        LocalDate date = LocalDate.now();

        // Get the month and format it as a full name
        String month = date.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());

        return month;
    }

    public static Player getPlayerFromString(String name) {
        List<Player> players = Bukkit.matchPlayer(name);
        if (players.size() == 1) {
            return players.get(0);
        }
        return null;
    }

    public static Player getPlayerFromUUID(UUID uuid) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getUniqueId().equals(uuid)) {
                return player;
            }
        }
        return null;
    }

    public static boolean isInt(String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static UUID getUUIDFromUsername(String name) {
        Player player = getPlayerFromString(name);
        if (player != null) {
            return player.getUniqueId();
        }
        //Search Poseidon Cache
        UUIDType uuidType = PoseidonUUID.getPlayerUUIDCacheStatus(name);
        switch (uuidType) {
            case ONLINE:
                return PoseidonUUID.getPlayerUUIDFromCache(name, true);
            case OFFLINE:
                return PoseidonUUID.getPlayerUUIDFromCache(name, false);
        }
        return null;

    }

    public static String getPlayerName(UUID uuid) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (uuid.equals(player.getUniqueId())) {
                return player.getName();
            }
        }
        return PoseidonUUID.getPlayerUsernameFromUUID(uuid);

    }

    //Essentials Code Start: com.earth2me.essentials.Util
    private static final Set<Integer> AIR_MATERIALS = new HashSet<Integer>();

    static {
        AIR_MATERIALS.add(Material.AIR.getId());
        AIR_MATERIALS.add(Material.SAPLING.getId());
        AIR_MATERIALS.add(Material.POWERED_RAIL.getId());
        AIR_MATERIALS.add(Material.DETECTOR_RAIL.getId());
        AIR_MATERIALS.add(Material.DEAD_BUSH.getId());
        AIR_MATERIALS.add(Material.RAILS.getId());
        AIR_MATERIALS.add(Material.YELLOW_FLOWER.getId());
        AIR_MATERIALS.add(Material.RED_ROSE.getId());
        AIR_MATERIALS.add(Material.RED_MUSHROOM.getId());
        AIR_MATERIALS.add(Material.BROWN_MUSHROOM.getId());
        AIR_MATERIALS.add(Material.SEEDS.getId());
        AIR_MATERIALS.add(Material.SIGN_POST.getId());
        AIR_MATERIALS.add(Material.WALL_SIGN.getId());
        AIR_MATERIALS.add(Material.LADDER.getId());
        AIR_MATERIALS.add(Material.SUGAR_CANE_BLOCK.getId());
        AIR_MATERIALS.add(Material.REDSTONE_WIRE.getId());
        AIR_MATERIALS.add(Material.REDSTONE_TORCH_OFF.getId());
        AIR_MATERIALS.add(Material.REDSTONE_TORCH_ON.getId());
        AIR_MATERIALS.add(Material.TORCH.getId());
        AIR_MATERIALS.add(Material.SOIL.getId());
        AIR_MATERIALS.add(Material.DIODE_BLOCK_OFF.getId());
        AIR_MATERIALS.add(Material.DIODE_BLOCK_ON.getId());
        AIR_MATERIALS.add(Material.TRAP_DOOR.getId());
        AIR_MATERIALS.add(Material.STONE_BUTTON.getId());
        AIR_MATERIALS.add(Material.STONE_PLATE.getId());
        AIR_MATERIALS.add(Material.WOOD_PLATE.getId());
        AIR_MATERIALS.add(Material.IRON_DOOR_BLOCK.getId());
        AIR_MATERIALS.add(Material.WOODEN_DOOR.getId());
        AIR_MATERIALS.add(Material.SNOW.getId());
    }

    public static Location getSafeDestination(final Location loc) throws Exception {
        if (loc == null || loc.getWorld() == null) {
            throw new Exception("Invalid Location Object");
        }
        final World world = loc.getWorld();
        int x = (int) Math.floor(loc.getX());
        int y = (int) Math.ceil(loc.getY());
        int z = (int) Math.floor(loc.getZ());

        while (isBlockAboveAir(world, x, y, z)) {
            y -= 1;
            if (y < 0) {
                break;
            }
        }

        while (isBlockUnsafe(world, x, y, z)) {
            y += 1;
            if (y >= 127) {
                x += 1;
                break;
            }
        }
        while (isBlockUnsafe(world, x, y, z)) {
            y -= 1;
            if (y <= 1) {
                y = 127;
                x += 1;
                if (x - 32 > loc.getBlockX()) {
                    throw new Exception("Sorry, there is a hole in the floor");
                }
            }
        }
        return new Location(world, x + 0.5D, y, z + 0.5D, loc.getYaw(), loc.getPitch());
    }

    private static boolean isBlockAboveAir(final World world, final int x, final int y, final int z) {
        return AIR_MATERIALS.contains(world.getBlockAt(x, y - 1, z).getType().getId());
    }

    private static boolean isBlockUnsafe(final World world, final int x, final int y, final int z) {
        final Block below = world.getBlockAt(x, y - 1, z);
        if (below.getType() == Material.LAVA || below.getType() == Material.STATIONARY_LAVA) {
            return true;
        }

        if (below.getType() == Material.FIRE) {
            return true;
        }

        if ((!AIR_MATERIALS.contains(world.getBlockAt(x, y, z).getType().getId()))
                || (!AIR_MATERIALS.contains(world.getBlockAt(x, y + 1, z).getType().getId()))) {
            return true;
        }
        return isBlockAboveAir(world, x, y, z);
    }
    //Essentials Code End

    public static boolean verifyHomeName(String name) {
        return Pattern.matches("^[a-zA-Z0-9]+$", name);
    }

    public static String formatColor(String s) {
        return s.replaceAll("(&([a-f0-9]))", "\u00A7$2");

    }

    public static void updateVanishedPlayers() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            FundamentalsPlayer fPlayer = FundamentalsPlayerMap.getInstance().getPlayer(p);
            if (fPlayer.isVanished()) {
                for (Player p2 : Bukkit.getOnlinePlayers()) {
                    if (!(p2.hasPermission("fundamentals.vanish.bypass") || p2.isOp())) {
                        p2.hidePlayer(p);
                    }
                }
            } else {
                for (Player p2 : Bukkit.getOnlinePlayers()) {
                    p2.showPlayer(p);
                }
            }
        }
    }

    public static void sendNewLinedMessage(CommandSender sender, String message) {
        String[] strings = message.split("\n");
        for (String string : strings) {
            sender.sendMessage(string);
        }
    }

    public static void sendLangFileMessage(CommandSender sender, String msgKey) {
        String[] strings = FundamentalsLanguage.getInstance().getMessage(msgKey).split("\n");
        for (String string : strings) {
            sender.sendMessage(string);
        }
    }

    public static boolean setEssentialsHidden(Player player, boolean visible) {
        if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
            Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            essentials.getUser(player).setHidden(visible);
            return true;
        }
        return false;
    }
}
