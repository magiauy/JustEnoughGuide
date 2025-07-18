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
import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.api.objects.events.PatchEvent;
import com.balugaq.jeg.api.recipe_complete.source.base.RecipeCompleteProvider;
import com.balugaq.jeg.api.recipe_complete.source.base.SlimefunSource;
import com.balugaq.jeg.api.recipe_complete.source.base.VanillaSource;
import com.balugaq.jeg.implementation.items.ItemsSetup;
import com.balugaq.jeg.utils.KeyUtil;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.StackUtils;
import com.balugaq.jeg.utils.Lang;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.ItemUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.SneakyThrows;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings("unused")
public class RecipeCompletableListener implements Listener {
    public static final int[] DISPENSER_SLOTS = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8};
    public static final Map<UUID, GuideEvents.ItemButtonClickEvent> LAST_EVENTS = new ConcurrentHashMap<>();
    public static final Map<UUID, GuideHistory> GUIDE_HISTORY = new ConcurrentHashMap<>();
    public static final Map<UUID, BiConsumer<GuideEvents.ItemButtonClickEvent, PlayerProfile>> PROFILE_CALLBACKS =
            new ConcurrentHashMap<>();
    public static final Set<UUID> listening = ConcurrentHashMap.newKeySet();
    public static final Map<SlimefunItem, Pair<int[], Boolean>> INGREDIENT_SLOTS = new ConcurrentHashMap<>();
    public static final List<SlimefunItem> NOT_APPLICABLE_ITEMS = new ArrayList<>();
    public static final Map<UUID, Location> DISPENSER_LISTENING = new ConcurrentHashMap<>();
    public static final NamespacedKey LAST_RECIPE_COMPLETE_KEY = KeyUtil.newKey("last_recipe_complete");
    private static ItemStack RECIPE_COMPLETABLE_BOOK_ITEM = null;

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

        blockMenu.addPlayerInventoryClickHandler(
                (RecipeCompletableClickHandler) (player, slot, itemStack, clickAction) -> {
                    // mixin start
                    if (StackUtils.itemsMatch(itemStack, getRecipeCompletableBookItem(), false, false, false, false)
                            && blockMenu.isPlayerInventoryClickable()) {
                        if (isSelectingItemStackToRecipeComplete(player)) {
                            return false;
                        }

                        enterSelectingItemStackToRecipeComplete(player);
                        int[] slots = getIngredientSlots(sf);
                        boolean unordered = isUnordered(sf);
                        for (SlimefunSource source : RecipeCompleteProvider.getSlimefunSources()) {
                            // Strategy mode
                            // Default strategy see {@link DefaultPlayerInventoryRecipeCompleteSlimefunSource}
                            if (source.handleable(blockMenu, player, clickAction, slots, unordered)) {
                                source.openGuide(
                                        blockMenu,
                                        player,
                                        clickAction,
                                        slots,
                                        unordered,
                                        () -> exitSelectingItemStackToRecipeComplete(player));
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
        return Optional.ofNullable(INGREDIENT_SLOTS.get(slimefunItem))
                .orElse(new Pair<>(new int[0], false))
                .first();
    }

    public static boolean isUnordered(@NotNull SlimefunItem slimefunItem) {
        return Optional.ofNullable(INGREDIENT_SLOTS.get(slimefunItem))
                .orElse(new Pair<>(new int[0], false))
                .second();
    }

    @SuppressWarnings("RedundantIfStatement")
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
        if (RECIPE_COMPLETABLE_BOOK_ITEM == null) {
            RECIPE_COMPLETABLE_BOOK_ITEM =
                    ItemsSetup.RECIPE_COMPLETE_GUIDE.getItem().clone();
        }

        return RECIPE_COMPLETABLE_BOOK_ITEM;
    }

    public static void addCallback(
            final @NotNull UUID uuid, @NotNull BiConsumer<GuideEvents.ItemButtonClickEvent, PlayerProfile> callback) {
        PROFILE_CALLBACKS.put(uuid, callback);
    }

    public static void removeCallback(@NotNull UUID uuid) {
        PROFILE_CALLBACKS.remove(uuid);
    }

    @SneakyThrows
    @NotNull public static PlayerProfile getPlayerProfile(@NotNull OfflinePlayer player) {
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

    @Nullable public static GuideEvents.ItemButtonClickEvent getLastEvent(@NotNull UUID playerUUID) {
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

    @SuppressWarnings({"deprecation", "DuplicateCondition", "ConstantValue", "SizeReplaceableByIsEmpty"})
    private static void tryPatchRecipeCompleteBook(@NotNull Player player, @NotNull ItemStack clickedItemStack) {
        for (ItemStack itemStack : player.getInventory()) {
            if (StackUtils.itemsMatch(itemStack, getRecipeCompletableBookItem(), false, false, false, false)) {
                ItemMeta meta = itemStack.getItemMeta();
                if (meta == null) {
                    continue;
                }

                List<String> lore = meta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }

                // Patch start
                boolean applied = meta.getPersistentDataContainer().has(LAST_RECIPE_COMPLETE_KEY);
                if (lore.size() >= 2 && applied) {
                    // Remove last two lines
                    if (lore.size() >= 2) {
                        lore.remove(lore.size() - 1);
                    }
                    if (lore.size() >= 1) {
                        lore.remove(lore.size() - 1);
                    }
                }

                String itemName = ItemUtils.getItemName(clickedItemStack);
                lore.add("");
                lore.add(ChatColors.color(Lang.getMessage("guide.recipe-complete-last-completed") + itemName));

                if (!applied) {
                    meta.getPersistentDataContainer().set(LAST_RECIPE_COMPLETE_KEY, PersistentDataType.BOOLEAN, true);
                }

                // Patch end

                meta.setLore(lore);
                itemStack.setItemMeta(meta);
                return;
            }
        }
    }

    @SuppressWarnings({"deprecation", "DuplicateCondition", "ConstantValue", "SizeReplaceableByIsEmpty"})
    private static void tryRemoveRecipeCompleteBookLastRecipeCompleteLore(@NotNull Player player) {
        for (ItemStack itemStack : player.getInventory()) {
            if (StackUtils.itemsMatch(itemStack, getRecipeCompletableBookItem(), false, false, false, false)) {
                ItemMeta meta = itemStack.getItemMeta();
                if (meta == null) {
                    continue;
                }

                List<String> lore = meta.getLore();
                if (lore == null) {
                    continue;
                }

                // Patch start
                boolean applied = meta.getPersistentDataContainer().has(LAST_RECIPE_COMPLETE_KEY);
                if (lore.size() >= 2 && applied) {
                    // Remove last two lines
                    if (lore.size() >= 2) {
                        lore.remove(lore.size() - 1);
                    }
                    if (lore.size() >= 1) {
                        lore.remove(lore.size() - 1);
                    }
                }

                meta.getPersistentDataContainer().set(LAST_RECIPE_COMPLETE_KEY, PersistentDataType.BOOLEAN, false);
                // Patch end

                meta.setLore(lore);
                itemStack.setItemMeta(meta);
            }
        }
    }

    public static boolean isSelectingItemStackToRecipeComplete(@NotNull Player player) {
        return listening.contains(player.getUniqueId());
    }

    public static void enterSelectingItemStackToRecipeComplete(@NotNull Player player) {
        listening.add(player.getUniqueId());
    }

    public static void exitSelectingItemStackToRecipeComplete(@NotNull Player player) {
        listening.remove(player.getUniqueId());
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

        if (!StackUtils.itemsMatch(
                event.getCurrentItem(), getRecipeCompletableBookItem(), false, false, false, false)) {
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

        ItemStack clickedItemStack = event.getClickedItem();
        if (clickedItemStack != null) {
            tryPatchRecipeCompleteBook(player, clickedItemStack);
        }
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        tryRemoveRecipeCompleteBookLastRecipeCompleteLore(event.getPlayer());
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.NORMAL)
    public void patchItem(@NotNull PatchEvent event) {
        PatchScope scope = event.getPatchScope();
        if (scope != PatchScope.SlimefunItem && scope != PatchScope.SearchItem) {
            return;
        }

        if (isSelectingItemStackToRecipeComplete(event.getPlayer())) {
            ItemStack old = event.getItemStack();
            if (old == null || old.getType() == Material.AIR) {
                return;
            }

            ItemMeta meta = old.getItemMeta();
            if (meta == null) {
                return;
            }

            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }

            // Patch start
            lore.add("");
            lore.add(ChatColors.color(Lang.getMessage("guide.recipe-complete-left-click")));
            lore.add(ChatColors.color(Lang.getMessage("guide.recipe-complete-right-click")));
            // Patch end

            meta.setLore(lore);
            old.setItemMeta(meta);
            event.setItemStack(old);
        }
    }

    /**
     * @author balugaq
     * @see RecipeCompletableListener#addNotApplicableItem(SlimefunItem)
     * @since 1.9
     */
    public interface NotApplicable {}

    /**
     * @author balugaq
     * @since 1.9
     */
    public interface TaggedRecipeCompletable {}

    /**
     * @author balugaq
     * @since 1.9
     */
    @SuppressWarnings("deprecation")
    @FunctionalInterface
    public interface RecipeCompletableClickHandler extends ChestMenu.MenuClickHandler, TaggedRecipeCompletable {}
}
