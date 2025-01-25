package com.balugaq.jeg.utils;

import com.balugaq.jeg.implementation.JustEnoughGuide;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * This class provides a way to access the ItemFlag constants that were added in different versions of Minecraft.
 * Used to fix compatibility issues with different versions of Minecraft.
 *
 * @author balugaq
 * @since 1.2
 */
@UtilityClass
public class JEGVersionedItemFlag {
    public static final @Nullable ItemFlag HIDE_ADDITIONAL_TOOLTIP;

    static {
        MinecraftVersion version = JustEnoughGuide.getMCVersion();
        HIDE_ADDITIONAL_TOOLTIP = version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5)
                ? getKey("HIDE_ADDITIONAL_TOOLTIP")
                : getKey("HIDE_POTION_EFFECTS");
    }

    @Nullable
    private static ItemFlag getKey(@NotNull String key) {
        try {
            Field field = ItemFlag.class.getDeclaredField(key);
            return (ItemFlag) field.get(null);
        } catch (Exception ignored) {
            return null;
        }
    }
}
