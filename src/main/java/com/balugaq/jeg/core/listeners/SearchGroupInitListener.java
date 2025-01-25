package com.balugaq.jeg.core.listeners;

import com.balugaq.jeg.api.groups.SearchGroup;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.events.SlimefunItemRegistryFinalizedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.jetbrains.annotations.NotNull;

public class SearchGroupInitListener implements Listener {
    @EventHandler
    public void onInit(SlimefunItemRegistryFinalizedEvent event) {
        SearchGroup.init();
    }

    @EventHandler
    public void onReload(@NotNull PluginDisableEvent event) {
        if (event.getPlugin() instanceof SlimefunAddon) {
            SearchGroup.LOADED = false;
            SearchGroup.CACHE.clear();
            SearchGroup.CACHE2.clear();
            SearchGroup.SPECIAL_CACHE.clear();
            SearchGroup.init();
        }
    }

    @EventHandler
    public void onReload(@NotNull PluginEnableEvent event) {
        if (event.getPlugin() instanceof SlimefunAddon) {
            SearchGroup.LOADED = false;
            SearchGroup.init();
        }
    }
}
