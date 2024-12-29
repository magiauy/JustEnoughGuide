package com.balugaq.jeg.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * This class provides utility methods for working with ItemStacks.
 */
@UtilityClass
public final class ItemStackUtil {
    /**
     * This method is used to convert an {@code MyItemStack extends ItemStack} to a pure {@code ItemStack}.
     *
     * @param item The MyItemStack to be converted.
     * @return A pure ItemStack.
     */
    @NotNull
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
