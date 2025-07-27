/*
 * Copyright (c) 2024-2025 balugaq
 *
 * This file is part of JustEnoughGuide, available under MIT license.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * - The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 * - The author's name (balugaq or 大香蕉) and project name (JustEnoughGuide or JEG) shall not be
 *   removed or altered from any source distribution or documentation.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.balugaq.jeg.core.integrations.networksexpansion;

import com.balugaq.jeg.api.recipe_complete.RecipeCompletableRegistry;
import com.balugaq.jeg.api.recipe_complete.source.base.RecipeCompleteProvider;
import com.balugaq.jeg.core.integrations.Integration;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
public class NetworksExpansionIntegrationMain implements Integration {
    public static final int[] ENCODER_RECIPE_SLOTS = new int[]{12, 13, 14, 21, 22, 23, 30, 31, 32};
    public static final int[] CRAFTING_GRID_NEW_STYLE_RECIPE_SLOTS = new int[]{6, 7, 8, 15, 16, 17, 24, 25, 26};
    public static final int[] EXPANSION_WORKBENCH_RECIPE_SLOTS = new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30};
    public static final int[] EXPANSION_WORKBENCH_6X6_RECIPE_SLOTS = new int[]{
            0, 1, 2, 3, 4, 5,
            9, 10, 11, 12, 13, 14,
            18, 19, 20, 21, 22, 23,
            27, 28, 29, 30, 31, 32,
            36, 37, 38, 39, 40, 41,
            45, 46, 47, 48, 49, 50
    };
    public static final int[] STORAGE_UPGRADE_TABLE_RECIPE_SLOTS = new int[]{
            2, 3, 4,
            11, 12, 13,
            20, 21, 22
    };
    public static final List<SlimefunItem> handledSlimefunItems = new ArrayList<>();
    public static @Nullable JavaPlugin plugin = null;

    public static @Nullable JavaPlugin getPlugin() {
        if (plugin == null) {
            plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Networks");
        }

        return plugin;
    }

    public static void rrc(@NotNull String id, int @NotNull [] slots, boolean unordered) {
        SlimefunItem slimefunItem = SlimefunItem.getById(id);
        if (slimefunItem != null) {
            rrc(slimefunItem, slots, unordered);
        }
    }

    public static void rrc(@NotNull SlimefunItem slimefunItem, int @NotNull [] slots, boolean unordered) {
        handledSlimefunItems.add(slimefunItem);
        RecipeCompletableRegistry.registerRecipeCompletable(slimefunItem, slots, unordered);
    }

    @Override
    public @NotNull String getHookPlugin() {
        return "NetworksExpansion";
    }

    @Override
    public void onEnable() {
        RecipeCompleteProvider.addSource(new NetworksExpansionRecipeCompleteSlimefunSource());
        RecipeCompleteProvider.addSource(new NetworksExpansionRecipeCompleteVanillaSource());

        rrc("NTW_EXPANSION_ANCIENT_ALTAR_RECIPE_ENCODER", ENCODER_RECIPE_SLOTS, false);
        rrc("NTW_EXPANSION_ARMOR_FORGE_RECIPE_ENCODER", ENCODER_RECIPE_SLOTS, false);
        rrc("NTW_EXPANSION_COMPRESSOR_RECIPE_ENCODER", ENCODER_RECIPE_SLOTS, false);
        rrc("NTW_EXPANSION_EXPANSION_WORKBENCH_RECIPE_ENCODER", ENCODER_RECIPE_SLOTS, false);
        rrc("NTW_EXPANSION_GRIND_STONE_RECIPE_ENCODER", ENCODER_RECIPE_SLOTS, false);
        rrc("NTW_EXPANSION_JUICER_RECIPE_ENCODER", ENCODER_RECIPE_SLOTS, false);
        rrc("NTW_EXPANSION_MAGIC_WORKBENCH_RECIPE_ENCODER", ENCODER_RECIPE_SLOTS, false);
        rrc("NTW_EXPANSION_ORE_CRUSHER_RECIPE_ENCODER", ENCODER_RECIPE_SLOTS, false);
        rrc("NTW_EXPANSION_PRESSURE_CHAMBER_RECIPE_ENCODER", ENCODER_RECIPE_SLOTS, false);
        rrc("NTW_EXPANSION_QUANTUM_WORKBENCH_RECIPE_ENCODER", ENCODER_RECIPE_SLOTS, false);
        rrc("NTW_EXPANSION_SMELTERY_RECIPE_ENCODER", ENCODER_RECIPE_SLOTS, false);
        rrc("NTW_EXPANSION_CRAFTING_GRID_NEW_STYLE", CRAFTING_GRID_NEW_STYLE_RECIPE_SLOTS, false);
        rrc("NTW_EXPANSION_WORKBENCH", EXPANSION_WORKBENCH_RECIPE_SLOTS, false);
        rrc("NTW_EXPANSION_WORKBENCH_6X6", EXPANSION_WORKBENCH_6X6_RECIPE_SLOTS, false);
        rrc("NTW_EXPANSION_STORAGE_UPGRADE_TABLE", STORAGE_UPGRADE_TABLE_RECIPE_SLOTS, false);
    }

    @Override
    public void onDisable() {
        for (SlimefunItem slimefunItem : handledSlimefunItems) {
            RecipeCompletableRegistry.unregisterRecipeCompletable(slimefunItem);
        }
    }
}
