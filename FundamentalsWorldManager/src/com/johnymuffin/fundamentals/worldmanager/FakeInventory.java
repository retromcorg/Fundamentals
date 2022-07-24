package com.johnymuffin.fundamentals.worldmanager;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;

public class FakeInventory implements IInventory {
    public ItemStack[] items;
//    public ItemStack[] armor = new ItemStack[4];

    public FakeInventory(int inventorySize) {
        items = new ItemStack[inventorySize];
    }


    public int getSize() {
        return this.items.length;
    }


    public ItemStack getItem(int i) {
        ItemStack[] aitemstack = this.items;
//        if (i >= aitemstack.length) {
//            i -= aitemstack.length;
//            aitemstack = this.armor;
//        }

        return aitemstack[i];
    }

    public ItemStack splitStack(int i, int j) {
        ItemStack[] aitemstack = this.items;
//        if (i >= this.items.length) {
//            aitemstack = this.armor;
//            i -= this.items.length;
//        }

        if (aitemstack[i] != null) {
            ItemStack itemstack;
            if (aitemstack[i].count <= j) {
                itemstack = aitemstack[i];
                aitemstack[i] = null;
                return itemstack;
            } else {
                itemstack = aitemstack[i].a(j);
                if (aitemstack[i].count == 0) {
                    aitemstack[i] = null;
                }

                return itemstack;
            }
        } else {
            return null;
        }
    }

    public void setItem(int i, ItemStack itemstack) {
        ItemStack[] aitemstack = this.items;
//        if (i >= aitemstack.length) {
//            i -= aitemstack.length;
//            aitemstack = this.armor;
//        }

        aitemstack[i] = itemstack;
    }

    public String getName() {
        return "Inventory";
    }

    public int getMaxStackSize() {
        return 64;
    }

    public void update() {
        throw new RuntimeException("Unsupported use of FakeInventory");
    }

    public boolean a_(EntityHuman entityHuman) {
        throw new RuntimeException("Unsupported use of FakeInventory");
    }

    public ItemStack[] getContents() {
        return this.items;
    }
}
