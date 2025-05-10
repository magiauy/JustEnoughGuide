package com.balugaq.jeg.utils.formatter;

import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.GuideUtil;
import com.balugaq.jeg.utils.Models;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.Getter;
import lombok.Setter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@Getter
public abstract class Format {
    @Setter
    public int size = 54;
    public final Map<Integer, Character> mapping = new HashMap<>();
    public final Map<Character, ItemStackFormat> formats = new HashMap<>();

    public Format() {
        formats.put('B', new Background());
        formats.put('R', new RealTimeSearch());
        formats.put('C', new BookMark());
        formats.put('c', new ItemMark());
        //formats.put('E', new BigRecipe());

        formats.put('b', new Back());
        formats.put('T', new Settings());
        formats.put('S', new Search());

        formats.put('P', new PagePrevious());
        formats.put('N', new PageNext());
        loadMapping();
    }

    public abstract void loadMapping();

    @OverridingMethodsMustInvokeSuper
    public void decorate(@SuppressWarnings("deprecation") ChestMenu menu, Player player) {
        for (var entry : mapping.entrySet()) {
            var format = formats.get(entry.getValue());
            if (format instanceof ItemStackSupplier supplier) {
                menu.addItem(entry.getKey(), supplier.get());
            }
            else if (format instanceof Back function) {
                menu.addItem(entry.getKey(), function.apply(player));
            }
            else if (format instanceof Settings function) {
                menu.addItem(entry.getKey(), function.apply(player));
            }
            else if (format instanceof Search function) {
                menu.addItem(entry.getKey(), function.apply(player));
            }
        }
    }

    @OverridingMethodsMustInvokeSuper
    public void decoratePage(@SuppressWarnings("deprecation") ChestMenu menu, Player player, int page, int maxPage) {
        for (var entry : mapping.entrySet()) {
            var format = formats.get(entry.getValue());
            if (format instanceof PagePrevious function) {
                menu.addItem(entry.getKey(), function.apply(player, page, maxPage));
            }
            else if (format instanceof PageNext function) {
                menu.addItem(entry.getKey(), function.apply(player, page, maxPage));
            }
        }
    }

    public void loadMapping(List<String> format) {
        int index = -1;
        for (var string : format) {
            for (var c : string.toCharArray()) {
                index++;
                if (c != ' ') {
                    mapping.put(index, c);
                }
            }
        }
    }

    public List<Integer> getChars(String s) {
        return getChars(s.toCharArray()[0]);
    }

    public List<Integer> getChars(char c) {
        List<Integer> list = new ArrayList<>();
        for (var entry : mapping.entrySet()) {
            if (entry.getValue() == c) {
                list.add(entry.getKey());
            }
        }
        return list;
    }

    public static class Background implements ItemStackSupplier {
        @Override
        public ItemStack get() {
            return ChestMenuUtils.getBackground();
        }
    }

    public static class Back implements ItemStackFunction<Player> {
        @Override
        public ItemStack apply(Player player) {
            return ChestMenuUtils.getBackButton(
                    player,
                    "",
                    "&f左键: &7返回上一页",
                    "&fShift + 左键: &7返回主菜单"
            );
        }
    }

    public static class PagePrevious implements ItemStackCiFunction<Player, Integer, Integer> {
        @Override
        public ItemStack apply(Player player, Integer page, Integer maxPage) {
            return ChestMenuUtils.getPreviousButton(player, page, maxPage);
        }
    }

    public static class PageNext implements ItemStackCiFunction<Player, Integer, Integer> {
        @Override
        public ItemStack apply(Player player, Integer page, Integer maxPage) {
            return ChestMenuUtils.getNextButton(player, page, maxPage);
        }
    }

    public static class BookMark implements ItemStackSupplier {
        @Override
        public ItemStack get() {
            return JustEnoughGuide.getConfigManager().isBookmark() ?
                    GuideUtil.getBookMarkMenuButton() :
                    ChestMenuUtils.getBackground();
        }
    }

    public static class ItemMark implements ItemStackSupplier {
        @Override
        public ItemStack get() {
            return JustEnoughGuide.getConfigManager().isBookmark() ?
                    GuideUtil.getItemMarkMenuButton() :
                    ChestMenuUtils.getBackground();
        }
    }

    public static class Settings implements ItemStackFunction<Player> {
        @Override
        public ItemStack apply(Player p) {
            return ChestMenuUtils.getMenuButton(p);
        }
    }

    public static class Search implements ItemStackFunction<Player> {
        @Override
        public ItemStack apply(Player p) {
            return ChestMenuUtils.getSearchButton(p);
        }
    }

    public static class RealTimeSearch implements ItemStackSupplier {
        @Override
        public ItemStack get() {
            return JustEnoughGuide.getConfigManager().isRTSSearch() ?
                    Models.RTS_ITEM :
                    ChestMenuUtils.getBackground();
        }
    }

    public static class BigRecipe implements ItemStackSupplier {
        @Override
        public ItemStack get() {
            return Models.SPECIAL_MENU_ITEM;
        }
    }

    public interface ItemStackCiFunction<A, B, C> extends CiFunction<A, B, C, ItemStack>, ItemStackFormat {

    }

    public interface ItemStackBiFunction<A, B> extends BiFunction<A, B, ItemStack>, ItemStackFormat {
        ItemStack apply(A a, B b);
    }

    public interface ItemStackFunction<A> extends Function<A, ItemStack>, ItemStackFormat {
        ItemStack apply(A a);
    }

    public interface ItemStackSupplier extends Supplier<ItemStack>, ItemStackFormat {
        ItemStack get();
    }

    public interface ItemStackFormat {
    }

    public interface CiFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }
}
