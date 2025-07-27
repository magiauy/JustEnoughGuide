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

package com.balugaq.jeg.implementation.option;

import com.balugaq.jeg.api.patches.JEGGuideSettings;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.compatibility.Converter;
import com.balugaq.jeg.utils.Lang;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideOption;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

// todo
/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"UnnecessaryUnicodeEscape", "SameReturnValue"})
public class RecursiveRecipeFillingGuideOption implements SlimefunGuideOption<Integer> {
    public static final @NotNull RecursiveRecipeFillingGuideOption instance = new RecursiveRecipeFillingGuideOption();

    public static @NotNull RecursiveRecipeFillingGuideOption instance() {
        return instance;
    }

    public static @NotNull NamespacedKey key0() {
        return new NamespacedKey(JustEnoughGuide.getInstance(), "recursive_recipe_filling");
    }

    public static int getSelectedOption(@NotNull Player p) {
        return PersistentDataAPI.getInt(p, key0(), 1);
    }

    @Override
    public @NotNull SlimefunAddon getAddon() {
        return JustEnoughGuide.getInstance();
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key0();
    }

    @Override
    public @NotNull Optional<ItemStack> getDisplayItem(@NotNull Player p, ItemStack guide) {
        int value = getSelectedOption(p, guide).orElse(1);
        
        // Use language manager instead of hardcoded Chinese text
        String name = Lang.getString("icon.options.recursive-recipe-filling.name");
        List<String> lore = Lang.getStringList("icon.options.recursive-recipe-filling.lore");
        
        // Replace {0} placeholder with current depth value
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, lore.get(i).replace("{0}", String.valueOf(value)));
        }
        
        ItemStack item = Converter.getItem(Material.FURNACE, name, lore);
        return Optional.of(item);
    }

    @Override
    public void onClick(@NotNull Player p, @NotNull ItemStack guide) {
        ChatInput.waitForPlayer(JustEnoughGuide.getInstance(), p, s -> {
            try {
                int value = Integer.parseInt(s);
                if (value < 1 || value > 16) {
                    String errorMessage = Lang.getString("icon.options.recursive-recipe-filling.input-error");
                    p.sendMessage(errorMessage);
                    return;
                }

                setSelectedOption(p, guide, value);
                JEGGuideSettings.openSettings(p, guide);
            } catch (NumberFormatException ignored) {
                String errorMessage = Lang.getString("icon.options.recursive-recipe-filling.input-error");
                p.sendMessage(errorMessage);
            }
        });
    }

    @Override
    public @NotNull Optional<Integer> getSelectedOption(@NotNull Player p, ItemStack guide) {
        return Optional.of(getSelectedOption(p));
    }

    @Override
    public void setSelectedOption(@NotNull Player p, ItemStack guide, Integer value) {
        PersistentDataAPI.setInt(p, getKey(), value);
    }
}
