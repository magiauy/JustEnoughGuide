package com.balugaq.jeg.api.groups;

import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.api.interfaces.NotDisplayInSurvivalMode;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.JEGVersionedItemFlag;
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This group is used to display the search results of the search feature.
 * Supports Pinyin search and page turning.
 *
 * @author balugaq
 * @since 1.0
 */
@SuppressWarnings({"deprecation", "unused"})
@NotDisplayInSurvivalMode
@NotDisplayInCheatMode
public class SearchGroup extends FlexItemGroup {
    private static final Map<SlimefunItem, Integer> ENABLED_ITEMS = new HashMap<>();
    private static final int BACK_SLOT = 1;
    private static final int SEARCH_SLOT = 7;
    private static final int PREVIOUS_SLOT = 46;
    private static final int NEXT_SLOT = 52;
    private static final int[] BORDER = new int[]{0, 2, 3, 4, 5, 6, 8, 45, 47, 48, 49, 50, 51, 53};
    private static final int[] MAIN_CONTENT = new int[]{
            9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35,
            36, 37, 38, 39, 40, 41, 42, 43, 44
    };
    private static final JavaPlugin JAVA_PLUGIN = JustEnoughGuide.getInstance();

    static {
        int i = 0;
        for (SlimefunItem slimefunItem : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            ENABLED_ITEMS.put(slimefunItem, i);
            i += 1;
        }
    }

    private final SlimefunGuideImplementation implementation;
    private final Player player;
    private final String searchTerm;
    private final boolean pinyin;
    private final int page;
    private final List<SlimefunItem> slimefunItemList;
    private Map<Integer, SearchGroup> pageMap = new LinkedHashMap<>();

    /**
     * Constructor for the SearchGroup.
     *
     * @param implementation The Slimefun guide implementation.
     * @param player         The player who opened the guide.
     * @param searchTerm     The search term.
     * @param pinyin         Whether the search term is in Pinyin.
     */
    public SearchGroup(SlimefunGuideImplementation implementation, @NotNull Player player, @NotNull String searchTerm, boolean pinyin) {
        super(new NamespacedKey(JAVA_PLUGIN, "jeg_search_group_" + UUID.randomUUID()), new ItemStack(Material.BARRIER));
        this.page = 1;
        this.searchTerm = searchTerm;
        this.pinyin = pinyin;
        this.player = player;
        this.implementation = implementation;
        this.slimefunItemList = getAllMatchedItems(player, searchTerm, pinyin);
        this.pageMap.put(1, this);
    }

    /**
     * Constructor for the SearchGroup.
     *
     * @param searchGroup The SearchGroup to copy.
     * @param page        The page to display.
     */
    protected SearchGroup(@NotNull SearchGroup searchGroup, int page) {
        super(searchGroup.key, new ItemStack(Material.BARRIER));
        this.page = page;
        this.searchTerm = searchGroup.searchTerm;
        this.pinyin = searchGroup.pinyin;
        this.player = searchGroup.player;
        this.implementation = searchGroup.implementation;
        this.slimefunItemList = searchGroup.slimefunItemList;
        this.pageMap.put(page, this);
    }

    /**
     * Always returns false.
     *
     * @param player            The player to print the error message to.
     * @param playerProfile     The player profile.
     * @param slimefunGuideMode The Slimefun guide mode.
     * @return false.
     */
    @Override
    public boolean isVisible(@NotNull Player player, @NotNull PlayerProfile playerProfile, @NotNull SlimefunGuideMode slimefunGuideMode) {
        return false;
    }

    /**
     * Opens the search group.
     *
     * @param player            The player who opened the guide.
     * @param playerProfile     The player profile.
     * @param slimefunGuideMode The Slimefun guide mode.
     */
    @Override
    public void open(@NotNull Player player, @NotNull PlayerProfile playerProfile, @NotNull SlimefunGuideMode slimefunGuideMode) {
        playerProfile.getGuideHistory().add(this, this.page);
        this.generateMenu(player, playerProfile, slimefunGuideMode).open(player);
    }

