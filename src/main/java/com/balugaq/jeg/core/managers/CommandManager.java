package com.balugaq.jeg.core.managers;

import com.balugaq.jeg.api.managers.AbstractManager;
import com.balugaq.jeg.core.commands.HelpCommand;
import com.balugaq.jeg.core.commands.JEGCommands;
import com.balugaq.jeg.core.commands.ReloadCommand;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import lombok.Getter;
import org.bukkit.command.PluginCommand;

import java.util.List;

/**
 * This class is responsible for managing the commands of JEG.
 *
 * @author balugaq
 * @since 1.0
 */
@Getter
public class CommandManager extends AbstractManager {

    private final JustEnoughGuide plugin;
    private final JEGCommands commands;

    public CommandManager(JustEnoughGuide plugin) {
        this.plugin = plugin;
        this.commands = new JEGCommands(plugin);
        this.commands.addCommand(new HelpCommand(plugin));
        this.commands.addCommand(new ReloadCommand(plugin));
    }

    public boolean registerCommands() {
        PluginCommand command = plugin.getCommand("justenoughguide");
        if (command != null) {
            command.setAliases(List.of("justenoughguide", "jeg"));
            command.setExecutor(commands);
        } else {
            return false;
        }

        return true;
    }
}
