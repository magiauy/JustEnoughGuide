package com.balugaq.jeg.core.commands;

import com.balugaq.jeg.api.interfaces.JEGCommand;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.List;

@Getter
public class HelpCommand implements JEGCommand {
    private final Plugin plugin;
    public HelpCommand(Plugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public @Nonnull List<String> onTabCompleteRaw(@Nonnull CommandSender sender, @Nonnull String[] args) {
        switch (args.length) {
            case 1 -> {
                return List.of(
                        "help"
                );
            }

            default -> {
                return List.of();
            }
        }
    }

    @Override
    public boolean canCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if (args.length == 1) {
            if ("help".equalsIgnoreCase(args[0])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        onHelp(sender);
    }

    private void onHelp(@Nonnull CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "JEG Commands:");
        sender.sendMessage(ChatColor.GREEN + "/jeg help - Show this help message");
        sender.sendMessage(ChatColor.GREEN + "/jeg reload - Reload JEG plugin");
    }
}
