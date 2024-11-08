package com.balugaq.jeg.managers;

import com.balugaq.jeg.JustEnoughGuide;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class ConfigManager {
    private final boolean AUTO_UPDATE;
    private final boolean DEBUG;
    private final boolean SURVIVAL_IMPROVEMENTS;
    private final boolean CHEAT_IMPROVEMENTS;
    private final boolean PINYIN_SEARCH;
    private final boolean BOOKMARK;
    private final JustEnoughGuide plugin;

    public ConfigManager(JustEnoughGuide plugin) {
        this.plugin = plugin;
        setupDefaultConfig();
        this.AUTO_UPDATE = plugin.getConfig().getBoolean("auto-update");
        this.DEBUG = plugin.getConfig().getBoolean("debug");
        this.SURVIVAL_IMPROVEMENTS = plugin.getConfig().getBoolean("guide.survival-improvements");
        this.CHEAT_IMPROVEMENTS = plugin.getConfig().getBoolean("guide.cheat-improvements");
        this.PINYIN_SEARCH = plugin.getConfig().getBoolean("improvements.pinyin-search");
        this.BOOKMARK = plugin.getConfig().getBoolean("improvements.bookmark");
    }

    private void setupDefaultConfig() {
        // config.yml
        final InputStream inputStream = plugin.getResource("config.yml");
        final File existingFile = new File(plugin.getDataFolder(), "config.yml");

        if (inputStream == null) {
            return;
        }

        final Reader reader = new InputStreamReader(inputStream);
        final FileConfiguration resourceConfig = YamlConfiguration.loadConfiguration(reader);
        final FileConfiguration existingConfig = YamlConfiguration.loadConfiguration(existingFile);

        for (String key : resourceConfig.getKeys(false)) {
            checkKey(existingConfig, resourceConfig, key);
        }

        try {
            existingConfig.save(existingFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ParametersAreNonnullByDefault
    private void checkKey(FileConfiguration existingConfig, FileConfiguration resourceConfig, String key) {
        final Object currentValue = existingConfig.get(key);
        final Object newValue = resourceConfig.get(key);
        if (newValue instanceof ConfigurationSection section) {
            for (String sectionKey : section.getKeys(false)) {
                checkKey(existingConfig, resourceConfig, key + "." + sectionKey);
            }
        } else if (currentValue == null) {
            existingConfig.set(key, newValue);
        }
    }

    public boolean isAutoUpdate() {
        return AUTO_UPDATE;
    }

    public boolean isDebug() {
        return DEBUG;
    }

    public boolean isSurvivalImprovement() {
        return SURVIVAL_IMPROVEMENTS;
    }

    public boolean isCheatImprovement() {
        return CHEAT_IMPROVEMENTS;
    }

    public boolean isPinyinSearch() {
        return PINYIN_SEARCH;
    }

    public boolean isBookmark() {
        return BOOKMARK;
    }
}
