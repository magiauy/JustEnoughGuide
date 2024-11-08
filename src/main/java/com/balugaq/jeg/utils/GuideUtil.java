package com.balugaq.jeg.utils;

import com.balugaq.jeg.guide.CheatGuideImplementation;
import com.balugaq.jeg.guide.SurvivalGuideImplementation;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Final_ROOT, balugaq
 * @since 2.0
 */
@UtilityClass
public class GuideUtil {
    private static final SurvivalGuideImplementation SURVIVAL_GUIDE_IMPLEMENTATION = new SurvivalGuideImplementation();
    private static final CheatGuideImplementation CHEAT_GUIDE_IMPLEMENTATION = new CheatGuideImplementation();
    private static final ItemStack BOOK_MARK_MENU_BUTTON = ItemStackUtil.getCleanItem(new CustomItemStack(
            Material.NETHER_STAR,
            "&e&l收藏物列表"
    ));
    private static final ItemStack ITEM_MARK_MENU_BUTTON = ItemStackUtil.getCleanItem(new CustomItemStack(
            Material.WRITABLE_BOOK,
            "&e&l收藏物品"
    ));
    @ParametersAreNonnullByDefault
    public static void openMainMenuAsync(Player player, SlimefunGuideMode mode, int selectedPage) {
        if (!PlayerProfile.get(player, profile -> Slimefun.runSync(() -> openMainMenu(player, profile, mode, selectedPage)))) {
            Slimefun.getLocalization().sendMessage(player, "messages.opening-guide");
        }
    }

    @ParametersAreNonnullByDefault
    public static void openMainMenu(Player player, PlayerProfile profile, SlimefunGuideMode mode, int selectedPage) {
        getGuide(player, mode).openMainMenu(profile, selectedPage);
    }

    public static SlimefunGuideImplementation getGuide(Player player, SlimefunGuideMode mode) {
        if (mode == SlimefunGuideMode.SURVIVAL_MODE) {
            return SURVIVAL_GUIDE_IMPLEMENTATION;
        }
        if (player.isOp() && mode == SlimefunGuideMode.CHEAT_MODE) {
            return CHEAT_GUIDE_IMPLEMENTATION;
        }

        return SURVIVAL_GUIDE_IMPLEMENTATION;
    }

    public static void removeLastEntry(@Nonnull GuideHistory guideHistory) {
        try {
            Method getLastEntry = guideHistory.getClass().getDeclaredMethod("getLastEntry", boolean.class);
            getLastEntry.setAccessible(true);
            getLastEntry.invoke(guideHistory, true);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public ItemStack getBookMarkMenuButton() {
        return BOOK_MARK_MENU_BUTTON;
    }

    public ItemStack getItemMarkMenuButton() {
        return ITEM_MARK_MENU_BUTTON;
    }
}
