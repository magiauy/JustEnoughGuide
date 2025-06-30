/*
 * Copyright (c) 2024-2025 balugaq
 *
 * This file is part of JustEnoughGuide, available under MIT license.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * - The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 * - The author's name (balugaq or 大香蕉) and project name (JustEnoughGuide or JEG) shall not be
 *   removed or altered from any source distribution or documentation.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.balugaq.jeg.core.listeners;

import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.utils.ReflectionUtil;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import lombok.SneakyThrows;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author balugaq
 * @since 1.9
 */
public class RecipeCompleteListener implements Listener {
    public static final Map<UUID, GuideEvents.ItemButtonClickEvent> LAST_EVENTS = new ConcurrentHashMap<>();
    public static final Map<UUID, GuideHistory> GUIDE_HISTORY = new ConcurrentHashMap<>();
    public static final Map<UUID, BiConsumer<GuideEvents.ItemButtonClickEvent, PlayerProfile>> PROFILE_CALLBACKS =
            new ConcurrentHashMap<>();

    public static void addCallback(
            @NotNull UUID uuid, @NotNull BiConsumer<GuideEvents.ItemButtonClickEvent, PlayerProfile> callback) {
        PROFILE_CALLBACKS.put(uuid, callback);
    }

    public static void removeCallback(@NotNull UUID uuid) {
        PROFILE_CALLBACKS.remove(uuid);
    }

    @SneakyThrows
    @NotNull public static PlayerProfile getPlayerProfile(@NotNull OfflinePlayer player) {
        // Shouldn't be null;
        return PlayerProfile.find(player).orElseThrow(() -> new RuntimeException("PlayerProfile not found"));
    }

    public static void tagGuideOpen(@NotNull Player player) {
        if (!PROFILE_CALLBACKS.containsKey(player.getUniqueId())) {
            return;
        }

        PlayerProfile profile = getPlayerProfile(player);
        saveOriginGuideHistory(profile);
        clearGuideHistory(profile);
    }

    private static void saveOriginGuideHistory(@NotNull PlayerProfile profile) {
        GuideHistory oldHistory = profile.getGuideHistory();
        GuideHistory newHistory = new GuideHistory(profile);
        ReflectionUtil.setValue(newHistory, "mainMenuPage", oldHistory.getMainMenuPage());
        LinkedList<?> queue = ReflectionUtil.getValue(oldHistory, "queue", LinkedList.class);
        ReflectionUtil.setValue(newHistory, "queue", queue != null ? queue.clone() : new LinkedList<>());
        GUIDE_HISTORY.put(profile.getUUID(), newHistory);
    }

    private static void clearGuideHistory(@NotNull PlayerProfile profile) {
        ReflectionUtil.setValue(profile, "guideHistory", new GuideHistory(profile));
    }

    @EventHandler
    public void onJEGItemClick(GuideEvents.@NotNull ItemButtonClickEvent event) {
        Player player = event.getPlayer();
        if (!PROFILE_CALLBACKS.containsKey(player.getUniqueId())) {
            return;
        }

        PlayerProfile profile = getPlayerProfile(player);
        rollbackGuideHistory(profile);
        PROFILE_CALLBACKS.get(player.getUniqueId()).accept(event, profile);
        PROFILE_CALLBACKS.remove(player.getUniqueId());
        LAST_EVENTS.put(player.getUniqueId(), event);
    }

    @Nullable public static GuideEvents.ItemButtonClickEvent getLastEvent(@NotNull UUID playerUUID) {
        return LAST_EVENTS.get(playerUUID);
    }

    public static void clearLastEvent(@NotNull UUID playerUUID) {
        LAST_EVENTS.remove(playerUUID);
    }

    private void rollbackGuideHistory(@NotNull PlayerProfile profile) {
        GuideHistory originHistory = GUIDE_HISTORY.get(profile.getUUID());
        if (originHistory == null) {
            return;
        }

        ReflectionUtil.setValue(profile, "guideHistory", originHistory);
    }
}
