package com.balugaq.jeg.core.commands;

import com.balugaq.jeg.api.interfaces.JEGCommand;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Getter
public class JEGCommands implements TabExecutor {
    private final JavaPlugin plugin;
    private final List<JEGCommand> commands = new ArrayList<>();
    private final JEGCommand defaultCommand;
    public JEGCommands(JavaPlugin plugin) {
        this.plugin = plugin;
        this.defaultCommand = new HelpCommand(this.plugin);
    }
    public void addCommand(JEGCommand command) {
        this.commands.add(command);
    }
    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
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

    public @Nonnull List<String> onTabCompleteRaw(@Nonnull CommandSender sender, @Nonnull String[] args) {
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
            @Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        List<String> raw = onTabCompleteRaw(sender, args);
        return StringUtil.copyPartialMatches(args[args.length - 1], raw, new ArrayList<>());
    }
}
