package com.balugaq.jeg.listeners;

import com.balugaq.jeg.JustEnoughGuide;
import com.balugaq.jeg.utils.GuideUtil;
import io.github.thebusybiscuit.slimefun4.api.events.SlimefunGuideOpenEvent;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class GuideListener implements Listener {
    @EventHandler
    public void onGuideOpen(SlimefunGuideOpenEvent e) {
        JustEnoughGuide.getInstance().debug("Listened to SlimefunGuideOpenEvent. Player: " + e.getPlayer().getName() + ", Layout: " + e.getGuideLayout());
        if (!e.isCancelled()) {
            e.setCancelled(true);

            openGuide(e.getPlayer(), e.getGuideLayout());
        }
    }

    public void openGuide(Player player, SlimefunGuideMode mode) {
        JustEnoughGuide.getInstance().debug("Opening guide for player: " + player.getName() + ", mode: " + mode);
        Optional<PlayerProfile> optional = PlayerProfile.find(player);

        if (optional.isPresent()) {
            PlayerProfile profile = optional.get();
            SlimefunGuideImplementation guide = GuideUtil.getGuide(player, mode);
            profile.getGuideHistory().openLastEntry(guide);
        } else {
            GuideUtil.openMainMenuAsync(player, mode, 1);
        }
    }
}
