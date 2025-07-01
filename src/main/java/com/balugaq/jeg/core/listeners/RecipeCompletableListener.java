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
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.api.recipe_complete.source.base.RecipeCompleteProvider;
import com.balugaq.jeg.api.recipe_complete.source.base.SlimefunSource;
import com.balugaq.jeg.api.recipe_complete.source.base.VanillaSource;
import com.balugaq.jeg.implementation.items.ItemsSetup;
import com.balugaq.jeg.utils.ReflectionUtil;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import lombok.SneakyThrows;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * @author balugaq
 * @since 1.9
 */
public class RecipeCompletableListener implements Listener {
    public static final int[] DISPENSER_SLOTS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    public static final Map<UUID, GuideEvents.ItemButtonClickEvent> LAST_EVENTS = new ConcurrentHashMap<>();
    public static final Map<UUID, GuideHistory> GUIDE_HISTORY = new ConcurrentHashMap<>();
    public static final Map<UUID, BiConsumer<GuideEvents.ItemButtonClickEvent, PlayerProfile>> PROFILE_CALLBACKS =
            new ConcurrentHashMap<>();
    public static final Set<UUID> listening = ConcurrentHashMap.newKeySet();
    public static final Map<SlimefunItem, Pair<int[], Boolean>> INGREDIENT_SLOTS = new ConcurrentHashMap<>();
    public static final List<SlimefunItem> NOT_APPLICABLE_ITEMS = new ArrayList<>();
    public static final Map<UUID, Location> DISPENSER_LISTENING = new ConcurrentHashMap<>();

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

    public static void registerRecipeCompletable(SlimefunItem slimefunItem, int @NotNull [] slots) {
        registerRecipeCompletable(slimefunItem, slots, false);
    }

    public static void registerRecipeCompletable(SlimefunItem slimefunItem, int @NotNull [] slots, boolean unordered) {
        INGREDIENT_SLOTS.put(slimefunItem, new Pair<>(slots, unordered));
    }

    public static void unregisterRecipeCompletable(SlimefunItem slimefunItem) {
        INGREDIENT_SLOTS.remove(slimefunItem);
    }

