package com.balugaq.jeg.interfaces;

import com.balugaq.jeg.JustEnoughGuide;
import com.balugaq.jeg.groups.BookmarkGroup;
import com.balugaq.jeg.groups.ItemMarkGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public interface JEGSlimefunGuideImplementation extends SlimefunGuideImplementation {
    @Nonnull ChestMenu create(@Nonnull Player p);
    default void openBookMarkGroup(Player player, PlayerProfile profile) {
        List<SlimefunItem> items = JustEnoughGuide.getBookmarkManager().getBookmarkedItems(player);
        if (items == null || items.isEmpty()) {
            return;
        }
        new BookmarkGroup(this, player, items).open(player, profile, getMode());
    }

    default void openItemMarkGroup(ItemGroup itemGroup, Player player, PlayerProfile profile) {
        new ItemMarkGroup(this, itemGroup, player).open(player, profile, getMode());
    }
}
