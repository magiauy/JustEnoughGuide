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
import com.balugaq.jeg.api.editor.GroupResorter;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.objects.CustomGroupConfiguration;
import com.balugaq.jeg.api.objects.enums.PatchScope;
import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.EventUtil;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.Lang;
import com.balugaq.jeg.utils.compatibility.Converter;
import com.balugaq.jeg.utils.compatibility.Sounds;
import com.balugaq.jeg.utils.formatter.Formats;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.Getter;
import lombok.ToString;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings({"deprecation", "unused"})
@ToString
@Getter
public class CustomGroup extends FlexItemGroup {
    public final @NotNull CustomGroupConfiguration configuration;
    public final List<Object> objects; // ItemGroup first, SlimefunItem then.
    private final int page;
    private Map<Integer, CustomGroup> pageMap = new LinkedHashMap<>();

    @ParametersAreNonnullByDefault
    public CustomGroup(CustomGroupConfiguration configuration) {
        super(configuration.key(), configuration.item(), configuration.tier());
        this.configuration = configuration;

        List<ItemGroup> itemGroups = new ArrayList<>();
        List<SlimefunItem> slimefunItems = new ArrayList<>();
        for (Object obj : configuration.objects()) {
            if (obj instanceof ItemGroup group) {
                if (configuration.mode() == CustomGroupConfiguration.Mode.TRANSFER) {
                    // hide ItemGroup / SlimefunItem
                    GuideUtil.setForceHiddens(group, true);
                }
                itemGroups.add(group);
            } else if (obj instanceof SlimefunItem sf) {
                slimefunItems.add(sf);
                sf.getItemGroup().remove(sf);
                sf.setItemGroup(this);
            }
        }

        GroupResorter.sort(itemGroups);
        List<Object> objects = new ArrayList<>();
        objects.addAll(itemGroups);
        objects.addAll(slimefunItems);
        this.objects = objects;

        this.page = 1;
        this.pageMap.put(1, this);
    }

    @ParametersAreNonnullByDefault
    public CustomGroup(CustomGroup customGroup, int page) {
        super(customGroup.configuration.key(), customGroup.configuration.item(), customGroup.configuration.tier());
        this.configuration = customGroup.configuration;
        this.objects = customGroup.objects;
        this.page = page;
        this.pageMap.put(page, this);
    }

    @ParametersAreNonnullByDefault
    @Override
    public boolean isVisible(Player player, PlayerProfile playerProfile, SlimefunGuideMode slimefunGuideMode) {
        return true;
    }

