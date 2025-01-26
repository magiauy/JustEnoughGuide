package com.balugaq.jeg.core.listeners;

import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.ReflectionUtil;
import com.balugaq.jeg.utils.SpecialMenuProvider;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Deque;
import java.util.Optional;

/**
 * @see SpecialMenuProvider
 * @author balugaq
 * @since 1.3
 */
public class SpecialMenuFixListener implements Listener {
    @EventHandler
    public void onSpecialMenuClose(InventoryCloseEvent event) {
        Debug.debug("SpecialMenuFixListener: onSpecialMenuClose");
        Player player = (Player) event.getPlayer();
        Optional<PlayerProfile> optional = PlayerProfile.find(player);
        if (optional.isPresent()) {
            PlayerProfile profile = optional.get();
            try {
                @SuppressWarnings("unchecked") Deque<Object> queue = (Deque<Object>) ReflectionUtil.getValue(profile.getGuideHistory(), "queue");
                if (queue == null || queue.isEmpty()) {
                    Debug.debug("SpecialMenuFixListener: queue is empty");
                    return;
                }

                Object entry = queue.getLast();
                Object object = ReflectionUtil.getValue(entry, "object");
                if (!(object instanceof String string)) {
                    Debug.debug("SpecialMenuFixListener: object is not a string");
                    return;
                }
                if (SpecialMenuProvider.PLACEHOLDER_SEARCH_TERM.equals(string)) {
                    Debug.debug("SpecialMenuFixListener: removing placeholder search term");
                    // remove the last entry from the queue, which is the placeholder search term
                    queue.removeLast();
                    return;
                }
            } catch (Throwable e) {
                Debug.debug(e);
            }
        }
    }
}
