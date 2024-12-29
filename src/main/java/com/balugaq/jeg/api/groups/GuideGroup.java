package com.balugaq.jeg.api.groups;

import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.ItemStackUtil;
import com.google.common.base.Preconditions;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This is the base class for all guide groups.
 * It provides a simple way to add guides to a group and open them in a chest menu.
 * The group can be customized by overriding the following methods:
 * - getSize() - returns the size of the chest menu
 * - isClassic() - returns whether the chest menu should be in classic mode
 * - getContentSlots() - returns the slots where the guides should be placed
 * - getBackSlot() - returns the slot where the back button should be placed
 *
 * @author balugaq
 * @since 1.3
 */
@SuppressWarnings({"deprecation", "unused"})
@Getter
@NotDisplayInCheatMode
public abstract class GuideGroup extends FlexItemGroup {
    private final Set<Integer> slots = new HashSet<>();
    private final Map<Integer, ItemStack> contents = new HashMap<>();
    private final Map<Integer, ChestMenu.MenuClickHandler> clickHandlers = new HashMap<>();

    protected GuideGroup(@NotNull NamespacedKey key, @NotNull ItemStack icon) {
        super(key, icon);
    }

    protected GuideGroup(@NotNull NamespacedKey key, @NotNull ItemStack icon, int tier) {
        super(key, icon, tier);
    }

    @NotNull
    public GuideGroup addGuide(@Range(from = 9, to = 44) int slot, @NotNull ItemStack itemStack, @NotNull ChestMenu.MenuClickHandler handler) {
        Preconditions.checkArgument(slot >= 9 && slot <= 44, "Slot must be between 9 and 44");
        Preconditions.checkArgument(itemStack != null, "Item must not be null");
        Preconditions.checkArgument(handler != null, "Handler must not be null");
        Preconditions.checkArgument(itemStack.getType() != Material.AIR, "Item must not be air");
        Preconditions.checkArgument(itemStack.getType().isItem(), "Item must be an item");
        Preconditions.checkArgument(slots.size() <= getContentSlots().length, "Too many guides in this group. Maximum of 36 allowed.");

        slots.add(slot);
        contents.put(slot, itemStack);
        clickHandlers.put(slot, handler);
        return this;
    }

    @NotNull
    public GuideGroup addGuide(@Range(from = 9, to = 44) int slot, @NotNull ItemStack itemStack) {
        return addGuide(slot, itemStack, ChestMenuUtils.getEmptyClickHandler());
    }

    @NotNull
    public GuideGroup addGuide(@NotNull ItemStack itemStack, @NotNull ChestMenu.MenuClickHandler handler) {
        return addGuide(findEmptySlot(), itemStack, handler);
    }

    @NotNull
    public GuideGroup addGuide(@NotNull ItemStack itemStack) {
        return addGuide(itemStack, ChestMenuUtils.getEmptyClickHandler());
    }

    private int findEmptySlot() {
        for (int i = 0; i < 54; i++) {
            if (!slots.contains(i)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean isVisible(@NotNull Player player, @NotNull PlayerProfile playerProfile, @NotNull SlimefunGuideMode slimefunGuideMode) {
        return true;
    }

    @Override
    public void open(@NotNull Player player, @NotNull PlayerProfile playerProfile, SlimefunGuideMode slimefunGuideMode) {
        SlimefunGuideImplementation guide = GuideUtil.getGuide(player, slimefunGuideMode);
        playerProfile.getGuideHistory().add(this, 1);
        if (guide instanceof JEGSlimefunGuideImplementation jeg) {
            ChestMenu menu = new ChestMenu(getDisplayName(player));
            menu.setSize(getSize());
            if (isClassic()) {
                jeg.createHeader(player, playerProfile, menu);
            }
            menu.addItem(getBackSlot(), ItemStackUtil.getCleanItem(ChestMenuUtils.getBackButton(player)));
            menu.addMenuClickHandler(getBackSlot(), (pl, s, is, action) -> {
                GuideHistory guideHistory = playerProfile.getGuideHistory();
                if (action.isShiftClicked()) {
                    SlimefunGuide.openMainMenu(playerProfile, slimefunGuideMode, guideHistory.getMainMenuPage());
                } else {
                    guideHistory.goBack(Slimefun.getRegistry().getSlimefunGuide(SlimefunGuideMode.SURVIVAL_MODE));
                }
                return false;
            });

            for (Map.Entry<Integer, ItemStack> entry : contents.entrySet()) {
                menu.addItem(entry.getKey(), entry.getValue());
            }

            for (Map.Entry<Integer, ChestMenu.MenuClickHandler> entry : clickHandlers.entrySet()) {
                menu.addMenuClickHandler(entry.getKey(), entry.getValue());
            }

            menu.open(player);
        } else {
            player.sendMessage("§cJEG 模块未启用。你不能打开 JEG 使用指南。");
        }
    }

    public abstract int getSize();

    public abstract boolean isClassic();

    public abstract int[] getContentSlots();
    public abstract int getBackSlot();
}
