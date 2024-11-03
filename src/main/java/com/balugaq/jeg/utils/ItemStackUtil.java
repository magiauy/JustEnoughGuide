package com.balugaq.jeg.utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ItemStackUtil {
    @Nonnull
    public static ItemStack cloneItem(@Nullable ItemStack item, int amount) {
        if (item == null) {
            return new ItemStack(Material.AIR);
        }

        ItemStack cloneItem = item.clone();
        cloneItem.setAmount(amount);

        return cloneItem;
    }

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

    public static void clearNBT(@Nonnull ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) {
            return;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        for (NamespacedKey namespacedKey : persistentDataContainer.getKeys()) {
            persistentDataContainer.remove(namespacedKey);
        }
        itemStack.setItemMeta(itemMeta);
    }

    @Nullable
    public static ItemStack cloneWithoutNBT(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        if (!itemStack.hasItemMeta()) {
            return new ItemStack(itemStack);
        }
        ItemStack result = new ItemStack(itemStack);
        ItemStackUtil.clearNBT(result);
        return result;
    }
}
