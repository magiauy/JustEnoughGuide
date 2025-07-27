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

package com.balugaq.jeg.api.groups;

import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.api.interfaces.NotDisplayInSurvivalMode;
import com.balugaq.jeg.api.objects.events.RTSEvents;
import com.balugaq.jeg.core.listeners.GuideListener;
import com.balugaq.jeg.core.listeners.RTSListener;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.Lang;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.Getter;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.view.AnvilView;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author balugaq
 * @since 1.3
 */
@SuppressWarnings("unused")
@NotDisplayInSurvivalMode
@NotDisplayInCheatMode
@Getter
public class RTSSearchGroup extends FlexItemGroup {
    public static final ItemStack PLACEHOLDER = Converter.getItem(
            Converter.getItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "&a", "&a", "&a"),
            meta -> meta.getPersistentDataContainer()
                    .set(RTSListener.FAKE_ITEM_KEY, PersistentDataType.STRING, "____JEG_FAKE_ITEM____"));
    // Use RTS_SEARCH_GROUPS, RTS_PAGES, RTS_PLAYERS or RTS_SEARCH_TERMS must be by keyword "synchronized"
    public static final Map<Player, SearchGroup> RTS_SEARCH_GROUPS = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> RTS_PAGES = new ConcurrentHashMap<>();
    public static final Map<Player, AnvilInventory> RTS_PLAYERS = new ConcurrentHashMap<>();
    public static final Map<Player, String> RTS_SEARCH_TERMS = new ConcurrentHashMap<>();
    public static final Function<Player, ItemStack> BACK_ICON =
            (player) -> ChestMenuUtils.getBackButton(player, "", "&fLeft click: &7Return to previous page", "&fShift + Left click: &7Return to main menu");
    public static final ItemStack INPUT_TEXT_ICON = Lang.INPUT_TEXT_ICON;
    public static final ItemStack AIR_ICON = new ItemStack(Material.AIR);
    private static final JavaPlugin JAVA_PLUGIN = JustEnoughGuide.getInstance();

    static {
        Bukkit.getScheduler()
                .runTaskTimer(
                        SearchGroup.JAVA_PLUGIN,
                        () -> {
                            Map<Player, AnvilInventory> copy;
                            synchronized (RTS_PLAYERS) {
                                copy = new HashMap<>(RTS_PLAYERS);
                            }

                            Map<Player, String> searchTermCopy;
                            synchronized (RTS_SEARCH_TERMS) {
                                searchTermCopy = new HashMap<>(RTS_SEARCH_TERMS);
                            }

                            Map<Player, String> writes = new HashMap<>();
                            copy.forEach((player, inventory) -> {
                                if (inventory == null) {
                                    return;
                                }
                                InventoryView view = player.getOpenInventory();
                                Inventory openingInventory = view.getTopInventory();
                                if (openingInventory instanceof AnvilInventory anvilInventory
                                        && openingInventory.equals(inventory)) {
                                    String oldSearchTerm = searchTermCopy.get(player);
                                    try {
                                        String newSearchTerm = null;
                                        
                                        // Try Paper 1.21+ AnvilView method first
                                        try {
                                            if (view instanceof AnvilView anvilView) {
                                                newSearchTerm = anvilView.getRenameText();
                                            }
                                        } catch (NoClassDefFoundError | NoSuchMethodError e) {
                                            // AnvilView not available on this server version
                                        }
                                        
                                        // Fallback to legacy AnvilInventory method if AnvilView failed
                                        if (newSearchTerm == null) {
                                            try {
                                                newSearchTerm = anvilInventory.getRenameText();
                                            } catch (NoSuchMethodError e) {
                                                Debug.debug("Both AnvilView and AnvilInventory getRenameText() methods are unavailable");
                                                return;
                                            }
                                        }
                                        
                                        if (oldSearchTerm == null || newSearchTerm == null) {
                                            writes.put(player, newSearchTerm);
                                            return;
                                        }

                                        if (!oldSearchTerm.equals(newSearchTerm)) {
                                            writes.put(player, newSearchTerm);
                                            RTSEvents.SearchTermChangeEvent event = new RTSEvents.SearchTermChangeEvent(
                                                    player,
                                                    view,
                                                    anvilInventory,
                                                    oldSearchTerm,
                                                    newSearchTerm,
                                                    GuideListener.guideModeMap.get(player));
                                            Bukkit.getPluginManager().callEvent(event);
                                        }
                                    } catch (Exception e) {
                                        Debug.trace(e);
                                    }
                                }
                            });

                            writes.forEach((player, searchTerm) -> {
                                if (player != null && searchTerm != null) {
                                    synchronized (RTS_SEARCH_TERMS) {
                                        RTS_SEARCH_TERMS.put(player, searchTerm);
                                    }
                                }
                            });
                        },
                        1,
                        4);
    }

    private final AnvilInventory anvilInventory;
    private final String presetSearchTerm;
    private final int page;

    /**
     * Creates a new RTS search group for a player with a specific preset search term.
     *
     * @param anvilInventory   The anvil inventory used for the search.
     * @param presetSearchTerm The preset search term to initialize the search with.
     */
    public RTSSearchGroup(AnvilInventory anvilInventory, String presetSearchTerm) {
        this(anvilInventory, presetSearchTerm, 1);
    }

    /**
     * Creates a new RTS search group for a player with a specific preset search term and page.
     *
     * @param anvilInventory   The anvil inventory used for the search.
     * @param presetSearchTerm The preset search term to initialize the search with.
     * @param page             The initial page number for the search results.
     */
    public RTSSearchGroup(AnvilInventory anvilInventory, String presetSearchTerm, int page) {
        super(
                new NamespacedKey(JAVA_PLUGIN, "jeg_rts_search_group_" + UUID.randomUUID()),
                new ItemStack(Material.BARRIER));
        this.anvilInventory = anvilInventory;
        this.presetSearchTerm = presetSearchTerm;
        this.page = page;
    }

    /**
     * Creates a new RTS inventory for a player in a specific guide mode.
     *
     * @param player    The player for whom the inventory is created.
     * @param guideMode The guide mode (e.g., CHEAT_MODE, SURVIVAL_MODE).
     * @return The created inventory.
     */
    public static Inventory newRTSInventoryFor(Player player, SlimefunGuideMode guideMode) {
        return newRTSInventoryFor(player, guideMode, null);
    }

    public static Inventory newRTSInventoryFor(Player player, SlimefunGuideMode guideMode, String presetSearchTerm) {
        return newRTSInventoryFor(player, guideMode, null, null, presetSearchTerm);
    }

    public static Inventory newRTSInventoryFor(
            Player player,
            SlimefunGuideMode guideMode,
            @Nullable BiConsumer<Integer, AnvilGUI.StateSnapshot> clickHandler,
            int @Nullable [] slots,
            @Nullable String presetSearchTerm) {
        AnvilGUI.Builder builder = new AnvilGUI.Builder()
                .plugin(SearchGroup.JAVA_PLUGIN)
                .itemLeft(BACK_ICON.apply(player))
                .itemRight(Lang.INPUT_TEXT_ICON)
                .itemOutput(AIR_ICON)
                .text("")
                .title(Lang.getGuideMessage("rts-title"))
                .onClose((stateSnapshot) -> {
                    RTSEvents.CloseRTSEvent event = new RTSEvents.CloseRTSEvent(player, stateSnapshot, guideMode);
                    Bukkit.getPluginManager().callEvent(event);
                });
        if (clickHandler != null) {
            builder.onClickAsync((slot, stateSnapshot) -> CompletableFuture.supplyAsync(() -> {
                if (slots != null) {
                    for (int s : slots) {
                        if (s == slot) {
                            return List.of(AnvilGUI.ResponseAction.run(() -> {
                                RTSEvents.ClickAnvilItemEvent event =
                                        new RTSEvents.ClickAnvilItemEvent(player, stateSnapshot, slot, guideMode);
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCancelled()) {
                                    clickHandler.accept(s, stateSnapshot);
                                }
                            }));
                        }
                    }
                }
                return Collections.emptyList();
            }));
        } else {
            builder.onClickAsync((slot, stateSnapshot) -> CompletableFuture.supplyAsync(Collections::emptyList));
        }

        if (presetSearchTerm != null) {
            builder.text(presetSearchTerm);
        }

        Inventory inventory = builder.open(player).getInventory();
        if (inventory instanceof AnvilInventory anvilInventory) {
            RTSEvents.OpenRTSEvent event =
                    new RTSEvents.OpenRTSEvent(player, anvilInventory, guideMode, presetSearchTerm);
            Bukkit.getPluginManager().callEvent(event);
        }
        return inventory;
    }

    @Override
    public boolean isVisible(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
        return false;
    }

    /**
     * Opens the RTS search group for a player with a specific guide mode.
     *
     * @param player            The player who is opening the search group.
     * @param playerProfile     The player profile associated with the player.
     * @param slimefunGuideMode The guide mode (e.g., CHEAT_MODE, SURVIVAL_MODE).
     */
    @Override
    public void open(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
        newRTSInventoryFor(
                player,
                slimefunGuideMode,
                (s, stateSnapshot) -> {
                    if (s == AnvilGUI.Slot.INPUT_LEFT) {
                        PlayerProfile profile = PlayerProfile.find(player).orElse(null);
                        if (profile == null) {
                            return;
                        }
                        // back button clicked
                        GuideHistory history = profile.getGuideHistory();
                        history.goBack(Slimefun.getRegistry().getSlimefunGuide(slimefunGuideMode));
                    } else if (s == AnvilGUI.Slot.INPUT_RIGHT) {
                        // previous page button clicked
                        SearchGroup rts = RTS_SEARCH_GROUPS.get(player);
                        if (rts != null) {
                            int oldPage = RTS_PAGES.getOrDefault(player, 1);
                            int newPage = Math.max(1, oldPage - 1);
                            RTSEvents.PageChangeEvent event = new RTSEvents.PageChangeEvent(
                                    player, RTS_PLAYERS.get(player), oldPage, newPage, slimefunGuideMode);
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                synchronized (RTS_PAGES) {
                                    RTS_PAGES.put(player, newPage);
                                }
                            }
                        }
                    } else if (s == AnvilGUI.Slot.OUTPUT) {
                        // next page button clicked
                        SearchGroup rts = RTS_SEARCH_GROUPS.get(player);
                        if (rts != null) {
                            int oldPage = RTS_PAGES.getOrDefault(player, 1);
                            int newPage = Math.min(
                                    (rts.slimefunItemList.size() - 1) / RTSListener.FILL_ORDER.length + 1, oldPage + 1);
                            RTSEvents.PageChangeEvent event = new RTSEvents.PageChangeEvent(
                                    player, RTS_PLAYERS.get(player), oldPage, newPage, slimefunGuideMode);
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                synchronized (RTS_PAGES) {
                                    RTS_PAGES.put(player, newPage);
                                }
                            }
                        }
                    }
                },
                new int[]{AnvilGUI.Slot.INPUT_LEFT, AnvilGUI.Slot.INPUT_RIGHT, AnvilGUI.Slot.OUTPUT},
                presetSearchTerm);
        synchronized (RTS_PAGES) {
            RTS_PAGES.put(player, this.page);
        }
        RTSEvents.PageChangeEvent event =
                new RTSEvents.PageChangeEvent(player, RTS_PLAYERS.get(player), page, page, slimefunGuideMode);
        Bukkit.getPluginManager().callEvent(event);
    }
}
