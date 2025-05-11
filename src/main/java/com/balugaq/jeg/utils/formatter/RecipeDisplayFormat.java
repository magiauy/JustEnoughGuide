package com.balugaq.jeg.utils.formatter;

import com.balugaq.jeg.implementation.JustEnoughGuide;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author balugaq
 * @since 1.6
 */
public class RecipeDisplayFormat extends Format {
    public static List<Integer> fenceShuffle(List<Integer> list) {
        int size = list.size();
        int splitPoint = (size + 1) / 2;

        List<Integer> firstHalf = new ArrayList<>(list.subList(0, splitPoint));
        List<Integer> secondHalf = new ArrayList<>(list.subList(splitPoint, size));

        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < secondHalf.size(); i++) {
            result.add(firstHalf.get(i));
            result.add(secondHalf.get(i));
        }

        if (firstHalf.size() > secondHalf.size()) {
            result.add(firstHalf.get(firstHalf.size() - 1));
        }

        return result;
    }

    @Override
    public void loadMapping() {
        loadMapping(JustEnoughGuide.getConfigManager().getRecipeDisplayFormat());
    }

    @Deprecated
    public void decorate(ChestMenu menu, Player player) {
        super.decorate(menu, player);
    }
}
