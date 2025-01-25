package com.balugaq.jeg.api.interfaces;

import com.balugaq.jeg.core.commands.JEGCommands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This interface is used to define a command that can be executed by JEG.
 * Used by {@link JEGCommands}.
 *
 * @author balugaq
 * @since 1.1
 */
public interface JEGCommand {
    @NotNull
    default List<String> onTabCompleteRaw(@NotNull CommandSender sender, @NotNull String[] args) {
        return new ArrayList<>();
    }

    default boolean canCommand(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return false;
    }

    void onCommand(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args);
}
