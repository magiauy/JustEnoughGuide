package com.balugaq.jeg.utils.clickhandler;

import com.balugaq.jeg.api.objects.ExtendedClickHandler;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.implementation.option.BeginnersGuideOption;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author balugaq
 * @since 1.5
 */
@SuppressWarnings("deprecation")
public class BeginnerUtils {
    public static void applyBeginnersGuide(SlimefunGuideImplementation guide, ChestMenu menu, int slot) {
        if (!JustEnoughGuide.getConfigManager().isBeginnerOption()) {
            return;
        }

        ChestMenu.MenuClickHandler origin = menu.getMenuClickHandler(slot);
        if (origin instanceof BeginnerClickHandler) {
            return;
        }

        menu.addMenuClickHandler(slot, (BeginnerClickHandler) (player, clickedSlot, clickedItem, action) -> {
            if (isNew(player) && action.isShiftClicked() && action.isRightClicked() && clickedItem != null) {
                PlayerProfile.get(player, profile -> {
                    guide.openSearch(profile, ChatColor.stripColor(ItemStackHelper.getDisplayName(clickedItem)), true);
                });
                return false;
            }

            // call origin handler
            if (origin != null) {
                return origin.onClick(player, clickedSlot, clickedItem, action);
            } else {
                return false;
            }
        });
    }

    public static boolean isNew(Player player) {
        return BeginnersGuideOption.isEnabled(player);
    }

    public interface BeginnerClickHandler extends ExtendedClickHandler {
    }
}
