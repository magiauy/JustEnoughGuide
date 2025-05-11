package com.balugaq.jeg.utils.formatter;

import com.balugaq.jeg.implementation.JustEnoughGuide;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.entity.Player;

public class HelperFormat extends Format {
    @Override
    public void loadMapping() {
        loadMapping(JustEnoughGuide.getConfigManager().getHelperFormat());
    }

    @Deprecated
    public void decorate(ChestMenu menu, Player player, int page, int maxPage) {
        super.decorate(menu, player);
        super.decoratePage(menu, player, page, maxPage);
    }
}
