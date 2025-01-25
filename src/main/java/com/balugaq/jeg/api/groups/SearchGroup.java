package com.balugaq.jeg.api.groups;

import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.api.interfaces.NotDisplayInSurvivalMode;
import com.balugaq.jeg.api.objects.Timer;
import com.balugaq.jeg.api.objects.enums.FilterType;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.balugaq.jeg.utils.JEGVersionedItemFlag;
import com.balugaq.jeg.utils.LocalHelper;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.RandomizedSet;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.Bukkit;
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
    public static boolean LOADED = false;
    public static final Map<Character, Set<SlimefunItem>> CACHE = new HashMap<>(); // fast way for by item name
    public static final Map<Character, Set<SlimefunItem>> CACHE2 = new HashMap<>(); // fast way for by display item name
    private static final boolean SHOW_HIDDEN_ITEM_GROUPS = Slimefun.getConfigManager().isShowHiddenItemGroupsInSearch();
    private static final int DEFAULT_HASH_SIZE = 5000;
    private static final Map<SlimefunItem, Integer> ENABLED_ITEMS = new HashMap<>(DEFAULT_HASH_SIZE);
    private static final Set<SlimefunItem> AVAILABLE_ITEMS = new HashSet<>(DEFAULT_HASH_SIZE);
    public static final Map<String, Set<String>> SPECIAL_CACHE = new HashMap<>();
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
        if (!LOADED) {
            init();
        }
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
    public static boolean isSearchFilterApplicable(SlimefunItem slimefunItem, String searchTerm, boolean pinyin) {
        if (slimefunItem == null) {
            return false;
        }
        String itemName = ChatColor.stripColor(slimefunItem.getItemName()).toLowerCase(Locale.ROOT);
        return isSearchFilterApplicable(itemName, searchTerm.toLowerCase(), pinyin);
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
    public static boolean isSearchFilterApplicable(ItemStack itemStack, String searchTerm, boolean pinyin) {
        if (itemStack == null) {
            return false;
        }
        String itemName =
                ChatColor.stripColor(ItemStackHelper.getDisplayName(itemStack)).toLowerCase(Locale.ROOT);
        return isSearchFilterApplicable(itemName, searchTerm.toLowerCase(), pinyin);
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
    public static boolean isSearchFilterApplicable(String itemName, String searchTerm, boolean pinyin) {
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
        Timer t = new Timer("Total Search");
        t.starts();
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
            // Quote the flag to be used as a literal replacement
            actualSearchTerm = actualSearchTerm.replaceAll(Pattern.quote(flag), Matcher.quoteReplacement(flag));
        }
        Set<SlimefunItem> merge = new HashSet<>(36 * 4);
        // The unfiltered items
        var timer0 = new Timer("Filter Items 0");
        timer0.starts();
        Set<SlimefunItem> items = AVAILABLE_ITEMS
                .stream()
                .filter(item -> item.getItemGroup().isAccessible(player)).collect(Collectors.toSet());
        timer0.logs();

        if (!actualSearchTerm.isBlank()) {
            var timer = new Timer("Name Matched Items 1");
            timer.starts();
            Set<SlimefunItem> nameMatched = new HashSet<>();
            boolean containsAll = true;
            if (containsAll) {
                Set<SlimefunItem> allMatched = null;
                for (char c : actualSearchTerm.toCharArray()) {
                    Debug.debug("Searching: " + c);
                    Set<SlimefunItem> cache = CACHE.get(c);
                    if (cache == null) {
                        cache = new HashSet<>();
                    }
                    if (allMatched == null) {
                        allMatched = cache;
                    } else {
                        allMatched.retainAll(cache);
                    }
                }
                if (allMatched != null) {
                    nameMatched.addAll(allMatched);
                }
            }
            timer.logs();
            var timer2 = new Timer("Machine Matched Items 2");
            timer2.starts();
            boolean containsMachine = true;
            Set<SlimefunItem> machineMatched = new HashSet<>();
            if (containsMachine) {
                Set<SlimefunItem> allMatched = null;
                for (char c : actualSearchTerm.toCharArray()) {
                    Debug.debug("Searching: " + c);
                    Set<SlimefunItem> cache = CACHE2.get(c);
                    if (cache == null) {
                        cache = new HashSet<>();
                    }
                    if (allMatched == null) {
                        allMatched = cache;
                    } else {
                        allMatched.retainAll(cache);
                    }
                }
                if (allMatched != null) {
                    machineMatched.addAll(allMatched);
                }
            }
            timer2.logs();
            merge.addAll(nameMatched);
            merge.addAll(machineMatched);
        }

        var timer3 = new Timer("Filter Items 3");
        timer3.starts();
        // Filter items
        if (!filters.isEmpty()) {
            for (Map.Entry<FilterType, String> entry : filters.entrySet()) {
                items = filterItems(entry.getKey(), entry.getValue(), pinyin, items);
            }

            merge.addAll(items);
        }
        timer3.logs();

        var timer4 = new Timer("Sort Items 4");
        timer4.starts();
        List<SlimefunItem> sorted = merge.stream().sorted((a, b) -> ENABLED_ITEMS.get(a) < ENABLED_ITEMS.get(b) ? -1 : 1).toList();
        timer4.logs();
        t.logs();
        return sorted;
    }

    public List<SlimefunItem> filterItems(FilterType filterType, String filterValue, boolean pinyin, List<SlimefunItem> items) {
        String lowerFilterValue = filterValue.toLowerCase();
        return items.stream().filter(item -> filterType.getFilter().apply(player, item, lowerFilterValue, pinyin)).toList();
    }

    public Set<SlimefunItem> filterItems(FilterType filterType, String filterValue, boolean pinyin, Set<SlimefunItem> items) {
        String lowerFilterValue = filterValue.toLowerCase();
        return items.stream().filter(item -> filterType.getFilter().apply(player, item, lowerFilterValue, pinyin)).collect(Collectors.toSet());
    }

    public static List<SlimefunItem> filterItems(Player player, FilterType filterType, String filterValue, boolean pinyin, List<SlimefunItem> items) {
        String lowerFilterValue = filterValue.toLowerCase();
        return items.stream().filter(item -> filterType.getFilter().apply(player, item, lowerFilterValue, pinyin)).toList();
    }

    public static Set<SlimefunItem> filterItems(Player player, FilterType filterType, String filterValue, boolean pinyin, Set<SlimefunItem> items) {
        String lowerFilterValue = filterValue.toLowerCase();
        return items.stream().filter(item -> filterType.getFilter().apply(player, item, lowerFilterValue, pinyin)).collect(Collectors.toSet());
    }

    public static void init() {
        if (!LOADED) {
            Debug.log("Initializing Search Group...");
            Timer.start();
            int i = 0;
            for (SlimefunItem item : Slimefun.getRegistry().getEnabledSlimefunItems()) {
                ENABLED_ITEMS.put(item, i);
                i += 1;
                if (item.isHidden() && !SHOW_HIDDEN_ITEM_GROUPS) {
                    continue;
                }

                ItemStack[] r = item.getRecipe();
                if (r == null) {
                    continue;
                }

                if (item.isDisabled()) {
                    continue;
                }
                AVAILABLE_ITEMS.add(item);
                try {
                    String id = item.getId();
                    if (!SPECIAL_CACHE.containsKey(id)) {
                        Set<String> cache = new HashSet<>();

                        // init cache
                        Object Orecipes = ReflectionUtil.getValue(item, "recipes");
                        if (Orecipes == null) {
                            Object Omaterial = ReflectionUtil.getValue(item, "material");
                            if (Omaterial == null) {
                                Object ORECIPE_LIST = ReflectionUtil.getValue(item, "RECIPE_LIST");
                                if (ORECIPE_LIST == null) {
                                    Object Ooutputs = ReflectionUtil.getValue(item, "outputs");
                                    if (Ooutputs == null) {
                                        Object OOUTPUTS = ReflectionUtil.getValue(item, "OUTPUTS");
                                        if (OOUTPUTS == null) {
                                            continue;
                                        }
                                        // InfinityExpansion StrainerBase
                                        if (OOUTPUTS instanceof ItemStack[] outputs) {
                                            if (!isInstance(item, "StrainerBase")) {
                                                continue;
                                            }
                                            for (ItemStack output : outputs) {
                                                cache.add(ItemStackHelper.getDisplayName(output));
                                            }
                                        }
                                    }
                                    // InfinityExpansion Quarry
                                    else if (Ooutputs instanceof Material[] outputs) {
                                        if (!isInstance(item, "Quarry")) {
                                            continue;
                                        }
                                        for (Material material : outputs) {
                                            cache.add(ItemStackHelper.getDisplayName(new ItemStack(material)));
                                        }
                                    }
                                }
                                // InfinityExpansion SingularityConstructor
                                else if (ORECIPE_LIST instanceof List<?> recipes) {
                                    if (!isInstance(item, "SingularityConstructor")) {
                                        continue;
                                    }
                                    for (Object recipe : recipes) {
                                        ItemStack input = (ItemStack) ReflectionUtil.getValue(recipe, "input");
                                        if (input != null) {
                                            cache.add(ItemStackHelper.getDisplayName(input));
                                        }
                                        SlimefunItemStack output = (SlimefunItemStack) ReflectionUtil.getValue(recipe, "output");
                                        if (output != null) {
                                            SlimefunItem slimefunItem = output.getItem();
                                            if (slimefunItem != null) {
                                                cache.add(slimefunItem.getItemName());
                                            }
                                        }
                                    }
                                }
                            } else {
                                // InfinityExpansion MaterialGenerator
                                if (!isInstance(item, "MaterialGenerator")) {
                                    continue;
                                }
                                cache.add(ItemStackHelper.getDisplayName(new ItemStack((Material) Omaterial)));
                            }
                        }
                        // InfinityExpansion ResourceSynthesizer
                        if (Orecipes instanceof SlimefunItemStack[] recipes) {
                            if (!isInstance(item, "ResourceSynthesizer")){
                                continue;
                            }
                            for (SlimefunItemStack slimefunItemStack : recipes) {
                                SlimefunItem slimefunItem = slimefunItemStack.getItem();
                                if (slimefunItem != null) {
                                    cache.add(slimefunItem.getItemName());
                                }
                            }
                        }
                        // InfinityExpansion GrowingMachine
                        else if (Orecipes instanceof EnumMap<?, ?> recipes) {
                            if (!isInstance(item, "GrowingMachine")){
                                continue;
                            }
                            recipes.values().forEach(obj -> {
                                ItemStack[] items = (ItemStack[]) obj;
                                for (ItemStack itemStack : items) {
                                    cache.add(ItemStackHelper.getDisplayName(itemStack));
                                }
                            });
                        }
                        // InfinityExpansion MachineBlock
                        else if (Orecipes instanceof List<?> recipes) {
                            if (!isInstance(item, "MachineBlock")) {
                                continue;
                            }
                            for (Object recipe : recipes) {
                                String[] strings = (String[]) ReflectionUtil.getValue(recipe, "strings");
                                if (strings == null) {
                                    continue;
                                }
                                for (String string : strings) {
                                    SlimefunItem slimefunItem = SlimefunItem.getById(string);
                                    if (slimefunItem != null) {
                                        cache.add(slimefunItem.getItemName());
                                    } else {
                                        Material material = Material.getMaterial(string);
                                        if (material != null) {
                                            cache.add(ItemStackHelper.getDisplayName(new ItemStack(material)));
                                        }
                                    }
                                }

                                ItemStack output = (ItemStack) ReflectionUtil.getValue(recipe, "output");
                                if (output != null) {
                                    cache.add(ItemStackHelper.getDisplayName(output));
                                }
                            }
                        }

                        if (!cache.isEmpty()) {
                            SPECIAL_CACHE.put(id, cache);
                        }
                    }
                } catch (Throwable ignored) {
                }
            }

            // InfinityExpansion StoneworksFactory
            Set<Material> materials = new HashSet<>();
            materials.add(Material.COBBLESTONE);
            materials.add(Material.STONE);
            materials.add(Material.SAND);
            materials.add(Material.STONE_BRICKS);
            materials.add(Material.SMOOTH_STONE);
            materials.add(Material.GLASS);
            materials.add(Material.CRACKED_STONE_BRICKS);
            materials.add(Material.GRAVEL);
            materials.add(Material.GRANITE);
            materials.add(Material.DIORITE);
            materials.add(Material.ANDESITE);
            materials.add(Material.POLISHED_GRANITE);
            materials.add(Material.POLISHED_DIORITE);
            materials.add(Material.POLISHED_ANDESITE);
            materials.add(Material.SANDSTONE);
            Set<String> cache = new HashSet<>();
            for (Material material : materials) {
                cache.add(ItemStackHelper.getDisplayName(new ItemStack(material)));
            }
            SPECIAL_CACHE.put("STONEWORKS_FACTORY", cache);

            // InfinityExpansion VoidHarvester
            SlimefunItem item2 = SlimefunItem.getById("VOID_BIT");
            if (item2 != null) {
                Set<String> cache2 = new HashSet<>();
                cache2.add(item2.getItemName());
                SPECIAL_CACHE.put("VOID_HARVESTER", cache2);
            }

            // InfinityExpansion MobDataCard
            label2:
            {
                try {
                    Class<?> MobDataCardClass = Class.forName("io.github.mooy1.infinityexpansion.items.mobdata.MobDataCard");
                    @SuppressWarnings("unchecked") Map<String, Object> cards = (Map<String, Object>) ReflectionUtil.getStaticValue(MobDataCardClass, "CARDS");
                    if (cards == null) {
                        break label2;
                    }
                    cards.values().forEach(card -> {
                        @SuppressWarnings("unchecked") RandomizedSet<ItemStack> drops = (RandomizedSet<ItemStack>) ReflectionUtil.getValue(card, "drops");
                        if (drops == null) {
                            return;
                        }
                        Set<String> cache2 = new HashSet<>();
                        for (ItemStack itemStack : drops.toMap().keySet()) {
                            cache2.add(ItemStackHelper.getDisplayName(itemStack));
                        }
                        SPECIAL_CACHE.put(((SlimefunItem) card).getId(), cache2);
                    });
                } catch (Throwable ignored) {
                }
            }

            Bukkit.getScheduler().runTaskAsynchronously(JustEnoughGuide.getInstance(), () -> {
                Timer timer = new Timer("Init Cache");
                timer.starts();
                for (SlimefunItem slimefunItem : AVAILABLE_ITEMS) {
                    String name = slimefunItem.getItemName();
                    for (char c : name.toCharArray()) {
                        char d = Character.toLowerCase(c);
                        CACHE.putIfAbsent(d, new HashSet<>());
                        CACHE.get(d).add(slimefunItem);
                    }

                    if (JustEnoughGuide.getConfigManager().isPinyinSearch()) {
                        final String pinyinName = PinyinHelper.toPinyin(name, PinyinStyleEnum.INPUT, "");
                        final String pinyinFirstLetter = PinyinHelper.toPinyin(name, PinyinStyleEnum.FIRST_LETTER, "");
                        for (char c : pinyinName.toCharArray()) {
                            char d = Character.toLowerCase(c);
                            CACHE.putIfAbsent(d, new HashSet<>());
                            CACHE.get(d).add(slimefunItem);
                        }
                        for (char c : pinyinFirstLetter.toCharArray()) {
                            char d = Character.toLowerCase(c);
                            CACHE.putIfAbsent(d, new HashSet<>());
                            Set<SlimefunItem> set = CACHE.get(d);
                            if (set == null) {
                                set = new HashSet<>();
                                CACHE.put(d, set);
                            }
                            set.add(slimefunItem);
                        }
                    }

                    if (slimefunItem instanceof AContainer ac) {
                        List<ItemStack> displayRecipes = ac.getDisplayRecipes();
                        for (ItemStack itemStack : displayRecipes) {
                            String name2 = ItemStackHelper.getDisplayName(itemStack);
                            for (char c : name2.toCharArray()) {
                                char d = Character.toLowerCase(c);
                                CACHE2.putIfAbsent(d, new HashSet<>());
                                Set<SlimefunItem> set = CACHE2.get(d);
                                if (set == null) {
                                    set = new HashSet<>();
                                    CACHE2.put(d, set);
                                }
                                set.add(slimefunItem);
                            }
                        }
                    }

                    String id = slimefunItem.getId();
                    if (SPECIAL_CACHE.containsKey(id)) {
                        Set<String> cache2 = SPECIAL_CACHE.get(id);
                        for (String s : cache2) {
                            for (char c : s.toCharArray()) {
                                char d = Character.toLowerCase(c);
                                CACHE2.putIfAbsent(d, new HashSet<>());
                                CACHE2.get(d).add(slimefunItem);
                            }
                        }
                    }
                }

                Set<SlimefunItemStack> ACCEPTED_ITEMS = new HashSet<>(Arrays.asList(
                        SlimefunItems.BILLON_INGOT, SlimefunItems.SOLDER_INGOT, SlimefunItems.NICKEL_INGOT,
                        SlimefunItems.COBALT_INGOT, SlimefunItems.DURALUMIN_INGOT, SlimefunItems.BRONZE_INGOT,
                        SlimefunItems.BRASS_INGOT, SlimefunItems.ALUMINUM_BRASS_INGOT, SlimefunItems.STEEL_INGOT,
                        SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.ALUMINUM_BRONZE_INGOT,
                        SlimefunItems.CORINTHIAN_BRONZE_INGOT, SlimefunItems.GILDED_IRON, SlimefunItems.REDSTONE_ALLOY,
                        SlimefunItems.HARDENED_METAL_INGOT, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.FERROSILICON,
                        SlimefunItems.ELECTRO_MAGNET, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.HEATING_COIL,
                        SlimefunItems.SYNTHETIC_EMERALD, SlimefunItems.GOLD_4K, SlimefunItems.GOLD_6K, SlimefunItems.GOLD_8K,
                        SlimefunItems.GOLD_10K, SlimefunItems.GOLD_12K, SlimefunItems.GOLD_14K, SlimefunItems.GOLD_16K,
                        SlimefunItems.GOLD_18K, SlimefunItems.GOLD_20K, SlimefunItems.GOLD_22K, SlimefunItems.GOLD_24K
                ));
                Set<String> items = new HashSet<>();
                for (SlimefunItemStack slimefunItemStack : ACCEPTED_ITEMS) {
                    SlimefunItem slimefunItem = slimefunItemStack.getItem();
                    if (slimefunItem != null) {
                        items.add(slimefunItem.getItemName());
                    }
                }
                SPECIAL_CACHE.put("SMART_FACTORY", items);

                timer.logs();
                Debug.debug("Cache initialized.");
            });
            LOADED = true;

            Timer.log();
            Debug.debug("Search Group initialized.");
            Debug.debug("Enabled items: " + ENABLED_ITEMS.size());
            Debug.debug("Available items: " + AVAILABLE_ITEMS.size());
            Debug.debug("Machine blocks cache: " + SPECIAL_CACHE.size());
        }
    }

    public static boolean isInstance(SlimefunItem item, String classSimpleName) {
        Class<?> clazz = item.getClass();
        while (clazz != SlimefunItem.class) {
            if (clazz.getSimpleName().equals(classSimpleName)) {
                return true;
            }
            clazz = clazz.getSuperclass();
        }
        return false;
    }
}
