package com.balugaq.jeg.core.commands;

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
import java.util.Locale;

@SuppressWarnings("unused")
public class JEGCommands implements TabExecutor {
    private final JavaPlugin plugin;

    public JEGCommands(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Unknown command. Type /jeg help");
            return true;
        }

        // Player or console
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "reload" -> {
                onReload(sender);
            }
            default -> {
                onHelp(sender);
            }
        }

        return true;
    }

    public @Nonnull List<String> onTabCompleteRaw(@Nonnull CommandSender sender, @Nonnull String[] args) {
        switch (args.length) {
            case 1 -> {
                return List.of(
                        "help",
                        "reload"
                );
            }

            default -> {
                return List.of();
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        List<String> raw = onTabCompleteRaw(sender, args);
        return StringUtil.copyPartialMatches(args[args.length - 1], raw, new ArrayList<>());
    }

    private void onHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "JEG Commands:");
        sender.sendMessage(ChatColor.GREEN + "/jeg help - Show this help message");
        sender.sendMessage(ChatColor.GREEN + "/jeg reload - Reload JEG plugin");
    }

    private void onReload(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Reloading JEG plugin...");
        try {
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "JEG plugin has been reloaded.");
        } catch (Throwable e) {
            sender.sendMessage(ChatColor.RED + "Failed to reload JEG plugin.");
            e.printStackTrace();
            return;
        }
    }
}
