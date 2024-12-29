package com.balugaq.jeg.core.commands;

import com.balugaq.jeg.api.interfaces.JEGCommand;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class is the command system of JEG. It handles all the commands and tab-completions.
 *
 * @author balugaq
 * @since 1.1
 */
@SuppressWarnings("unused")
@Getter
public class JEGCommands implements TabExecutor {
    private final JavaPlugin plugin;
    private final List<JEGCommand> commands = new ArrayList<>();
    private final @NotNull JEGCommand defaultCommand;

    public JEGCommands(JavaPlugin plugin) {
        this.plugin = plugin;
        this.defaultCommand = new HelpCommand(this.plugin);
    }

    public void addCommand(JEGCommand command) {
        this.commands.add(command);
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Unknown command. Type /jeg help");
            return true;
        }

        // Player or console
        for (JEGCommand jegCommand : this.commands) {
            if (jegCommand.canCommand(sender, command, label, args)) {
                jegCommand.onCommand(sender, command, label, args);
                return true;
            }
        }

        this.defaultCommand.onCommand(sender, command, label, args);

        return true;
    }

    public @NotNull List<String> onTabCompleteRaw(@NotNull CommandSender sender, @NotNull String[] args) {
        List<String> result = new ArrayList<>();
        for (JEGCommand jegCommand : this.commands) {
            List<String> partial = jegCommand.onTabCompleteRaw(sender, args);
            if (partial != null) {
                result.addAll(partial);
            }
        }

        return result;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String @NotNull [] args) {
        List<String> raw = onTabCompleteRaw(sender, args);
        return StringUtil.copyPartialMatches(args[args.length - 1], raw, new ArrayList<>());
    }
}
