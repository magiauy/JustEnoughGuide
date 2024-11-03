package com.balugaq.jeg.guide;

import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.entity.Player;

import javax.annotation.ParametersAreNonnullByDefault;

public interface MyGuideImplementation {
    @ParametersAreNonnullByDefault
    void createHeader(Player p, PlayerProfile profile, ChestMenu menu);
}
