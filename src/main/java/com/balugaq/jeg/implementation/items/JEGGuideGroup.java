package com.balugaq.jeg.implementation.items;

import com.balugaq.jeg.api.groups.ClassicGuideGroup;
import com.balugaq.jeg.api.interfaces.JEGSlimefunGuideImplementation;
import com.balugaq.jeg.api.interfaces.NotDisplayInCheatMode;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.GuideUtil;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A implementation of the ClassicGuideGroup for JEG.
 * @author balugaq
 * @since 1.3
 */
@Getter
@NotDisplayInCheatMode
public class JEGGuideGroup extends ClassicGuideGroup {
    private static final ItemStack HEADER = new CustomItemStack(
            Material.BEACON,
            "&bJEG 使用指南",
            "&b作者: 大香蕉",
            "&bJEG 优化了粘液科技的指南，使其更人性化。",
            "&b查看以下指南书以快速上手 JEG 增加的功能。"
    );
    private static final int[] GUIDE_SLOTS = {
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private static final int[] BORDER_SLOTS = {
            9,  17,
            18, 26,
            27, 35,
            36, 44,
    };

    protected JEGGuideGroup(@NotNull NamespacedKey key, @NotNull ItemStack icon) {
        super(key, icon, Integer.MAX_VALUE);
        for (int slot : BORDER_SLOTS) {
            addGuide(slot, ChestMenuUtils.getBackground());
        }
        addGuide(13, HEADER);
        final AtomicInteger index = new AtomicInteger(0);
        doIf(JustEnoughGuide.getConfigManager().isPinyinSearch(), () -> {
            addGuide(GUIDE_SLOTS[index.getAndIncrement()], new CustomItemStack(
                    Material.CLOCK,
                    "&b功能: 拼音搜索",
                    "&b介绍: 你可以通过拼音搜索指南来快速找到你想要的物品。",
                    "&b点击尝试功能。"
            ), (p, s, i, a) -> {
                try {
                    p.performCommand("sf search ding");
                } catch (Throwable e) {
                    p.sendMessage("§c无法执行操作，请检查 Slimefun 是否正确安装。");
                    e.printStackTrace();
                }
                return false;
            });
        });

        addGuide(GUIDE_SLOTS[index.getAndIncrement()], new CustomItemStack(
                Material.NAME_TAG,
                "&b功能: 搜索翻页",
                "&b介绍: 你可以在搜索中翻页来浏览更多搜索结果。",
                "&b点击尝试功能。"
        ), (p, s, i, a) -> {
            try {
                p.performCommand("sf search a");
            } catch (Throwable e) {
                p.sendMessage("§c无法执行操作，请检查 Slimefun 是否正确安装。");
                e.printStackTrace();
            }
            return false;
        });

        doIf(JustEnoughGuide.getConfigManager().isBookmark(), () -> {
            addGuide(GUIDE_SLOTS[index.getAndIncrement()], new CustomItemStack(
                    Material.BOOK,
                    "&b功能: 标记物品",
                    "&b介绍: 你可以打开一个物品组，对于支持的附属。",
                    "&b      你可以点击物品组界面下方的“书”图标以进入标记状态。",
                    "&a      点击返回按钮以退出标记状态。",
                    "&b点击尝试功能。"
            ), (p, s, i, a) -> {
                try {
                    if (Slimefun.instance() == null) {
                        p.sendMessage("§c无法获取 Slimefun 实例，无法使用此功能。");
                    }

                    SlimefunGuideImplementation guide = GuideUtil.getGuide(p, SlimefunGuideMode.SURVIVAL_MODE);
                    if (guide == null) {
                        p.sendMessage("§c无法获取指南，请检查是否正确安装 Slimefun。");
                        return false;
                    }

                    if (!(guide instanceof JEGSlimefunGuideImplementation jegGuide)) {
                        p.sendMessage("§c功能未启用，无法使用此功能。");
                        return false;
                    }

                    PlayerProfile profile = PlayerProfile.find(p).orElse(null);
                    if (profile == null) {
                        p.sendMessage("§c无法获取玩家资料，请检查是否正确安装 Slimefun。");
                        return false;
                    }

                    for (ItemGroup itemGroup : Slimefun.getRegistry().getAllItemGroups()) {
                        if (itemGroup.getKey().equals(new NamespacedKey(Slimefun.instance(), "basic_machines"))) {
                            jegGuide.openItemMarkGroup(itemGroup, p, profile);
                            return false;
                        }
                    }
                } catch (Throwable e) {
                    p.sendMessage("§c无法执行操作，请检查 Slimefun 是否正确安装。");
                    e.printStackTrace();
                }
                return false;
            });
        });

        doIf(JustEnoughGuide.getConfigManager().isBookmark(), () -> {
            addGuide(GUIDE_SLOTS[index.getAndIncrement()], new CustomItemStack(
                    Material.NETHER_STAR,
                    "&b功能: 查阅标记物品",
                    "&b介绍: 你可以查看你标记过的物品。",
                    "&b      你可以点击物品组界面下方的“下界之星”图标以查看标记过的物品。",
                    "&a      点击返回按钮以退出查看状态。",
                    "&b点击尝试功能。"
            ), (p, s, i, a) -> {
                try {
                    if (Slimefun.instance() == null) {
                        p.sendMessage("§c无法获取 Slimefun 实例，无法使用此功能。");
                    }

                    SlimefunGuideImplementation guide = GuideUtil.getGuide(p, SlimefunGuideMode.SURVIVAL_MODE);
                    if (guide == null) {
                        p.sendMessage("§c无法获取指南，请检查是否正确安装 Slimefun。");
                        return false;
                    }

                    if (!(guide instanceof JEGSlimefunGuideImplementation jegGuide)) {
                        p.sendMessage("§c功能未启用，无法使用此功能。");
                        return false;
                    }

                    PlayerProfile profile = PlayerProfile.find(p).orElse(null);
                    if (profile == null) {
                        p.sendMessage("§c无法获取玩家资料，请检查是否正确安装 Slimefun。");
                        return false;
                    }

                    jegGuide.openBookMarkGroup(p, profile);
                } catch (Throwable e) {
                    p.sendMessage("§c无法执行操作，请检查 Slimefun 是否正确安装。");
                    e.printStackTrace();
                }
                return false;
            });
        });

        addGuide(GUIDE_SLOTS[index.getAndIncrement()], new CustomItemStack(
                Material.CRAFTING_TABLE,
                "&b功能: 跳转物品组",
                "&b介绍: 当你在查阅一个物品的配方时，你可以快速跳转到所需物品所属的物品组。",
                "&b      你可以 Shift + 左键 点击所需物品，以快速跳转到该物品所属的物品组。",
                "&b点击尝试功能。"
        ), (p, s, i, a) -> {
            try {
                if (Slimefun.instance() == null) {
                    p.sendMessage("§c无法获取 Slimefun 实例，无法使用此功能。");
                    return false;
                }

                SlimefunGuideImplementation guide = GuideUtil.getGuide(p, SlimefunGuideMode.SURVIVAL_MODE);
                if (guide == null) {
                    p.sendMessage("§c无法获取指南，请检查是否正确安装 Slimefun。");
                    return false;
                }

                if (!(guide instanceof JEGSlimefunGuideImplementation jegGuide)) {
                    p.sendMessage("§c功能未启用，无法使用此功能。");
                    return false;
                }

                PlayerProfile profile = PlayerProfile.find(p).orElse(null);
                if (profile == null) {
                    p.sendMessage("§c无法获取玩家资料，请检查是否正确安装 Slimefun。");
                    return false;
                }

                SlimefunItem exampleItem = SlimefunItems.ELECTRIC_DUST_WASHER_3.getItem();
                if (exampleItem == null) {
                    p.sendMessage("§c无法获取示例物品，请检查是否正确安装 Slimefun。");
                    return false;
                }

                jegGuide.displayItem(profile, exampleItem, true);
            } catch (Throwable e) {
                p.sendMessage("§c无法执行操作，请检查 Slimefun 是否正确安装。");
                e.printStackTrace();
            }
            return false;
        });

        addGuide(GUIDE_SLOTS[index.getAndIncrement()], new CustomItemStack(
                Material.COMPARATOR,
                "&b功能: 智能搜索",
                "&b介绍: 当你使用搜索时，会自动搜索相关的机器，并添加到显示列表中",
                "&c     不支持拼音搜索。",
                "&b点击尝试功能。"
        ), (p, s, i, a) -> {
            try {
                p.performCommand("sf search 硫酸");
            } catch (Throwable e) {
                p.sendMessage("§c无法执行操作，请检查 Slimefun 是否正确安装。");
                e.printStackTrace();
            }
            return false;
        });

        addGuide(GUIDE_SLOTS[index.getAndIncrement()], new CustomItemStack(
                Material.LODESTONE,
                "&b功能: 搜索拓展",
                "&b介绍: 你可以通过在开头添加以下标识符来指定搜索范围:",
                "&b      &a#m &7- 仅搜索工作配方涉及的物品",
                "&b      &a#t &7- 仅搜索配方类型名称涉及的物品",
                "&b      &a#r &7- 仅搜索配方中的物品涉及的物品",
                "&c     不支持拼音搜索。",
                "&c     不支持组合使用。",
                "&b点击尝试功能。"
        ), (p, s, i, a) -> {
            try {
                if (a.isRightClicked()) {
                    p.performCommand("sf search #m粉");
                } else if (a.isShiftClicked()) {
                    p.performCommand("sf search #t增强型工作台");
                } else {
                    p.performCommand("sf search #r电池");
                }
            } catch (Throwable e) {
                p.sendMessage("§c无法执行操作，请检查 Slimefun 是否正确安装。");
                e.printStackTrace();
            }
            return false;
        });
    }

    public static void doIf(boolean expression, @NotNull Runnable runnable) {
        if (expression) {
            runnable.run();
        }
    }
}
