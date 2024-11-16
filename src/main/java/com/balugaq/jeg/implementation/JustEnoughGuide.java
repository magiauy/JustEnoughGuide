package com.balugaq.jeg.implementation;

import com.balugaq.jeg.core.managers.BookmarkManager;
import com.balugaq.jeg.core.managers.CommandManager;
import com.balugaq.jeg.core.managers.ConfigManager;
import com.balugaq.jeg.core.managers.IntegrationManager;
import com.balugaq.jeg.core.managers.ListenerManager;
import com.balugaq.jeg.implementation.guide.CheatGuideImplementation;
import com.balugaq.jeg.implementation.guide.SurvivalGuideImplementation;
import com.balugaq.jeg.utils.MCVersion;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.google.common.base.Preconditions;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.guide.CheatSheetSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import lombok.Getter;
import net.guizhanss.guizhanlibplugin.updater.GuizhanUpdater;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.EnumMap;
import java.util.Map;

/**
 * This is the main class of the JustEnoughGuide plugin.
 * It depends on the Slimefun4 plugin and provides a set of features to improve the game experience.
 *
 * @author balugaq
 * @since 1.0
 */
@SuppressWarnings("unused")
@Getter
public class JustEnoughGuide extends JavaPlugin implements SlimefunAddon {
    private static final int RECOMMENDED_JAVA_VERSION = 17;
    private static final MCVersion RECOMMENDED_MC_VERSION = MCVersion.MINECRAFT_1_16;
    private static JustEnoughGuide instance;
    private final String username;
    private final String repo;
    private final String branch;
    private BookmarkManager bookmarkManager;
    private CommandManager commandManager;
    private ConfigManager configManager;
    private IntegrationManager integrationManager;
    private ListenerManager listenerManager;
    private MCVersion mcVersion;
    private int javaVersion;


    public JustEnoughGuide() {
        this.username = "balugaq";
        this.repo = "JustEnoughGuide";
        this.branch = "master";
    }

    public static BookmarkManager getBookmarkManager() {
        return getInstance().bookmarkManager;
    }

    public static CommandManager getCommandManager() {
        return getInstance().commandManager;
    }

    public static ConfigManager getConfigManager() {
        return getInstance().configManager;
    }

    public static IntegrationManager getIntegrationManager() {
        return getInstance().integrationManager;
    }

    public static ListenerManager getListenerManager() {
        return getInstance().listenerManager;
    }

    public static MCVersion getMCVersion() {
        return getInstance().mcVersion;
    }

    public static JustEnoughGuide getInstance() {
        Preconditions.checkArgument(instance != null, "JustEnoughGuide 未被启用！");
        return JustEnoughGuide.instance;
    }

    @Override
    public void onEnable() {
        Preconditions.checkArgument(instance == null, "JustEnoughGuide 已被启用！");
        instance = this;

        getLogger().info("正在加载配置文件...");
        saveDefaultConfig();
        this.configManager = new ConfigManager(this);
        this.configManager.onLoad();

        // Checking environment compatibility
        boolean isCompatible = environmentCheck();

        if (!isCompatible) {
            getLogger().warning("环境不兼容！插件已被禁用！");
            onDisable();
            return;
        }

        getLogger().info("正在适配其他插件...");
        this.integrationManager = new IntegrationManager(this);
        this.integrationManager.onLoad();

        getLogger().info("正在注册监听器...");
        this.listenerManager = new ListenerManager(this);
        this.listenerManager.onLoad();

        getLogger().info("尝试自动更新...");
        tryUpdate();

        getLogger().info("正在注册指令");
        this.commandManager = new CommandManager(this);
        this.commandManager.onLoad();

        if (!commandManager.registerCommands()) {
            getLogger().warning("注册指令失败！");
        }

        final boolean survivalOverride = getConfigManager().isSurvivalImprovement();
        final boolean cheatOverride = getConfigManager().isCheatImprovement();
        if (survivalOverride || cheatOverride) {
            getLogger().info("已开启指南替换！");
            getLogger().info("正在替换指南...");
            Field field = ReflectionUtil.getField(Slimefun.getRegistry().getClass(), "guides");
            if (field != null) {
                field.setAccessible(true);

                Map<SlimefunGuideMode, SlimefunGuideImplementation> newGuides = new EnumMap<>(SlimefunGuideMode.class);
                newGuides.put(SlimefunGuideMode.SURVIVAL_MODE, survivalOverride ? new SurvivalGuideImplementation() : new SurvivalSlimefunGuide());
                newGuides.put(SlimefunGuideMode.CHEAT_MODE, cheatOverride ? new CheatGuideImplementation() : new CheatSheetSlimefunGuide());
                try {
                    field.set(Slimefun.getRegistry(), newGuides);
                } catch (IllegalAccessException ignored) {

                }
            }
            getLogger().info(survivalOverride ? "已开启替换生存指南" : "未开启替换生存指南");
            getLogger().info(cheatOverride ? "已开启替换作弊指南" : "未开启替换作弊指南");

            getLogger().info("正在加载书签...");
            this.bookmarkManager = new BookmarkManager(this);
            this.bookmarkManager.onLoad();
        }
        getLogger().info("成功启用此附属");
    }

