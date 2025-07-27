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
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideOption;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * This class is used to represent the option to show the beginner's guide.
 * which is editable in the settings menu.
 *
 * @author balugaq
 * @since 1.5
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

    public static int getSettings(@NotNull Player p) {
        return getSelectedOption(p);
    }

    public static int getSelectedOption(@NotNull Player p) {
        return PersistentDataAPI.getInt(p, key0());
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
        ItemStack item = Converter.getItem(
                Material.FURNACE,
                "&a配方补全深度",
                "&7配方补全深度越大，需要的时间越长",
                "&7如果遇到一个材料不存在，会尝试补全",
                "&7这个材料的材料，以此类推，此过程视为一层深度",
                "",
                "&7当前深度: " + value + " (限制范围: 1~16)"
        );
        return Optional.of(item);
    }

    @Override
    public void onClick(@NotNull Player p, @NotNull ItemStack guide) {
        setSelectedOption(p, guide, getSelectedOption(p, guide).orElse(1));
        JEGGuideSettings.openSettings(p, guide);
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
