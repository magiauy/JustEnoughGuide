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

import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.EventUtil;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.compatibility.Sounds;
import com.balugaq.jeg.utils.formatter.Formats;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * This class used to create groups to display all the vanilla items in the guide.
 * Display for JEG recipe complete in NetworksExpansion / SlimeAEPlugin
 *
 * @author balugaq
 * @see JustEnoughGuide#vanillaItemsGroupDisplayableFor(Player, boolean)
 * @see JustEnoughGuide#vanillaItemsGroupIsDisplayableFor(Player)
 * @since 1.7
 */
@SuppressWarnings({"deprecation", "unused"})
@NotDisplayInCheatMode
public class VanillaItemsGroup extends FlexItemGroup {
    // todo: load SlimefunItem instances during runtime and try disable the annoying error message
    // todo: and avoid that player search them out.
    public static final List<ItemStack> itemStacks = new ArrayList<>();
    private static final @NotNull Set<Player> displayableFor = ConcurrentHashMap.newKeySet();
    private static final JavaPlugin JAVA_PLUGIN = JustEnoughGuide.getInstance();

    static {
        for (Material material : Material.values()) {
            if (!material.isAir() && material.isItem() && !material.isLegacy()) {
                itemStacks.add(new ItemStack(material));
            }
        }
    }

    private final int page;
    private Map<Integer, VanillaItemsGroup> pageMap = new LinkedHashMap<>();

    @ParametersAreNonnullByDefault
    public VanillaItemsGroup(NamespacedKey key, ItemStack icon) {
        super(key, icon, Integer.MAX_VALUE);
        this.page = 1;
        this.pageMap.put(1, this);
    }
    /**
     * Constructor of hiddenItemsGroup.
     *
     * @param hiddenItemsGroup The hiddenItemsGroup to copy.
     * @param page             The page number to display.
     */
    protected VanillaItemsGroup(@NotNull VanillaItemsGroup hiddenItemsGroup, int page) {
        super(hiddenItemsGroup.key, new ItemStack(Material.BARRIER));
        this.page = page;
        this.pageMap.put(page, this);
    }

    /**
     * @see JustEnoughGuide#vanillaItemsGroupDisplayableFor(Player, boolean)
     */
    public static void displayableFor(@NotNull Player player, boolean displayable) {
        if (displayable) {
            displayableFor.add(player);
        } else {
            displayableFor.remove(player);
        }
    }

    /**
     * @see JustEnoughGuide#vanillaItemsGroupIsDisplayableFor(Player)
     */
    public static boolean isDisplayableFor(@NotNull Player player) {
        return displayableFor.contains(player);
    }

    /**
     * Always returns false.
     *
     * @param player            The player who opened the group.
     * @param playerProfile     The player's profile.
     * @param slimefunGuideMode The Slimefun guide mode.
     * @return false.
     */
    @Override
    public boolean isVisible(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
        return displayableFor.contains(player);
    }

    /**
     * Opens the group for the player.
     *
     * @param player            The player who opened the group.
     * @param playerProfile     The player's profile.
     * @param slimefunGuideMode The Slimefun guide mode.
     */
    @Override
    public void open(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
        playerProfile.getGuideHistory().add(this, this.page);
        this.generateMenu(player, playerProfile, slimefunGuideMode).open(player);
    }

    /**
     * Reopens the menu for the player.
     *
     * @param player            The player who opened the group.
     * @param playerProfile     The player's profile.
     * @param slimefunGuideMode The Slimefun guide mode.
     */
    public void refresh(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
        this.open(player, playerProfile, slimefunGuideMode);
    }