    @Override
    public void onDisable() {
        Preconditions.checkArgument(instance != null, "JustEnoughGuide 未被启用！");
        Field field = ReflectionUtil.getField(Slimefun.getRegistry().getClass(), "guides");
        if (field != null) {
            field.setAccessible(true);

            Map<SlimefunGuideMode, SlimefunGuideImplementation> newGuides = new EnumMap<>(SlimefunGuideMode.class);
            newGuides.put(SlimefunGuideMode.SURVIVAL_MODE, new SurvivalSlimefunGuide());
            newGuides.put(SlimefunGuideMode.CHEAT_MODE, new CheatSheetSlimefunGuide());
            try {
                field.set(Slimefun.getRegistry(), newGuides);
            } catch (IllegalAccessException ignored) {

            }
        }

        // Managers
        this.bookmarkManager.onUnload();
        this.integrationManager.onUnload();
        this.commandManager.onUnload();
        this.listenerManager.onUnload();
        this.configManager.onUnload();

        this.bookmarkManager = null;
        this.integrationManager = null;
        this.commandManager = null;
        this.listenerManager = null;
        this.configManager = null;

        // Other fields
        this.mcVersion = null;
        this.javaVersion = 0;

        // Clear instance
        instance = null;
        getLogger().info("成功禁用此附属");
    }

    public void tryUpdate() {
        try {
            if (getConfigManager().isAutoUpdate() && getDescription().getVersion().startsWith("Build")) {
                GuizhanUpdater.start(this, getFile(), username, repo, branch);
            }
        } catch (UnsupportedClassVersionError ignored) {
            getLogger().warning("自动更新失败！");
        }
    }

    @Nonnull
    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Nullable
    @Override
    public String getBugTrackerURL() {
        return MessageFormat.format("https://github.com/{0}/{1}/issues/", this.username, this.repo);
    }

    public void debug(String message) {
        if (getConfigManager().isDebug()) {
            getLogger().warning("[DEBUG] " + message);
        }
    }

    public String getVersion() {
        return getDescription().getVersion();
    }

    private boolean environmentCheck() {
        this.mcVersion = MCVersion.getCurrentVersion();
        this.javaVersion = NumberUtils.getJavaVersion();
        if (mcVersion == null) {
            getLogger().warning("无法获取到 Minecraft 版本！");
            return false;
        }

        if (mcVersion == MCVersion.UNKNOWN) {
            getLogger().warning("无法识别到 Minecraft 版本！");
            return false;
        }

        if (!mcVersion.isAtLeast(RECOMMENDED_MC_VERSION)) {
            return false;
        }

        if (javaVersion < RECOMMENDED_JAVA_VERSION) {
            getLogger().warning("Java 版本过低，请使用 Java " + RECOMMENDED_JAVA_VERSION + " 或以上版本！");
            return false;
        }

        return true;
    }
}