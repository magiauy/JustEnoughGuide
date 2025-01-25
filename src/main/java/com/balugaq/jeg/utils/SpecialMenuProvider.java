package com.balugaq.jeg.utils;

import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import lombok.experimental.UtilityClass;
import me.matl114.logitech.Utils.UtilClass.MenuClass.CustomMenu;
import me.matl114.logitech.Utils.UtilClass.MenuClass.CustomMenuHandler;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

// todo: check research, fallback
@SuppressWarnings("unchecked")
@UtilityClass
public class SpecialMenuProvider {
    public static final int COMMON_RECIPE_LENGTH = 9;
    public static boolean ENABLED_FinalTECH;
    public static boolean ENABLED_Nexcavate;
    public static boolean ENABLED_LogiTech;
    public static boolean ENABLED_InfinityExpansion;
    public static boolean ENABLED_ObsidianExpansion;
    // FinalTECH | FinalTECH-Changed
    public static @org.jetbrains.annotations.Nullable Method methodRecipeItemGroup_getBySlimefunItem = null;
    // Nexcavate
    public static @org.jetbrains.annotations.Nullable Method methodPlayerProgress_get = null;
    public static @org.jetbrains.annotations.Nullable Method methodNEGUI_openRecipe = null;
    public static @org.jetbrains.annotations.Nullable Method methodNEGUI_openResearchScreen = null;
    public static @org.jetbrains.annotations.Nullable Method methodNexcavateRegistry_getResearchMap = null;
    public static @org.jetbrains.annotations.Nullable Object objectNexcavate_registry = null;
    // LogiTech
    public static @org.jetbrains.annotations.Nullable Method methodMenuUtils_createItemRecipeDisplay = null;
    public static @org.jetbrains.annotations.Nullable Method methodMenuFactory_buildGuide = null;
    public static @org.jetbrains.annotations.Nullable Method methodCustomMenu_open = null;
    // InfinityExpansion
    public static @org.jetbrains.annotations.Nullable Method methodInfinityGroup_openInfinityRecipe = null;
    public static @org.jetbrains.annotations.Nullable Method methodObsidianExpansion_openFORGERecipe = null;
    // ObsidianExpansion
    public static @org.jetbrains.annotations.Nullable Constructor<?> constructorInfinityExpansion_BackEntry = null;
    public static @org.jetbrains.annotations.Nullable Constructor<?> constructorObsidianExpansion_BackEntry = null;

