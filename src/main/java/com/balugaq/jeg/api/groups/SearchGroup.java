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
import com.balugaq.jeg.utils.SpecialMenuProvider;
import com.balugaq.jeg.utils.clickhandler.BeginnerUtils;
import com.balugaq.jeg.utils.clickhandler.GroupLinker;
import com.balugaq.jeg.utils.compatibility.Converter;
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.RandomizedSet;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.matl114.logitech.SlimefunItem.Machines.AbstractMachine;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
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

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
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
    @Deprecated
    public static final Integer ACONTAINER_OFFSET = 50000;
    public static final Map<Character, Reference<Set<SlimefunItem>>> CACHE = new HashMap<>(); // fast way for by item name
    public static final Map<Character, Reference<Set<SlimefunItem>>> CACHE2 = new HashMap<>(); // fast way for by display item name
    public static final Map<String, Reference<Set<String>>> SPECIAL_CACHE = new HashMap<>();
    public static final Set<String> SHARED_CHARS = new HashSet<>();
    public static final Set<String> BLACKLIST = new HashSet<>();
    public static final Boolean SHOW_HIDDEN_ITEM_GROUPS = Slimefun.getConfigManager().isShowHiddenItemGroupsInSearch();
    public static final Integer DEFAULT_HASH_SIZE = 5000;
    public static final Map<SlimefunItem, Integer> ENABLED_ITEMS = new HashMap<>(DEFAULT_HASH_SIZE);
    public static final Set<SlimefunItem> AVAILABLE_ITEMS = new HashSet<>(DEFAULT_HASH_SIZE);
    public static final Integer BACK_SLOT = 1;
    public static final Integer SEARCH_SLOT = 7;
    public static final Integer PREVIOUS_SLOT = 46;
    public static final Integer NEXT_SLOT = 52;
    public static final Integer[] BORDER = new Integer[]{0, 2, 3, 4, 5, 6, 8, 45, 47, 48, 49, 50, 51, 53};
    public static final Integer[] MAIN_CONTENT = new Integer[]{
            9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35,
            36, 37, 38, 39, 40, 41, 42, 43, 44
    };
    public static final JavaPlugin JAVA_PLUGIN = JustEnoughGuide.getInstance();
    public static @NotNull Boolean LOADED = false;
    public final SlimefunGuideImplementation implementation;
    public final Player player;
    public final String searchTerm;
    public final Boolean pinyin;
    public final @NotNull Integer page;
    public final List<SlimefunItem> slimefunItemList;
    public final boolean re_search_when_cache_failed;
    public Map<Integer, SearchGroup> pageMap = new LinkedHashMap<>();

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
        this(implementation, player, searchTerm, pinyin, true);
    }

    /**
     * Constructor for the SearchGroup.
     *
     * @param implementation              The Slimefun guide implementation.
     * @param player                      The player who opened the guide.
     * @param searchTerm                  The search term.
     * @param pinyin                      Whether the search term is in Pinyin.
     * @param re_search_when_cache_failed Whether to re-search when cache failed.
     */
    public SearchGroup(
            SlimefunGuideImplementation implementation,
            @NotNull Player player,
            @NotNull String searchTerm,
            boolean pinyin,
            boolean re_search_when_cache_failed) {
        super(new NamespacedKey(JAVA_PLUGIN, "jeg_search_group_" + UUID.randomUUID()), new ItemStack(Material.BARRIER));
        if (!LOADED) {
            init();
        }
        this.page = 1;
        this.searchTerm = searchTerm;
        this.pinyin = pinyin;
        this.player = player;
        this.re_search_when_cache_failed = re_search_when_cache_failed;
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
        this.re_search_when_cache_failed = searchGroup.re_search_when_cache_failed;
        this.implementation = searchGroup.implementation;
        this.slimefunItemList = searchGroup.slimefunItemList;
        this.pageMap.put(page, this);
    }

    /**
     * Checks if the search filter is applicable.
     *
     * @param slimefunItem The Slimefun item to check.
     * @param searchTerm   The search term.
     * @param pinyin       Whether the search term is in Pinyin.
     * @return True if the search filter is applicable, false otherwise.
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
     * @param itemStack  The item stack to check.
     * @param searchTerm The search term.
     * @param pinyin     Whether the search term is in Pinyin.
     * @return True if the search filter is applicable, false otherwise.
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
     * @param itemName   The item name to check.
     * @param searchTerm The search term.
     * @param pinyin     Whether the search term is in Pinyin.
     * @return True if the search filter is applicable, false otherwise.
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
            final String pinyinFirstLetter = PinyinHelper.toPinyin(itemName, PinyinStyleEnum.FIRST_LETTER, "");
            return pinyinFirstLetter.contains(searchTerm);
        }

        return false;
    }

    /**
     * Filters items based on the given filter type, filter value, and pinyin flag.
     *
     * @param player      The player.
     * @param filterType  The filter type.
     * @param filterValue The filter value.
     * @param pinyin      Whether the search term is in Pinyin.
     * @param items       The list of items to filter.
     * @return The filtered list of items.
     */
    public static @NotNull List<SlimefunItem> filterItems(Player player, @NotNull FilterType filterType, @NotNull String filterValue, boolean pinyin, @NotNull List<SlimefunItem> items) {
        String lowerFilterValue = filterValue.toLowerCase();
        return items.stream().filter(item -> filterType.getFilter().apply(player, item, lowerFilterValue, pinyin)).toList();
    }

    /**
     * Filters items based on the given filter type, filter value, and pinyin flag.
     *
     * @param player      The player.
     * @param filterType  The filter type.
     * @param filterValue The filter value.
     * @param pinyin      Whether the search term is in Pinyin.
     * @param items       The set of items to filter.
     * @return The filtered set of items.
     */
    public static @NotNull Set<SlimefunItem> filterItems(Player player, @NotNull FilterType filterType, @NotNull String filterValue, boolean pinyin, @NotNull Set<SlimefunItem> items) {
        String lowerFilterValue = filterValue.toLowerCase();
        return items.stream().filter(item -> filterType.getFilter().apply(player, item, lowerFilterValue, pinyin)).collect(Collectors.toSet());
    }

    /**
     * Initializes the search group by populating caches and preparing data.
     */
    public static void init() {
        if (!LOADED) {
            LOADED = true;
            Debug.debug("Initializing Search Group...");
            Timer.start();
            Bukkit.getScheduler().runTaskAsynchronously(JAVA_PLUGIN, () -> {
                // Blacklist
                BLACKLIST.add("快捷");

                // Initialize asynchronously
                int i = 0;
                for (SlimefunItem item : Slimefun.getRegistry().getEnabledSlimefunItems()) {
                    try {
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
                                                    Object Ooutput = ReflectionUtil.getValue(item, "output");
                                                    if (Ooutput == null) {
                                                        Object Ogeneration = ReflectionUtil.getValue(item, "generation");
                                                        if (Ogeneration == null) {
                                                            Object Otemplates = ReflectionUtil.getValue(item, "templates");
                                                            if (Otemplates == null) {
                                                                continue;
                                                            }

                                                            // RykenSlimeCustomizer CustomTemplateMachine
                                                            else if (Otemplates instanceof List<?> templates) {
                                                                for (Object template : templates) {
                                                                    Object _Orecipes = ReflectionUtil.getValue(template, "recipes");
                                                                    if (_Orecipes == null) {
                                                                        Method method = ReflectionUtil.getMethod(template.getClass(), "recipes");
                                                                        if (method != null) {
                                                                            try {
                                                                                method.setAccessible(true);
                                                                                _Orecipes = method.invoke(template);
                                                                            } catch (Throwable ignored) {
                                                                            }
                                                                        }
                                                                    }

                                                                    if (_Orecipes instanceof List<?> _recipes) {
                                                                        for (Object _recipe : _recipes) {
                                                                            if (_recipe instanceof MachineRecipe machineRecipe) {
                                                                                ItemStack[] _output = machineRecipe.getOutput();
                                                                                for (var __output : _output) {
                                                                                    cache.add(ItemStackHelper.getDisplayName(__output));
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        // RykenSlimeCustomizer CustomMaterialGenerator
                                                        else if (Ogeneration instanceof List<?> generation) {
                                                            if (!generation.isEmpty()) {
                                                                Object first = generation.get(0);
                                                                if (first instanceof ItemStack) {
                                                                    for (var g : generation) {
                                                                        if (g instanceof ItemStack itemStack) {
                                                                            cache.add(ItemStackHelper.getDisplayName(itemStack));
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    // Chinese Localized SlimeCustomizer CustomMaterialGenerator
                                                    else if (Ooutput instanceof ItemStack output) {
                                                        cache.add(ItemStackHelper.getDisplayName(output));
                                                    }
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
                                    if (!isInstance(item, "ResourceSynthesizer")) {
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
                                    if (!isInstance(item, "GrowingMachine")) {
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
                                    SPECIAL_CACHE.put(id, new SoftReference<>(cache));
                                }
                            }
                        } catch (Throwable ignored) {
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
                SPECIAL_CACHE.put("STONEWORKS_FACTORY", new SoftReference<>(cache));

                // InfinityExpansion VoidHarvester
                SlimefunItem item2 = SlimefunItem.getById("VOID_BIT");
                if (item2 != null) {
                    Set<String> cache2 = new HashSet<>();
                    cache2.add(item2.getItemName());
                    SPECIAL_CACHE.put("VOID_HARVESTER", new SoftReference<>(cache2));
                    SPECIAL_CACHE.put("INFINITY_VOID_HARVESTER", new SoftReference<>(cache2));
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
                            SPECIAL_CACHE.put(((SlimefunItem) card).getId(), new SoftReference<>(cache2));
                        });
                    } catch (Throwable ignored) {
                    }
                }

                for (SlimefunItem slimefunItem : AVAILABLE_ITEMS) {
                    try {
                        if (slimefunItem == null) {
                            continue;
                        }
                        String name = ChatColor.stripColor(slimefunItem.getItemName());
                        for (char c : name.toCharArray()) {
                            char d = Character.toLowerCase(c);
                            CACHE.putIfAbsent(d, new SoftReference<>(new HashSet<>()));
                            Reference<Set<SlimefunItem>> ref = CACHE.get(d);
                            if (ref != null) {
                                Set<SlimefunItem> set = ref.get();
                                if (set != null) {
                                    set.add(slimefunItem);
                                }
                            }
                        }

                        if (JustEnoughGuide.getConfigManager().isPinyinSearch()) {
                            final String pinyinFirstLetter = PinyinHelper.toPinyin(name, PinyinStyleEnum.FIRST_LETTER, "");
                            for (char c : pinyinFirstLetter.toCharArray()) {
                                char d = Character.toLowerCase(c);
                                CACHE.putIfAbsent(d, new SoftReference<>(new HashSet<>()));
                                Reference<Set<SlimefunItem>> ref = CACHE.get(d);
                                if (ref != null) {
                                    Set<SlimefunItem> set = ref.get();
                                    if (set == null) {
                                        set = new HashSet<>();
                                        CACHE.put(d, new SoftReference<>(set));
                                    }
                                    set.add(slimefunItem);
                                }
                            }
                        }

                        List<ItemStack> displayRecipes = null;
                        if (slimefunItem instanceof AContainer ac) {
                            displayRecipes = ac.getDisplayRecipes();
                        } else if (slimefunItem instanceof MultiBlockMachine mb) {
                            try {
                                displayRecipes = mb.getDisplayRecipes();
                            } catch (Throwable e) {
                                Debug.trace(e, "initailizing searching");
                            }
                        } else if (SpecialMenuProvider.ENABLED_LogiTech && SpecialMenuProvider.classLogiTech_CustomSlimefunItem != null && SpecialMenuProvider.classLogiTech_CustomSlimefunItem.isInstance(slimefunItem) && slimefunItem instanceof RecipeDisplayItem rdi) {
                            try {
                                displayRecipes = rdi.getDisplayRecipes();
                            } catch (Throwable e) {
                                Debug.trace(e, "initailizing searching");
                            }
                        }
                        if (displayRecipes != null) {
                            for (ItemStack itemStack : displayRecipes) {
                                if (itemStack != null) {
                                    String name2 = ChatColor.stripColor(ItemStackHelper.getDisplayName(itemStack));
                                    for (char c : name2.toCharArray()) {
                                        char d = Character.toLowerCase(c);
                                        CACHE2.putIfAbsent(d, new SoftReference<>(new HashSet<>()));
                                        Reference<Set<SlimefunItem>> ref = CACHE2.get(d);
                                        if (ref != null) {
                                            Set<SlimefunItem> set = ref.get();
                                            if (set == null) {
                                                set = new HashSet<>();
                                                CACHE2.put(d, new SoftReference<>(set));
                                            }
                                            if (!inBlacklist(slimefunItem)) {
                                                set.add(slimefunItem);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        String id = slimefunItem.getId();
                        if (SPECIAL_CACHE.containsKey(id)) {
                            Reference<Set<String>> ref2 = SPECIAL_CACHE.get(id);
                            if (ref2 != null) {
                                Set<String> cache2 = ref2.get();
                                if (cache2 != null) {
                                    for (String s : cache2) {
                                        for (char c : s.toCharArray()) {
                                            char d = Character.toLowerCase(c);
                                            CACHE2.putIfAbsent(d, new SoftReference<>(new HashSet<>()));
                                            Reference<Set<SlimefunItem>> ref = CACHE2.get(d);
                                            if (ref != null) {
                                                Set<SlimefunItem> set = ref.get();
                                                if (set != null) {
                                                    if (!inBlacklist(slimefunItem)) {
                                                        set.add(slimefunItem);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Throwable ignored) {
                    }
                }

                // FluffyMachines SmartFactory
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
                SPECIAL_CACHE.put("SMART_FACTORY", new SoftReference<>(items));

                // shared cache
                SHARED_CHARS.add("粘黏");
                SHARED_CHARS.add("荧萤");
                SHARED_CHARS.add("机器级");
                SHARED_CHARS.add("灵零");
                SHARED_CHARS.add("动力");
                for (String s : SHARED_CHARS) {
                    Set<SlimefunItem> sharedItems = new HashSet<>();
                    for (char c : s.toCharArray()) {
                        Reference<Set<SlimefunItem>> ref = CACHE.get(c);
                        if (ref == null) {
                            continue;
                        }
                        Set<SlimefunItem> set = ref.get();
                        if (set == null) {
                            continue;
                        }
                        sharedItems.addAll(set);
                    }
                    if (!sharedItems.isEmpty()) {
                        for (char c : s.toCharArray()) {
                            Reference<Set<SlimefunItem>> ref = CACHE.get(c);
                            if (ref != null) {
                                Set<SlimefunItem> set = ref.get();
                                if (set != null) {
                                    set.addAll(sharedItems);
                                    Debug.debug("Shared cache added to CACHE char \"" + c + "\" (" + sharedItems.size() + " items)");
                                }
                            }
                        }
                    }


                    Set<SlimefunItem> sharedItems2 = new HashSet<>();
                    for (char c : s.toCharArray()) {
                        Reference<Set<SlimefunItem>> ref = CACHE2.get(c);
                        if (ref == null) {
                            continue;
                        }
                        Set<SlimefunItem> set = ref.get();
                        if (set == null) {
                            continue;
                        }
                        sharedItems2.addAll(set);
                    }
                    if (!sharedItems2.isEmpty()) {
                        for (char c : s.toCharArray()) {
                            Reference<Set<SlimefunItem>> ref = CACHE2.get(c);
                            if (ref != null) {
                                Set<SlimefunItem> set = ref.get();
                                if (set != null) {
                                    for (SlimefunItem slimefunItem : sharedItems2) {
                                        if (!inBlacklist(slimefunItem)) {
                                            set.add(slimefunItem);
                                        }
                                    }
                                    Debug.debug("Shared cache added to CACHE2 char \"" + c + "\" (" + sharedItems2.size() + " items)");
                                }
                            }
                        }
                    }
                }

                Debug.debug("Cache initialized.");

                Timer.log();
                Debug.debug("Search Group initialized.");
                Debug.debug("Enabled items: " + ENABLED_ITEMS.size());
                Debug.debug("Available items: " + AVAILABLE_ITEMS.size());
                Debug.debug("Machine blocks cache: " + SPECIAL_CACHE.size());
                Debug.debug("Shared cache: " + SHARED_CHARS.size());
                Debug.debug("Cache 1 (Keywords): " + CACHE.size());
                Debug.debug("Cache 2 (Display Recipes): " + CACHE2.size());
            });
        }
    }

    /**
     * Checks if the given Slimefun item is an instance of the specified class.
     *
     * @param item            The Slimefun item.
     * @param classSimpleName The simple name of the class to check against.
     * @return True if the item is an instance of the specified class, false otherwise.
     */
    public static boolean isInstance(@NotNull SlimefunItem item, String classSimpleName) {
        Class<?> clazz = item.getClass();
        while (clazz != SlimefunItem.class) {
            if (clazz.getSimpleName().equals(classSimpleName)) {
                return true;
            }
            clazz = clazz.getSuperclass();
        }
        return false;
    }

    public static boolean inBlacklist(SlimefunItem slimefunItem) {
        return inBlacklist(slimefunItem.getItemName());
    }

    public static boolean inBlacklist(String itemName) {
        for (String s : BLACKLIST) {
            if (ChatColor.stripColor(itemName).contains(s)) {
                return true;
            }
        }
        return false;
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
    @NotNull
    private ChestMenu generateMenu(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
        ChestMenu chestMenu =
                new ChestMenu("你正在搜索: %item%".replace("%item%", ChatUtils.crop(ChatColor.WHITE, searchTerm)));

        chestMenu.setEmptySlotsClickable(false);
        chestMenu.addMenuOpeningHandler(pl -> pl.playSound(pl.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1));

        chestMenu.addItem(BACK_SLOT, ItemStackUtil.getCleanItem(ChestMenuUtils.getBackButton(player, "", "&f左键: &7返回上一页", "&fShift + 左键: &7返回主菜单")));
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
                ItemStack itemstack = ItemStackUtil.getCleanItem(Converter.getItem(slimefunItem.getItem(), meta -> {
                    ItemGroup itemGroup = slimefunItem.getItemGroup();
                    List<String> additionLore = List.of(
                            "",
                            ChatColor.DARK_GRAY + "\u21E8 " + ChatColor.WHITE
                                    + (LocalHelper.getAddonName(itemGroup, slimefunItem.getId())) + ChatColor.WHITE + " - "
                                    + LocalHelper.getDisplayName(itemGroup, player));
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
                BeginnerUtils.applyBeginnersGuide(implementation, chestMenu, MAIN_CONTENT[i]);
                GroupLinker.applyGroupLinker(implementation, chestMenu, MAIN_CONTENT[i]);
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
    public SearchGroup getByPage(int page) {
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
     * Gets all matched items based on the search term and pinyin flag.
     *
     * @param p          The player.
     * @param searchTerm The search term.
     * @param pinyin     Whether the search term is in Pinyin.
     * @return The matched items.
     */
    @Deprecated
    public @NotNull List<SlimefunItem> getAllMatchedItems(@NotNull Player p, @NotNull String searchTerm, boolean pinyin) {
        return filterItems(p, searchTerm, pinyin);
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

    /**
     * Filters items based on the search term and pinyin flag.
     *
     * @param player     The player.
     * @param searchTerm The search term.
     * @param pinyin     Whether the search term is in Pinyin.
     * @return The matched items.
     */
    public @NotNull List<SlimefunItem> filterItems(@NotNull Player player, @NotNull String searchTerm, boolean pinyin) {
        StringBuilder actualSearchTermBuilder = new StringBuilder();
        String[] split = searchTerm.split(" ");
        Map<FilterType, String> filters = new HashMap<>();
        for (String s : split) {
            boolean isFilter = false;
            for (FilterType filterType : FilterType.values()) {
                if (s.startsWith(filterType.getSymbol()) && s.length() > filterType.getSymbol().length()) {
                    isFilter = true;
                    String filterValue = s.substring(filterType.getSymbol().length());
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
            String flag = filterType.getSymbol();
            // Quote the flag to be used as a literal replacement
            actualSearchTerm = actualSearchTerm.replaceAll(Pattern.quote(flag), Matcher.quoteReplacement(flag));
        }
        Set<SlimefunItem> merge = new HashSet<>(36 * 4);
        // The unfiltered items
        Set<SlimefunItem> items = new HashSet<>(AVAILABLE_ITEMS
                .stream()
                .filter(item -> item.getItemGroup().isAccessible(player)).toList());

        if (!actualSearchTerm.isBlank()) {
            Set<SlimefunItem> nameMatched = new HashSet<>();
            Set<SlimefunItem> allMatched = null;
            for (char c : actualSearchTerm.toCharArray()) {
                Set<SlimefunItem> cache;
                Reference<Set<SlimefunItem>> ref = CACHE.get(c);
                if (ref == null) {
                    cache = new HashSet<>();
                } else {
                    cache = ref.get();
                }
                if (cache == null) {
                    cache = new HashSet<>();
                }
                if (allMatched == null) {
                    allMatched = new HashSet<>(cache);
                } else {
                    allMatched.retainAll(new HashSet<>(cache));
                }
            }
            if (allMatched != null) {
                nameMatched.addAll(allMatched);
            }
            Set<SlimefunItem> machineMatched = new HashSet<>();
            Set<SlimefunItem> allMatched2 = null;
            for (char c : actualSearchTerm.toCharArray()) {
                Set<SlimefunItem> cache;
                Reference<Set<SlimefunItem>> ref = CACHE2.get(c);
                if (ref == null) {
                    cache = new HashSet<>();
                } else {
                    cache = ref.get();
                }
                if (cache == null) {
                    cache = new HashSet<>();
                }
                if (allMatched2 == null) {
                    allMatched2 = new HashSet<>(cache);
                } else {
                    allMatched2.retainAll(new HashSet<>(cache));
                }
            }
            if (allMatched2 != null) {
                machineMatched.addAll(allMatched2);
            }
            Debug.debug("Name matched: " + nameMatched.size());
            Debug.debug("Machine matched: " + machineMatched.size());
            merge.addAll(nameMatched);
            merge.addAll(machineMatched);
            if (this.re_search_when_cache_failed) {
                if (nameMatched.isEmpty()) {
                    Debug.debug("Re-searching item name by filters (Normal search)");
                    Set<SlimefunItem> clone = new HashSet<>(items);
                    Set<SlimefunItem> result = filterItems(FilterType.BY_ITEM_NAME, actualSearchTerm, pinyin, clone);
                    merge.addAll(result);
                }
                if (machineMatched.isEmpty()) {
                    Debug.debug("Re-searching display item name by filters (Normal search)");
                    Set<SlimefunItem> clone = new HashSet<>(items);
                    Set<SlimefunItem> result = filterItems(FilterType.BY_DISPLAY_ITEM_NAME, actualSearchTerm, pinyin, clone);
                    merge.addAll(result);
                }
            }
        }

        // Filter items
        if (!filters.isEmpty()) {
            for (Map.Entry<FilterType, String> entry : filters.entrySet()) {
                items = filterItems(entry.getKey(), entry.getValue(), pinyin, items);
            }

            merge.addAll(items);
        }

        return merge.stream().sorted((a, b) -> ENABLED_ITEMS.get(a) < ENABLED_ITEMS.get(b) ? -1 : 1).toList();
    }

    /**
     * Filters items based on the given filter type, filter value, and pinyin flag.
     *
     * @param filterType  The filter type.
     * @param filterValue The filter value.
     * @param pinyin      Whether the search term is in Pinyin.
     * @param items       The list of items to filter.
     * @return The filtered list of items.
     */
    public @NotNull List<SlimefunItem> filterItems(@NotNull FilterType filterType, @NotNull String filterValue, boolean pinyin, @NotNull List<SlimefunItem> items) {
        String lowerFilterValue = filterValue.toLowerCase();
        return items.stream().filter(item -> filterType.getFilter().apply(player, item, lowerFilterValue, pinyin)).toList();
    }

    /**
     * Filters items based on the given filter type, filter value, and pinyin flag.
     *
     * @param filterType  The filter type.
     * @param filterValue The filter value.
     * @param pinyin      Whether the search term is in Pinyin.
     * @param items       The set of items to filter.
     * @return The filtered set of items.
     */
    public @NotNull Set<SlimefunItem> filterItems(@NotNull FilterType filterType, @NotNull String filterValue, boolean pinyin, @NotNull Set<SlimefunItem> items) {
        String lowerFilterValue = filterValue.toLowerCase();
        return items.stream().filter(item -> filterType.getFilter().apply(player, item, lowerFilterValue, pinyin)).collect(Collectors.toSet());
    }
}
