package com.balugaq.jeg.utils.formatter;

import com.balugaq.jeg.implementation.JustEnoughGuide;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.entity.Player;

public class RecipeFormat extends Format {
    @Override
    public void loadMapping() {
        loadMapping(JustEnoughGuide.getConfigManager().getRecipeFormat());
    }

    @Deprecated
    public void decorate(ChestMenu menu, Player player) {
        super.decorate(menu, player);
    }
}
