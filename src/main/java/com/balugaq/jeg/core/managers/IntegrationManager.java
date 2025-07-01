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
import com.balugaq.jeg.api.recipe_complete.source.base.RecipeCompleteProvider;
import com.balugaq.jeg.core.integrations.Integration;
import com.balugaq.jeg.core.integrations.def.DefaultPlayerInventoryRecipeCompleteSlimefunSource;
import com.balugaq.jeg.core.integrations.def.DefaultPlayerInventoryRecipeCompleteVanillaSource;
import com.balugaq.jeg.core.integrations.fastmachines.FastMachinesIntegrationMain;
import com.balugaq.jeg.core.integrations.finalTECHChangedv3.FinalTECHChangedIntegrationMain;
import com.balugaq.jeg.core.integrations.finalTECHv2.FinalTECHIntegrationMain;
import com.balugaq.jeg.core.integrations.finaltechv1.FinalTechIntegrationMain;
import com.balugaq.jeg.core.integrations.fluffymachines.FluffyMachinesIntegrationMain;
import com.balugaq.jeg.core.integrations.galacitfun.GalactifunIntegrationMain;
import com.balugaq.jeg.core.integrations.gastronomicon.GastronomiconIntegrationMain;
import com.balugaq.jeg.core.integrations.infinityexpansion.InfinityExpansionIntegrationMain;
import com.balugaq.jeg.core.integrations.logitech.LogitechIntegrationMain;
import com.balugaq.jeg.core.integrations.networks.NetworksIntegrationMain;
import com.balugaq.jeg.core.integrations.networksexpansion.NetworksExpansionIntegrationMain;
import com.balugaq.jeg.core.integrations.obsidianexpansion.ObsidianExpansionIntegrationMain;
import com.balugaq.jeg.core.integrations.slimeaeplugin.SlimeAEPluginIntegrationMain;
import com.balugaq.jeg.core.integrations.slimetinker.SlimeTinkerIntegrationMain;
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
    private boolean enabledFinalTech;
    private boolean enabledFinalTECH;
    private boolean enabledFinalTECH_Changed;
    private boolean enabledNexcavate;
    private boolean enabledLogiTech;
    private boolean enabledInfinityExpansion;
    private boolean enabledInfinityExpansion2;
    private boolean enabledInfinityExpansion_Changed;
    private boolean enabledObsidianExpansion;
    private boolean enabledSlimeFrame;
    private boolean enabledFastMachines;
    private boolean enabledSlimeAEPlugin;
    private boolean enabledFluffyMachines;
    private boolean enabledGalactifun;
    private boolean enabledGastronomicon;
    private boolean enabledSlimeTinker;

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

            this.enabledFinalTech = Bukkit.getPluginManager().isPluginEnabled("FinalTech");
            this.enabledFinalTECH_Changed = Bukkit.getPluginManager().isPluginEnabled("FinalTECH-Changed");
            this.enabledFinalTECH = enabledFinalTECH_Changed || Bukkit.getPluginManager().isPluginEnabled("FinalTECH");
            this.enabledNexcavate = Bukkit.getPluginManager().isPluginEnabled("Nexcavate");
            this.enabledLogiTech = Bukkit.getPluginManager().isPluginEnabled("LogiTech");
            this.enabledInfinityExpansion2 = Bukkit.getPluginManager().isPluginEnabled("InfinityExpansion2");
            this.enabledInfinityExpansion_Changed = Bukkit.getPluginManager().isPluginEnabled("InfinityExpansion-Changed");
            this.enabledInfinityExpansion = enabledInfinityExpansion_Changed || Bukkit.getPluginManager().isPluginEnabled("InfinityExpansion");
            this.enabledObsidianExpansion = Bukkit.getPluginManager().isPluginEnabled("ObsidianExpansion");
            this.enabledSlimeFrame = Bukkit.getPluginManager().isPluginEnabled("SlimeFrame");
            this.enabledFastMachines = Bukkit.getPluginManager().isPluginEnabled("FastMachines");
            this.enabledSlimeAEPlugin = Bukkit.getPluginManager().isPluginEnabled("SlimeAEPlugin");
            this.enabledFluffyMachines = Bukkit.getPluginManager().isPluginEnabled("FluffyMachines");
            this.enabledGalactifun = Bukkit.getPluginManager().isPluginEnabled("Galactifun");
            this.enabledGastronomicon = Bukkit.getPluginManager().isPluginEnabled("Gastronomicon");
            this.enabledSlimeTinker = Bukkit.getPluginManager().isPluginEnabled("SlimeTinker");

            if (enabledFastMachines) {
                integrations.add(new FastMachinesIntegrationMain());
            }
            if (enabledFinalTech) {
                integrations.add(new FinalTechIntegrationMain());
            }

            if (enabledFinalTECH_Changed) {
                integrations.add(new FinalTECHIntegrationMain());
            }
            // intentionally "else"
            else {
                if (enabledFinalTECH) {
                    integrations.add(new FinalTECHChangedIntegrationMain());
                }
            }

            if (enabledFluffyMachines) {
                integrations.add(new FluffyMachinesIntegrationMain());
            }
            if (enabledGalactifun) {
                integrations.add(new GalactifunIntegrationMain());
            }
            if (enabledGastronomicon) {
                integrations.add(new GastronomiconIntegrationMain());
            }
            if (enabledInfinityExpansion) {
                integrations.add(new InfinityExpansionIntegrationMain());
            }
            if (enabledLogiTech) {
                integrations.add(new LogitechIntegrationMain());
            }
            if (enabledNetworks) {
                integrations.add(new NetworksIntegrationMain());
            }
            if (enabledNetworksExpansion) {
                integrations.add(new NetworksExpansionIntegrationMain());
            }
            if (enabledObsidianExpansion) {
                integrations.add(new ObsidianExpansionIntegrationMain());
            }
            if (enabledSlimeAEPlugin) {
                integrations.add(new SlimeAEPluginIntegrationMain());
            }
            if (enabledSlimeTinker) {
                integrations.add(new SlimeTinkerIntegrationMain());
            }

            startupIntegrations();

            RecipeCompleteProvider.addSource(new DefaultPlayerInventoryRecipeCompleteSlimefunSource());
            RecipeCompleteProvider.addSource(new DefaultPlayerInventoryRecipeCompleteVanillaSource());
        }, 1L);
    }

    public boolean hasRecipeCompletableWithGuide() {
        return hasRecipeCompletableWithGuide;
    }

    private void startupIntegrations() {
        for (Integration integration : integrations) {
            plugin.getLogger().info("Hooking " + integration.getHookPlugin());
            try {
                integration.onEnable();
                plugin.getLogger().info("Hooked " + integration.getHookPlugin());
            } catch (Throwable e) {
                Debug.trace(e);
            }
        }
    }

    public void shutdownIntegrations() {
        for (Integration integration : integrations) {
            plugin.getLogger().info("Unhooking " + integration.getHookPlugin());
            try {
                integration.onDisable();
                plugin.getLogger().info("Unhooked " + integration.getHookPlugin());
            } catch (Throwable e) {
                Debug.trace(e);
            }
        }
    }

    public boolean isEnabledFinalTech() {
        return enabledFinalTech;
    }

    public boolean isEnabledFinalTECH() {
        return enabledFinalTECH;
    }

    public boolean isEnabledFinalTECH_Changed() {
        return enabledFinalTECH_Changed;
    }
}
