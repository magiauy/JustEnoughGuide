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

package com.balugaq.jeg.api.cost;

import com.balugaq.jeg.api.objects.annotations.CallTimeSensitive;
import com.balugaq.jeg.core.managers.IntegrationManager;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.StackUtils;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.sefiraat.emctech.managers.ConfigManager;
import io.github.sefiraat.emctech.utils.EmcUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.AbstractEnergyProvider;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.RandomizedSet;
import lombok.SneakyThrows;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"unchecked", "unused", "JavaExistingMethodCanBeUsed", "deprecation", "rawtypes", "DataFlowIssue", "ConstantValue"})
public class CERCalculator {
    public static final Set<SlimefunItem> machines = new HashSet<>();

    public static void load() {
        IntegrationManager.scheduleRun(CERCalculator::loadInternal);
        ValueTable.load();
    }

    @CallTimeSensitive(CallTimeSensitive.AfterSlimefunLoaded)
    @ApiStatus.Internal
    private static void loadInternal() {
        for (SlimefunItem sf : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            String className = sf.getClass().getName();
            if (sf instanceof AContainer || className.equals("org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomTemplateMachine") || sf instanceof AbstractEnergyProvider || className.equals("org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomMaterialGenerator") || className.equals("io.ncbpfluffybear.slimecustomizer.objects.CustomMaterialGenerator") || className.equals("io.github.mooy1.infinityexpansion.items.blocks.StrainerBase") || className.equals("io.github.mooy1.infinityexpansion.items.quarries.Quarry") || className.equals("io.github.mooy1.infinityexpansion.items.machines.SingularityConstructor") || className.equals("io.github.mooy1.infinityexpansion.items.machines.MaterialGenerator") || className.equals("io.github.mooy1.infinityexpansion.items.machines.ResourceSynthesizer") || className.equals("io.github.mooy1.infinityexpansion.items.machines.GrowingMachine") || isInstance(sf, "MachineBlock") || isInstance(sf, "AbstractElectricMachine") || className.equals("io.github.mooy1.infinityexpansion.items.machines.StoneworksFactory") || className.equals("io.github.mooy1.infinityexpansion.items.machines.VoidHarvester") || className.equals("io.github.mooy1.infinityexpansion.items.mobdata.MobDataCard")) {
                machines.add(sf);
            }
        }
    }

