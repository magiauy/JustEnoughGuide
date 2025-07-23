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

import city.norain.slimefun4.VaultIntegration;
import com.balugaq.jeg.api.interfaces.BookmarkRelocation;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.api.interfaces.NotDisplayInSurvivalMode;
import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.EventUtil;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.JEGVersionedItemFlag;
import com.balugaq.jeg.utils.LocalHelper;
import com.balugaq.jeg.utils.compatibility.Converter;
import com.balugaq.jeg.utils.compatibility.Sounds;
import com.balugaq.jeg.utils.formatter.Formats;
import com.balugaq.jeg.utils.Lang;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This class used to create groups to mark items into {@link BookmarkGroup} in the guide.
 * Will not display Item Mark Button in {@link NestedItemGroup}
 *
 * @author balugaq
 * @since 1.1
 */
@SuppressWarnings({"deprecation", "unused", "UnnecessaryUnicodeEscape"})
@NotDisplayInSurvivalMode
@NotDisplayInCheatMode
public class ItemMarkGroup extends FlexItemGroup {
    private static final ItemStack ICON_BACKGROUND = Lang.getIcon("item-mark-background", Material.GREEN_STAINED_GLASS_PANE);
    private static final JavaPlugin JAVA_PLUGIN = JustEnoughGuide.getInstance();

    @Deprecated
    private final int BACK_SLOT = 1;

    @Deprecated
    private final int SEARCH_SLOT = 7;

    @Deprecated
    private final int PREVIOUS_SLOT = 46;

    @Deprecated
    private final int NEXT_SLOT = 52;

    @Deprecated
    private final int[] BORDER = new int[]{0, 2, 3, 4, 5, 6, 8, 45, 47, 48, 49, 50, 51, 53};

    @Deprecated
    private final int[] MAIN_CONTENT = new int[]{
            9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35,
            36, 37, 38, 39, 40, 41, 42, 43, 44
    };

    private final JEGSlimefunGuideImplementation implementation;
    private final Player player;
    private final @NotNull ItemGroup itemGroup;
    private final int page;
    private final @NotNull List<SlimefunItem> slimefunItemList;
    private Map<Integer, ItemMarkGroup> pageMap = new LinkedHashMap<>();

    /**
     * Create a new instance of ItemMarkGroup.
     *
     * @param implementation The implementation of JEGSlimefunGuideImplementation.
     * @param itemGroup      The item group to mark items.
     * @param player         The player who open the guide.
     */
    public ItemMarkGroup(JEGSlimefunGuideImplementation implementation, @NotNull ItemGroup itemGroup, Player player) {
        this(implementation, itemGroup, player, 1);
    }

    /**
     * Create a new instance of ItemMarkGroup.
     *
     * @param implementation The implementation of JEGSlimefunGuideImplementation.
     * @param itemGroup      The item group to mark items.
     * @param player         The player who open the guide.
     * @param page           The page number to display.
     */
    public ItemMarkGroup(
            JEGSlimefunGuideImplementation implementation, @NotNull ItemGroup itemGroup, Player player, int page) {
        super(
                new NamespacedKey(JAVA_PLUGIN, "jeg_item_mark_group_" + UUID.randomUUID()),
                new ItemStack(Material.BARRIER));
        this.page = page;
        this.player = player;
        this.itemGroup = itemGroup;
        this.slimefunItemList = itemGroup.getItems();
        this.implementation = implementation;
        this.pageMap.put(page, this);
    }

    /**
     * Get the page number of this group.
     *
     * @param itemMarkGroup The ItemMarkGroup instance.
     * @param page          The page number to get.
     */
    protected ItemMarkGroup(@NotNull ItemMarkGroup itemMarkGroup, int page) {
        this(itemMarkGroup.implementation, itemMarkGroup.itemGroup, itemMarkGroup.player, page);
    }

    /**
     * Always return false.
     *
     * @param player            The player who open the guide.
     * @param playerProfile     The player profile.
     * @param slimefunGuideMode The slimefun guide mode.
     * @return false.
     */
    @Override
    public boolean isVisible(
            final @NotNull Player player,
            final @NotNull PlayerProfile playerProfile,
            final @NotNull SlimefunGuideMode slimefunGuideMode) {
        return false;
    }