    static {
        ENABLED_FinalTECH = Bukkit.getPluginManager().isPluginEnabled("FinalTECH") || Bukkit.getPluginManager().isPluginEnabled("FinalTECH-Changed");
        ENABLED_Nexcavate = Bukkit.getPluginManager().isPluginEnabled("Nexcavate");
        ENABLED_LogiTech = Bukkit.getPluginManager().isPluginEnabled("LogiTech");
        ENABLED_InfinityExpansion = Bukkit.getPluginManager().isPluginEnabled("InfinityExpansion");
        ENABLED_ObsidianExpansion = Bukkit.getPluginManager().isPluginEnabled("ObsidianExpansion");
        // FinalTECH | FinalTECH-Changed
        try {
            Method method = ReflectionUtil.getMethod(Class.forName("io.taraxacum.finaltech.core.group.RecipeItemGroup"), "getBySlimefunItem", Player.class, PlayerProfile.class, SlimefunGuideMode.class, SlimefunItem.class, int.class);
            if (method != null) {
                method.setAccessible(true);
                methodRecipeItemGroup_getBySlimefunItem = method;
            }
        } catch (ClassNotFoundException ignored) {
        }
        // Nexcavate
        try {
            Method method = ReflectionUtil.getMethod(Class.forName("me.char321.nexcavate.research.progress.PlayerProgress"), "get", Player.class);
            if (method != null) {
                method.setAccessible(true);
                methodPlayerProgress_get = method;
            }
        } catch (ClassNotFoundException ignored) {
        }
        try {
            Method method = ReflectionUtil.getMethod(Class.forName("me.char321.nexcavate.gui.NEGUI"), "openRecipe");
            if (method != null) {
                method.setAccessible(true);
                methodNEGUI_openRecipe = method;
            }
        } catch (ClassNotFoundException ignored) {
        }
        try {
            Method method = ReflectionUtil.getMethod(Class.forName("me.char321.nexcavate.gui.NEGUI"), "openResearchScreen");
            if (method != null) {
                method.setAccessible(true);
                methodNEGUI_openResearchScreen = method;
            }
        } catch (ClassNotFoundException ignored) {
        }
        try {
            Method method = ReflectionUtil.getMethod(Class.forName("me.char321.nexcavate.NexcavateRegistry"), "getResearchMap");
            if (method != null) {
                method.setAccessible(true);
                methodNexcavateRegistry_getResearchMap = method;
            }
        } catch (ClassNotFoundException ignored) {
        }
        try {
            Object instance = ReflectionUtil.getStaticValue(Class.forName("me.char321.nexcavate.Nexcavate"), "instance");
            objectNexcavate_registry = ReflectionUtil.getValue(instance, "registry");
        } catch (ClassNotFoundException ignored) {
        }
        // LogiTech
        try {
            Method method = ReflectionUtil.getMethod(Class.forName("me.matl114.logitech.Utils.MenuUtils"), "createItemRecipeDisplay", 3);
            if (method != null) {
                method.setAccessible(true);
                methodMenuUtils_createItemRecipeDisplay = method;
            }
        } catch (ClassNotFoundException ignored) {
        }
        try {
            Method method = ReflectionUtil.getMethod(Class.forName("me.matl114.logitech.Utils.UtilClass.MenuClass.MenuFactory"), "buildGuide");
            if (method != null) {
                method.setAccessible(true);
                methodMenuFactory_buildGuide = method;
            }
        } catch (ClassNotFoundException ignored) {
        }
        try {
            Method method = ReflectionUtil.getMethod(Class.forName("me.matl114.logitech.Utils.UtilClass.MenuClass.CustomMenu"), "open");
            if (method != null) {
                method.setAccessible(true);
                methodCustomMenu_open = method;
            }
        } catch (ClassNotFoundException ignored) {
        }
        // InfinityExpansion
        try {
            Constructor<?> constructor = ReflectionUtil.getConstructor(Class.forName("io.github.mooy1.infinityexpansion.categories.InfinityGroup$BackEntry"), BlockMenu.class, PlayerProfile.class, SlimefunGuideImplementation.class);
            if (constructor != null) {
                constructor.setAccessible(true);
                constructorInfinityExpansion_BackEntry = constructor;
            }
        } catch (ClassNotFoundException ignored) {
        }
        try {
            Method method = ReflectionUtil.getMethod(Class.forName("io.github.mooy1.infinityexpansion.categories.InfinityGroup"), "openInfinityRecipe");
            if (method != null) {
                method.setAccessible(true);
                methodInfinityGroup_openInfinityRecipe = method;
            }
        } catch (ClassNotFoundException ignored) {
        }
        // ObsidianExpansion
        try {
            Constructor<?> constructor = ReflectionUtil.getConstructor(Class.forName("me.lucasgithuber.obsidianexpansion.utils.ObsidianForgeGroup$BackEntry"), BlockMenu.class, PlayerProfile.class, SlimefunGuideImplementation.class);
            if (constructor != null) {
                constructor.setAccessible(true);
                constructorObsidianExpansion_BackEntry = constructor;
            }
        } catch (ClassNotFoundException ignored) {
        }
        try {
            Method method = ReflectionUtil.getMethod(Class.forName("me.lucasgithuber.obsidianexpansion.utils.ObsidianForgeGroup"), "openFORGERecipe");
            if (method != null) {
                method.setAccessible(true);
                methodObsidianExpansion_openFORGERecipe = method;
            }
        } catch (ClassNotFoundException ignored) {
        }

        Debug.debug("SpecialMenuProvider initialized");
        Debug.debug("ENABLED_FinalTECH: " + ENABLED_FinalTECH);
        Debug.debug("ENABLED_Nexcavate: " + ENABLED_Nexcavate);
        Debug.debug("ENABLED_LogiTech: " + ENABLED_LogiTech);
        Debug.debug("ENABLED_InfinityExpansion: " + ENABLED_InfinityExpansion);
        Debug.debug("ENABLED_ObsidianExpansion: " + ENABLED_ObsidianExpansion);
        Debug.debug("-------------FinalTECH | FinalTECH-Changed-------------");
        Debug.debug("methodRecipeItemGroup_getBySlimefunItem: " + (methodRecipeItemGroup_getBySlimefunItem != null));
        Debug.debug("-------------Nexcavate------------");
        Debug.debug("methodPlayerProgress_get: " + (methodPlayerProgress_get != null));
        Debug.debug("methodNEGUI_openRecipe: " + (methodNEGUI_openRecipe != null));
        Debug.debug("methodNEGUI_openResearchScreen: " + (methodNEGUI_openResearchScreen != null));
        Debug.debug("-------------LogiTech-------------");
        Debug.debug("methodMenuUtils_createItemRecipeDisplay: " + (methodMenuUtils_createItemRecipeDisplay != null));
        Debug.debug("methodMenuFactory_build: " + (methodMenuFactory_buildGuide != null));
        Debug.debug("methodCustomMenu_open: " + (methodCustomMenu_open != null));
        Debug.debug("-------------InfinityExpansion----------");
        Debug.debug("methodInfinityGroup_openInfinityRecipe: " + (methodInfinityGroup_openInfinityRecipe != null));
        Debug.debug("constructorInfinityExpansion_BackEntry: " + (constructorInfinityExpansion_BackEntry != null));
        Debug.debug("-------------ObsidianExpansion----------");
        Debug.debug("methodObsidianExpansion_openFORGERecipe: " + (methodObsidianExpansion_openFORGERecipe != null));
        Debug.debug("constructorObsidianExpansion_BackEntry: " + (constructorObsidianExpansion_BackEntry != null));
    }

