package com.balugaq.jeg.api.groups;

import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.api.interfaces.NotDisplayInSurvivalMode;
import com.balugaq.jeg.api.objects.enums.FilterType;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.JEGVersionedItemFlag;
import com.balugaq.jeg.utils.LocalHelper;
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import javax.annotation.ParametersAreNonnullByDefault;
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
    private static final boolean SHOW_HIDDEN_ITEM_GROUPS = Slimefun.getConfigManager().isShowHiddenItemGroupsInSearch();
    private static final Map<SlimefunItem, Integer> ENABLED_ITEMS = new HashMap<>();
    private static final List<SlimefunItem> AVAILABLE_ITEMS = new ArrayList<>();
    private static final int BACK_SLOT = 1;
    private static final int SEARCH_SLOT = 7;
    private static final int PREVIOUS_SLOT = 46;
    private static final int NEXT_SLOT = 52;
    private static final int[] BORDER = new int[] {0, 2, 3, 4, 5, 6, 8, 45, 47, 48, 49, 50, 51, 53};
    private static final int[] MAIN_CONTENT = new int[] {
        9, 10, 11, 12, 13, 14, 15, 16, 17,
        18, 19, 20, 21, 22, 23, 24, 25, 26,
        27, 28, 29, 30, 31, 32, 33, 34, 35,
        36, 37, 38, 39, 40, 41, 42, 43, 44
    };
    private static final JavaPlugin JAVA_PLUGIN = JustEnoughGuide.getInstance();

    static {
        int i = 0;
        for (SlimefunItem item : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            ENABLED_ITEMS.put(item, i);
            i += 1;
            if (item.isHidden() && !SHOW_HIDDEN_ITEM_GROUPS) {
                continue;
            }

            ItemStack[] recipe = item.getRecipe();
            if (recipe == null) {
                continue;
            }

            if (item instanceof MultiBlockMachine) {
                continue;
            }

            if (item.isDisabled()) {
                continue;
            }
            AVAILABLE_ITEMS.add(item);
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
    public SearchGroup(
            SlimefunGuideImplementation implementation,
            @NotNull Player player,
            @NotNull String searchTerm,
            boolean pinyin) {
        super(new NamespacedKey(JAVA_PLUGIN, "jeg_search_group_" + UUID.randomUUID()), new ItemStack(Material.BARRIER));
        this.page = 1;
        this.searchTerm = searchTerm;
        this.pinyin = pinyin;
        this.player = player;
        this.implementation = implementation;
        this.slimefunItemList = filterItems(player, searchTerm, pinyin);
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
    public boolean isVisible(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
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
    public void open(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
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
    public void refresh(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
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
    @NotNull private ChestMenu generateMenu(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
        ChestMenu chestMenu =
                new ChestMenu("你正在搜索: %item%".replace("%item%", ChatUtils.crop(ChatColor.WHITE, searchTerm)));

        chestMenu.setEmptySlotsClickable(false);
        chestMenu.addMenuOpeningHandler(pl -> pl.playSound(pl.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1));

        chestMenu.addItem(BACK_SLOT, ItemStackUtil.getCleanItem(ChestMenuUtils.getBackButton(player)));
        chestMenu.addMenuClickHandler(BACK_SLOT, (pl, s, is, action) -> {
            GuideHistory guideHistory = playerProfile.getGuideHistory();
            if (action.isShiftClicked()) {
                SlimefunGuide.openMainMenu(playerProfile, slimefunGuideMode, guideHistory.getMainMenuPage());
            } else {
                guideHistory.goBack(Slimefun.getRegistry().getSlimefunGuide(slimefunGuideMode));
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
                    msg -> implementation.openSearch(
                            playerProfile, msg, implementation.getMode() == SlimefunGuideMode.SURVIVAL_MODE));

            return false;
        });

        chestMenu.addItem(
                PREVIOUS_SLOT,
                ItemStackUtil.getCleanItem(ChestMenuUtils.getPreviousButton(
                        player, this.page, (this.slimefunItemList.size() - 1) / MAIN_CONTENT.length + 1)));
        chestMenu.addMenuClickHandler(PREVIOUS_SLOT, (p, slot, item, action) -> {
            GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
            SearchGroup searchGroup = this.getByPage(Math.max(this.page - 1, 1));
            searchGroup.open(player, playerProfile, slimefunGuideMode);
            return false;
        });

        chestMenu.addItem(
                NEXT_SLOT,
                ItemStackUtil.getCleanItem(ChestMenuUtils.getNextButton(
                        player, this.page, (this.slimefunItemList.size() - 1) / MAIN_CONTENT.length + 1)));
        chestMenu.addMenuClickHandler(NEXT_SLOT, (p, slot, item, action) -> {
            GuideUtil.removeLastEntry(playerProfile.getGuideHistory());
            SearchGroup searchGroup = this.getByPage(
                    Math.min(this.page + 1, (this.slimefunItemList.size() - 1) / MAIN_CONTENT.length + 1));
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
                    List<String> additionLore = List.of(
                            "",
                            ChatColor.DARK_GRAY + "\u21E8 " + ChatColor.WHITE
                                    + (LocalHelper.getAddonName(itemGroup, slimefunItem.getId())) + " - "
                                    + itemGroup.getDisplayName(player));
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
                        if (implementation.getMode() != SlimefunGuideMode.SURVIVAL_MODE
                                && (pl.isOp() || pl.hasPermission("slimefun.cheat.items"))) {
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
    @NotNull private SearchGroup getByPage(int page) {
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
        return filterItems(p, searchTerm, pinyin);
    }

    /**
     * Checks if the search filter is applicable.
     *
     * @param slimefunItem The Slimefun item.
     * @param searchTerm   The search term.
     * @param pinyin       Whether the search term is in Pinyin.
     * @return True if the search filter is applicable.
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
        String itemName =
                ChatColor.stripColor(ItemStackHelper.getDisplayName(itemStack)).toLowerCase(Locale.ROOT);
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

        // Quick escape for common cases
        boolean result = itemName.contains(searchTerm);
        if (result) {
            return true;
        }

        if (pinyin) {
            final String pinyinName = PinyinHelper.toPinyin(itemName, PinyinStyleEnum.INPUT, "");
            final String pinyinFirstLetter = PinyinHelper.toPinyin(itemName, PinyinStyleEnum.FIRST_LETTER, "");
            return pinyinName.contains(searchTerm)
                    || pinyinFirstLetter.contains(searchTerm);
        }

        return false;
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

    public List<SlimefunItem> filterItems(Player player, String searchTerm, boolean pinyin) {
        StringBuilder actualSearchTermBuilder = new StringBuilder();
        String[] split = searchTerm.split(" ");
        Map<FilterType, String> filters = new HashMap<>();
        for (String s : split) {
            boolean isFilter = false;
            for (FilterType filterType : FilterType.values()) {
                if (s.startsWith(filterType.getFlag()) && s.length() > filterType.getFlag().length()) {
                    isFilter = true;
                    String filterValue = s.substring(filterType.getFlag().length());
                    filters.put(filterType, filterValue);
                    break;
                }
            }

            if (!isFilter) {
                actualSearchTermBuilder.append(s).append(" ");
            }
        }

        String actualSearchTerm = actualSearchTermBuilder.toString().trim();
        for (FilterType filterType : FilterType.values()) {
            String flag = filterType.getFlag();
            // Escape the flag

            // origin = \flag, replace = flag
            // regex = \\\\flag -> compile -> \\flag -> regex -> \flag
            actualSearchTerm = actualSearchTerm.replaceAll("\\\\" + flag, flag);
        }
        List<SlimefunItem> merge = new ArrayList<>();
        // The unfiltered items
        List<SlimefunItem> items = AVAILABLE_ITEMS
                .stream()
                .filter(item -> item.getItemGroup().isAccessible(player))
                .toList();

        if (!actualSearchTerm.isBlank()) {
            List<SlimefunItem> nameMatched = filterItems(FilterType.BY_ITEM_NAME, actualSearchTerm, pinyin, items);
            List<SlimefunItem> machineMatched = filterItems(FilterType.BY_DISPLAY_ITEM_NAME, actualSearchTerm, pinyin, items);
            for (SlimefunItem item : nameMatched) {
                if (!merge.contains(item)) {
                    merge.add(item);
                }
            }
            for (SlimefunItem item : machineMatched) {
                if (!merge.contains(item)) {
                    merge.add(item);
                }
            }
        }

        // Filter items
        if (!filters.isEmpty()) {
            for (Map.Entry<FilterType, String> entry : filters.entrySet()) {
                items = filterItems(entry.getKey(), entry.getValue(), pinyin, items);
            }

            for (SlimefunItem item : items) {
                if (!merge.contains(item)) {
                    merge.add(item);
                }
            }
        }

        return new ArrayList<>(merge);
    }

    public List<SlimefunItem> filterItems(FilterType filterType, String filterValue, boolean pinyin, List<SlimefunItem> items) {
        String lowerFilterValue = filterValue.toLowerCase();
        switch (filterType) {
            case BY_ADDON_NAME -> {
                return items.stream().filter(item -> {
                    SlimefunAddon addon = item.getAddon();
                    String localAddonName = LocalHelper.getAddonName(addon, item.getId()).toLowerCase();
                    String originModName = (addon == null ? "Slimefun" : addon.getName()).toLowerCase();
                    if (localAddonName.contains(lowerFilterValue) || originModName.contains(lowerFilterValue)) {
                        return true;
                    }
                    return false;
                }).toList();
            }
            case BY_RECIPE_ITEM_NAME -> {
                return items.stream().filter(item -> {
                    ItemStack[] recipe = item.getRecipe();
                    if (recipe == null) {
                        return false;
                    }

                    for (ItemStack itemStack : recipe) {
                        if (isSearchFilterApplicable(itemStack, filterValue, false)) {
                            return true;
                        }
                    }

                    return false;
                }).toList();
            }
            case BY_RECIPE_TYPE_NAME -> {
                return items.stream().filter(item -> {
                    ItemStack recipeTypeIcon = item.getRecipeType().getItem(player);
                    if (recipeTypeIcon == null) {
                        return false;
                    }

                    return isSearchFilterApplicable(recipeTypeIcon, filterValue, false);
                }).toList();
            }
            case BY_DISPLAY_ITEM_NAME -> {
                return items.stream().filter(item -> {
                    if (item instanceof AContainer ac) {
                        try {
                            for (ItemStack itemStack : ac.getDisplayRecipes()) {
                                if (isSearchFilterApplicable(itemStack, filterValue, false)) {
                                    return true;
                                }
                            }
                        } catch (Throwable ignored) {
                            return false;
                        }
                    }

                    return false;
                }).toList();
            }
            case BY_MATERIAL_NAME -> {
                return items.stream().filter(item -> {
                    if (item.getItem().getType().name().toLowerCase().contains(lowerFilterValue)) {
                        return true;
                    }
                    return false;
                }).toList();
            }
            case BY_ITEM_NAME -> {
                return items.stream().filter(item -> {
                    if (isSearchFilterApplicable(item, filterValue, pinyin)) {
                        return true;
                    }
                    return false;
                }).toList();
            }
            default -> {
                return items;
            }
        }
    }
}
