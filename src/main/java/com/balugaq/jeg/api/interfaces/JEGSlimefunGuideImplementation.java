package com.balugaq.jeg.api.interfaces;

import com.balugaq.jeg.api.groups.BookmarkGroup;
import com.balugaq.jeg.api.groups.ItemMarkGroup;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public interface JEGSlimefunGuideImplementation extends SlimefunGuideImplementation {
    @ParametersAreNonnullByDefault
    void createHeader(Player p, PlayerProfile profile, ChestMenu menu);

    @NotNull ChestMenu create(@NotNull Player p);

    default void openBookMarkGroup(@NotNull Player player, @NotNull PlayerProfile profile) {
        List<SlimefunItem> items = JustEnoughGuide.getBookmarkManager().getBookmarkedItems(player);
        if (items == null || items.isEmpty()) {
            player.sendMessage(ChatColor.RED + "你还没有收藏任何物品!");
            return;
        }
        new BookmarkGroup(this, player, items).open(player, profile, getMode());
    }

    default void openItemMarkGroup(
            @NotNull ItemGroup itemGroup, @NotNull Player player, @NotNull PlayerProfile profile) {
        new ItemMarkGroup(this, itemGroup, player).open(player, profile, getMode());
    }
}
