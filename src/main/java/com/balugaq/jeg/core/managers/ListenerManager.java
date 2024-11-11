package com.balugaq.jeg.core.managers;

import com.balugaq.jeg.api.managers.AbstractManager;
import com.balugaq.jeg.core.listeners.GuideListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for managing the listeners of the plugin.
 *
 * @author balugaq
 * @since 1.0
 */
@Getter
public class ListenerManager extends AbstractManager {
    private final JavaPlugin plugin;
    List<Listener> listeners = new ArrayList<>();

    public ListenerManager(JavaPlugin plugin) {
        this.plugin = plugin;
        listeners.add(new GuideListener());
        registerListeners();
    }

    private void registerListeners() {
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }
}