    @Nullable
    public static FlexItemGroup getFinalTECHRecipeItemGroup(@Nonnull Player player, @Nonnull PlayerProfile playerProfile, @Nonnull SlimefunGuideMode slimefunGuideMode, @Nonnull SlimefunItem slimefunItem) throws InvocationTargetException, IllegalAccessException {
        if (!ENABLED_FinalTECH) {
            return null;
        }

        if (methodRecipeItemGroup_getBySlimefunItem == null) {
            return null;
        }
        methodRecipeItemGroup_getBySlimefunItem.setAccessible(true);
        return (FlexItemGroup) methodRecipeItemGroup_getBySlimefunItem.invoke(null, player, playerProfile, slimefunGuideMode, slimefunItem, null);
    }

    public static boolean isSpecialItem(@Nonnull SlimefunItem slimefunItem) {
        return isFinalTECHItem(slimefunItem) || isNexcavateItem(slimefunItem) || isLogiTechItem(slimefunItem) || isInfinityItem(slimefunItem) || isObsidianForgeItem(slimefunItem);
    }

    public static boolean isFinalTECHItem(@Nonnull SlimefunItem slimefunItem) {
        if (!ENABLED_FinalTECH) {
            return false;
        }

        String addonName = slimefunItem.getAddon().getName();
        if ("FinalTECH".equals(addonName) || "FinalTECH-Changed".equals(addonName)) {
            if (slimefunItem.getRecipe().length > COMMON_RECIPE_LENGTH) {
                return true;
            }
        }
        return false;
    }

    public static void openNexcavateGuide(@Nonnull Player player, @Nonnull SlimefunItem slimefunItem) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        if (!isNexcavateItem(slimefunItem)) {
            return;
        }

        ItemStack item = slimefunItem.getItem();
        if (methodNexcavateRegistry_getResearchMap == null) {
            return;
        }

        Object research = null;
        if (objectNexcavate_registry == null) {
            return;
        }
        Map<NamespacedKey, Object> researchMap = (Map<NamespacedKey, Object>) methodNexcavateRegistry_getResearchMap.invoke(objectNexcavate_registry);
        for (Object lresearch : researchMap.values()) {
            SlimefunItem NEItem = (SlimefunItem) ReflectionUtil.getValue(lresearch, "item");
            if (NEItem == null) {
                continue;
            }
            // No material conflict in Nexcavate
            if (NEItem.getItem().getType() == item.getType()) {
                research = lresearch;
                break;
            }
        }

