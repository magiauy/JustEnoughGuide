package com.balugaq.jeg.core.managers;

import com.balugaq.jeg.api.managers.AbstractManager;
import com.balugaq.jeg.core.commands.JEGCommands;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import lombok.Getter;
import org.bukkit.command.PluginCommand;

import java.util.List;

@Getter
public class CommandManager extends AbstractManager {

    private final JustEnoughGuide plugin;

    public CommandManager(JustEnoughGuide plugin) {
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