    /**
     * Opens the group for the player.
     *
     * @param player            The player who open the guide.
     * @param playerProfile     The player profile.
     * @param slimefunGuideMode The slimefun guide mode.
     */
    @Override
    public void open(
            final @NotNull Player player,
            final @NotNull PlayerProfile playerProfile,
            final @NotNull SlimefunGuideMode slimefunGuideMode) {
        playerProfile.getGuideHistory().add(this, this.page);
        this.generateMenu(player, playerProfile, slimefunGuideMode).open(player);
    }

    /**
     * Refresh the group for the player.
     *
     * @param player            The player who open the guide.
     * @param playerProfile     The player profile.
     * @param slimefunGuideMode The slimefun guide mode.
     */
    public void refresh(
            final @NotNull Player player,
            final @NotNull PlayerProfile playerProfile,
            final @NotNull SlimefunGuideMode slimefunGuideMode) {
        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
        this.open(player, playerProfile, slimefunGuideMode);
    }

    /**
     * Get the ItemMarkGroup instance by page number.
     *
     * @param player            The player who open the guide.
     * @param playerProfile     The player profile.
     * @param slimefunGuideMode The slimefun guide mode.
     * @return The ItemMarkGroup instance by page number.
     */
    @NotNull
    private ChestMenu generateMenu(
            final @NotNull Player player,
            final @NotNull PlayerProfile playerProfile,
            final @NotNull SlimefunGuideMode slimefunGuideMode) {
        ChestMenu chestMenu = new ChestMenu("Add to favorites - JEG");

        chestMenu.setEmptySlotsClickable(false);
        chestMenu.addMenuOpeningHandler(pl -> pl.playSound(pl.getLocation(), Sounds.GUIDE_BUTTON_CLICK_SOUND, 1, 1));

        for (int ss : itemGroup instanceof BookmarkRelocation relocation
                ? relocation.getBackButton(implementation, player)
                : Formats.sub.getChars('b')) {
            chestMenu.addItem(ss, PatchScope.Back.patch(player, ChestMenuUtils.getBackButton(player)));
            chestMenu.addMenuClickHandler(ss, (pl, s, is, action) -> EventUtil.callEvent(
                            new GuideEvents.BackButtonClickEvent(pl, is, s, action, chestMenu, implementation))
                    .ifSuccess(() -> {
                        GuideHistory guideHistory = playerProfile.getGuideHistory();
                        if (action.isShiftClicked()) {
                            SlimefunGuide.openMainMenu(
                                    playerProfile, slimefunGuideMode, guideHistory.getMainMenuPage());
                        } else {
                            guideHistory.goBack(Slimefun.getRegistry().getSlimefunGuide(slimefunGuideMode));
                        }
                        return false;
                    }));
        }

        // Search feature!
        for (int ss : itemGroup instanceof BookmarkRelocation relocation
                ? relocation.getSearchButton(implementation, player)
                : Formats.sub.getChars('S')) {
            chestMenu.addItem(ss, PatchScope.Search.patch(player, ChestMenuUtils.getSearchButton(player)));
            chestMenu.addMenuClickHandler(ss, (pl, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.SearchButtonClickEvent(pl, item, slot, action, chestMenu, implementation))
                    .ifSuccess(() -> {
                        pl.closeInventory();

                        Slimefun.getLocalization().sendMessage(pl, "guide.search.message");
                        ChatInput.waitForPlayer(
                                JAVA_PLUGIN,
                                pl,
                                msg -> implementation.openSearch(
                                        playerProfile,
                                        msg,
                                        implementation.getMode() == SlimefunGuideMode.SURVIVAL_MODE));

                        return false;
                    }));
        }

        for (int ss : itemGroup instanceof BookmarkRelocation relocation
                ? relocation.getPreviousButton(implementation, player)
                : Formats.sub.getChars('P')) {
            chestMenu.addItem(
                    ss,
                    PatchScope.PreviousPage.patch(
                            player,
                            ChestMenuUtils.getPreviousButton(
                                    player,
                                    this.page,
                                    (this.slimefunItemList.size() - 1)
                                            / Formats.sub.getChars('i').size()
                                            + 1)));
            chestMenu.addMenuClickHandler(ss, (p, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.PreviousButtonClickEvent(p, item, slot, action, chestMenu, implementation))
                    .ifSuccess(() -> {
                        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                        ItemMarkGroup itemMarkGroup = this.getByPage(Math.max(this.page - 1, 1));
                        itemMarkGroup.open(player, playerProfile, slimefunGuideMode);
                        return false;
                    }));
        }

        for (int ss : itemGroup instanceof BookmarkRelocation relocation
                ? relocation.getNextButton(implementation, player)
                : Formats.sub.getChars('N')) {
            chestMenu.addItem(
                    ss,
                    PatchScope.NextPage.patch(
                            player,
                            ChestMenuUtils.getNextButton(
                                    player,
                                    this.page,
                                    (this.slimefunItemList.size() - 1)
                                            / Formats.sub.getChars('i').size()
                                            + 1)));
            chestMenu.addMenuClickHandler(ss, (p, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.NextButtonClickEvent(p, item, slot, action, chestMenu, implementation))
                    .ifSuccess(() -> {
                        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                        ItemMarkGroup itemMarkGroup = this.getByPage(Math.min(
                                this.page + 1,
                                (this.slimefunItemList.size() - 1)
                                        / Formats.sub.getChars('i').size()
                                        + 1));
                        itemMarkGroup.open(player, playerProfile, slimefunGuideMode);
                        return false;
                    }));
        }

        for (int ss : itemGroup instanceof BookmarkRelocation relocation
                ? relocation.getBorder(implementation, player)
                : Formats.sub.getChars('B')) {
            chestMenu.addItem(ss, PatchScope.Background.patch(player, ICON_BACKGROUND));
            chestMenu.addMenuClickHandler(ss, ChestMenuUtils.getEmptyClickHandler());
        }

        List<Integer> contentSlots = itemGroup instanceof BookmarkRelocation relocation
                ? relocation.getMainContents(implementation, player)
                : Formats.sub.getChars('i');

        for (int i = 0; i < contentSlots.size(); i++) {
            int index = i + this.page * contentSlots.size() - contentSlots.size();
            if (index < this.slimefunItemList.size()) {
                SlimefunItem slimefunItem = slimefunItemList.get(index);
                Research research = slimefunItem.getResearch();
                ItemStack itemstack;
                ChestMenu.MenuClickHandler handler;
                if (implementation.getMode() == SlimefunGuideMode.SURVIVAL_MODE
                        && research != null
                        && !playerProfile.hasUnlocked(research)) {
                    String lore;

                    if (VaultIntegration.isEnabled()) {
                        lore = String.format("%.2f", research.getCurrencyCost()) + " Currency";
                    } else {
                        lore = research.getLevelCost() + " Levels";
                    }

                    itemstack = ItemStackUtil.getCleanItem(Converter.getItem(
                            ChestMenuUtils.getNoPermissionItem(),
                            "&f" + ItemUtils.getItemName(slimefunItem.getItem()),
                            "&7" + slimefunItem.getId(),
                            "&4&l" + Slimefun.getLocalization().getMessage(player, "guide.locked"),
                            "",
                            Lang.getGuideMessage("click-to-unlock"),
                            "",
                            Lang.getGuideMessage("cost", "cost", research.getCost())));
                    handler = (pl, slot, item, action) -> EventUtil.callEvent(new GuideEvents.ResearchItemEvent(
                                    pl, item, slot, action, chestMenu, implementation))
                            .ifSuccess(() -> {
                                research.unlockFromGuide(
                                        implementation, pl, playerProfile, slimefunItem, itemGroup, page);
                                return false;
                            });
                } else {
                    itemstack = ItemStackUtil.getCleanItem(Converter.getItem(slimefunItem.getItem(), meta -> {
                        ItemGroup itemGroup = slimefunItem.getItemGroup();
                        List<String> additionLore = List.of(
                                "",
                                ChatColor.DARK_GRAY + "\u21E8 " + ChatColor.WHITE
                                        + (LocalHelper.getAddonName(itemGroup, slimefunItem.getId())) + " - "
                                        + itemGroup.getDisplayName(player),
                                Lang.getGuideMessage("mark-item"));
                        if (meta.hasLore() && meta.getLore() != null) {
                            List<String> lore = meta.getLore();
                            lore.addAll(additionLore);
                            meta.setLore(lore);
                        } else {
                            meta.setLore(additionLore);
                        }

                        meta.addItemFlags(
                                ItemFlag.HIDE_ATTRIBUTES,
                                ItemFlag.HIDE_ENCHANTS,
                                JEGVersionedItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                    }));
                    handler = (pl, slot, itm, action) -> EventUtil.callEvent(
                                    new GuideEvents.CollectItemEvent(pl, itm, slot, action, chestMenu, implementation))
                            .ifSuccess(() -> {
                                try {
                                    JustEnoughGuide.getBookmarkManager().addBookmark(pl, slimefunItem);
                                    pl.sendMessage(Lang.getGuideMessage("marked"));
                                    pl.playSound(pl.getLocation(), Sounds.COLLECTED_ITEM, 1f, 1f);
                                } catch (Exception | LinkageError x) {
                                    printErrorMessage(pl, slimefunItem, x);
                                }

                                return false;
                            });
                }

                chestMenu.addItem(contentSlots.get(i), PatchScope.ItemMarkItem.patch(player, itemstack), handler);
            }
        }

        GuideUtil.addRTSButton(chestMenu, player, playerProfile, Formats.sub, slimefunGuideMode, implementation);
        GuideUtil.addBookMarkButton(chestMenu, player, playerProfile, Formats.sub, implementation, this);
        GuideUtil.addItemMarkButton(chestMenu, player, playerProfile, Formats.sub, implementation, this);

        Formats.sub.renderCustom(chestMenu);
        return chestMenu;
    }

    /**
     * Get the ItemMarkGroup instance by page number.
     *
     * @param page The page number to get.
     * @return The ItemMarkGroup instance by page number.
     */
    @NotNull
    private ItemMarkGroup getByPage(int page) {
        if (this.pageMap.containsKey(page)) {
            return this.pageMap.get(page);
        } else {
            synchronized (this.pageMap.get(1)) {
                if (this.pageMap.containsKey(page)) {
                    return this.pageMap.get(page);
                }

                ItemMarkGroup itemMarkGroup = new ItemMarkGroup(this, page);
                itemMarkGroup.pageMap = this.pageMap;
                this.pageMap.put(page, itemMarkGroup);
                return itemMarkGroup;
            }
        }
    }

    /**
     * Get the ItemMarkGroup instance by page number.
     *
     * @param p            The player who open the guide.
     * @param slimefunItem The SlimefunItem to check.
     * @return The ItemMarkGroup instance by page number.
     */
    @ParametersAreNonnullByDefault
    private boolean isItemGroupAccessible(Player p, SlimefunItem slimefunItem) {
        return Slimefun.getConfigManager().isShowHiddenItemGroupsInSearch()
                || slimefunItem.getItemGroup().isAccessible(p);
    }

    @ParametersAreNonnullByDefault
    private void printErrorMessage(Player p, Throwable x) {
        p.sendMessage(Lang.getError("internal-error"));
        JustEnoughGuide.getInstance().getLogger().log(Level.SEVERE, Lang.getError("error-occurred"), x);
        JustEnoughGuide.getInstance().getLogger().warning(Lang.getError("trying-fix-guide", "player_name", p.getName()));
        PlayerProfile profile = PlayerProfile.find(p).orElse(null);
        if (profile == null) {
            return;
        }
        GuideUtil.removeLastEntry(profile.getGuideHistory());
    }

    @ParametersAreNonnullByDefault
    private void printErrorMessage(Player p, SlimefunItem item, Throwable x) {
        p.sendMessage(Lang.getError("internal-error"));
        item.error(Lang.getError("item-error"), x);
        JustEnoughGuide.getInstance()
                .getLogger()
                .warning(Lang.getError("trying-fix-guide", "player_name", p.getName()));
        PlayerProfile profile = PlayerProfile.find(p).orElse(null);
        if (profile == null) {
            return;
        }
        GuideUtil.removeLastEntry(profile.getGuideHistory());
    }
}
