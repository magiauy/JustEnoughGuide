package com.balugaq.jeg.core.managers;

import com.balugaq.jeg.api.managers.AbstractManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class is responsible for managing integrations with other plugins.
 *
 * @author balugaq
 * @since 1.2
 */
@Getter
public class IntegrationManager extends AbstractManager {
    private final JavaPlugin plugin;
    private final boolean enabledNetworksExpansion;
    private final boolean enabledOreWorkshop;

    public IntegrationManager(JavaPlugin plugin) {
        boolean tmp;
        this.plugin = plugin;

        // Check if NetworksExpansion is enabled
        try {
            Class.forName("com.ytdd9527.networksexpansion.core.listener.NetworksGuideListener");
            tmp = true;
        } catch (ClassNotFoundException e) {
            tmp = false;
        }

        enabledNetworksExpansion = tmp;

        // Check if OreWorkshop is enabled
        this.enabledOreWorkshop = plugin.getServer().getPluginManager().isPluginEnabled("OreWorkshop");
    }
}
