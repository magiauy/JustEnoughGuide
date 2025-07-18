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

package com.balugaq.jeg.core.integrations.emctech;

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
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"UnnecessaryUnicodeEscape", "SameReturnValue"})
public class EMCValueDisplayOption implements SlimefunGuideOption<Boolean> {
    public static final @NotNull EMCValueDisplayOption instance = new EMCValueDisplayOption();

    public static @NotNull EMCValueDisplayOption instance() {
        return instance;
    }

    public static @NotNull NamespacedKey key0() {
        return new NamespacedKey(JustEnoughGuide.getInstance(), "emc_item");
    }

    public static boolean isEnabled(@NotNull Player p) {
        return getSelectedOption(p);
    }

    public static boolean getSelectedOption(@NotNull Player p) {
        return !PersistentDataAPI.hasByte(p, key0()) || PersistentDataAPI.getByte(p, key0()) == (byte) 1;
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
        boolean enabled = getSelectedOption(p, guide).orElse(true);
        ItemStack item = Converter.getItem(
                isEnabled(p) ? Material.CARTOGRAPHY_TABLE : Material.CRAFTING_TABLE,
                "&bEMC value display: &" + (enabled ? "aEnabled" : "4Disabled"),
                "",
                "&7You can now choose whether to",
                "&7display the EMC value of a new item",
                "&7when viewing it.",
                "",
                "&7Note: This EMC value is from EMCTech",
                "&7and is not equivalent to the values of",
                "&7other mods like Randomized or others.",
                "&7\u21E8 &eClick " + (enabled ? "Disable" : "Enable") + " EMC value display");
        return Optional.of(item);
    }

    @Override
    public void onClick(@NotNull Player p, @NotNull ItemStack guide) {
        setSelectedOption(p, guide, !getSelectedOption(p, guide).orElse(true));
        JEGGuideSettings.openSettings(p, guide);
    }

    @Override
    public @NotNull Optional<Boolean> getSelectedOption(@NotNull Player p, ItemStack guide) {
        NamespacedKey key = getKey();
        boolean value = !PersistentDataAPI.hasByte(p, key) || PersistentDataAPI.getByte(p, key) == (byte) 1;
        return Optional.of(value);
    }

    @Override
    public void setSelectedOption(@NotNull Player p, ItemStack guide, Boolean value) {
        PersistentDataAPI.setByte(p, getKey(), value ? (byte) 1 : (byte) 0);
    }
}
