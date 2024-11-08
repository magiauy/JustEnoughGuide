package com.balugaq.jeg.core.managers;

import com.balugaq.jeg.core.commands.JEGCommands;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CommandManager {

    private final JavaPlugin plugin;

    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean registerCommands() {
        PluginCommand command = plugin.getCommand("justenoughguide");
        if (command != null) {
            command.setAliases(List.of("justenoughguide", "jeg"));
            command.setExecutor(new JEGCommands(plugin));
        } else {
            return false;
        }

        return true;
    }
}
