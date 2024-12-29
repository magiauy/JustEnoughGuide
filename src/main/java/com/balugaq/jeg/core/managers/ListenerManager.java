package com.balugaq.jeg.core.managers;

import com.balugaq.jeg.api.managers.AbstractManager;
import com.balugaq.jeg.core.listeners.GuideListener;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * This class is responsible for managing the listeners of the plugin.
 *
 * @author balugaq
 * @since 1.0
 */
@Getter
public class ListenerManager extends AbstractManager {
    private final JavaPlugin plugin;

    @NotNull List<Listener> listeners = new ArrayList<>();

    public ListenerManager(JavaPlugin plugin) {
        this.plugin = plugin;
        listeners.add(new GuideListener());
    }

    private void registerListeners() {
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    private void unregisterListeners() {
        for (Listener listener : listeners) {
            HandlerList.unregisterAll(listener);
        }
    }

    @Override
    public void onLoad() {
        registerListeners();
    }

    @Override
    public void onUnload() {
        unregisterListeners();
    }
}
