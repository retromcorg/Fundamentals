package com.johnymuffin.fundamentals.worldmanager;

import com.johnymuffin.beta.fundamentals.simplejson.JSONArray;
import com.johnymuffin.beta.fundamentals.simplejson.JSONObject;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Utility {

    @Deprecated
    public static String InventoryToString(Inventory invInventory) {
        String serialization = invInventory.getSize() + ";";
        for (int i = 0; i < invInventory.getSize(); i++) {
            ItemStack is = invInventory.getItem(i);
            if (is != null) {
                String serializedItemStack = new String();

                String isType = String.valueOf(is.getType().getId());
                serializedItemStack += "t@" + isType;

                if (is.getDurability() != 0) {
                    String isDurability = String.valueOf(is.getDurability());
                    serializedItemStack += ":d@" + isDurability;
                }

                if (is.getAmount() != 1) {
                    String isAmount = String.valueOf(is.getAmount());
                    serializedItemStack += ":a@" + isAmount;
                }

//                Map<Enchantment, Integer> isEnch = is.getEnchantments();
//                if (isEnch.size() > 0) {
//                    for (Entry<Enchantment, Integer> ench : isEnch.entrySet()) {
//                        serializedItemStack += ":e@" + ench.getKey().getId() + "@" + ench.getValue();
//                    }
//                }

                serialization += i + "#" + serializedItemStack + ";";
            }
        }
        return serialization;
    }

    public static JSONObject inventoryToJsonObject(Inventory inventory) {
        JSONArray inventoryArray = new JSONArray();

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack != null) {
                if (itemStack != null) {
                    JSONObject item = new JSONObject();
                    item.put("slot", i);
                    item.put("type", String.valueOf(itemStack.getType().getId()));
                    if (itemStack.getDurability() != 0) {
                        item.put("durability", String.valueOf(itemStack.getDurability()));
                    }
                    if (itemStack.getAmount() != 1) {
                        item.put("amount", String.valueOf(itemStack.getAmount()));
                    }
                    inventoryArray.add(item);
                }
            }
        }
        JSONObject inventoryObject = new JSONObject();
        inventoryObject.put("inventory", inventoryArray);
        inventoryObject.put("size", inventory.getSize());
        return inventoryObject;
    }

    public static Inventory JsonArrayToInventory(JSONObject inventoryArray) {
        int inventorySize = Integer.valueOf(String.valueOf(inventoryArray.getOrDefault("size", 0)));
        Inventory deserializedInventory = new CraftInventory(new FakeInventory(inventorySize));

        for (Object item : ((JSONArray) inventoryArray.get("inventory"))) {
            JSONObject jsonItem = (JSONObject) item;
            int slot = Integer.valueOf(String.valueOf(jsonItem.get("slot")));
            int type = Integer.valueOf(String.valueOf(jsonItem.get("type")));

            ItemStack itemStack = new ItemStack(Material.getMaterial(Integer.valueOf(type)));
            if (jsonItem.containsKey("durability")) {
                itemStack.setDurability(Short.valueOf(String.valueOf(jsonItem.get("durability"))));
            }
            if (jsonItem.containsKey("amount")) {
                itemStack.setAmount(Integer.valueOf(String.valueOf(jsonItem.get("amount"))));
            }
            deserializedInventory.setItem(slot, itemStack);
        }
        return deserializedInventory;
    }

    @Deprecated
    public static Inventory StringToInventory(String invString) {
        String[] serializedBlocks = invString.split(";");
        String invInfo = serializedBlocks[0];
//        Inventory deserializedInventory = Bukkit.getServer().createInventory(null, Integer.valueOf(invInfo));
        Inventory deserializedInventory = new CraftInventory(new FakeInventory(36));

        for (int i = 1; i < serializedBlocks.length; i++) {
            String[] serializedBlock = serializedBlocks[i].split("#");
            int stackPosition = Integer.valueOf(serializedBlock[0]);

            if (stackPosition >= deserializedInventory.getSize()) {
                continue;
            }

            ItemStack is = null;
            Boolean createdItemStack = false;

            String[] serializedItemStack = serializedBlock[1].split(":");
            for (String itemInfo : serializedItemStack) {
                String[] itemAttribute = itemInfo.split("@");
                if (itemAttribute[0].equals("t")) {
                    is = new ItemStack(Material.getMaterial(Integer.valueOf(itemAttribute[1])));
                    createdItemStack = true;
                } else if (itemAttribute[0].equals("d") && createdItemStack) {
                    is.setDurability(Short.valueOf(itemAttribute[1]));
                } else if (itemAttribute[0].equals("a") && createdItemStack) {
                    is.setAmount(Integer.valueOf(itemAttribute[1]));
                }
            }
            deserializedInventory.setItem(stackPosition, is);
        }

        return deserializedInventory;
    }


}
