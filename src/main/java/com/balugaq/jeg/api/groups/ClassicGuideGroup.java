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

    protected ClassicGuideGroup(@NotNull NamespacedKey key, @NotNull ItemStack icon) {
        super(key, icon);
    }

    protected ClassicGuideGroup(@NotNull NamespacedKey key, @NotNull ItemStack icon, int tier) {
        super(key, icon, tier);
    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public boolean isClassic() {
        return CLASSIC;
    }

    @Override
    public int[] getContentSlots() {
        return CONTENT_SLOTS;
    }

    @Override
    public int getBackSlot() {
        return BACK_SLOT;
    }
}