    @Override
    public void open(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
        playerProfile.getGuideHistory().add(this, this.page);
        this.generateMenu(player, playerProfile, slimefunGuideMode).open(player);
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
            final @NotNull Player player,
            final @NotNull PlayerProfile playerProfile,
            final @NotNull SlimefunGuideMode slimefunGuideMode) {
        ChestMenu chestMenu = new ChestMenu(ItemStackHelper.getDisplayName(configuration.item()));

        chestMenu.setEmptySlotsClickable(false);
        chestMenu.addMenuOpeningHandler(pl -> pl.playSound(pl.getLocation(), Sounds.GUIDE_BUTTON_CLICK_SOUND, 1, 1));

        SlimefunGuideImplementation implementation = Slimefun.getRegistry().getSlimefunGuide(slimefunGuideMode);

        for (int ss : Formats.sub.getChars('b')) {
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
        for (int ss : Formats.sub.getChars('S')) {
            chestMenu.addItem(ss, PatchScope.Search.patch(player, ChestMenuUtils.getSearchButton(player)));
            chestMenu.addMenuClickHandler(ss, (pl, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.SearchButtonClickEvent(pl, item, slot, action, chestMenu, implementation))
                    .ifSuccess(() -> {
                        pl.closeInventory();

                        Slimefun.getLocalization().sendMessage(pl, "guide.search.message");
                        ChatInput.waitForPlayer(
                                JustEnoughGuide.getInstance(),
                                pl,
                                msg -> implementation.openSearch(
                                        playerProfile,
                                        msg,
                                        implementation.getMode() == SlimefunGuideMode.SURVIVAL_MODE));

                        return false;
                    }));
        }

        for (int ss : Formats.sub.getChars('P')) {
            chestMenu.addItem(
                    ss,
                    PatchScope.PreviousPage.patch(
                            player,
                            ChestMenuUtils.getPreviousButton(
                                    player,
                                    this.page,
                                    (this.objects.size() - 1)
                                            / Formats.sub.getChars('i').size()
                                            + 1)));
            chestMenu.addMenuClickHandler(ss, (p, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.PreviousButtonClickEvent(p, item, slot, action, chestMenu, implementation))
                    .ifSuccess(() -> {
                        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                        CustomGroup customGroup = this.getByPage(Math.max(this.page - 1, 1));
                        customGroup.open(player, playerProfile, slimefunGuideMode);
                        return false;
                    }));
        }

        for (int ss : Formats.sub.getChars('N')) {
            chestMenu.addItem(
                    ss,
                    PatchScope.NextPage.patch(
                            player,
                            ChestMenuUtils.getNextButton(
                                    player,
                                    this.page,
                                    (this.objects.size() - 1)
                                            / Formats.sub.getChars('i').size()
                                            + 1)));
            chestMenu.addMenuClickHandler(ss, (p, slot, item, action) -> EventUtil.callEvent(
                            new GuideEvents.NextButtonClickEvent(p, item, slot, action, chestMenu, implementation))
                    .ifSuccess(() -> {
                        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
                        CustomGroup customGroup = this.getByPage(Math.min(
                                this.page + 1,
                                (this.objects.size() - 1)
                                        / Formats.sub.getChars('i').size()
                                        + 1));
                        customGroup.open(player, playerProfile, slimefunGuideMode);
                        return false;
                    }));
        }

        for (int ss : Formats.sub.getChars('B')) {
            chestMenu.addItem(ss, PatchScope.Background.patch(player, ChestMenuUtils.getBackground()));
            chestMenu.addMenuClickHandler(ss, ChestMenuUtils.getEmptyClickHandler());
        }

        List<Integer> contentSlots = Formats.sub.getChars('i');
        for (int i = 0; i < contentSlots.size(); i++) {
            int index = i + this.page * contentSlots.size() - contentSlots.size();
            if (index < this.objects.size()) {
                Object o = objects.get(index);
                if (o instanceof SlimefunItem slimefunItem) {
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
                                            implementation,
                                            pl,
                                            playerProfile,
                                            slimefunItem,
                                            slimefunItem.getItemGroup(),
                                            page);
                                    return false;
                                });
                    } else {
                        itemstack = Converter.getItem(slimefunItem.getItem());
                        handler = (pl, slot, itm, action) -> EventUtil.callEvent(new GuideEvents.ItemButtonClickEvent(
                                        pl, itm, slot, action, chestMenu, implementation))
                                .ifSuccess(() -> {
                                    try {
                                        if (implementation.getMode() != SlimefunGuideMode.SURVIVAL_MODE
                                                && (pl.isOp() || pl.hasPermission("slimefun.cheat.items"))) {
                                            pl.getInventory()
                                                    .addItem(slimefunItem
                                                            .getItem()
                                                            .clone());
                                        } else {
                                            implementation.displayItem(playerProfile, slimefunItem, true);
                                        }
                                    } catch (Exception | LinkageError x) {
                                        printErrorMessage(pl, slimefunItem, x);
                                    }

                                    return false;
                                });
                    }

                    chestMenu.addItem(contentSlots.get(i), PatchScope.SlimefunItem.patch(player, itemstack), handler);
                } else if (o instanceof ItemGroup itemGroup) {
                    if (GuideUtil.getGuide(player, SlimefunGuideMode.SURVIVAL_MODE)
                            instanceof JEGSlimefunGuideImplementation guide) {
                        guide.showItemGroup0(chestMenu, player, playerProfile, itemGroup, contentSlots.get(i));
                    }
                }
            }
        }

        GuideUtil.addRTSButton(chestMenu, player, playerProfile, Formats.sub, slimefunGuideMode, implementation);
        if (implementation instanceof JEGSlimefunGuideImplementation jeg) {
            GuideUtil.addBookMarkButton(chestMenu, player, playerProfile, Formats.sub, jeg, this);
            GuideUtil.addItemMarkButton(chestMenu, player, playerProfile, Formats.sub, jeg, this);
        }

        Formats.sub.renderCustom(chestMenu);
        return chestMenu;
    }

    public void ifig(Object object, @NotNull Consumer<ItemGroup> consumer) {
        if (object instanceof ItemGroup ig) {
            consumer.accept(ig);
        }
    }

    public void ifsf(Object object, @NotNull Consumer<SlimefunItem> consumer) {
        if (object instanceof SlimefunItem si) {
            consumer.accept(si);
        }
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
    public boolean isCrossAddonItemGroup() {
        return true;
    }

    /**
     * Gets the customGroup by page.
     *
     * @param page The page number.
     * @return The customGroup by page.
     */
    @NotNull
    private CustomGroup getByPage(int page) {
        if (this.pageMap.containsKey(page)) {
            return this.pageMap.get(page);
        } else {
            synchronized (this.pageMap.get(1)) {
                if (this.pageMap.containsKey(page)) {
                    return this.pageMap.get(page);
                }

                CustomGroup customGroup = new CustomGroup(this, page);
                customGroup.pageMap = this.pageMap;
                this.pageMap.put(page, customGroup);
                return customGroup;
            }
        }
    }
}
