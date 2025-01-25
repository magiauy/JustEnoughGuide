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

    /**
     * Creates a new guide group with the given key and icon.
     *
     * @param key  The key of the group.
     * @param icon The icon of the group.
     */
    protected GuideGroup(@NotNull NamespacedKey key, @NotNull ItemStack icon) {
        super(key, icon);
    }

    /**
     * Creates a new guide group with the given key, icon, and tier.
     *
     * @param key  The key of the group.
     * @param icon The icon of the group.
     * @param tier The tier of the group.
     */
    protected GuideGroup(@NotNull NamespacedKey key, @NotNull ItemStack icon, int tier) {
        super(key, icon, tier);
    }

    /**
     * Adds a guide to the group.
     *
     * @param slot      The slot where the guide should be placed.
     * @param itemStack The item stack representing the guide.
     * @param handler   The click handler for the guide.
     * @return The group itself.
     */
    @NotNull
    public GuideGroup addGuide(
            @Range(from = 9, to = 44) int slot,
            @NotNull ItemStack itemStack,
            @NotNull ChestMenu.MenuClickHandler handler) {
        Preconditions.checkArgument(slot >= 9 && slot <= 44, "Slot must be between 9 and 44");
        Preconditions.checkArgument(itemStack != null, "Item must not be null");
        Preconditions.checkArgument(handler != null, "Handler must not be null");
        Preconditions.checkArgument(itemStack.getType() != Material.AIR, "Item must not be air");
        Preconditions.checkArgument(itemStack.getType().isItem(), "Item must be an item");
        Preconditions.checkArgument(
                slots.size() <= getContentSlots().length, "Too many guides in this group. Maximum of 36 allowed.");

        slots.add(slot);
        contents.put(slot, itemStack);
        clickHandlers.put(slot, handler);
        return this;
    }

    /**
     * Adds a guide to the group.
     *
     * @param slot      The slot where the guide should be placed.
     * @param itemStack The item stack representing the guide.
     * @return The group itself.
     */
    @NotNull
    public GuideGroup addGuide(@Range(from = 9, to = 44) int slot, @NotNull ItemStack itemStack) {
        return addGuide(slot, itemStack, ChestMenuUtils.getEmptyClickHandler());
    }

    /**
     * Adds a guide to the group.
     *
     * @param itemStack The item stack representing the guide.
     * @param handler   The click handler for the guide.
     * @return The group itself.
     */
    @NotNull
    public GuideGroup addGuide(@NotNull ItemStack itemStack, @NotNull ChestMenu.MenuClickHandler handler) {
        return addGuide(findEmptySlot(), itemStack, handler);
    }

    /**
     * Adds a guide to the group.
     *
     * @param itemStack The item stack representing the guide.
     * @return The group itself.
     */
    @NotNull
    public GuideGroup addGuide(@NotNull ItemStack itemStack) {
        return addGuide(itemStack, ChestMenuUtils.getEmptyClickHandler());
    }

    /**
     * Finds an empty slot in the chest menu.
     *
     * @return An empty slot in the chest menu, or -1 if no slot is available.
     */
    private int findEmptySlot() {
        for (int i = 0; i < 54; i++) {
            if (!slots.contains(i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns whether the group is visible to the given player.
     *
     * @param player            The player to check.
     * @param playerProfile     The player profile of the player.
     * @param slimefunGuideMode The guide mode of the player.
     * @return Whether the group is visible to the given player.
     */
    @Override
    public boolean isVisible(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
        return true;
    }

    /**
     * Opens the customized group for the given player.
     *
     * @param player            The player to open the group for.
     * @param playerProfile     The player profile of the player.
     * @param slimefunGuideMode The guide mode of the player.
     */
    @Override
    public void open(
            @NotNull Player player,
            @NotNull PlayerProfile playerProfile,
            @NotNull SlimefunGuideMode slimefunGuideMode) {
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

    /**
     * Returns the size of the chest menu.
     *
     * @return The size of the chest menu.
     */
    public abstract int getSize();

    /**
     * Returns whether the chest menu should be generated in classic mode.
     *
     * @return Whether the chest menu should be generated in classic mode.
     */
    public abstract boolean isClassic();

    /**
     * Returns the slots where the guides should be placed.
     *
     * @return The slots where the guides should be placed.
     */
    public abstract int[] getContentSlots();

    /**
     * Returns the slot where the back button should be placed.
     *
     * @return The slot where the back button should be placed.
     */
    public abstract int getBackSlot();
}
