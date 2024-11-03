package com.balugaq.jeg.manages;

import com.balugaq.jeg.JustEnoughGuide;
import com.balugaq.jeg.listeners.GuideListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class ListenerManager {
    List<Listener> listeners = new ArrayList<>();
    private JustEnoughGuide plugin;

    public ListenerManager(JustEnoughGuide plugin) {
        listeners.add(new GuideListener());
        registerListeners();
    }

    private void registerListeners() {
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin.getInstance());
        }
    }
}