    /**
     * Generates the menu for the player.
     *
     * @param player            The player who opened the group.
     * @param playerProfile     The player's profile.
     * @param slimefunGuideMode The Slimefun guide mode.
     * @return The generated menu.
     */
    @NotNull
    private ChestMenu generateMenu(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
        ChestMenu chestMenu = new ChestMenu("原版物品");

        chestMenu.setEmptySlotsClickable(false);
        chestMenu.addMenuOpeningHandler(pl -> pl.playSound(pl.getLocation(), Sounds.GUIDE_BUTTON_CLICK_SOUND, 1, 1));

        SlimefunGuideImplementation implementation = Slimefun.getRegistry().getSlimefunGuide(slimefunGuideMode);

        for (var ss : Formats.sub.getChars('b')) {
            chestMenu.addItem(ss, ItemStackUtil.getCleanItem(ChestMenuUtils.getBackButton(player)));
            chestMenu.addMenuClickHandler(ss, (pl, s, is, action) -> EventUtil.callEvent(new GuideEvents.BackButtonClickEvent(pl, is, s, action, chestMenu, implementation)).ifSuccess(() -> {
                GuideHistory guideHistory = playerProfile.getGuideHistory();
                if (action.isShiftClicked()) {
                    SlimefunGuide.openMainMenu(playerProfile, slimefunGuideMode, guideHistory.getMainMenuPage());
                } else {
                    guideHistory.goBack(Slimefun.getRegistry().getSlimefunGuide(SlimefunGuideMode.CHEAT_MODE));
                }
                return false;
            }));
        }

        // Search feature!
        for (var ss : Formats.sub.getChars('S')) {
            chestMenu.addItem(ss, ItemStackUtil.getCleanItem(ChestMenuUtils.getSearchButton(player)));
            chestMenu.addMenuClickHandler(ss, (pl, slot, item, action) -> EventUtil.callEvent(new GuideEvents.SearchButtonClickEvent(pl, item, slot, action, chestMenu, implementation)).ifSuccess(() -> {
                pl.closeInventory();

                Slimefun.getLocalization().sendMessage(pl, "guide.search.message");
                ChatInput.waitForPlayer(
                        JAVA_PLUGIN,
                        pl,
                        msg -> implementation.openSearch(
                                playerProfile, msg, implementation.getMode() == SlimefunGuideMode.SURVIVAL_MODE));

                return false;
            }));
        }

        for (var ss : Formats.sub.getChars('P')) {
            chestMenu.addItem(
                    ss,
                    ItemStackUtil.getCleanItem(ChestMenuUtils.getPreviousButton(
                            player, this.page, (itemStacks.size() - 1) / Formats.sub.getChars('i').size() + 1)));
            chestMenu.addMenuClickHandler(ss, (p, slot, item, action) -> EventUtil.callEvent(new GuideEvents.PreviousButtonClickEvent(p, item, slot, action, chestMenu, implementation)).ifSuccess(() -> {
                GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                VanillaItemsGroup hiddenItemsGroup = this.getByPage(Math.max(this.page - 1, 1));
                hiddenItemsGroup.open(player, playerProfile, slimefunGuideMode);
                return false;
            }));
        }

        for (var ss : Formats.sub.getChars('N')) {
            chestMenu.addItem(
                    ss,
                    ItemStackUtil.getCleanItem(ChestMenuUtils.getNextButton(
                            player, this.page, (itemStacks.size() - 1) / Formats.sub.getChars('i').size() + 1)));
            chestMenu.addMenuClickHandler(ss, (p, slot, item, action) -> EventUtil.callEvent(new GuideEvents.NextButtonClickEvent(p, item, slot, action, chestMenu, implementation)).ifSuccess(() -> {
                GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                VanillaItemsGroup hiddenItemsGroup = this.getByPage(
                        Math.min(this.page + 1, (itemStacks.size() - 1) / Formats.sub.getChars('i').size() + 1));
                hiddenItemsGroup.open(player, playerProfile, slimefunGuideMode);
                return false;
            }));
        }

        for (var ss : Formats.sub.getChars('B')) {
            chestMenu.addItem(ss, ItemStackUtil.getCleanItem(ChestMenuUtils.getBackground()));
            chestMenu.addMenuClickHandler(ss, ChestMenuUtils.getEmptyClickHandler());
        }

        var contentSlots = Formats.sub.getChars('i');
        for (int i = 0; i < contentSlots.size(); i++) {
            int index = i + this.page * contentSlots.size() - contentSlots.size();
            if (index < itemStacks.size()) {
                ItemStack itemStack = itemStacks.get(index);
                chestMenu.addItem(contentSlots.get(i), ItemStackUtil.getCleanItem(itemStack), ChestMenuUtils.getEmptyClickHandler());
            }
        }

        GuideUtil.addRTSButton(chestMenu, player, playerProfile, Formats.sub, slimefunGuideMode, implementation);
        if (implementation instanceof JEGSlimefunGuideImplementation jeg) {
            GuideUtil.addBookMarkButton(chestMenu, player, playerProfile, Formats.sub, jeg, this);
            GuideUtil.addItemMarkButton(chestMenu, player, playerProfile, Formats.sub, jeg, this);
        }

        return chestMenu;
    }

    /**
     * Gets the hiddenItemsGroup by page.
     *
     * @param page The page number.
     * @return The hiddenItemsGroup by page.
     */
    @NotNull
    private VanillaItemsGroup getByPage(int page) {
        if (this.pageMap.containsKey(page)) {
            return this.pageMap.get(page);
        } else {
            synchronized (this.pageMap.get(1)) {
                if (this.pageMap.containsKey(page)) {
                    return this.pageMap.get(page);
                }

                VanillaItemsGroup hiddenItemsGroup = new VanillaItemsGroup(this, page);
                hiddenItemsGroup.pageMap = this.pageMap;
                this.pageMap.put(page, hiddenItemsGroup);
                return hiddenItemsGroup;
            }
        }
    }

    /**
     * Checks if the item group is accessible for the player.
     *
     * @param p            The player.
     * @param slimefunItem The Slimefun item.
     * @return True if the item group is accessible for the player.
     */
    @ParametersAreNonnullByDefault
    private boolean isItemGroupAccessible(@NotNull Player p, @NotNull SlimefunItem slimefunItem) {
        return Slimefun.getConfigManager().isShowHiddenItemGroupsInSearch()
                || slimefunItem.getItemGroup().isAccessible(p);
    }

    /**
     * Prints an error message to the player.
     *
     * @param p The player.
     * @param x The exception.
     */
    @ParametersAreNonnullByDefault
    private void printErrorMessage(@NotNull Player p, @NotNull Throwable x) {
        p.sendMessage("&4服务器发生了一个内部错误. 请联系管理员处理.");
        JAVA_PLUGIN.getLogger().log(Level.SEVERE, "在打开指南书里的 Slimefun 物品时发生了意外!", x);
    }

    /**
     * Prints an error message to the player.
     *
     * @param p    The player.
     * @param item The Slimefun item.
     * @param x    The exception.
     */
    @ParametersAreNonnullByDefault
    private void printErrorMessage(@NotNull Player p, @NotNull SlimefunItem item, @NotNull Throwable x) {
        p.sendMessage(ChatColor.DARK_RED
                + "An internal server error has occurred. Please inform an admin, check the console for"
                + " further info.");
        item.error(
                "This item has caused an error message to be thrown while viewing it in the Slimefun" + " guide.", x);
    }

    @Override
    public int getTier() {
        return Integer.MAX_VALUE;
    }
}