    public static double calc(@NotNull SlimefunItem sf, @NotNull String searchTerm) {
        if (sf == null || sf.isDisabled() || searchTerm == null || !machines.contains(sf)) {
            return -1.0D;
        }

        return calc0(sf, searchTerm);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @SneakyThrows
    @ParametersAreNonnullByDefault
    private static double calc0(SlimefunItem sf, String searchTerm) {
        String className = sf.getClass().getName();
        double cost = ValueTable.getValue(sf);

        if (sf instanceof AContainer ac) {
            for (var recipe : ac.getMachineRecipes()) {
                for (var item : recipe.getOutput()) {
                    if (similar(searchTerm, item)) {
                        return calc(cost + ValueTable.getValue(recipe.getInput()), item.getAmount(), recipe.getTicks(), (double) ac.getEnergyConsumption() / ac.getSpeed() * recipe.getTicks());
                    }
                }
            }
        }

        // RykenSlimefunCustomizer - CustomTemplateMachine
        // vals:
        // boolean fasterIfMoreTemplates
        // boolean moreOutputIfMoreTemplates
        // int consumption
        // List<MachineTemplate> templates
        //
        // record MachineTemplate(ItemStack template, List<CustomMachineRecipe> recipes)
        //
        else if (className.equals("org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomTemplateMachine")) {
            boolean fasterIfMoreTemplates = ReflectionUtil.getValue(sf, "fasterIfMoreTemplates", boolean.class);
            boolean moreOutputIfMoreTemplates = ReflectionUtil.getValue(sf, "moreOutputIfMoreTemplates", boolean.class);
            int consumption = ReflectionUtil.getValue(sf, "consumption", int.class);
            List/*<MachineTemplate>*/ templates = ReflectionUtil.getValue(sf, "templates", List.class);
            for (var template /* record MachineTemplate */: templates) {
                ItemStack t = (ItemStack) ReflectionUtil.invokeMethod(template, "template");
                int mavg = t.getMaxStackSize() * 2;
                List/*<CustomMachineRecipe>*/<MachineRecipe> recipes = (List<MachineRecipe>) ReflectionUtil.invokeMethod(sf, "recipes");
                for (var recipe : recipes) {
                    for (var item : recipe.getOutput()) {
                        if (similar(searchTerm, item)) {
                            double btv = ValueTable.getTemplateValue(t);
                            return calc(cost + (fasterIfMoreTemplates ? btv / mavg : btv), moreOutputIfMoreTemplates ? (double) item.getAmount() / mavg : item.getAmount(), fasterIfMoreTemplates ? (double) recipe.getTicks() / mavg : recipe.getTicks(), (double) consumption * recipe.getTicks());
                        }
                    }
                }
            }
        }

        // Slimefun - AGenerator
        // SlimeCustomizer - SCAGenerator
        // RykenSlimefunCustomizer - CustomGenerator
        else if (sf instanceof AbstractEnergyProvider aep) {
            for (var ft : aep.getFuelTypes()) {
                if (similar(searchTerm, ft.getOutput())) {
                    return calc(cost + ValueTable.getValue(ft.getInput()), ft.getOutput().getAmount(), ft.getTicks(), -aep.getEnergyProduction() * ft.getTicks());
                }
            }
        }

        // RykenSlimefunCustomizer - CustomMaterialGenerator
        // vals:
        // int tickRate
        // int per
        // List<ItemStack> generation
        // List<Integer> chances
        // boolean chooseOne
        //
        else if (className.equals("org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomMaterialGenerator")) {
            int tickRate = ReflectionUtil.getValue(sf, "tickRate", int.class);
            int per = ReflectionUtil.getValue(sf, "per", int.class);
            List/*<ItemStack>*/<ItemStack> generation = ReflectionUtil.getValue(sf, "generation", List.class);
            List/*<Integer>*/<Integer> chances = ReflectionUtil.getValue(sf, "chances", List.class);
            boolean chooseOne = ReflectionUtil.getValue(sf, "chooseOne", boolean.class);
            for (int a = 0; a < generation.size(); a++) {
                var i = generation.get(a);
                if (similar(searchTerm, i)) {
                    if (chances != null && !chances.isEmpty()) {
                        double bv = i.getAmount() * chances.get(a);
                        return calc(cost, chooseOne ? bv / chances.size() : bv, tickRate, per * tickRate);
                    } else {
                        return calc(cost, i.getAmount(), tickRate, per * tickRate);
                    }
                }
            }
        }

        // SlimeCustomizer - CustomMaterialGenerator
        // vals:
        // int tickRate
        // ItemStack output
        //
        else if (className.equals("io.ncbpfluffybear.slimecustomizer.objects.CustomMaterialGenerator")) {
            int tickRate = ReflectionUtil.getValue(sf, "tickRate", int.class);
            ItemStack output = ReflectionUtil.getValue(sf, "output", ItemStack.class);
            if (similar(searchTerm, output)) {
                return calc(cost, output.getAmount(), tickRate, 0);
            }
        }

        // InfinityExpansion - StrainerBase
        // vals:
        // static ItemStack POTATO
        // static ItemStack[] OUTPUTS
        //
        else if (className.equals("io.github.mooy1.infinityexpansion.items.blocks.StrainerBase")) {
            ItemStack POTATO = ReflectionUtil.getStaticValue(sf.getClass(), "POTATO", ItemStack.class);
            if (similar(searchTerm, POTATO)) {
                return calc(cost, POTATO.getAmount(), 40, 0);
            }

            ItemStack[] OUTPUTS = ReflectionUtil.getStaticValue(sf.getClass(), "OUTPUTS", ItemStack[].class);
            for (var o : OUTPUTS) {
                if (similar(searchTerm, o)) {
                    return calc(cost, o.getAmount(), 40, 0);
                }
            }
        }

        // InfinityExpansion - Quarry
        // vals:
        // static int INTERVAL
        // Material[] outputs
        // int speed
        // int energyPerTick
        //
        else if (className.equals("io.github.mooy1.infinityexpansion.items.quarries.Quarry")) {
            int INTERVAL = ReflectionUtil.getStaticValue(sf.getClass(), "INTERVAL", int.class);
            Material[] outputs = ReflectionUtil.getValue(sf, "OUTPUTS", Material[].class);
            int speed = ReflectionUtil.getValue(sf, "speed", int.class);
            int energyPerTick = ReflectionUtil.getValue(sf, "energyPerTick", int.class);
            for (var m : outputs) {
                if (similar(searchTerm, new ItemStack(m))) {
                    return calc(cost, speed, INTERVAL, energyPerTick * INTERVAL);
                }
            }
        }

        // InfinityExpansion - SingularityConstructor
        // vals:
        // static List<Recipe> RECIPE_LIST
        // int energyPerTick
        //
        // Recipe vals:
        // SlimefunItemStack output
        // ItemStack input
        // String id
        // int amount
        //
        else if (className.equals("io.github.mooy1.infinityexpansion.items.machines.SingularityConstructor")) {
            List/*<Recipe>*/ RECIPE_LIST = ReflectionUtil.getStaticValue(sf.getClass(), "RECIPE_LIST", List.class);
            int energyPerTick = ReflectionUtil.getValue(sf, "energyPerTick", int.class);
            for (var recipe : RECIPE_LIST) {
                SlimefunItemStack output = ReflectionUtil.getValue(recipe, "output", SlimefunItemStack.class);
                ItemStack input = ReflectionUtil.getValue(recipe, "input", ItemStack.class);
                int amount = ReflectionUtil.getValue(sf, "amount", int.class);
                if (similar(searchTerm, Converter.getItem(output))) {
                    int speed = sf.getId().equals("SINGULARITY_CONSTRUCTOR") ? 1 : sf.getId().equals("INFINITY_CONSTRUCTOR") ? 64 : 1;
                    return calc(cost + ValueTable.getValue(input, amount), 1, speed, (double) (energyPerTick * amount) / speed);
                }
            }
        }

        // InfinityExpansion MaterialGenerator
        // vals:
        // Material material
        // int speed
        // int energyPerTick
        else if (className.equals("io.github.mooy1.infinityexpansion.items.machines.MaterialGenerator")) {
            Material material = ReflectionUtil.getValue(sf, "material", Material.class);
            int speed = ReflectionUtil.getValue(sf, "speed", int.class);
            int energyPerTick = ReflectionUtil.getValue(sf, "energyPerTick", int.class);
            if (similar(searchTerm, new ItemStack(material))) {
                return calc(cost, speed, speed, energyPerTick);
            }
        }

        // InfinityExpansion - ResourceSynthesizer
        // vals:
        // SlimefunItemStack[] recipes
        // int energyPerTick
        //
        else if (className.equals("io.github.mooy1.infinityexpansion.items.machines.ResourceSynthesizer")) {
            SlimefunItemStack[] recipes = ReflectionUtil.getValue(sf, "recipes", SlimefunItemStack[].class);
            int energyPerTick = ReflectionUtil.getValue(sf, "energyPerTick", int.class);
            for (int i = 0; i < recipes.length; i += 3) {
                ItemStack i1 = recipes[i];
                ItemStack i2 = recipes[i + 1];
                ItemStack output = Converter.getItem(recipes[i + 2]);
                if (similar(searchTerm, output)) {
                    return calc(cost + ValueTable.getValue(i1) + ValueTable.getValue(i2), output.getAmount(), 1, energyPerTick);
                }
            }
        }

        // InfinityExpansion - GrowingMachine
        // vals:
        // EnumMap<Material, ItemStack[]> recipes
        // int ticksPerOutput
        // int energyPerTick
        //
        else if (className.equals("io.github.mooy1.infinityexpansion.items.machines.GrowingMachine")) {
            EnumMap/*<Material, ItemStack[]>*/<Material, ItemStack[]> recipes = ReflectionUtil.getValue(sf, "recipes", EnumMap.class);
            int ticksPerOutput = ReflectionUtil.getValue(sf, "ticksPerOutput", int.class);
            int energyPerTick = ReflectionUtil.getValue(sf, "energyPerTick", int.class);
            for (var entry : recipes.entrySet()) {
                Material material = entry.getKey();
                ItemStack[] os = entry.getValue();
                for (ItemStack o : os) {
                    if (similar(searchTerm, o)) {
                        return calc(cost + ValueTable.getValue(new ItemStack(material)), o.getAmount(), ticksPerOutput, energyPerTick * ticksPerOutput);
                    }
                }
            }
        }

        // InfinityLib - MachineBlock
        // vals:
        // List<MachineBlockRecipe> recipes
        // int ticksPerOutput
        // int energyPerTick
        //
        // MachineBlockRecipe vals:
        // String[] strings
        // int[] amounts
        // ItemStack output
        //
        else if (isInstance(sf, "MachineBlock")) {
            List/*MachineBlockRecipe*/ recipes = ReflectionUtil.getValue(sf, "recipes", List.class);
            int ticksPerOutput = ReflectionUtil.getValue(sf, "ticksPerOutput", int.class);
            int energyPerTick = ReflectionUtil.getValue(sf, "energyPerTick", int.class);

            if (recipes != null) {
                for (var recipe : recipes) {
                    String[] strings = ReflectionUtil.getValue(sf, "strings", String[].class);
                    int[] amounts = ReflectionUtil.getValue(sf, "amounts", int[].class);
                    ItemStack output = ReflectionUtil.getValue(sf, "output", ItemStack.class);
                    if (similar(searchTerm, output)) {
                        return calc(cost + ValueTable.getValue(translateIntoItemStackArray(strings, amounts)), output.getAmount(), ticksPerOutput, energyPerTick * ticksPerOutput);
                    }
                }
            } else {
                // ?
            }
        }

        // DynaTech - AbstractElectricMachine
        // vals:
        // List<MachineRecipe> recipes
        // int energyConsumedPerTick
        // int processingSpeed
        //
        else if (isInstance(sf, "AbstractElectricMachine")) {
            List/*<MachineRecipe>*/<MachineRecipe> recipes = ReflectionUtil.getValue(sf, "recipes", List.class);
            int energyConsumedPerTick = ReflectionUtil.getValue(sf, "energyConsumedPerTick", int.class);
            int processingSpeed = ReflectionUtil.getValue(sf, "processingSpeed", int.class);
            for (var recipe : recipes) {
                for (var o : recipe.getOutput()) {
                    if (similar(searchTerm, o)) {
                        return calc(cost + ValueTable.getValue(recipe.getInput()), o.getAmount(), (double) recipe.getTicks() / processingSpeed, (double) (energyConsumedPerTick * recipe.getTicks()) / processingSpeed);
                    }
                }
            }
        }

        // InfinityExpansion StoneworksFactory
        // vals:
        // int energyPerTick
        //
        else if (className.equals("io.github.mooy1.infinityexpansion.items.machines.StoneworksFactory")) {
            int energyPerTick = ReflectionUtil.getValue(sf, "energyPerTick", int.class);
            Set<Material> materials = new HashSet<>();
            materials.add(Material.COBBLESTONE);
            materials.add(Material.STONE);
            materials.add(Material.SAND);
            materials.add(Material.STONE_BRICKS);
            materials.add(Material.SMOOTH_STONE);
            materials.add(Material.GLASS);
            materials.add(Material.CRACKED_STONE_BRICKS);
            materials.add(Material.GRAVEL);
            materials.add(Material.GRANITE);
            materials.add(Material.DIORITE);
            materials.add(Material.ANDESITE);
            materials.add(Material.POLISHED_GRANITE);
            materials.add(Material.POLISHED_DIORITE);
            materials.add(Material.POLISHED_ANDESITE);
            materials.add(Material.SANDSTONE);
            for (var material : materials) {
                if (similar(searchTerm, new ItemStack(material))) {
                    return calc(cost + ValueTable.getValue(material), 1, 1, energyPerTick);
                }
            }
        }

        // InfinityExpansion - VoidHarvester
        // vals:
        // static int TIME
        // int speed
        // int energyPerTick
        //
        else if (className.equals("io.github.mooy1.infinityexpansion.items.machines.VoidHarvester")) {
            int TIME = ReflectionUtil.getStaticValue(sf.getClass(), "TIME", int.class);
            int speed = ReflectionUtil.getValue(sf, "speed", int.class);
            int energyPerTick = ReflectionUtil.getValue(sf, "energyPerTick", int.class);
            ItemStack output;
            if (sf.getAddon().getName().equals("InfinityExpansion-Changed")) {
                output = SlimefunItem.getById("VOID_DUST").getItem();
            } else {
                output = SlimefunItem.getById("VOID_BIT").getItem();
            }

            if (similar(searchTerm, output)) {
                return calc(cost, 1, (double) TIME / speed, energyPerTick);
            }
        }

        // InfinityExpansion - MobDataCard
        // vals:
        // RandomizedSet<ItemStack> drops
        //
        if (className.equals("io.github.mooy1.infinityexpansion.items.mobdata.MobDataCard")) {
            RandomizedSet<ItemStack> drops = ReflectionUtil.getValue(sf, "drops", RandomizedSet.class);
            SlimefunItem chamber = SlimefunItem.getById("MOB_SIMULATION_CHAMBER");
            int energy = ReflectionUtil.getValue(chamber, "energy", int.class);
            int interval = ReflectionUtil.getValue(chamber, "interval", int.class);
            for (var drop : drops) {
                if (similar(searchTerm, drop)) {
                    return calc(cost + ValueTable.getValue(chamber), drop.getAmount(), interval, energy * interval);
                }
            }
        }

        return -2.0D;
    }

    private static ItemStack[] translateIntoItemStackArray(String[] strings, int[] amounts) {
        int v = Math.min(amounts.length, strings.length);
        ItemStack[] array = new ItemStack[v];
        for (int i = 0; i < v; i++) {
            String s = strings[i];
            if (s == null) {
                array[i] = null;
                continue;
            }
            int amount = amounts[i];

            SlimefunItem sf = SlimefunItem.getById(s);
            if (sf != null) {
                array[i] = StackUtils.getAsQuantity(sf.getItem(), amount);
            } else {
                Material material = Material.getMaterial(s);
                if (material == null) {
                    array[i] = null;
                    continue;
                }

                array[i] = new ItemStack(material, amount);
            }
        }

        return array;
    }

    public static double calc(double cost, double outputAmount, double processTicks, double energyCost) {
        if (Double.isInfinite(cost) || Double.isNaN(cost) || Double.isInfinite(outputAmount) || Double.isNaN(outputAmount) || Double.isInfinite(processTicks) || Double.isNaN(processTicks) || Double.isInfinite(energyCost) || Double.isNaN(energyCost)) {
            return -3.0D;
        }

        if (cost < 0.0D || outputAmount <= 0.0D || processTicks < 0.0D) {
            return -4.0D;
        }

        if (energyCost >= 0.0D) {
            return Math.log10(128.0D * outputAmount / (1 + processTicks * ((1 + energyCost) / 10D) * ((1 + cost) / 1.28e6D)));
        } else {
            return Math.log10(128.0D * outputAmount / (1 + processTicks * ((1 + cost) / 1.28e7D) - energyCost));
        }
    }

    public static boolean similar(String i1, ItemStack i2) {
        String name = ItemStackHelper.getDisplayName(i2);
        Pattern p = Pattern.compile("&|§k.+&|§");
        Matcher matcher;
        while ((matcher = p.matcher(i1)).find()) {
            name = matcher.replaceAll("&");
        }
        name = ChatColor.stripColor(name);
        return i1.replaceAll(" ", "").equalsIgnoreCase(name.replaceAll(" ", ""));
    }

    public static <T extends SlimefunItem> boolean isInstance(@NotNull T item, String classSimpleName) {
        Class<?> clazz = item.getClass();
        while (clazz != SlimefunItem.class) {
            if (clazz.getSimpleName().equals(classSimpleName)) {
                return true;
            }
            clazz = clazz.getSuperclass();
        }
        return false;
    }

    @SuppressWarnings({"unused", "deprecation", "DataFlowIssue"})
    public static class ValueTable {
        private static final HashMap<Integer, Double> valueMap = new HashMap<>();

        public static void load() {
            IntegrationManager.scheduleRun(ValueTable::loadInternal);
        }

        @CallTimeSensitive(CallTimeSensitive.AfterSlimefunLoaded)
        @ApiStatus.Internal
        private static void loadInternal() {
            for (SlimefunItem sf : Slimefun.getRegistry().getEnabledSlimefunItems()) {
                getItemValueSlimefun(sf);
            }

            for (Material material : Material.values()) {
                if (material.isItem() && !material.isLegacy() && !material.isAir()) {
                    getItemValueVanilla(new ItemStack(material));
                }
            }
        }

        public static double getValue(SlimefunItem sf) {
            return getValue(sf.getItem());
        }

        public static double getValue(ItemStack itemStack, int amount) {
            if (itemStack == null) {
                return 0.0D;
            }

            int hash = itemStack.hashCode();
            if (valueMap.containsKey(hash)) {
                return valueMap.get(hash) * amount;
            }

            SlimefunItem sf = SlimefunItem.getByItem(itemStack);
            double v;
            if (sf != null) {
                v = getItemValueSlimefun(sf);
            } else {
                v = getItemValueVanilla(itemStack);
            }
            setValue(itemStack, v);
            return v;
        }

        private static void setValue(ItemStack itemStack, double value) {
            valueMap.put(itemStack.hashCode(), value);
        }

        public static double getValue(ItemStack itemStack) {
            return getValue(StackUtils.getAsQuantity(itemStack, 1), itemStack.getAmount());
        }

        public static double getValue(ItemStack[] itemStacks) {
            double v = 0.0D;
            for (ItemStack itemStack : itemStacks) {
                v += getValue(itemStack);
            }

            return v;
        }

        public static double getValue(Material material) {
            return getValue(new ItemStack(material));
        }

        public static double getTemplateValue(ItemStack itemStack) {
            return getValue(itemStack) / 1e4;
        }

        public static void setup() {
            addBaseValues();
        }

        private static void addBaseValues() {
            setValue(new ItemStack(Material.GRASS_BLOCK), 1);
            setValue(new ItemStack(Material.MOSS_BLOCK), 1);
            setValue(new ItemStack(Material.DIRT), 1);
            setValue(new ItemStack(Material.PODZOL), 1);
            setValue(new ItemStack(Material.CRIMSON_NYLIUM), 1);
            setValue(new ItemStack(Material.WARPED_NYLIUM), 1);
            setValue(new ItemStack(Material.COBBLESTONE), 1);
            setValue(new ItemStack(Material.DIORITE), 1);
            setValue(new ItemStack(Material.ANDESITE), 1);
            setValue(new ItemStack(Material.GRANITE), 1);
            setValue(new ItemStack(Material.OAK_SAPLING), 2);
            setValue(new ItemStack(Material.SPRUCE_SAPLING), 2);
            setValue(new ItemStack(Material.BIRCH_SAPLING), 2);
            setValue(new ItemStack(Material.JUNGLE_SAPLING), 2);
            setValue(new ItemStack(Material.ACACIA_SAPLING), 2);
            setValue(new ItemStack(Material.DARK_OAK_SAPLING), 2);
            setValue(new ItemStack(Material.MANGROVE_PROPAGULE), 2);
            setValue(new ItemStack(Material.SAND), 1);
            setValue(new ItemStack(Material.RED_SAND), 1);
            setValue(new ItemStack(Material.GRAVEL), 1);
            setValue(new ItemStack(Material.GOLD_ORE), 0);
            setValue(new ItemStack(Material.IRON_ORE), 0);
            setValue(new ItemStack(Material.COAL_ORE), 0);
            setValue(new ItemStack(Material.NETHER_GOLD_ORE), 0);
            setValue(new ItemStack(Material.OAK_LOG), 4);
            setValue(new ItemStack(Material.SPRUCE_LOG), 4);
            setValue(new ItemStack(Material.BIRCH_LOG), 4);
            setValue(new ItemStack(Material.JUNGLE_LOG), 4);
            setValue(new ItemStack(Material.ACACIA_LOG), 4);
            setValue(new ItemStack(Material.DARK_OAK_LOG), 4);
            setValue(new ItemStack(Material.CRIMSON_STEM), 4);
            setValue(new ItemStack(Material.WARPED_STEM), 4);
            setValue(new ItemStack(Material.MANGROVE_LOG), 4);
            setValue(new ItemStack(Material.STRIPPED_OAK_LOG), 4);
            setValue(new ItemStack(Material.STRIPPED_SPRUCE_LOG), 4);
            setValue(new ItemStack(Material.STRIPPED_BIRCH_LOG), 4);
            setValue(new ItemStack(Material.STRIPPED_JUNGLE_LOG), 4);
            setValue(new ItemStack(Material.STRIPPED_ACACIA_LOG), 4);
            setValue(new ItemStack(Material.STRIPPED_DARK_OAK_LOG), 4);
            setValue(new ItemStack(Material.STRIPPED_CRIMSON_STEM), 4);
            setValue(new ItemStack(Material.STRIPPED_WARPED_STEM), 4);
            setValue(new ItemStack(Material.STRIPPED_MANGROVE_LOG), 4);
            setValue(new ItemStack(Material.STRIPPED_OAK_WOOD), 4);
            setValue(new ItemStack(Material.STRIPPED_SPRUCE_WOOD), 4);
            setValue(new ItemStack(Material.STRIPPED_BIRCH_WOOD), 4);
            setValue(new ItemStack(Material.STRIPPED_JUNGLE_WOOD), 4);
            setValue(new ItemStack(Material.STRIPPED_ACACIA_WOOD), 4);
            setValue(new ItemStack(Material.STRIPPED_DARK_OAK_WOOD), 4);
            setValue(new ItemStack(Material.STRIPPED_CRIMSON_HYPHAE), 4);
            setValue(new ItemStack(Material.STRIPPED_WARPED_HYPHAE), 4);
            setValue(new ItemStack(Material.STRIPPED_MANGROVE_WOOD), 4);
            setValue(new ItemStack(Material.OAK_WOOD), 4);
            setValue(new ItemStack(Material.SPRUCE_WOOD), 4);
            setValue(new ItemStack(Material.BIRCH_WOOD), 4);
            setValue(new ItemStack(Material.JUNGLE_WOOD), 4);
            setValue(new ItemStack(Material.ACACIA_WOOD), 4);
            setValue(new ItemStack(Material.DARK_OAK_WOOD), 4);
            setValue(new ItemStack(Material.CRIMSON_HYPHAE), 4);
            setValue(new ItemStack(Material.WARPED_HYPHAE), 4);
            setValue(new ItemStack(Material.MANGROVE_WOOD), 4);
            setValue(new ItemStack(Material.OAK_LEAVES), 1);
            setValue(new ItemStack(Material.SPRUCE_LEAVES), 1);
            setValue(new ItemStack(Material.BIRCH_LEAVES), 1);
            setValue(new ItemStack(Material.JUNGLE_LEAVES), 1);
            setValue(new ItemStack(Material.ACACIA_LEAVES), 1);
            setValue(new ItemStack(Material.DARK_OAK_LEAVES), 1);
            setValue(new ItemStack(Material.AZALEA_LEAVES), 1);
            setValue(new ItemStack(Material.FLOWERING_AZALEA_LEAVES), 1);
            setValue(new ItemStack(Material.MANGROVE_LEAVES), 1);
            setValue(new ItemStack(Material.AZALEA), 8);
            setValue(new ItemStack(Material.WET_SPONGE), 16);
            setValue(new ItemStack(Material.LAPIS_ORE), 0);
            setValue(new ItemStack(Material.COBWEB), 4);
            setValue(new ItemStack(Material.FERN), 1);
            setValue(new ItemStack(Material.DEAD_BUSH), 1);
            setValue(new ItemStack(Material.SEAGRASS), 1);
            setValue(new ItemStack(Material.SEA_PICKLE), 4);
            setValue(new ItemStack(Material.DANDELION), 2);
            setValue(new ItemStack(Material.POPPY), 2);
            setValue(new ItemStack(Material.BLUE_ORCHID), 2);
            setValue(new ItemStack(Material.ALLIUM), 2);
            setValue(new ItemStack(Material.AZURE_BLUET), 2);
            setValue(new ItemStack(Material.RED_TULIP), 2);
            setValue(new ItemStack(Material.ORANGE_TULIP), 2);
            setValue(new ItemStack(Material.WHITE_TULIP), 2);
            setValue(new ItemStack(Material.PINK_TULIP), 2);
            setValue(new ItemStack(Material.OXEYE_DAISY), 2);
            setValue(new ItemStack(Material.CORNFLOWER), 2);
            setValue(new ItemStack(Material.LILY_OF_THE_VALLEY), 2);
            setValue(new ItemStack(Material.WITHER_ROSE), 32);
            setValue(new ItemStack(Material.BROWN_MUSHROOM), 4);
            setValue(new ItemStack(Material.RED_MUSHROOM), 4);
            setValue(new ItemStack(Material.CRIMSON_FUNGUS), 4);
            setValue(new ItemStack(Material.WARPED_FUNGUS), 4);
            setValue(new ItemStack(Material.CRIMSON_ROOTS), 2);
            setValue(new ItemStack(Material.WARPED_ROOTS), 2);
            setValue(new ItemStack(Material.WEEPING_VINES), 2);
            setValue(new ItemStack(Material.TWISTING_VINES), 2);
            setValue(new ItemStack(Material.MANGROVE_ROOTS), 4);
            setValue(new ItemStack(Material.MUD), 8);
            setValue(new ItemStack(Material.SCULK_SHRIEKER), 1024);
            setValue(new ItemStack(Material.SCULK_VEIN), 16);
            setValue(new ItemStack(Material.SUGAR_CANE), 2);
            setValue(new ItemStack(Material.KELP), 1);
            setValue(new ItemStack(Material.BAMBOO), 1);
            setValue(new ItemStack(Material.OBSIDIAN), 16);
            setValue(new ItemStack(Material.CHORUS_FLOWER), 8);
            setValue(new ItemStack(Material.DIAMOND_ORE), 0);
            setValue(new ItemStack(Material.REDSTONE_ORE), 0);
            setValue(new ItemStack(Material.ICE), 1);
            setValue(new ItemStack(Material.CACTUS), 1);
            setValue(new ItemStack(Material.PUMPKIN), 4);
            setValue(new ItemStack(Material.CARVED_PUMPKIN), 4);
            setValue(new ItemStack(Material.NETHERRACK), 1);
            setValue(new ItemStack(Material.SOUL_SAND), 2);
            setValue(new ItemStack(Material.SOUL_SOIL), 2);
            setValue(new ItemStack(Material.BASALT), 1);
            setValue(new ItemStack(Material.BROWN_MUSHROOM_BLOCK), 4);
            setValue(new ItemStack(Material.RED_MUSHROOM_BLOCK), 4);
            setValue(new ItemStack(Material.MUSHROOM_STEM), 4);
            setValue(new ItemStack(Material.VINE), 2);
            setValue(new ItemStack(Material.MYCELIUM), 8);
            setValue(new ItemStack(Material.LILY_PAD), 8);
            setValue(new ItemStack(Material.END_STONE), 2);
            setValue(new ItemStack(Material.DRAGON_EGG), 32768);
            setValue(new ItemStack(Material.EMERALD_ORE), 0);
            setValue(new ItemStack(Material.NETHER_QUARTZ_ORE), 0);
            setValue(new ItemStack(Material.SUNFLOWER), 2);
            setValue(new ItemStack(Material.LILAC), 2);
            setValue(new ItemStack(Material.ROSE_BUSH), 2);
            setValue(new ItemStack(Material.PEONY), 2);
            setValue(new ItemStack(Material.TALL_GRASS), 1);
            setValue(new ItemStack(Material.LARGE_FERN), 1);
            setValue(new ItemStack(Material.TURTLE_EGG), 64);
            setValue(new ItemStack(Material.DEAD_TUBE_CORAL_BLOCK), 8);
            setValue(new ItemStack(Material.DEAD_BRAIN_CORAL_BLOCK), 8);
            setValue(new ItemStack(Material.DEAD_BUBBLE_CORAL_BLOCK), 8);
            setValue(new ItemStack(Material.DEAD_FIRE_CORAL_BLOCK), 8);
            setValue(new ItemStack(Material.DEAD_HORN_CORAL_BLOCK), 8);
            setValue(new ItemStack(Material.TUBE_CORAL_BLOCK), 8);
            setValue(new ItemStack(Material.BRAIN_CORAL_BLOCK), 8);
            setValue(new ItemStack(Material.BUBBLE_CORAL_BLOCK), 8);
            setValue(new ItemStack(Material.FIRE_CORAL_BLOCK), 8);
            setValue(new ItemStack(Material.HORN_CORAL_BLOCK), 8);
            setValue(new ItemStack(Material.TUBE_CORAL), 8);
            setValue(new ItemStack(Material.BRAIN_CORAL), 8);
            setValue(new ItemStack(Material.BUBBLE_CORAL), 8);
            setValue(new ItemStack(Material.FIRE_CORAL), 8);
            setValue(new ItemStack(Material.HORN_CORAL), 8);
            setValue(new ItemStack(Material.DEAD_TUBE_CORAL), 8);
            setValue(new ItemStack(Material.DEAD_BRAIN_CORAL), 8);
            setValue(new ItemStack(Material.DEAD_BUBBLE_CORAL), 8);
            setValue(new ItemStack(Material.DEAD_FIRE_CORAL), 8);
            setValue(new ItemStack(Material.DEAD_HORN_CORAL), 8);
            setValue(new ItemStack(Material.TUBE_CORAL_FAN), 8);
            setValue(new ItemStack(Material.BRAIN_CORAL_FAN), 8);
            setValue(new ItemStack(Material.BUBBLE_CORAL_FAN), 8);
            setValue(new ItemStack(Material.FIRE_CORAL_FAN), 8);
            setValue(new ItemStack(Material.HORN_CORAL_FAN), 8);
            setValue(new ItemStack(Material.DEAD_TUBE_CORAL_FAN), 8);
            setValue(new ItemStack(Material.DEAD_BRAIN_CORAL_FAN), 8);
            setValue(new ItemStack(Material.DEAD_BUBBLE_CORAL_FAN), 8);
            setValue(new ItemStack(Material.DEAD_FIRE_CORAL_FAN), 8);
            setValue(new ItemStack(Material.DEAD_HORN_CORAL_FAN), 8);
            setValue(new ItemStack(Material.APPLE), 8);
            setValue(new ItemStack(Material.DIAMOND), 1028);
            setValue(new ItemStack(Material.IRON_INGOT), 32);
            setValue(new ItemStack(Material.GOLD_INGOT), 32);
            setValue(new ItemStack(Material.NETHERITE_SCRAP), 4096);
            setValue(new ItemStack(Material.STRING), 4);
            setValue(new ItemStack(Material.FEATHER), 2);
            setValue(new ItemStack(Material.GUNPOWDER), 8);
            setValue(new ItemStack(Material.WHEAT_SEEDS), 2);
            setValue(new ItemStack(Material.WHEAT), 2);
            setValue(new ItemStack(Material.FLINT), 1);
            setValue(new ItemStack(Material.PORKCHOP), 4);
            setValue(new ItemStack(Material.WATER_BUCKET), 128);
            setValue(new ItemStack(Material.LAVA_BUCKET), 256);
            setValue(new ItemStack(Material.SADDLE), 256);
            setValue(new ItemStack(Material.REDSTONE), 16);
            setValue(new ItemStack(Material.COAL), 16);
            setValue(new ItemStack(Material.SNOWBALL), 2);
            setValue(new ItemStack(Material.MILK_BUCKET), 256);
            setValue(new ItemStack(Material.CLAY_BALL), 8);
            setValue(new ItemStack(Material.SLIME_BALL), 8);
            setValue(new ItemStack(Material.EGG), 4);
            setValue(new ItemStack(Material.GLOWSTONE_DUST), 8);
            setValue(new ItemStack(Material.COD), 64);
            setValue(new ItemStack(Material.SALMON), 64);
            setValue(new ItemStack(Material.TROPICAL_FISH), 128);
            setValue(new ItemStack(Material.PUFFERFISH), 64);
            setValue(new ItemStack(Material.INK_SAC), 16);
            setValue(new ItemStack(Material.COCOA_BEANS), 16);
            setValue(new ItemStack(Material.LAPIS_LAZULI), 16);
            setValue(new ItemStack(Material.BONE), 4);
            setValue(new ItemStack(Material.MELON_SLICE), 2);
            setValue(new ItemStack(Material.BEEF), 4);
            setValue(new ItemStack(Material.CHICKEN), 4);
            setValue(new ItemStack(Material.ROTTEN_FLESH), 4);
            setValue(new ItemStack(Material.ENDER_PEARL), 8);
            setValue(new ItemStack(Material.BLAZE_ROD), 8);
            setValue(new ItemStack(Material.GHAST_TEAR), 16);
            setValue(new ItemStack(Material.NETHER_WART), 8);
            setValue(new ItemStack(Material.SPIDER_EYE), 4);
            setValue(new ItemStack(Material.EMERALD), 128);
            setValue(new ItemStack(Material.CARROT), 2);
            setValue(new ItemStack(Material.POTATO), 2);
            setValue(new ItemStack(Material.POISONOUS_POTATO), 8);
            setValue(new ItemStack(Material.SKELETON_SKULL), 128);
            setValue(new ItemStack(Material.WITHER_SKELETON_SKULL), 1024);
            setValue(new ItemStack(Material.ZOMBIE_HEAD), 128);
            setValue(new ItemStack(Material.CREEPER_HEAD), 128);
            setValue(new ItemStack(Material.DRAGON_HEAD), 24576);
            setValue(new ItemStack(Material.NETHER_STAR), 16384);
            setValue(new ItemStack(Material.QUARTZ), 32);
            setValue(new ItemStack(Material.PRISMARINE_SHARD), 8);
            setValue(new ItemStack(Material.PRISMARINE_CRYSTALS), 16);
            setValue(new ItemStack(Material.RABBIT), 2);
            setValue(new ItemStack(Material.RABBIT_FOOT), 16);
            setValue(new ItemStack(Material.RABBIT_HIDE), 2);
            setValue(new ItemStack(Material.NAME_TAG), 256);
            setValue(new ItemStack(Material.MUTTON), 2);
            setValue(new ItemStack(Material.CHORUS_FRUIT), 4);
            setValue(new ItemStack(Material.BEETROOT), 2);
            setValue(new ItemStack(Material.BEETROOT_SEEDS), 2);
            setValue(new ItemStack(Material.DRAGON_BREATH), 512);
            setValue(new ItemStack(Material.ELYTRA), 0);
            setValue(new ItemStack(Material.SHULKER_SHELL), 32);
            setValue(new ItemStack(Material.PHANTOM_MEMBRANE), 16);
            setValue(new ItemStack(Material.NAUTILUS_SHELL), 128);
            setValue(new ItemStack(Material.HEART_OF_THE_SEA), 8192);
            setValue(new ItemStack(Material.BELL), 128);
            setValue(new ItemStack(Material.SWEET_BERRIES), 2);
            setValue(new ItemStack(Material.SHROOMLIGHT), 4);
            setValue(new ItemStack(Material.HONEYCOMB), 1);
            setValue(new ItemStack(Material.CRYING_OBSIDIAN), 64);
            setValue(new ItemStack(Material.BLACKSTONE), 2);
            setValue(new ItemStack(Material.GILDED_BLACKSTONE), 16);
            setValue(new ItemStack(Material.HONEY_BOTTLE), 1);
            setValue(new ItemStack(Material.PLAYER_HEAD), 0);
            setValue(new ItemStack(Material.RAW_IRON), 32);
            setValue(new ItemStack(Material.RAW_GOLD), 32);
            setValue(new ItemStack(Material.RAW_COPPER), 32);
            setValue(new ItemStack(Material.COPPER_INGOT), 32);
            setValue(new ItemStack(Material.DEEPSLATE), 1);
            setValue(new ItemStack(Material.AMETHYST_SHARD), 16);
            setValue(new ItemStack(Material.ECHO_SHARD), 32);
            setValue(new ItemStack(Material.COBBLED_DEEPSLATE), 1);
            setValue(new ItemStack(Material.FROGSPAWN), 64);
            setValue(SlimefunItem.getById("COPPER_DUST").getItem(), 32);
            setValue(SlimefunItem.getById("IRON_DUST").getItem(), 32);
            setValue(SlimefunItem.getById("GOLD_DUST").getItem(), 32);
            setValue(SlimefunItem.getById("TIN_DUST").getItem(), 32);
            setValue(SlimefunItem.getById("MAGNESIUM_DUST").getItem(), 32);
            setValue(SlimefunItem.getById("ZINC_DUST").getItem(), 32);
            setValue(SlimefunItem.getById("LEAD_DUST").getItem(), 32);
            setValue(SlimefunItem.getById("SILVER_DUST").getItem(), 32);
            setValue(SlimefunItem.getById("ALUMINUM_DUST").getItem(), 32);
            setValue(SlimefunItem.getById("NETHER_ICE").getItem(), 128);
            setValue(SlimefunItem.getById("BUCKET_OF_OIL").getItem(), 512);
            setValue(SlimefunItem.getById("SIFTED_ORE").getItem(), 32);
            setValue(SlimefunItem.getById("STONE_CHUNK").getItem(), 1);
            setValue(SlimefunItem.getById("BASIC_CIRCUIT_BOARD").getItem(), 1024);
        }

        private static double getItemValueVanilla(@NotNull ItemStack itemStack) {
            Double storedValue = valueMap.get(itemStack.hashCode());
            if (storedValue != null) {
                return storedValue;
            } else {
                double value = 0.0F;
                List<Recipe> recipeList = Bukkit.getRecipesFor(itemStack);
                if (recipeList.isEmpty()) {
                    return 0.0F;
                } else {
                    for (Recipe recipe : recipeList) {
                        double recipeValue = processRecipeVanilla(recipe);
                        if (recipeValue > (double) 0.0F) {
                            recipeValue = (double) Math.round(recipeValue * (double) 100.0F) / (double) 100.0F;
                            if (value == (double) 0.0F) {
                                value = recipeValue;
                            } else if (recipeValue < value) {
                                value = recipeValue;
                            }
                        }
                    }

                    return value;
                }
            }
        }

        private static double processRecipeVanilla(@NotNull Recipe recipe) {
            if (recipe instanceof ShapedRecipe shapedRecipe) {
                return processShapedRecipe(shapedRecipe);
            } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
                return processShapelessRecipe(shapelessRecipe);
            } else if (recipe instanceof CookingRecipe<?> cookingRecipe) {
                return processCookingRecipe(cookingRecipe);
            } else if (recipe instanceof SmithingRecipe smithingRecipe) {
                return processSmithingRecipe(smithingRecipe);
            } else if (recipe instanceof StonecuttingRecipe stonecuttingRecipe) {
                return processStonecuttingRecipe(stonecuttingRecipe);
            } else {
                return 0.0F;
            }
        }

        private static double processShapedRecipe(@NotNull ShapedRecipe shapedRecipe) {
            double value = 0.0F;
            Map<Character, Double> valueMap = new HashMap<>();

            for (Map.Entry<Character, ItemStack> entry : shapedRecipe.getIngredientMap().entrySet()) {
                if (entry.getValue() != null) {
                    double entryValue = getItemValueVanilla(entry.getValue());
                    if (entryValue == (double) 0.0F) {
                        return 0.0F;
                    }

                    valueMap.put(entry.getKey(), entryValue);
                }
            }

            for (String string : shapedRecipe.getShape()) {
                for (char character : string.toCharArray()) {
                    Double charValue = valueMap.get(character);
                    if (charValue != null) {
                        value += valueMap.get(character);
                    }
                }
            }

            value /= shapedRecipe.getResult().getAmount();
            return value;
        }

        private static double processShapelessRecipe(@NotNull ShapelessRecipe shapelessRecipe) {
            double value = 0.0F;

            for (ItemStack itemStack : shapelessRecipe.getIngredientList()) {
                double itemValue = getItemValueVanilla(itemStack);
                if (itemValue == (double) 0.0F) {
                    return 0.0F;
                }

                value += itemValue;
            }

            value /= shapelessRecipe.getResult().getAmount();
            return value;
        }

        private static double processCookingRecipe(@NotNull CookingRecipe<?> cookingRecipe) {
            double value = getItemValueVanilla(cookingRecipe.getInput());

            value /= cookingRecipe.getResult().getAmount();
            return value;
        }

        private static double processSmithingRecipe(@NotNull SmithingRecipe smithingRecipe) {
            double baseValue = getItemValueVanilla(smithingRecipe.getBase().getItemStack());
            double additionValue = getItemValueVanilla(smithingRecipe.getAddition().getItemStack());

            double value = baseValue + additionValue;
            value /= smithingRecipe.getResult().getAmount();
            return value;
        }

        private static double processStonecuttingRecipe(@NotNull StonecuttingRecipe stonecuttingRecipe) {
            double value = getItemValueVanilla(stonecuttingRecipe.getInput());

            value /= stonecuttingRecipe.getResult().getAmount();
            return value;
        }

        private static double getItemValueSlimefun(@NotNull SlimefunItem slimefunItem) {
            Double storedValue = valueMap.get(slimefunItem.getItem().hashCode());
            if (storedValue != null) {
                return storedValue;
            } else if (slimefunItem.isDisabled()) {
                return 0.0F;
            } else if (ConfigManager.isAddonBlacklisted(slimefunItem.getAddon().getName())) {
                return 0.0F;
            } else {
                double value = 0.0F;
                double recipeValue = processRecipeSlimefun(slimefunItem.getRecipe(), slimefunItem.getRecipeOutput().getAmount() + 1);
                if (recipeValue > (double) 0.0F) {
                    recipeValue = (double) Math.round(recipeValue * (double) 100.0F) / (double) 100.0F;
                    value = recipeValue;
                }

                return value;
            }
        }

        private static double processRecipeSlimefun(@NotNull ItemStack[] recipe, int outputAmount) {
            double value = 0.0F;

            for (ItemStack itemStack : recipe) {
                if (itemStack != null) {
                    SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
                    double itemValue;
                    if (slimefunItem == null) {
                        itemValue = EmcUtils.getEmcValue(itemStack) * (double) itemStack.getAmount();
                    } else {
                        itemValue = getItemValueSlimefun(slimefunItem) * (double) itemStack.getAmount();
                    }

                    if (itemValue == (double) 0.0F) {
                        return 0.0F;
                    }

                    value += itemValue;
                }
            }

            return value / (double) outputAmount;
        }
    }
}
