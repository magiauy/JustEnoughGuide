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

import com.balugaq.jeg.core.integrations.networks.NetworksExpansionRecipeCompleteSource;
import com.balugaq.jeg.api.recipe_complete.source.base.RecipeCompleteProvider;
import com.balugaq.jeg.core.integrations.Integration;
import com.balugaq.jeg.core.integrations.networks.NetworksIntegrationMain;
import com.balugaq.jeg.core.listeners.RecipeCompletableListener;
import com.ytdd9527.networksexpansion.implementation.ExpansionItems;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

import java.util.ArrayList;
import java.util.List;

public class NetworksExpansionIntegrationMain implements Integration {
    public static final List<SlimefunItem> handledSlimefunItems = new ArrayList<>();

    @Override
    public String getHookPlugin() {
        return "NetworksExpansion";
    }

    @Override
    public void onEnable() {
        RecipeCompleteProvider.addSource(new NetworksExpansionRecipeCompleteSource());

        rrc(ExpansionItems.ANCIENT_ALTAR_RECIPE_ENCODER, NetworksIntegrationMain.ENCODER_RECIPE_SLOTS);
        rrc(ExpansionItems.ARMOR_FORGE_RECIPE_ENCODER, NetworksIntegrationMain.CRAFTING_GRID_RECIPE_SLOTS);
        rrc(ExpansionItems.COMPRESSOR_RECIPE_ENCODER, NetworksIntegrationMain.CRAFTING_GRID_RECIPE_SLOTS);
        rrc(ExpansionItems.EXPANSION_WORKBENCH_RECIPE_ENCODER, NetworksIntegrationMain.CRAFTING_GRID_RECIPE_SLOTS);
        rrc(ExpansionItems.GRIND_STONE_RECIPE_ENCODER, NetworksIntegrationMain.CRAFTING_GRID_RECIPE_SLOTS);
        rrc(ExpansionItems.JUICER_RECIPE_ENCODER, NetworksIntegrationMain.CRAFTING_GRID_RECIPE_SLOTS);
        rrc(ExpansionItems.MAGIC_WORKBENCH_RECIPE_ENCODER, NetworksIntegrationMain.CRAFTING_GRID_RECIPE_SLOTS);
        rrc(ExpansionItems.ORE_CRUSHER_RECIPE_ENCODER, NetworksIntegrationMain.CRAFTING_GRID_RECIPE_SLOTS);
        rrc(ExpansionItems.PRESSURE_CHAMBER_RECIPE_ENCODER, NetworksIntegrationMain.CRAFTING_GRID_RECIPE_SLOTS);
        rrc(ExpansionItems.QUANTUM_WORKBENCH_RECIPE_ENCODER, NetworksIntegrationMain.CRAFTING_GRID_RECIPE_SLOTS);
        rrc(ExpansionItems.SMELTERY_RECIPE_ENCODER, NetworksIntegrationMain.CRAFTING_GRID_RECIPE_SLOTS);
        rrc(ExpansionItems.NETWORK_CRAFTING_GRID_NEW_STYLE, NetworksIntegrationMain.CRAFTING_GRID_RECIPE_SLOTS);
    }

    @Override
    public void onDisable() {
        for (SlimefunItem slimefunItem : handledSlimefunItems) {
            RecipeCompletableListener.unregisterRecipeCompletable(slimefunItem);
        }
    }

    public static void rrc(SlimefunItem slimefunItem, int[] slots) {
        handledSlimefunItems.add(slimefunItem);
        RecipeCompletableListener.registerRecipeCompletable(slimefunItem, slots, false);
    }
}
