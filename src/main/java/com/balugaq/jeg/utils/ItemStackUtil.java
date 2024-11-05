package com.balugaq.jeg.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ItemStackUtil {
    @Nonnull
    public static ItemStack getCleanItem(@Nullable ItemStack item) {
        if (item == null) {
            return new ItemStack(Material.AIR);
        }

        ItemStack cleanItem = new ItemStack(item.getType());
        cleanItem.setAmount(item.getAmount());
        if (item.hasItemMeta()) {
            cleanItem.setItemMeta(item.getItemMeta());
        }

        return cleanItem;
    }
}
