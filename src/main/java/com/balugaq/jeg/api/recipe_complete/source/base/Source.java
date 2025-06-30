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

package com.balugaq.jeg.api.recipe_complete.source.base;

import com.balugaq.netex.api.data.SimpleRecipeChoice;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
public interface Source {
    JavaPlugin plugin();

    default @Nullable List<RecipeChoice> getRecipe(@NotNull ItemStack itemStack) {
        SlimefunItem sf = SlimefunItem.getByItem(itemStack);
        if (sf != null) {
            List<RecipeChoice> raw = new ArrayList<>(Arrays.stream(sf.getRecipe())
                    .map(item -> item == null ? null : new SimpleRecipeChoice(item))
                    .toList());
            if (raw.size() < 9) {
                for (int i = raw.size(); i < 9; i++) {
                    raw.add(null);
                }
            }

            return raw;
        } else {
            Recipe[] recipes = Slimefun.getMinecraftRecipeService().getRecipesFor(itemStack);
            for (Recipe recipe : recipes) {
                if (recipe instanceof ShapedRecipe shapedRecipe) {
                    List<RecipeChoice> choices = new ArrayList<>(9);
                    String[] shape = shapedRecipe.getShape();

                    for (int i = 0; i < 3; i++) {
                        String line = i < shape.length ? shape[i] : "   ";
                        for (int j = 0; j < 3; j++) {
                            if (j >= line.length()) {
                                choices.add(null);
                            } else {
                                choices.add(shapedRecipe.getChoiceMap().get(line.charAt(j)));
                            }
                        }
                    }

                    if (choices.size() < 9) {
                        for (int i = choices.size(); i < 9; i++) {
                            choices.add(null);
                        }
                    }

                    return choices;
                } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
                    List<RecipeChoice> raw = new ArrayList<>(shapelessRecipe.getChoiceList());
                    if (raw.size() < 9) {
                        for (int i = raw.size(); i < 9; i++) {
                            raw.add(null);
                        }
                    }

                    return raw;
                }
            }
        }

        return null;
    }

    @ParametersAreNonnullByDefault
    default @Nullable ItemStack getItemStackFromPlayerInventory(Player player, ItemStack itemStack) {
        // get from player inventory
        for (ItemStack itemStack1 : player.getInventory().getContents()) {
            if (itemStack1 != null && itemStack1.getType() != Material.AIR) {
                if (StackUtils.itemsMatch(itemStack1, itemStack, true, false)) {
                    ItemStack clone = StackUtils.getAsQuantity(itemStack1, 1);
                    int newAmount = itemStack1.getAmount() - 1;

                    itemStack1.setAmount(newAmount);
                    if (newAmount == 0) {
                        itemStack1.setType(Material.AIR);
                    }

                    return clone;
                }
            }
        }
        return null;
    }
}
