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

package com.balugaq.jeg.api.filters;

import com.balugaq.jeg.api.interfaces.DisplayInCheatMode;
import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.utils.KeyUtil;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.LockedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SeasonalItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author balugaq
 * @since 1.8
 */
public class CheatGroupHandlerFactory {
    public static @NotNull GroupHandler getHandler(@NotNull ItemGroup group) {
        if (group.getClass().isAnnotationPresent(NotDisplayInCheatMode.class)
                || group.getClass().isAnnotationPresent(DisplayInCheatMode.class)) {
            return new AnnotationHandler();
        }
        if (group instanceof FlexItemGroup) {
            return new FlexGroupHandler();
        }
        return new DefaultGroupHandler();
    }

    /**
     * @author balugaq
     * @since 1.8
     */
    public static class FlexGroupHandler implements GroupHandler {
        @Override
        public void handle(@NotNull ItemGroup group, @NotNull Player p, @NotNull PlayerProfile profile, @NotNull List<ItemGroup> groups, @NotNull List<ItemGroup> specialGroups) {
            FlexItemGroup flexItemGroup = (FlexItemGroup) group;
            NamespacedKey key = group.getKey();
            String namespace = key.getNamespace();

            if (shouldSkipMainGroup(group, namespace)) {
                return;
            }

            if (shouldAddNestedGroup(group, namespace)) {
                groups.add(group);
                return;
            }

            if (flexItemGroup.isVisible(p, profile, SlimefunGuideMode.SURVIVAL_MODE)) {
                groups.add(group);
            } else if (shouldAddToSpecialGroups(flexItemGroup)) {
                specialGroups.add(group);
            }
        }

        private boolean shouldSkipMainGroup(@NotNull ItemGroup group, @NotNull String namespace) {
            String className = group.getClass().getName();
            return (namespace.equalsIgnoreCase("networks") || namespace.equalsIgnoreCase("networks-changed"))
                    && className.equalsIgnoreCase("com.balugaq.netex.api.groups.MainItemGroup")
                    || (namespace.equalsIgnoreCase("finaltech") || namespace.equalsIgnoreCase("finaltech-changed"))
                    && className.equalsIgnoreCase("io.taraxacum.finaltech.core.group.MainItemGroup")
                    || namespace.equalsIgnoreCase("mobengineering")
                    && className.equalsIgnoreCase("io.github.ytdd9527.mobengineering.implementation.slimefun.groups.MainItemGroup")
                    || namespace.equalsIgnoreCase("nexcavate")
                    && className.equalsIgnoreCase("me.char321.nexcavate.slimefun.NEItemGroup");
        }

        private boolean shouldAddNestedGroup(@NotNull ItemGroup group, @NotNull String namespace) {
            return (namespace.equalsIgnoreCase("networks") || namespace.equalsIgnoreCase("networks-changed")
                    || namespace.equalsIgnoreCase("finaltech") || namespace.equalsIgnoreCase("finaltech-changed")
                    || namespace.equalsIgnoreCase("mobengineering"))
                    && group instanceof NestedItemGroup;
        }

        private boolean shouldAddToSpecialGroups(@NotNull FlexItemGroup flexItemGroup) {
            String s = flexItemGroup.getClass().getName();
            return !s.startsWith("com.balugaq.netex.api.groups")
                    && !s.startsWith("io.taraxacum.finaltech.core.group")
                    && !s.equals("me.matl114.matlib.implement.slimefun.menu.menuGroup.CustomMenuGroup")
                    && !flexItemGroup.getKey().equals(KeyUtil.customKey("logitech", "info"))
                    && !s.equals("io.github.mooy1.infinityexpansion.categories.InfinityGroup")
                    && !s.equals("me.lucasgithuber.obsidianexpansion.utils.ObsidianForgeGroup")
                    && !s.equals("io.github.slimefunguguproject.bump.implementation.groups.AppraiseInfoGroup");
        }
    }

    /**
     * @author balugaq
     * @since 1.8
     */
    public static class DefaultGroupHandler implements GroupHandler {
        @Override
        public void handle(@NotNull ItemGroup group, @NotNull Player p, @NotNull PlayerProfile profile, @NotNull List<ItemGroup> groups, @NotNull List<ItemGroup> specialGroups) {
            if (!group.isVisible(p)) {
                groups.add(group);
            } else if (group instanceof SeasonalItemGroup || group instanceof LockedItemGroup) {
                specialGroups.add(group);
            } else {
                handleSpecialSubGroups(group, specialGroups);
            }
        }

        private void handleSpecialSubGroups(@NotNull ItemGroup group, @NotNull List<ItemGroup> specialGroups) {
            String className = group.getClass().getName();
            String key = group.getKey().getKey();

            if (className.equalsIgnoreCase("io.github.mooy1.infinityexpansion.infinitylib.groups.SubGroup")
                    && key.equalsIgnoreCase("infinity_cheat")) {
                specialGroups.add(group);
            } else if (className.equalsIgnoreCase("me.lucasgithuber.obsidianexpansion.infinitylib.groups.SubGroup")
                    && key.equalsIgnoreCase("omc_forge_cheat")) {
                specialGroups.add(group);
            } else if (className.startsWith("io.github.sefiraat.networks.slimefun.NetworksItemGroups")
                    && key.equalsIgnoreCase("disabled_items")) {
                specialGroups.add(group);
            }
        }
    }

    /**
     * @author balugaq
     * @since 1.8
     */
    public static class AnnotationHandler implements GroupHandler {
        @Override
        public void handle(@NotNull ItemGroup group, @NotNull Player p, @NotNull PlayerProfile profile, @NotNull List<ItemGroup> groups, @NotNull List<ItemGroup> specialGroups) {
            if (group.getClass().isAnnotationPresent(NotDisplayInCheatMode.class)) {
                return;
            }
            if (group.getClass().isAnnotationPresent(DisplayInCheatMode.class)) {
                groups.add(group);
            }
        }
    }
}