    @SuppressWarnings("deprecation")
    private static void tryAddClickHandler(@NotNull BlockMenu blockMenu) {
        SlimefunItem sf = blockMenu.getPreset().getSlimefunItem();
        if (!isApplicable(sf)) {
            return;
        }

        if (!hasIngredientSlots(sf)) {
            return;
        }

        ChestMenu.MenuClickHandler old = blockMenu.getPlayerInventoryClickHandler();
        if (old instanceof TaggedRecipeCompletable) {
            // Already added
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
                    // Default strategy see {@link DefaultPlayerInventoryRecipeCompleteSlimefunSource}
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

    public static boolean isApplicable(@NotNull SlimefunItem slimefunItem) {
        if (slimefunItem instanceof NotApplicable) {
            return false;
        }

        if (NOT_APPLICABLE_ITEMS.contains(slimefunItem)) {
            return false;
        }

        // No idea yet.
        return true;
    }

    public static @NotNull ItemStack getRecipeCompletableBookItem() {
        return ItemsSetup.RECIPE_COMPLETE_GUIDE.getItem();
    }

    public static void addCallback(
            @NotNull UUID uuid, @NotNull BiConsumer<GuideEvents.ItemButtonClickEvent, PlayerProfile> callback) {
        PROFILE_CALLBACKS.put(uuid, callback);
    }

    public static void removeCallback(@NotNull UUID uuid) {
        PROFILE_CALLBACKS.remove(uuid);
    }

    @SneakyThrows
    @NotNull
    public static PlayerProfile getPlayerProfile(@NotNull OfflinePlayer player) {
        // Shouldn't be null;
        return PlayerProfile.find(player).orElseThrow(() -> new RuntimeException("PlayerProfile not found"));
    }

    public static void tagGuideOpen(@NotNull Player player) {
        if (!PROFILE_CALLBACKS.containsKey(player.getUniqueId())) {
            return;
        }

        PlayerProfile profile = getPlayerProfile(player);
        saveOriginGuideHistory(profile);
        clearGuideHistory(profile);
    }

    public static void saveOriginGuideHistory(@NotNull PlayerProfile profile) {
        GuideHistory oldHistory = profile.getGuideHistory();
        GuideHistory newHistory = new GuideHistory(profile);
        ReflectionUtil.setValue(newHistory, "mainMenuPage", oldHistory.getMainMenuPage());
        LinkedList<?> queue = ReflectionUtil.getValue(oldHistory, "queue", LinkedList.class);
        ReflectionUtil.setValue(newHistory, "queue", queue != null ? queue.clone() : new LinkedList<>());
        GUIDE_HISTORY.put(profile.getUUID(), newHistory);
    }

    public static void clearGuideHistory(@NotNull PlayerProfile profile) {
        ReflectionUtil.setValue(profile, "guideHistory", new GuideHistory(profile));
    }

    @Nullable
    public static GuideEvents.ItemButtonClickEvent getLastEvent(@NotNull UUID playerUUID) {
        return LAST_EVENTS.get(playerUUID);
    }

    public static void clearLastEvent(@NotNull UUID playerUUID) {
        LAST_EVENTS.remove(playerUUID);
    }

    @ParametersAreNonnullByDefault
    public static void addDispenserListening(UUID uuid, Location location) {
        DISPENSER_LISTENING.put(uuid, location);
    }

    @ParametersAreNonnullByDefault
    public static boolean isOpeningDispenser(UUID uuid) {
        return DISPENSER_LISTENING.containsKey(uuid);
    }

    @ParametersAreNonnullByDefault
    public static void removeDispenserListening(UUID uuid) {
        DISPENSER_LISTENING.remove(uuid);
    }

    @ParametersAreNonnullByDefault
    private static void tryAddVanillaListen(InventoryOpenEvent event, Block block, Inventory inventory) {
        addDispenserListening(event.getPlayer().getUniqueId(), block.getLocation());
    }

    public static void rollbackGuideHistory(@NotNull PlayerProfile profile) {
        GuideHistory originHistory = RecipeCompletableListener.GUIDE_HISTORY.get(profile.getUUID());
        if (originHistory == null) {
            return;
        }

        ReflectionUtil.setValue(profile, "guideHistory", originHistory);
    }

    @EventHandler
    public void prepare(@NotNull InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof BlockMenu blockMenu) {
            tryAddClickHandler(blockMenu);
        }

        if (event.getInventory().getHolder() instanceof Dispenser dispenser) {
            tryAddVanillaListen(event, dispenser.getBlock(), event.getInventory());
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void clickVanilla(@NotNull InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (event.getRawSlot() < inventory.getSize()) {
            return;
        }

        if (!(inventory.getHolder() instanceof Dispenser dispenser)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (!isOpeningDispenser(player.getUniqueId())) {
            return;
        }

        if (!SlimefunUtils.isItemSimilar(event.getCurrentItem(), getRecipeCompletableBookItem(), false, false, true, false)) {
            return;
        }

        Block block = dispenser.getBlock();
        ClickAction clickAction = new ClickAction(event.isRightClick(), event.isShiftClick());
        for (VanillaSource source : RecipeCompleteProvider.getVanillaSources()) {
            // Strategy mode
            // Default strategy see {@link DefaultPlayerInventoryRecipeCompleteVanillaSource}
            if (source.handleable(block, inventory, player, clickAction, DISPENSER_SLOTS, false)) {
                source.openGuide(block, inventory, player, clickAction, DISPENSER_SLOTS, false, null);
                break;
            }
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void exitVanilla(@NotNull InventoryOpenEvent event) {
        removeDispenserListening(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onJEGItemClick(GuideEvents.@NotNull ItemButtonClickEvent event) {
        Player player = event.getPlayer();
        if (!RecipeCompletableListener.PROFILE_CALLBACKS.containsKey(player.getUniqueId())) {
            return;
        }

        PlayerProfile profile = RecipeCompletableListener.getPlayerProfile(player);
        rollbackGuideHistory(profile);
        RecipeCompletableListener.PROFILE_CALLBACKS.get(player.getUniqueId()).accept(event, profile);
        RecipeCompletableListener.PROFILE_CALLBACKS.remove(player.getUniqueId());
        RecipeCompletableListener.LAST_EVENTS.put(player.getUniqueId(), event);
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
