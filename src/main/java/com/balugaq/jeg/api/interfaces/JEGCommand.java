package com.balugaq.jeg.api.interfaces;

import com.balugaq.jeg.core.commands.JEGCommands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
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
    @Nonnull
    default List<String> onTabCompleteRaw(@Nonnull CommandSender sender, @Nonnull String[] args) {
        return new ArrayList<>();
    }

    default boolean canCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        return false;
    }

    void onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args);
}
