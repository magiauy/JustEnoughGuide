package com.balugaq.jeg.implementation;

import com.balugaq.jeg.core.managers.BookmarkManager;
import com.balugaq.jeg.core.managers.CommandManager;
import com.balugaq.jeg.core.managers.ConfigManager;
import com.balugaq.jeg.core.managers.ListenerManager;
import com.balugaq.jeg.implementation.guide.CheatGuideImplementation;
import com.balugaq.jeg.implementation.guide.SurvivalGuideImplementation;
import com.balugaq.jeg.utils.ReflectionUtil;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.guide.CheatSheetSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
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
    private static JustEnoughGuide instance;
    private final String username;
    private final String repo;
    private final String branch;
    private BookmarkManager bookmarkManager;
    private CommandManager commandManager;
    private ConfigManager configManager;
    private ListenerManager listenerManager;


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

    public static ListenerManager getListenerManager() {
        return getInstance().listenerManager;
    }

    public static JustEnoughGuide getInstance() {
        return JustEnoughGuide.instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("正在加载配置文件...");
        this.configManager = new ConfigManager(this);
        saveDefaultConfig();

        getLogger().info("正在注册监听器...");
        this.listenerManager = new ListenerManager(this);

        getLogger().info("尝试自动更新...");
        tryUpdate();

        getLogger().info("正在注册指令");
        this.commandManager = new CommandManager(this);
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
        }
        getLogger().info("成功启用此附属");
    }

    @Override
    public void onDisable() {
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
}