        if (research == null) {
            return;
        }

        if (isPlayerResearchedNexcavate(player, research)) {
            methodNEGUI_openRecipe.invoke(null, player, research);
        } else {
            methodNEGUI_openResearchScreen.invoke(null, player);
        }
    }

    public static boolean isPlayerResearchedNexcavate(@Nonnull Player player, @Nonnull Object research) throws InvocationTargetException, IllegalAccessException {
        if (!ENABLED_Nexcavate) {
            return false;
        }

        if (methodPlayerProgress_get == null) {
            return false;
        }
        methodPlayerProgress_get.setAccessible(true);
        Object playerProgress = methodPlayerProgress_get.invoke(null, player);
        if (playerProgress == null) {
            return false;
        }

        Method method = ReflectionUtil.getMethod(playerProgress.getClass(), "isResearched", NamespacedKey.class);
        if (method == null) {
            return false;
        }
        method.setAccessible(true);
        NamespacedKey key = (NamespacedKey) ReflectionUtil.getValue(research, "key");
        return (boolean) method.invoke(playerProgress, key);
    }

    public static boolean isNexcavateItem(@Nonnull SlimefunItem slimefunItem) {
        if (!ENABLED_Nexcavate) {
            return false;
        }

        String addonName = slimefunItem.getAddon().getName();
        if ("Nexcavate".equals(addonName)) {
            for (ItemStack itemStack : slimefunItem.getRecipe()) {
                if (itemStack != null) {
                    // Go to fallback
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static void openLogiTechMenu(@Nonnull Player player, @Nonnull PlayerProfile playerProfile, @Nonnull SlimefunItem slimefunItem) throws InvocationTargetException, IllegalAccessException {
        if (!isLogiTechItem(slimefunItem)) {
            return;
        }

        if (methodMenuUtils_createItemRecipeDisplay == null) {
            return;
        }

        Object menuFactory = methodMenuUtils_createItemRecipeDisplay.invoke(null, slimefunItem, new CustomMenuHandlerImpl(), null);
        if (menuFactory == null) {
            return;
        }

        if (methodMenuFactory_buildGuide == null) {
            return;
        }
        Object menu = methodMenuFactory_buildGuide.invoke(menuFactory, null, null);
        if (menu == null) {
            return;
        }

        if (methodCustomMenu_open == null) {
            return;
        }

        methodCustomMenu_open.invoke(menu, player);
        insertUselessHistory(playerProfile);
    }

    public static boolean isLogiTechItem(@Nonnull SlimefunItem slimefunItem) {
        if (!ENABLED_LogiTech) {
            return false;
        }

        String addonName = slimefunItem.getAddon().getName();
        if ("LogiTech".equals(addonName)) {
            if (slimefunItem.getRecipe().length > COMMON_RECIPE_LENGTH) {
                return true;
            }
        }
        return false;
    }

    public static void openInfinityMenu(@Nonnull Player player, @Nonnull PlayerProfile playerProfile, @Nonnull SlimefunItem slimefunItem, @Nonnull SlimefunGuideMode slimefunGuideMode) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!ENABLED_InfinityExpansion) {
            return;
        }

        if (isInfinityItem(slimefunItem)) {
            if (constructorInfinityExpansion_BackEntry == null || methodInfinityGroup_openInfinityRecipe == null) {
                return;
            }
            Object backEntry = constructorInfinityExpansion_BackEntry.newInstance(null, playerProfile, Slimefun.getRegistry().getSlimefunGuide(slimefunGuideMode));
            methodInfinityGroup_openInfinityRecipe.invoke(null, player, slimefunItem.getId(), backEntry);
            insertUselessHistory(playerProfile);
        }
    }

    public static boolean isInfinityItem(@Nonnull SlimefunItem slimefunItem) {
        if (!ENABLED_InfinityExpansion) {
            return false;
        }

        String addonName = slimefunItem.getAddon().getName();
        if ("InfinityExpansion".equals(addonName)) {
            if (slimefunItem.getRecipe().length > COMMON_RECIPE_LENGTH) {
                return true;
            }
        }
        return false;
    }

    public static void openObsidianForgeMenu(@Nonnull Player player, @Nonnull PlayerProfile playerProfile, @Nonnull SlimefunItem slimefunItem, @Nonnull SlimefunGuideMode slimefunGuideMode) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!ENABLED_ObsidianExpansion) {
            return;
        }
        if (isObsidianForgeItem(slimefunItem)) {
            if (constructorObsidianExpansion_BackEntry == null || methodObsidianExpansion_openFORGERecipe == null) {
                return;
            }
            Object backEntry = constructorObsidianExpansion_BackEntry.newInstance(null, playerProfile, Slimefun.getRegistry().getSlimefunGuide(slimefunGuideMode));
            methodObsidianExpansion_openFORGERecipe.invoke(null, player, slimefunItem.getId(), backEntry);
            insertUselessHistory(playerProfile);
        }
    }

    public static boolean isObsidianForgeItem(@Nonnull SlimefunItem slimefunItem) {
        if (!ENABLED_ObsidianExpansion) {
            return false;
        }

        String addonName = slimefunItem.getAddon().getName();
        if ("ObsidianExpansion".equals(addonName)) {
            if (slimefunItem.getRecipe().length > COMMON_RECIPE_LENGTH) {
                return true;
            }
        }
        return false;
    }

    public static boolean open(@Nonnull Player player, @Nonnull PlayerProfile playerProfile, @Nonnull SlimefunGuideMode slimefunGuideMode, @Nonnull SlimefunItem slimefunItem) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException {
        if (player == null) {
            return false;
        }
        if (isFinalTECHItem(slimefunItem)) {
            FlexItemGroup flexItemGroup = getFinalTECHRecipeItemGroup(player, playerProfile, slimefunGuideMode, slimefunItem);
            if (flexItemGroup != null) {
                flexItemGroup.open(player, playerProfile, slimefunGuideMode);
                Debug.debug("Opened FinalTECH special menu");
                return true;
            }
        } else if (isNexcavateItem(slimefunItem)) {
            openNexcavateGuide(player, slimefunItem);
            Debug.debug("Opened Nexcavate special menu");
            return true;
        } else if (isLogiTechItem(slimefunItem)) {
            openLogiTechMenu(player, playerProfile, slimefunItem);
            Debug.debug("Opened LogiTech special menu");
            return true;
        } else if (isInfinityItem(slimefunItem)) {
            openInfinityMenu(player, playerProfile, slimefunItem, slimefunGuideMode);
            Debug.debug("Opened InfinityExpansion special menu");
            return true;
        } else if (isObsidianForgeItem(slimefunItem)) {
            openObsidianForgeMenu(player, playerProfile, slimefunItem, slimefunGuideMode);
            Debug.debug("Opened ObsidianExpansion special menu");
            return true;
        }
        return false;
    }

    public static void fallbackOpen(@Nonnull Player player, @Nonnull PlayerProfile playerProfile, @Nonnull SlimefunGuideMode slimefunGuideMode, @Nonnull SlimefunItem slimefunItem) {
        SlimefunGuideImplementation implementation = Slimefun.getRegistry().getSlimefunGuide(slimefunGuideMode);
        if (implementation instanceof JEGSlimefunGuideImplementation jeg) {
            jeg.displayItem(playerProfile, slimefunItem, true, false);
        } else {
            implementation.displayItem(playerProfile, slimefunItem, true);
        }
    }

    public void insertUselessHistory(@NotNull PlayerProfile playerProfile) {
        playerProfile.getGuideHistory().add("undefined");
    }

    public class CustomMenuHandlerImpl implements CustomMenuHandler {
        @Override
        public ChestMenu.@NotNull MenuClickHandler getInstance(CustomMenu menu) {
            return (p, s, i, a) -> {
                PlayerProfile.find(p).ifPresent(playerProfile -> {
                    playerProfile.getGuideHistory().goBack(Slimefun.getRegistry().getSlimefunGuide(SlimefunGuideMode.SURVIVAL_MODE));
                });
                return false;
            };
        }
    }
}
