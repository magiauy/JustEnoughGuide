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

import com.balugaq.jeg.api.recipe_complete.source.base.RecipeCompleteProvider;
import com.balugaq.jeg.core.integrations.Integration;
import com.balugaq.jeg.api.recipe_complete.RecipeCompletableRegistry;
import com.ytdd9527.networksexpansion.implementation.ExpansionItems;
import io.github.sefiraat.networks.Networks;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
public class NetworksExpansionIntegrationMain implements Integration {
    public static final int[] ENCODER_RECIPE_SLOTS = new int[]{12, 13, 14, 21, 22, 23, 30, 31, 32};
    public static final int[] CRAFTING_GRID_RECIPE_SLOTS = new int[]{6, 7, 8, 15, 16, 17, 24, 25, 26};
    public static final List<SlimefunItem> handledSlimefunItems = new ArrayList<>();
    public static JavaPlugin plugin = null;

    public static JavaPlugin getPlugin() {
        if (plugin == null) {
            plugin = Networks.getInstance();
        }

        return plugin;
    }

    public static void rrc(SlimefunItem slimefunItem, int[] slots, boolean unordered) {
        handledSlimefunItems.add(slimefunItem);
        RecipeCompletableRegistry.registerRecipeCompletable(slimefunItem, slots, unordered);
    }

    @Override
    public String getHookPlugin() {
        return "NetworksExpansion";
    }

    @Override
    public void onEnable() {
        RecipeCompleteProvider.addSource(new NetworksExpansionRecipeCompleteSource());

        rrc(ExpansionItems.ANCIENT_ALTAR_RECIPE_ENCODER, ENCODER_RECIPE_SLOTS, false);
        rrc(ExpansionItems.ARMOR_FORGE_RECIPE_ENCODER, CRAFTING_GRID_RECIPE_SLOTS, false);
        rrc(ExpansionItems.COMPRESSOR_RECIPE_ENCODER, CRAFTING_GRID_RECIPE_SLOTS, false);
        rrc(ExpansionItems.EXPANSION_WORKBENCH_RECIPE_ENCODER, CRAFTING_GRID_RECIPE_SLOTS, false);
        rrc(ExpansionItems.GRIND_STONE_RECIPE_ENCODER, CRAFTING_GRID_RECIPE_SLOTS, false);
        rrc(ExpansionItems.JUICER_RECIPE_ENCODER, CRAFTING_GRID_RECIPE_SLOTS, false);
        rrc(ExpansionItems.MAGIC_WORKBENCH_RECIPE_ENCODER, CRAFTING_GRID_RECIPE_SLOTS, false);
        rrc(ExpansionItems.ORE_CRUSHER_RECIPE_ENCODER, CRAFTING_GRID_RECIPE_SLOTS, false);
        rrc(ExpansionItems.PRESSURE_CHAMBER_RECIPE_ENCODER, CRAFTING_GRID_RECIPE_SLOTS, false);
        rrc(ExpansionItems.QUANTUM_WORKBENCH_RECIPE_ENCODER, CRAFTING_GRID_RECIPE_SLOTS, false);
        rrc(ExpansionItems.SMELTERY_RECIPE_ENCODER, CRAFTING_GRID_RECIPE_SLOTS, false);
        rrc(ExpansionItems.NETWORK_CRAFTING_GRID_NEW_STYLE, CRAFTING_GRID_RECIPE_SLOTS, false);
    }

    @Override
    public void onDisable() {
        for (SlimefunItem slimefunItem : handledSlimefunItems) {
            RecipeCompletableRegistry.unregisterRecipeCompletable(slimefunItem);
        }
    }
}
