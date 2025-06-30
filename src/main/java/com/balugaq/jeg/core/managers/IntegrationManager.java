/*
 * Copyright (c) 2024-2025 balugaq
 *
 * This file is part of JustEnoughGuide, available under MIT license.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * - The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 * - The author's name (balugaq or 大香蕉) and project name (JustEnoughGuide or JEG) shall not be
 *   removed or altered from any source distribution or documentation.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.balugaq.jeg.core.managers;

import com.balugaq.jeg.api.managers.AbstractManager;
import com.balugaq.jeg.core.integrations.Integration;
import com.balugaq.jeg.core.integrations.logitech.LogitechIntegrationMain;
import com.balugaq.jeg.core.integrations.networks.NetworksIntegrationMain;
import com.balugaq.jeg.core.integrations.networksexpansion.NetworksExpansionIntegrationMain;
import com.balugaq.jeg.utils.Debug;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for managing integrations with other plugins.
 *
 * @author balugaq
 * @since 1.2
 */
@Getter
public class IntegrationManager extends AbstractManager {
    private static final List<Integration> integrations = new ArrayList<>();
    private final @NotNull JavaPlugin plugin;
    private boolean enabledNetworks;
    private boolean enabledNetworksExpansion;
    private boolean hasRecipeCompletableWithGuide;
    private boolean enabledOreWorkshop;
    private boolean enabledFinalTECH;
    private boolean enabledFinalTECH_Changed;
    private boolean enabledNexcavate;
    private boolean enabledLogiTech;
    private boolean enabledInfinityExpansion;
    private boolean enabledInfinityExpansion_Changed;
    private boolean enabledObsidianExpansion;

    public IntegrationManager(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // Check if NetworksExpansion is enabled
            try {
                Class.forName("com.ytdd9527.networksexpansion.implementation.ExpansionItems");
                enabledNetworksExpansion = true;
            } catch (ClassNotFoundException e) {
                enabledNetworksExpansion = false;
            }

            if (enabledNetworksExpansion) {
                enabledNetworks = true;
                try {
                    Class.forName("com.balugaq.netex.api.interfaces.RecipeCompletableWithGuide");
                    hasRecipeCompletableWithGuide = true;
                } catch (ClassNotFoundException e) {
                    hasRecipeCompletableWithGuide = false;
                }
            } else {
                hasRecipeCompletableWithGuide = false;
                try {
                    Class.forName("io.github.sefiraat.networks.Networks");
                    enabledNetworks = true;
                } catch (ClassNotFoundException ignored) {
                    enabledNetworks = false;
                }
            }

            // Check if OreWorkshop is enabled
            this.enabledOreWorkshop = plugin.getServer().getPluginManager().isPluginEnabled("OreWorkshop");

            this.enabledFinalTECH_Changed = Bukkit.getPluginManager().isPluginEnabled("FinalTECH-Changed");
            this.enabledFinalTECH = enabledFinalTECH_Changed || Bukkit.getPluginManager().isPluginEnabled("FinalTECH");
            this.enabledNexcavate = Bukkit.getPluginManager().isPluginEnabled("Nexcavate");
            this.enabledLogiTech = Bukkit.getPluginManager().isPluginEnabled("LogiTech");
            this.enabledInfinityExpansion_Changed = Bukkit.getPluginManager().isPluginEnabled("InfinityExpansion-Changed");
            this.enabledInfinityExpansion = enabledInfinityExpansion_Changed || Bukkit.getPluginManager().isPluginEnabled("InfinityExpansion");
            this.enabledObsidianExpansion = Bukkit.getPluginManager().isPluginEnabled("ObsidianExpansion");

            if (enabledLogiTech) {
                integrations.add(new LogitechIntegrationMain());
            }
            if (enabledNetworks) {
                integrations.add(new NetworksIntegrationMain());
            }
            if (enabledNetworksExpansion) {
                integrations.add(new NetworksExpansionIntegrationMain());
            }

            startupIntegrations();
        }, 1L);
    }

    public boolean hasRecipeCompletableWithGuide() {
        return hasRecipeCompletableWithGuide;
    }

    private void startupIntegrations() {
        for (Integration integration : integrations) {
            plugin.getLogger().info("Hooked " + integration.getHookPlugin());
            try {
                integration.onEnable();
            } catch (Throwable e) {
                Debug.trace(e);
            }
        }
    }

    public void shutdownIntegrations() {
        for (Integration integration : integrations) {
            try {
                integration.onDisable();
            } catch (Throwable e) {
                Debug.trace(e);
            }
        }
    }
}
