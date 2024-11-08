package com.balugaq.jeg.core.managers;

import com.balugaq.jeg.core.listeners.GuideListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ListenerManager {
    List<Listener> listeners = new ArrayList<>();
    private JavaPlugin plugin;

    public ListenerManager(JavaPlugin plugin) {
        listeners.add(new GuideListener());
        registerListeners();
    }

    private void registerListeners() {
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }
}