    /**
     * Refreshes the search group.
     *
     * @param player            The player who opened the guide.
     * @param playerProfile     The player profile.
     * @param slimefunGuideMode The Slimefun guide mode.
     */
    public void refresh(@NotNull Player player, @NotNull PlayerProfile playerProfile, @NotNull SlimefunGuideMode slimefunGuideMode) {
        GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
        this.open(player, playerProfile, slimefunGuideMode);
    }

    /**
     * Generates the menu for the search group.
     *
     * @param player            The player who opened the guide.
     * @param playerProfile     The player profile.
     * @param slimefunGuideMode The Slimefun guide mode.
     * @return The generated menu.
     */
    @NotNull
    private ChestMenu generateMenu(@NotNull Player player, @NotNull PlayerProfile playerProfile, @NotNull SlimefunGuideMode slimefunGuideMode) {
        ChestMenu chestMenu = new ChestMenu("你正在搜索: %item%".replace("%item%", ChatUtils.crop(ChatColor.WHITE, searchTerm)));

        chestMenu.setEmptySlotsClickable(false);
        chestMenu.addMenuOpeningHandler(pl -> pl.playSound(pl.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1));

        chestMenu.addItem(BACK_SLOT, ItemStackUtil.getCleanItem(ChestMenuUtils.getBackButton(player)));
        chestMenu.addMenuClickHandler(BACK_SLOT, (pl, s, is, action) -> {
            GuideHistory guideHistory = playerProfile.getGuideHistory();
            if (action.isShiftClicked()) {
                SlimefunGuide.openMainMenu(playerProfile, slimefunGuideMode, guideHistory.getMainMenuPage());
            } else {
                guideHistory.goBack(Slimefun.getRegistry().getSlimefunGuide(SlimefunGuideMode.SURVIVAL_MODE));
            }
            return false;
        });

        // Search feature!
        chestMenu.addItem(SEARCH_SLOT, ItemStackUtil.getCleanItem(ChestMenuUtils.getSearchButton(player)));
        chestMenu.addMenuClickHandler(SEARCH_SLOT, (pl, slot, item, action) -> {
            pl.closeInventory();

            Slimefun.getLocalization().sendMessage(pl, "guide.search.message");
            ChatInput.waitForPlayer(
                    JAVA_PLUGIN,
                    pl,
                    msg -> implementation.openSearch(playerProfile, msg, implementation.getMode() == SlimefunGuideMode.SURVIVAL_MODE));

            return false;
        });

        chestMenu.addItem(PREVIOUS_SLOT, ItemStackUtil.getCleanItem(ChestMenuUtils.getPreviousButton(player, this.page, (this.slimefunItemList.size() - 1) / MAIN_CONTENT.length + 1)));
        chestMenu.addMenuClickHandler(PREVIOUS_SLOT, (p, slot, item, action) -> {
            GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
            SearchGroup searchGroup = this.getByPage(Math.max(this.page - 1, 1));
            searchGroup.open(player, playerProfile, slimefunGuideMode);
            return false;
        });

        chestMenu.addItem(NEXT_SLOT, ItemStackUtil.getCleanItem(ChestMenuUtils.getNextButton(player, this.page, (this.slimefunItemList.size() - 1) / MAIN_CONTENT.length + 1)));
        chestMenu.addMenuClickHandler(NEXT_SLOT, (p, slot, item, action) -> {
            GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
            SearchGroup searchGroup = this.getByPage(Math.min(this.page + 1, (this.slimefunItemList.size() - 1) / MAIN_CONTENT.length + 1));
            searchGroup.open(player, playerProfile, slimefunGuideMode);
            return false;
        });

        for (int slot : BORDER) {
            chestMenu.addItem(slot, ItemStackUtil.getCleanItem(ChestMenuUtils.getBackground()));
            chestMenu.addMenuClickHandler(slot, ChestMenuUtils.getEmptyClickHandler());
        }

        for (int i = 0; i < MAIN_CONTENT.length; i++) {
            int index = i + this.page * MAIN_CONTENT.length - MAIN_CONTENT.length;
            if (index < this.slimefunItemList.size()) {
                SlimefunItem slimefunItem = slimefunItemList.get(index);
                ItemStack itemstack = ItemStackUtil.getCleanItem(new CustomItemStack(slimefunItem.getItem(), meta -> {
                    ItemGroup itemGroup = slimefunItem.getItemGroup();
                    List<String> additionLore = List.of("", ChatColor.DARK_GRAY + "\u21E8 " + ChatColor.WHITE + (itemGroup.getAddon() == null ? "Slimefun" : itemGroup.getAddon().getName()) + " - " + itemGroup.getDisplayName(player));
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
                chestMenu.addItem(MAIN_CONTENT[i], ItemStackUtil.getCleanItem(itemstack), (pl, slot, itm, action) -> {
                    try {
                        if (implementation.getMode() != SlimefunGuideMode.SURVIVAL_MODE && (pl.isOp() || pl.hasPermission("slimefun.cheat.items"))) {
                            pl.getInventory().addItem(slimefunItem.getItem().clone());
                        } else {
                            implementation.displayItem(playerProfile, slimefunItem, true);
                        }
                    } catch (Exception | LinkageError x) {
                        printErrorMessage(pl, slimefunItem, x);
                    }

                    return false;
                });
            }
        }

        return chestMenu;
    }

    /**
     * Gets the search group by page.
     *
     * @param page The page to get.
     * @return The search group by page.
     */
    @NotNull
    private SearchGroup getByPage(int page) {
        if (this.pageMap.containsKey(page)) {
            return this.pageMap.get(page);
        } else {
            synchronized (this.pageMap.get(1)) {
                if (this.pageMap.containsKey(page)) {
                    return this.pageMap.get(page);
                }

                SearchGroup searchGroup = new SearchGroup(this, page);
                searchGroup.pageMap = this.pageMap;
                this.pageMap.put(page, searchGroup);
                return searchGroup;
            }
        }
    }

    /**
     * Gets all matched items.
     *
     * @param p          The player.
     * @param searchTerm The search term.
     * @param pinyin     Whether the search term is in Pinyin.
     * @return The matched items.
     */
    private @NotNull List<SlimefunItem> getAllMatchedItems(@NotNull Player p, @NotNull String searchTerm, boolean pinyin) {
        if (searchTerm.length() > 2 && searchTerm.startsWith("#m")) {
            String substring = searchTerm.substring(2);
            return ENABLED_ITEMS
                    .keySet()
                    .stream()
                    .filter(item -> {
                        if (item.isHidden() || !isItemGroupAccessible(p, item)) {
                            return false;
                        }

                        if (!(item instanceof AContainer ac)) {
                            return false;
                        }

                        try {
                            for (ItemStack itemStack : ac.getDisplayRecipes()) {
                                if (isSearchFilterApplicable(itemStack, substring, false)) {
                                    return true;
                                }
                            }
                        } catch (Throwable ignored) {
                            return false;
                        }

                        return false;
                    })
                    .toList();
        } else if (searchTerm.length() > 2 && searchTerm.startsWith("#t")) {
            String substring = searchTerm.substring(2);
            return ENABLED_ITEMS
                    .keySet()
                    .stream()
                    .filter(item -> {
                        if (item.isHidden() || !isItemGroupAccessible(p, item)) {
                            return false;
                        }

                        ItemStack recipeTypeIcon = item.getRecipeType().getItem(p);
                        if (recipeTypeIcon == null) {
                            return false;
                        }

                        return isSearchFilterApplicable(recipeTypeIcon, substring, false);
                    })
                    .toList();
        } else if (searchTerm.length() > 2 && searchTerm.startsWith("#r")) {
            String substring = searchTerm.substring(2);
            return ENABLED_ITEMS
                    .keySet()
                    .stream()
                    .filter(item -> {
                        if (item.isHidden() || !isItemGroupAccessible(p, item)) {
                            return false;
                        }

                        ItemStack[] recipe = item.getRecipe();
                        if (recipe == null) {
                            return false;
                        }

                        for (ItemStack itemStack : recipe) {
                            if (isSearchFilterApplicable(itemStack, substring, false)) {
                                return true;
                            }
                        }

                        return false;
                    })
                    .toList();
        } else {
            return ENABLED_ITEMS
                    .keySet()
                    .stream()
                    .filter(item -> {
                        if (item.isHidden() || !isItemGroupAccessible(p, item)) {
                            return false;
                        }

                        if (item instanceof MultiBlockMachine) {
                            return false;
                        }

                        if (item instanceof AContainer ac) {
                            try {
                                for (ItemStack itemStack : ac.getDisplayRecipes()) {
                                    if (isSearchFilterApplicable(itemStack, searchTerm, false)) {
                                        return true;
                                    }
                                }
                            } catch (Throwable ignored) {
                                return false;
                            }
                        }

                        if (isSearchFilterApplicable(item, searchTerm, pinyin)) {
                            return true;
                        }

                        return false;
                    })
                    .sorted((a, b) -> {
                        return ENABLED_ITEMS.get(a) > ENABLED_ITEMS.get(b) ? 1 : -1;
                    })
                    .toList();
        }
    }

    /**
     * Checks if the item group is accessible.
     *
     * @param p            The player.
     * @param slimefunItem The Slimefun item.
     * @return True if the item group is accessible.
     */
    @ParametersAreNonnullByDefault
    private boolean isItemGroupAccessible(Player p, SlimefunItem slimefunItem) {
        return Slimefun.getConfigManager().isShowHiddenItemGroupsInSearch()
                || slimefunItem.getItemGroup().isAccessible(p);
    }

    /**
     * Checks if the search filter is applicable.
     *
     * @param slimefunItem The Slimefun item.
     * @param searchTerm   The search term.
     * @param pinyin       Whether the search term is in Pinyin.
     * @return
     */
    @ParametersAreNonnullByDefault
    private boolean isSearchFilterApplicable(SlimefunItem slimefunItem, String searchTerm, boolean pinyin) {
        if (slimefunItem == null) {
            return false;
        }
        String itemName = ChatColor.stripColor(slimefunItem.getItemName()).toLowerCase(Locale.ROOT);
        return isSearchFilterApplicable(itemName, searchTerm, pinyin);
    }

    /**
     * Checks if the search filter is applicable.
     *
     * @param itemStack  The item stack.
     * @param searchTerm The search term.
     * @param pinyin     Whether the search term is in Pinyin.
     * @return True if the search filter is applicable.
     */
    @ParametersAreNonnullByDefault
    private boolean isSearchFilterApplicable(ItemStack itemStack, String searchTerm, boolean pinyin) {
        if (itemStack == null) {
            return false;
        }
        String itemName = ChatColor.stripColor(ItemStackHelper.getDisplayName(itemStack)).toLowerCase(Locale.ROOT);
        return isSearchFilterApplicable(itemName, searchTerm, pinyin);
    }

    /**
     * Checks if the search filter is applicable.
     *
     * @param itemName   The item name.
     * @param searchTerm The search term.
     * @param pinyin     Whether the search term is in Pinyin.
     * @return True if the search filter is applicable.
     */
    @ParametersAreNonnullByDefault
    private boolean isSearchFilterApplicable(String itemName, String searchTerm, boolean pinyin) {
        if (itemName.isEmpty()) {
            return false;
        }
        if (pinyin) {
            final String pinyinName = PinyinHelper.toPinyin(itemName, PinyinStyleEnum.INPUT, "");
            final String pinyinFirstLetter = PinyinHelper.toPinyin(itemName, PinyinStyleEnum.FIRST_LETTER, "");
            return itemName.contains(searchTerm) || pinyinName.contains(searchTerm) || pinyinFirstLetter.contains(searchTerm);
        } else {
            return itemName.contains(searchTerm);
        }
    }

    /**
     * Prints an error message.
     *
     * @param p The player.
     * @param x The exception.
     */
    @ParametersAreNonnullByDefault
    private void printErrorMessage(Player p, Throwable x) {
        p.sendMessage("&4服务器发生了一个内部错误. 请联系管理员处理.");
        JAVA_PLUGIN.getLogger().log(Level.SEVERE, "在打开指南书里的 Slimefun 物品时发生了意外!", x);
    }

    /**
     * Prints an error message.
     *
     * @param p    The player.
     * @param item The Slimefun item.
     * @param x    The exception.
     */
    @ParametersAreNonnullByDefault
    private void printErrorMessage(Player p, SlimefunItem item, Throwable x) {
        p.sendMessage(ChatColor.DARK_RED
                + "An internal server error has occurred. Please inform an admin, check the console for"
                + " further info.");
        item.error(
                "This item has caused an error message to be thrown while viewing it in the Slimefun" + " guide.", x);
    }
}
