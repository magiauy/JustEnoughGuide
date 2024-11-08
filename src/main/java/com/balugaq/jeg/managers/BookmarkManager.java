package com.balugaq.jeg.managers;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerBackpack;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public class BookmarkManager {
    private static final int DATA_ITEM_SLOT = 0;
    private static final String BACKPACK_NAME = "JEGBookmarkBackpack";
    private final NamespacedKey BOOKMARKS_KEY;
    private final Plugin plugin;

    public BookmarkManager(@Nonnull Plugin plugin) {
        this.plugin = plugin;
        this.BOOKMARKS_KEY = new NamespacedKey(plugin, "bookmarks");
    }

    public void addBookmark(@Nonnull Player player, @Nonnull SlimefunItem slimefunItem) {
        PlayerBackpack backpack = getOrCreateBookmarkBackpack(player);
        if (backpack == null) {
            return;
        }

        addBookmark0(player, backpack, slimefunItem);
    }

    private void addBookmark0(@Nonnull Player player, @Nonnull PlayerBackpack backpack, @Nonnull SlimefunItem slimefunItem) {
        ItemStack bookmarksItem = backpack.getInventory().getItem(DATA_ITEM_SLOT);
        if (bookmarksItem == null || bookmarksItem.getType() == Material.AIR) {
            bookmarksItem = markItemAsBookmarksItem(new ItemStack(Material.DIRT), player);
        }

        CustomItemStack customItemStack = new CustomItemStack(bookmarksItem, itemMeta -> {
            List<String> lore = itemMeta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            String id = slimefunItem.getId();
            lore.remove(id);
            lore.add(id);
            itemMeta.setLore(lore);
        });

        backpack.getInventory().setItem(DATA_ITEM_SLOT, new ItemStack(customItemStack));
        Slimefun.getDatabaseManager().getProfileDataController().saveBackpackInventory(backpack, DATA_ITEM_SLOT);
    }

    @Nullable
    public List<SlimefunItem> getBookmarkedItems(@Nonnull Player player) {
        PlayerBackpack backpack = getBookmarkBackpack(player);
        if (backpack == null) {
            return null;
        }

        ItemStack bookmarksItem = backpack.getInventory().getItem(DATA_ITEM_SLOT);
        if (bookmarksItem == null || bookmarksItem.getType() == Material.AIR) {
            return null;
        }

        if (!isBookmarksItem(bookmarksItem, player)) {
            return null;
        }

        List<SlimefunItem> bookmarkedItems = new ArrayList<>();
        new CustomItemStack(bookmarksItem, itemMeta -> {
            List<String> lore = itemMeta.getLore();
            if (lore == null) {
                return;
            }
            for (String id : lore) {
                SlimefunItem sfitem = SlimefunItem.getById(id);
                if (sfitem != null) {
                    bookmarkedItems.add(sfitem);
                }
            }
        });

        return bookmarkedItems;
    }

    public void removeBookmark(Player player, SlimefunItem slimefunItem) {
        PlayerBackpack backpack = getBookmarkBackpack(player);
        if (backpack == null) {
            return;
        }

        removeBookmark0(backpack, slimefunItem);
    }

    private void removeBookmark0(PlayerBackpack backpack, SlimefunItem slimefunItem) {
        ItemStack bookmarksItem = backpack.getInventory().getItem(DATA_ITEM_SLOT);
        if (bookmarksItem == null || bookmarksItem.getType() == Material.AIR) {
            return;
        }

        CustomItemStack customItemStack = new CustomItemStack(bookmarksItem, itemMeta -> {
            List<String> lore = itemMeta.getLore();
            if (lore == null) {
                return;
            }
            lore.remove(slimefunItem.getId());
            itemMeta.setLore(lore);
        });

        backpack.getInventory().setItem(DATA_ITEM_SLOT, new ItemStack(customItemStack));
        Slimefun.getDatabaseManager().getProfileDataController().saveBackpackInventory(backpack, DATA_ITEM_SLOT);
    }

    public void clearBookmarks(Player player) {
        PlayerBackpack backpack = getBookmarkBackpack(player);
        if (backpack == null) {
            return;
        }

        clearBookmarks0(backpack);
    }

    private void clearBookmarks0(PlayerBackpack backpack) {
        ItemStack bookmarksItem = backpack.getInventory().getItem(DATA_ITEM_SLOT);
        if (bookmarksItem == null || bookmarksItem.getType() == Material.AIR) {
            return;
        }

        CustomItemStack customItemStack = new CustomItemStack(bookmarksItem, itemMeta -> {
            itemMeta.setLore(new ArrayList<>());
        });

        backpack.getInventory().setItem(DATA_ITEM_SLOT, new ItemStack(customItemStack));
        Slimefun.getDatabaseManager().getProfileDataController().saveBackpackInventory(backpack, DATA_ITEM_SLOT);
    }

    @Nullable
    public PlayerBackpack getOrCreateBookmarkBackpack(Player player) {
        PlayerBackpack backpack = getBookmarkBackpack(player);
        if (backpack == null) {
            backpack = createBackpack(player);
        }

        return backpack;
    }

    @Nullable
    public PlayerBackpack createBackpack(Player player) {
        PlayerProfile profile = Slimefun.getDatabaseManager().getProfileDataController().getProfile(player);
        if (profile == null) {
            return null;
        }

        PlayerBackpack backpack = Slimefun.getDatabaseManager()
                .getProfileDataController()
                .createBackpack(player, BACKPACK_NAME, profile.nextBackpackNum(), 9);
        backpack.getInventory().setItem(DATA_ITEM_SLOT, markItemAsBookmarksItem(new ItemStack(Material.DIRT), player));
        Slimefun.getDatabaseManager().getProfileDataController().saveBackpackInventory(backpack, DATA_ITEM_SLOT);
        return backpack;
    }

    @Nullable
    public PlayerBackpack getBookmarkBackpack(Player player) {
        PlayerProfile profile = Slimefun.getDatabaseManager().getProfileDataController().getProfile(player);
        if (profile == null) {
            return null;
        }

        Set<PlayerBackpack> backpacks = Slimefun.getDatabaseManager().getProfileDataController().getBackpacks(profile.getUUID().toString());
        if (backpacks.isEmpty()) {
            return null;
        }

        for (PlayerBackpack backpack : backpacks) {
            if (backpack.getName().equals(BACKPACK_NAME)) {
                Inventory inventory = backpack.getInventory();
                ItemStack[] contents = inventory.getContents();

                ItemStack bookmarksItem = contents[DATA_ITEM_SLOT];
                if (bookmarksItem == null || bookmarksItem.getType() == Material.AIR) {
                    return null;
                }

                if (!isBookmarksItem(bookmarksItem, player)) {
                    return null;
                }

                for (int i = 0; i < contents.length; i++) {
                    if (i != DATA_ITEM_SLOT && contents[i] != null && contents[i].getType() != Material.AIR) {
                        return null;
                    }
                }

                return backpack;
            }
        }

        return null;
    }

    @Nonnull
    public ItemStack markItemAsBookmarksItem(@Nonnull ItemStack itemStack, @Nonnull Player player) {
        CustomItemStack customItemStack = new CustomItemStack(itemStack, itemMeta -> {
            itemMeta.getPersistentDataContainer().set(BOOKMARKS_KEY, PersistentDataType.STRING, player.getUniqueId().toString());
        });

        return new ItemStack(customItemStack);
    }

    public boolean isBookmarksItem(@Nonnull ItemStack itemStack, @Nonnull Player player) {
        AtomicBoolean isBookmarksItem = new AtomicBoolean(false);
        new CustomItemStack(itemStack, itemMeta -> {
            String uuid = itemMeta.getPersistentDataContainer().get(BOOKMARKS_KEY, PersistentDataType.STRING);
            if (uuid != null && uuid.equals(player.getUniqueId().toString())) {
                isBookmarksItem.set(true);
            }
        });
        return isBookmarksItem.get();
    }

    public void unmarkBookmarksItem(@Nonnull ItemStack itemStack) {
        new CustomItemStack(itemStack, itemMeta -> {
            itemMeta.getPersistentDataContainer().remove(BOOKMARKS_KEY);
        });
    }
}
