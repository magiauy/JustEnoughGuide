package com.balugaq.jeg.api.groups;

import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A classic implementation of GuideGroup.
 *
 * @author balugaq
 * @since 1.3
 */
@SuppressWarnings("unused")
@Getter
@NotDisplayInCheatMode
public class ClassicGuideGroup extends GuideGroup {
    private static final int[] CONTENT_SLOTS = {
        9, 10, 11, 12, 13, 14, 15, 16, 17,
        18, 19, 20, 21, 22, 23, 24, 25, 26,
        27, 28, 29, 30, 31, 32, 33, 34, 35,
        36, 37, 38, 39, 40, 41, 42, 43, 44
    };

    private static final int BACK_SLOT = 1;
    private static final int SIZE = 54;
    private static final boolean CLASSIC = true;

    /**
     * Constructor.
     *
     * @param key  The key of the group.
     * @param icon The icon of the group.
     */
    protected ClassicGuideGroup(@NotNull NamespacedKey key, @NotNull ItemStack icon) {
        super(key, icon);
    }

    /**
     * Constructor.
     *
     * @param key  The key of the group.
     * @param icon The icon of the group.
     * @param tier The tier of the group.
     */
    protected ClassicGuideGroup(@NotNull NamespacedKey key, @NotNull ItemStack icon, int tier) {
        super(key, icon, tier);
    }

    /**
     * Returns the size of the group.
     *
     * @return The size of the group.
     */
    @Override
    public int getSize() {
        return SIZE;
    }

    /**
     * Returns whether the group is classic or not.
     *
     * @return Whether the group is classic or not.
     */
    @Override
    public boolean isClassic() {
        return CLASSIC;
    }

    /**
     * Returns the slots where the content of the group can be placed.
     *
     * @return The slots where the content of the group can be placed.
     */
    @Override
    public int[] getContentSlots() {
        return CONTENT_SLOTS;
    }

    /**
     * Returns the slot where the back of the group can be placed.
     *
     * @return The slot where the back of the group can be placed.
     */
    @Override
    public int getBackSlot() {
        return BACK_SLOT;
    }
}
