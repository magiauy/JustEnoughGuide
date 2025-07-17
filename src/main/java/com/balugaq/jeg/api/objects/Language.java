package com.balugaq.jeg.api.objects;

import com.google.common.base.Preconditions;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public final class Language {
    private final @NotNull String lang;
    private final @NotNull File currentFile;
    private final @NotNull FileConfiguration currentConfig;

    @ParametersAreNonnullByDefault
    public Language(String lang, File currentFile, FileConfiguration defaultConfig) {
        Preconditions.checkArgument(lang != null, "Language key cannot be null");
        Preconditions.checkArgument(currentFile != null, "Current file cannot be null");
        Preconditions.checkArgument(defaultConfig != null, "default config cannot be null");
        this.lang = lang;
        this.currentFile = currentFile;
        this.currentConfig = YamlConfiguration.loadConfiguration(currentFile);
        this.currentConfig.setDefaults(defaultConfig);
        Iterator var4 = defaultConfig.getKeys(true).iterator();

        while (var4.hasNext()) {
            String key = (String) var4.next();
            if (!this.currentConfig.contains(key)) {
                this.currentConfig.set(key, defaultConfig.get(key));
            }
        }

        this.save();
    }

    @Nonnull
    public String getName() {
        return this.lang;
    }

    @Nonnull
    public FileConfiguration getLang() {
        return this.currentConfig;
    }

    public void save() {
        try {
            this.currentConfig.save(this.currentFile);
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }
}
