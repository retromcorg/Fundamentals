package com.johnymuffin.beta.fundamentals.commands;

import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;

public class CommandHat implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = null;
        if (commandSender instanceof Player) {
            player = (Player) commandSender;
        }

        if (commandSender.hasPermission("fundamentals.hat") || commandSender.isOp()) {
            this.placeOnHead(player, player.getItemInHand());
        }
        else {
            commandSender.sendMessage(FundamentalsLanguage.getInstance().getMessage("no_permission"));
        }
        return true;
    }

    private boolean placeOnHead(Player player, ItemStack item) {
        PlayerInventory inv = player.getInventory();
        if (item.getType() == Material.AIR) {
            player.sendMessage(FundamentalsLanguage.getInstance().getMessage("hat_air"));
            return false;
        } else {
            int id = item.getTypeId();
            if (id >= 0 && id <= 255) {
                ItemStack helmet = inv.getHelmet();
                ItemStack hat = new ItemStack(item.getType(), item.getAmount() < 0 ? item.getAmount() : 1, item.getDurability());
                MaterialData data = item.getData();
                if (data != null) {
                    hat.setData(item.getData());
                }

                inv.setHelmet(hat);
                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    this.removeExact(inv, item);
                }

                if (helmet.getAmount() > 0) {
                    HashMap<Integer, ItemStack> leftover = inv.addItem(new ItemStack[]{helmet});
                    if (!leftover.isEmpty()) {
                        player.sendMessage(FundamentalsLanguage.getInstance().getMessage("hat_dropping"));

                        for (Entry<Integer, ItemStack> e : leftover.entrySet()) {
                            player.getWorld().dropItem(player.getLocation(), e.getValue());
                        }
                    }
                }

                player.sendMessage(FundamentalsLanguage.getInstance().getMessage("hat_success"));
                return true;
            } else {
                player.sendMessage(FundamentalsLanguage.getInstance().getMessage("hat_invalid"));
                return false;
            }
        }
    }

    public ItemStack stackFromString(String item, int count) {
        Material material = Material.matchMaterial(item);
        return material == null ? null : new ItemStack(material, count);
    }

    private void removeExact(PlayerInventory inv, ItemStack item) {
        ItemStack[] contents = inv.getContents();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null && contents[i].equals(item)) {
                if (contents[i].getAmount() > 1) {
                    contents[i].setAmount(contents[i].getAmount() - 1);
                } else {
                    inv.setItem(i, null);
                }
                break;
            }
        }
    }
}
