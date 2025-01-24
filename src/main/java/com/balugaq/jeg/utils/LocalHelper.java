package com.balugaq.jeg.utils;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LocalHelper {
    private static final String def = "未知附属";
    private static final Map<String, Map<String, SlimefunItemStack>> rscItems = new HashMap<>();
    // default language is zh-CN
    private static final Map<String, String> addonLocals = new HashMap<>();
    // depends on rsc addons' info.yml
    private static final Map<String, Set<String>> rscLocals = new HashMap<>();
    static {
        addonLocals.put("Slimefun", "粘液科技");
        addonLocals.put("ColoredEnderChests", "彩色末影箱");
        addonLocals.put("DyedBackpacks", "染色背包");
        addonLocals.put("EnderCargo", "末影货运接口");
        addonLocals.put("EcoPower", "环保能源");
        addonLocals.put("ElectricSpawners", "电动刷怪笼");
        addonLocals.put("ExoticGarden", "异域花园");
        addonLocals.put("ExtraGear", "更多装备");
        addonLocals.put("ExtraHeads", "更多头颅");
        addonLocals.put("HotbarPets", "背包宠物");
        addonLocals.put("luckyblocks-sf", "幸运方块"); // Same as SlimefunLuckyBlocks
        addonLocals.put("RedstoneConnector", "红石连接器");
        addonLocals.put("PrivateStorage", "私人储存");
        addonLocals.put("SlimefunOreChunks", "更多矿石块");
        addonLocals.put("SlimyTreeTaps", "粘液木龙头");
        addonLocals.put("SoulJars", "灵魂罐");
        addonLocals.put("MoreTools", "更多工具");
        addonLocals.put("LiteXpansion", "工业");
        addonLocals.put("MobCapturer", "生物捕捉");
        addonLocals.put("SoundMuffler", "消音器");
        addonLocals.put("ExtraTools", "额外工具");
        addonLocals.put("TranscEndence", "末地科技");
        addonLocals.put("Liquid", "液体");
        addonLocals.put("SlimefunWarfare", "战争工艺");
        addonLocals.put("InfernalExpansion", "下界工艺");
        addonLocals.put("FluffyMachines", "蓬松机器");
        addonLocals.put("SlimyRepair", "粘液物品修复");
        addonLocals.put("InfinityExpansion", "无尽贪婪");
        addonLocals.put("FoxyMachines", "神秘科技");
        addonLocals.put("GlobalWarming", "全球变暖");
        addonLocals.put("GlobiaMachines", "全球机器");
        addonLocals.put("DynaTech", "动力科技");
        addonLocals.put("GeneticChickengineering", "鸡因工程"); // Same as GeneticChickengineering-Reborn
        addonLocals.put("GeneticChickengineering-Reborn", "鸡因工程"); // Same as GeneticChickengineering
        addonLocals.put("ClayTech", "粘土科技"); // Same as ClayTech-Fixed
        addonLocals.put("ClayTech-Fixed", "粘土科技"); // Same as ClayTech
        addonLocals.put("SpaceTech", "太空科技"); // Same as SpaceTech-Fixed
        addonLocals.put("SpaceTech-Fixed", "太空科技"); // Same as SpaceTech
        addonLocals.put("FNAmplifications", "FN科技");
        addonLocals.put("SimpleMaterialGenerators", "简单材料生成器");
        addonLocals.put("Netheopoiesis", "下界乌托邦");
        addonLocals.put("Networks", "网络"); // Same as Networks-Changed (sometimes it is NetworksExpansion)
        addonLocals.put("EMC2", "等价交换(EMC2)"); // Avoid conflict with EquivalencyTech
        addonLocals.put("Nexcavate", "文明复兴");
        addonLocals.put("SimpleStorage", "简易储存");
        addonLocals.put("SimpleUtils", "简易工具");
        addonLocals.put("AlchimiaVitae", "炼金术自传");
        addonLocals.put("SlimeTinker", "粘液匠魂");
        addonLocals.put("PotionExpansion", "药剂科技");
        addonLocals.put("FlowerPower", "源之花");
        addonLocals.put("Galactifun", "星际");
        addonLocals.put("Galactifun2", "星际2");
        addonLocals.put("ElementManipulation", "化学工程");
        addonLocals.put("CrystamaeHistoria", "魔法水晶编年史");
        addonLocals.put("DankTech", "无底储存");
        addonLocals.put("DankTech2", "无底储存2");
        addonLocals.put("Networks-Changed", "网络"); // Same as Networks
        addonLocals.put("VillagerUtil", "村民工具");
        addonLocals.put("MissileWarfare", "导弹科技");
        addonLocals.put("SensibleToolbox", "STB/未来科技");
        addonLocals.put("Endrex", "末地拓展");
        addonLocals.put("Bump", "Bump魔法");
        addonLocals.put("FinalTech", "乱序技艺"); // Same as FinalTECH
        addonLocals.put("FinalTECH", "乱序技艺"); // Same as FinalTech
        addonLocals.put("SlimefunLuckyBlocks", "幸运方块"); // Same as luckyblocks-sf
        addonLocals.put("FutureTech", "未来科技");
        addonLocals.put("DemonicExpansion", "魑魅拓展");
        addonLocals.put("BedrockTechnology", "基岩科技");
        addonLocals.put("SlimefunItemExpansion", "更多物品");
        addonLocals.put("SupplementalServiceableness", "更多日用物品");
        addonLocals.put("GuizhanCraft", "鬼斩科技");
        addonLocals.put("Magmanimous", "熔岩之息");
        addonLocals.put("UltimateGenerators-RC27", "终极发电机"); // Same as UltimateGenerators
        addonLocals.put("UltimateGenerators", "终极发电机"); // Same as UltimateGenerators-RC27
        addonLocals.put("UltimateGenerators2", "终极发电机2");
        addonLocals.put("CrispyMachine", "酥脆科技");
        addonLocals.put("Chocoholics", "虫火谷工艺");
        addonLocals.put("draconic", "龙之研究"); // Same as DracFun
        addonLocals.put("DracFun", "龙之研究"); // Same as draconic
        addonLocals.put("EzSFAddon", "EZ科技");
        addonLocals.put("EzTech", "EZ科技");
        addonLocals.put("RandomExpansion", "随机拓展");
        addonLocals.put("SlimyBees", "林业蜜蜂");
        addonLocals.put("ObsidianExpansion", "黑曜石科技");
        addonLocals.put("EMCTech", "EMC科技");
        addonLocals.put("RelicsOfCthonia", "克苏尼亚遗物");
        addonLocals.put("Supreme", "至尊研究院");
        addonLocals.put("DyeBench", "染色科技");
        addonLocals.put("MiniBlocks", "迷你方块");
        addonLocals.put("SpiritsUnchained", "灵魂巧匠");
        addonLocals.put("Cultivation", "农耕工艺");
        addonLocals.put("Gastronomicon", "美食家");
        addonLocals.put("SmallSpace", "小世界");
        addonLocals.put("BetterReactor", "工业反应堆"); // Avoid conflict with Fusion
        addonLocals.put("VillagerTrade", "村民交易");
        addonLocals.put("SlimeFrame", "粘液战甲");
        addonLocals.put("AdvancedTech", "先进科技");
        addonLocals.put("Quaptics", "量子光学");
        addonLocals.put("CompressionCraft", "压缩工艺");
        addonLocals.put("ThermalFun", "灼岩科技");
        addonLocals.put("FastMachines", "快捷机器");
        addonLocals.put("MomoTech", "乱码科技");
        addonLocals.put("LogicTech", "逻辑工艺"); // Same as LogicTECH, a SlimefunCustomizer configuration
        addonLocals.put("LogiTech", "逻辑工艺"); // Same as LogiTECH, a Slimefun addon
        addonLocals.put("LogicTECH", "逻辑工艺"); // Same as LogicTech
        addonLocals.put("LogiTECH", "逻辑工艺"); // Same as LogiTech
        addonLocals.put("SlimeAEPlugin", "能源应用2");
        addonLocals.put("SlimeChem", "粘液化学");
        addonLocals.put("WilderNether", "迷狱生机");
        addonLocals.put("MapJammers", "地图干扰");
        addonLocals.put("Cakecraft", "蛋糕工艺"); // Same as MyFirstAddon
        addonLocals.put("SFMobDrops", "自定义生物掉落");
        addonLocals.put("Drugfun", "自定义医药用品");
        addonLocals.put("SlimefunNukes", "粘液核弹");
        addonLocals.put("SlimeCustomizer", "自定义粘液附属");
        addonLocals.put("RykenSlimeCustomizer", "Ryken自定义附属"); // Same as RykenSlimefunCustomizer
        addonLocals.put("RykenSlimefunCustomizer", "Ryken自定义附属"); // Same as RykenSlimeCustomizer
        addonLocals.put("FinalTECH-Changed", "乱序技艺-改版");
        addonLocals.put("BloodAlchemy", "血炼金术");
        addonLocals.put("Laboratory", "实验室");
        addonLocals.put("MobEngineering", "生物工程");
        addonLocals.put("TsingshanTechnology", "青山科技");
        addonLocals.put("PomaExpansion", "高级安卓机器人");
        addonLocals.put("BuildingStaff", "建筑魔杖");
        addonLocals.put("IDreamOfEasy", "易梦");
        addonLocals.put("Magic-8-Ball", "魔法8号球");
        addonLocals.put("InfinityExpansionAutomation", "无尽自动化");
        addonLocals.put("ZeroTech", "澪数工艺");
        addonLocals.put("Ex-Limus", "新手工具");
        addonLocals.put("NotEnoughAddons", "多彩科技");
        addonLocals.put("SFWorldEdit", "粘液创世神[SW]"); // Avoid conflict with SlimefunWorldedit
        addonLocals.put("RSCEditor", "RSC编辑器");
        addonLocals.put("JustEnoughGuide", "更好的粘液书");
        addonLocals.put("SummaryHelper", "粘液刻管理");
        addonLocals.put("HardcoreSlimefun", "硬核粘液");
        addonLocals.put("SFCalc", "粘液计算器");
        addonLocals.put("SfChunkInfo", "区块信息");
        addonLocals.put("SlimefunAdvancements", "自定义粘液任务");
        addonLocals.put("SlimeHUD", "方块信息显示");
        addonLocals.put("RaySlimefunAddon", "高级自定义粘液附属");
        addonLocals.put("SCrafter", "SC科技");
        addonLocals.put("CrispyMachines", "酥脆机器");
        addonLocals.put("DimensionTraveler", "维度旅者");
        addonLocals.put("HardlessMachine", "弹跳工具");
        addonLocals.put("XingChengCraft", "星辰工艺"); // Same as XingChenCraft
        addonLocals.put("XingChenCraft", "星辰工艺"); // Same as XingChengCraft
        addonLocals.put("DefoLiationTech", "落叶科技");
        addonLocals.put("HaimanTech2", "海曼科技院");
        addonLocals.put("HaimanTech", "海曼科技");
        addonLocals.put("InfiniteExtensionV2", "无尽扩展V2");
        addonLocals.put("InfiniteExtension", "无尽扩展");
        addonLocals.put("OrangeTech", "橘子科技");
        addonLocals.put("GreedAndCreation", "贪婪与创世");
        addonLocals.put("BocchiTechnology", "波奇科技");
        addonLocals.put("OreTech", "矿物科技");
        addonLocals.put("HLGtech", "生物科技");
        addonLocals.put("InfiniteExtensionV2-Reconfiguration", "无尽扩展V2-改版");
        addonLocals.put("BigSnakeTech", "大蛇科技");
        addonLocals.put("EpoTech", "纪元科技");
        addonLocals.put("EnchanterLimit", "限制附魔机");
        addonLocals.put("BlockLimiter", "方块限制");
        addonLocals.put("SfItemsExporter", "粘液物品导出");
        addonLocals.put("SlimeGlue", "粘液胶");
        addonLocals.put("KeepSoulbound", "高级灵魂绑定");
        addonLocals.put("SlimeFunItemBanned", "禁用物品");
        addonLocals.put("Azap", "狱刑");
        addonLocals.put("CringleBosses", "混沌Boss");
        addonLocals.put("SlimefunNotchApple", "粘液Notch旗帜图案");
        addonLocals.put("Huolaiy", "火莱伊工艺");
        addonLocals.put("WonderfulTransmitter", "奇妙发射器");
        addonLocals.put("OreGeneration", "矿物生成器"); // Avoid conflict with Mineralgenerator
        addonLocals.put("SlimeSec", "安全粘液");
        addonLocals.put("Paradoxium", "凤凰科技");
        addonLocals.put("LuckyPandas", "幸运熊猫");
        addonLocals.put("PhoenixSciences", "凤凰科学");
        addonLocals.put("DarkMatter", "夜魅");
        addonLocals.put("GeneticManipulation", "遗传学基因");
        addonLocals.put("MoneyAndThings", "固态货币");
        addonLocals.put("BeyondHorizons", "以太");
        addonLocals.put("ChestTerminal", "箱子终端");
        addonLocals.put("Hohenheim", "嬗变工艺");
        addonLocals.put("BetterFarming", "工艺农场");
        addonLocals.put("NewBeginnings", "新生");
        addonLocals.put("EndCombat", "终焉");
        addonLocals.put("EnderPanda", "末地熊猫");
        addonLocals.put("SlimeVoid", "虚无空间");
        addonLocals.put("ArcaneExploration", "怪物强化");
        addonLocals.put("MagicXpansion", "霊幻之梦");
        addonLocals.put("SlimeQuest", "粘液任务");
        addonLocals.put("CompressedMachines", "压缩机器");
        addonLocals.put("DisguiseCookie", "伪装曲奇");
        addonLocals.put("FireSlime", "碳泥科技");
        addonLocals.put("NetherEnough", "深渊幻章");
        addonLocals.put("BarrelWiper", "蓬松拆桶器");
        addonLocals.put("BearFluidTanks", "熊式储液罐");
        addonLocals.put("Tofu-Addons", "豆腐工艺");
        addonLocals.put("AdditionalWeaponry", "武器工厂");
        addonLocals.put("BoxOfChocolates", "巧克力工艺");
        addonLocals.put("MagicPowder", "魔芋工艺");
        addonLocals.put("XpCreator", "造物主工艺");
        addonLocals.put("SlimefunCombat", "原子弹模型");
        addonLocals.put("ObsidianArmor", "黑曜石合金装甲");
        addonLocals.put("FinalGenerations", "世代同堂");
        addonLocals.put("Fusion", "工业反应堆 Fusion"); // Avoid conflict with BetterReactor
        addonLocals.put("Slimedustry", "粘液工业");
        addonLocals.put("Spikes", "更多地刺");
        addonLocals.put("SlimeRP", "现代工厂");
        addonLocals.put("Brewery", "酿酒"); // Avoid conflict with BreweryMenu
        addonLocals.put("EquivalencyTech", "等价交换(ET)"); // Avoid conflict with EMC2
        addonLocals.put("GeyserHeads", "互通头颅材质");
        addonLocals.put("VariousClutter", "杂乱物品");
        addonLocals.put("Mineralgenerator", "Mineral 矿物生成器"); // Avoid conflict with OreGeneration
        addonLocals.put("CivilizationEvolution", "AG科技");
        addonLocals.put("RemiliasUtilities", "雷米科技");
        addonLocals.put("BetterChests", "更好的箱子");
        addonLocals.put("SlimeFood", "粘液美食");
        addonLocals.put("SlimeVision", "粘液可视化");
        addonLocals.put("WorldeditSlimefun", "粘液创世神[WS]"); // Avoid conflict with SFWorldedit
        addonLocals.put("MinimizeFactory", "压缩工厂");
        addonLocals.put("InfinityCompress", "无尽压缩");
        addonLocals.put("SlimeFrameExtension", "粘液战甲扩展");
        addonLocals.put("BreweryMenu", "酿酒GUI"); // Avoid conflict with Brewery
        addonLocals.put("MySlimefunAddon", "自制拓展");
        addonLocals.put("MyFirstAddon", "蛋糕工艺"); // Same as Cakecraft
    }

    @Nonnull
    public static String getOfficialAddonName(@Nonnull ItemGroup itemGroup, @Nonnull String itemId) {
        return getOfficialAddonName(itemGroup.getAddon(), itemId, def);
    }

    @Nonnull
    public static String getOfficialAddonName(@Nonnull ItemGroup itemGroup, @Nonnull String itemId, String callback) {
        return itemGroup.getAddon() == null ? def : getOfficialAddonName(itemGroup.getAddon(), itemId, callback);
    }

    @Nonnull
    public static String getOfficialAddonName(@Nullable SlimefunAddon addon, @Nonnull String itemId) {
        return getOfficialAddonName(addon, itemId, def);
    }

    @Nonnull
    public static String getOfficialAddonName(@Nullable SlimefunAddon addon, @Nonnull String itemId, String callback) {
        return getOfficialAddonName(addon == null ? "Slimefun" : addon.getName(), itemId, callback);
    }

    @Nonnull
    public static String getOfficialAddonName(@Nonnull String addonName, @Nonnull String itemId) {
        return getOfficialAddonName(addonName, itemId, def);
    }

    @Nonnull
    public static String getOfficialAddonName(@Nonnull String addonName, @Nonnull String itemId, String callback) {
        return getAddonName(addonName, itemId, callback) + " (" + addonName + ")";
    }

    @Nonnull
    public static String getAddonName(@Nonnull ItemGroup itemGroup, @Nonnull String itemId) {
        return getAddonName(itemGroup, itemId, def);
    }

    @Nonnull
    public static String getAddonName(@Nonnull ItemGroup itemGroup, @Nonnull String itemId, @Nonnull String callback) {
        return itemGroup.getAddon() == null ? def : getAddonName(itemGroup.getAddon().getName(), itemId, callback);
    }

    @Nonnull
    public static String getAddonName(@Nullable SlimefunAddon addon, @Nonnull String itemId) {
        return getAddonName(addon, itemId, def);
    }

    @Nonnull
    public static String getAddonName(@Nullable SlimefunAddon addon, @Nonnull String itemId, String callback) {
        return getAddonName(addon == null ? addonLocals.get("Slimefun") : addon.getName(), itemId, callback);
    }

    @Nonnull
    public static String getAddonName(@Nonnull String addonName, @Nonnull String itemId) {
        return getAddonName(addonName, itemId, def);
    }

    @Nonnull
    public static String getAddonName(@Nonnull String addonName, @Nonnull String itemId, String callback) {
        if (addonName == null) {
            return callback;
        }

        if ("RykenSlimefunCustomizer".equalsIgnoreCase(addonName) || "RykenSlimeCustomizer".equalsIgnoreCase(addonName)) {
            return getRSCLocalName(itemId);
        }
        String localName = addonLocals.get(addonName);
        return localName == null ? callback : localName;
    }

    public static void addRSCLocal(String rscAddonName, String itemId) {
        if (!rscLocals.containsKey(rscAddonName)) {
            rscLocals.put(rscAddonName, new HashSet<>());
        }

        rscLocals.get(rscAddonName).add(itemId);
    }

    // get a rsc addon name by item id
    public static String getRSCLocalName(String itemId) {
        for (Map.Entry<String, Set<String>> entry : rscLocals.entrySet()) {
            if (entry.getValue().contains(itemId)) {
                return entry.getKey();
            }
        }

        String def = addonLocals.get("RykenSlimefunCustomizer");
        if (def == null) {
            def = addonLocals.get("RykenSlimeCustomizer");
        }

        if (rscItems.isEmpty()) {
            try {
                Plugin rsc1 = Bukkit.getPluginManager().getPlugin("RykenSlimefunCustomizer");
                Plugin rsc2 = null;
                if (rsc1 == null) {
                    rsc2 = Bukkit.getPluginManager().getPlugin("RykenSlimeCustomizer");
                    if (rsc2 == null) {
                        return def;
                    }
                }

                Plugin rsc = rsc1 == null ? rsc2 : rsc1;
                if (rsc == null) {
                    return def;
                }
                Object addonManager = ReflectionUtil.getValue(rsc, "addonManager");
                Object projectAddons = ReflectionUtil.getValue(addonManager, "projectAddons");
                @SuppressWarnings("unchecked") Map<Object, Object> map = (Map<Object, Object>) projectAddons;
                for (Map.Entry<Object, Object> entry : map.entrySet()) {
                    Object addon = entry.getValue();
                    Object addonName = ReflectionUtil.getValue(addon, "addonName");
                    String name = (String) addonName;
                    Object preloadItems = ReflectionUtil.getValue(addon, "preloadItems");
                    @SuppressWarnings("unchecked") Map<Object, Object> items = (Map<Object, Object>) preloadItems;
                    Map<String, SlimefunItemStack> read = new HashMap<>();
                    for (Map.Entry<Object, Object> itemEntry : items.entrySet()) {
                        String id = (String) itemEntry.getKey();
                        SlimefunItemStack item = (SlimefunItemStack) itemEntry.getValue();
                        read.put(id, item);
                    }
                    rscItems.put(name, read);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        for (Map.Entry<String, Map<String, SlimefunItemStack>> entry : rscItems.entrySet()) {
            Map<String, SlimefunItemStack> items = entry.getValue();
            if (items.containsKey(itemId)) {
                return entry.getKey();
            }
        }

        return def;
    }
}
