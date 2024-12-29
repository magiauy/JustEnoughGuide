package com.balugaq.jeg.implementation.items;

import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.SlimefunItemUtil;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

/**
 * This class is responsible for registering all the JEG groups.
 *
 * @author balugaq
 * @since 1.3
 */
public class GroupSetup {
    public static ItemGroup guideGroup;

    public static void setup() {
        guideGroup = new JEGGuideGroup(
                new NamespacedKey(JustEnoughGuide.getInstance(), "jeg_guide_group"),
                new CustomItemStack(Material.KNOWLEDGE_BOOK, "&bJEG 使用指南"));
        guideGroup.register(JustEnoughGuide.getInstance());
    }

    public static void shutdown() {
        SlimefunItemUtil.unregisterItemGroup(guideGroup);
    }
}
