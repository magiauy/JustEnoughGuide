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

package com.balugaq.jeg.core.listeners;

import com.balugaq.jeg.api.objects.collection.Pair;
import com.balugaq.jeg.api.recipe_complete.source.base.RecipeCompleteProvider;
import com.balugaq.jeg.api.recipe_complete.source.base.SlimefunSource;
import com.balugaq.jeg.implementation.items.ItemsSetup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RecipeCompletableListener implements Listener {
    private static final Set<UUID> listening = ConcurrentHashMap.newKeySet();
    private static final Map<SlimefunItem, Pair<int[], Boolean>> INGREDIENT_SLOTS = new ConcurrentHashMap<>();
    private static final List<SlimefunItem> NOT_APPLICABLE_ITEMS = new ArrayList<>();

    /**
     * @param slimefunItem the {@link SlimefunItem} to add
     * @see NotApplicable
     */
    public static void addNotApplicableItem(SlimefunItem slimefunItem) {
        NOT_APPLICABLE_ITEMS.add(slimefunItem);
    }

    /**
     * @param slimefunItem the {@link SlimefunItem} to remove
     * @see NotApplicable
     */
    public static void removeNotApplicableItem(SlimefunItem slimefunItem) {
        NOT_APPLICABLE_ITEMS.remove(slimefunItem);
    }

    public static void registerRecipeCompletable(SlimefunItem slimefunItem, int[] slots) {
        registerRecipeCompletable(slimefunItem, slots, false);
    }

    public static void registerRecipeCompletable(SlimefunItem slimefunItem, int[] slots, boolean unordered) {
        INGREDIENT_SLOTS.put(slimefunItem, new Pair<>(slots, unordered));
    }

    public static void unregisterRecipeCompletable(SlimefunItem slimefunItem) {
        INGREDIENT_SLOTS.remove(slimefunItem);
    }

    @SuppressWarnings("deprecation")
    private static void tryAddClickHandler(BlockMenu blockMenu) {
        SlimefunItem sf = blockMenu.getPreset().getSlimefunItem();
        if (!isApplicable(sf)) {
            return;
        }

        if (!hasIngredientSlots(sf)) {
            return;
        }

        ChestMenu.MenuClickHandler old = blockMenu.getPlayerInventoryClickHandler();
        if (old instanceof TaggedRecipeCompletable) {
            return;
        }

        blockMenu.addPlayerInventoryClickHandler((RecipeCompletableClickHandler) (player, slot, itemStack, clickAction) -> {
            // mixin start
            if (SlimefunUtils.isItemSimilar(itemStack, getRecipeCompletableBookItem(), false, false, true, false) && blockMenu.isPlayerInventoryClickable()) {
                if (listening.contains(player.getUniqueId())) {
                    return false;
                }

                listening.add(player.getUniqueId());
                int[] slots = getIngredientSlots(sf);
                boolean unordered = isUnordered(sf);
                for (SlimefunSource source : RecipeCompleteProvider.getSlimefunSources()) {
                    // Strategy mode
                    // Default strategy see {@link DefaultPlayerInventoryRecipeCompleteSource}
                    if (source.handleable(blockMenu, player, clickAction, slots, unordered)) {
                        source.openGuide(blockMenu, player, clickAction, slots, unordered, () -> {
                            listening.remove(player.getUniqueId());
                        });
                        break;
                    }
                }

                return false;
            }
            // mixin end

            if (old != null) {
                return old.onClick(player, slot, itemStack, clickAction);
            }

            return true;
        });
    }

    public static boolean hasIngredientSlots(@NotNull SlimefunItem slimefunItem) {
        return INGREDIENT_SLOTS.containsKey(slimefunItem);
    }

    public static int @NotNull [] getIngredientSlots(@NotNull SlimefunItem slimefunItem) {
        return Optional.ofNullable(INGREDIENT_SLOTS.get(slimefunItem)).orElse(new Pair<>(new int[0], false)).first();
    }

    public static boolean isUnordered(@NotNull SlimefunItem slimefunItem) {
        return Optional.ofNullable(INGREDIENT_SLOTS.get(slimefunItem)).orElse(new Pair<>(new int[0], false)).second();
    }

    private static boolean isApplicable(@NotNull SlimefunItem slimefunItem) {
        if (slimefunItem instanceof NotApplicable) {
            return false;
        }

        if (NOT_APPLICABLE_ITEMS.contains(slimefunItem)) {
            return false;
        }

        // No idea yet.
        return true;
    }

    public static ItemStack getRecipeCompletableBookItem() {
        return ItemsSetup.RECIPE_COMPLETE_GUIDE.getItem();
    }

    @EventHandler
    public void prepare(InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof BlockMenu blockMenu) {
            tryAddClickHandler(blockMenu);
        }

        if (event.getInventory().getHolder() instanceof Dispenser dispenser) {
            // todo
        }
    }

    /**
     * @author balugaq
     * @see RecipeCompletableListener#addNotApplicableItem(SlimefunItem)
     * @since 1.9
     */
    public interface NotApplicable {
    }

    /**
     * @author balugaq
     * @since 1.9
     */
    public interface TaggedRecipeCompletable {
    }

    /**
     * @author balugaq
     * @since 1.9
     */
    @SuppressWarnings("deprecation")
    @FunctionalInterface
    public interface RecipeCompletableClickHandler extends ChestMenu.MenuClickHandler, TaggedRecipeCompletable {
    }
}
