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

package com.balugaq.jeg.api.objects.events;

import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * {@link SearchButtonClickEvent}
 * {@link RTSButtonClickEvent}
 * {@link ItemGroupButtonClickEvent}
 * {@link ItemButtonClickEvent}
 * {@link PreviousButtonClickEvent}
 * {@link NextButtonClickEvent}
 * {@link BookMarkButtonClickEvent}
 * {@link ItemMarkButtonClickEvent}
 * {@link BackButtonClickEvent}
 * {@link WikiButtonClickEvent}
 * {@link RecipeTypeButtonClickEvent}
 * {@link BigRecipeButtonClickEvent}
 * {@link AuthorInformationButtonClickEvent}
 * {@link FeatureButtonClickEvent}
 * {@link SearchItemEvent}
 * {@link ResearchItemEvent}
 * {@link CollectItemEvent}
 * {@link SettingsButtonClickEvent}
 * {@link BeginnerButtonClickEvent}
 * {@link GroupLinkButtonClickEvent}
 *
 * @author balugaq
 * @since 1.7
 */
@SuppressWarnings({"deprecation", "unused"})
public class GuideEvents {
    /**
     * @author balugaq
     * @since 1.7
     */
    public interface JEGGuideEvent {
        @Nullable ItemStack getClickedItem();

        @Range(from = 0, to = 53)
        int getClickedSlot();

        @NotNull ClickAction getClickAction();

        @NotNull ChestMenu getMenu();

        @NotNull SlimefunGuideImplementation getGuide();
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    public static class GroupLinkButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public GroupLinkButtonClickEvent(
                @NotNull Player who,
                @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                @NotNull ClickAction clickAction,
                @NotNull ChestMenu menu,
                @NotNull SlimefunGuideImplementation guide
        ) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    public static class BeginnerButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public BeginnerButtonClickEvent(
                @NotNull Player who,
                @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                @NotNull ClickAction clickAction,
                @NotNull ChestMenu menu,
                @NotNull SlimefunGuideImplementation guide
        ) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    public static class SettingsButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public SettingsButtonClickEvent(
                @NotNull Player who,
                @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                @NotNull ClickAction clickAction,
                @NotNull ChestMenu menu,
                @NotNull SlimefunGuideImplementation guide
        ) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    public static class CollectItemEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public CollectItemEvent(
                @NotNull Player who,
                @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                @NotNull ClickAction clickAction,
                @NotNull ChestMenu menu,
                @NotNull SlimefunGuideImplementation guide
        ) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    public static class ResearchItemEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public ResearchItemEvent(
                @NotNull Player who,
                @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                @NotNull ClickAction clickAction,
                @NotNull ChestMenu menu,
                @NotNull SlimefunGuideImplementation guide
        ) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    public static class SearchItemEvent extends Event implements Cancellable {
        private static final HandlerList HANDLERS = new HandlerList();
        private final @NotNull Player player;
        private final @NotNull String searchTerm;
        private boolean cancelled = false;

        public SearchItemEvent(
                @NotNull Player player,
                @NotNull String searchTerm
        ) {
            super(false);
            this.player = player;
            this.searchTerm = searchTerm;
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    public static class FeatureButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public FeatureButtonClickEvent(
                @NotNull Player who,
                @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                @NotNull ClickAction clickAction,
                @NotNull ChestMenu menu,
                @NotNull SlimefunGuideImplementation guide
        ) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    public static class AuthorInformationButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public AuthorInformationButtonClickEvent(
                @NotNull Player who,
                @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                @NotNull ClickAction clickAction,
                @NotNull ChestMenu menu,
                @NotNull SlimefunGuideImplementation guide
        ) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    public static class BigRecipeButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public BigRecipeButtonClickEvent(
                @NotNull Player who,
                @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                @NotNull ClickAction clickAction,
                @NotNull ChestMenu menu,
                @NotNull SlimefunGuideImplementation guide
        ) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    public static class RecipeTypeButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public RecipeTypeButtonClickEvent(
                @NotNull Player who,
                @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                @NotNull ClickAction clickAction,
                @NotNull ChestMenu menu,
                @NotNull SlimefunGuideImplementation guide
        ) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    public static class WikiButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public WikiButtonClickEvent(
                @NotNull Player who,
                @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                @NotNull ClickAction clickAction,
                @NotNull ChestMenu menu,
                @NotNull SlimefunGuideImplementation guide
        ) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    public static class BackButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public BackButtonClickEvent(
                @NotNull Player who,
                @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                @NotNull ClickAction clickAction,
                @NotNull ChestMenu menu,
                @NotNull SlimefunGuideImplementation guide
        ) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    public static class ItemMarkButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public ItemMarkButtonClickEvent(
                @NotNull Player who,
                @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                @NotNull ClickAction clickAction,
                @NotNull ChestMenu menu,
                @NotNull SlimefunGuideImplementation guide
        ) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    public static class BookMarkButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public BookMarkButtonClickEvent(
                @NotNull Player who,
                @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                @NotNull ClickAction clickAction,
                @NotNull ChestMenu menu,
                @NotNull SlimefunGuideImplementation guide
        ) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    public static class NextButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public NextButtonClickEvent(
                @NotNull Player who,
                @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                @NotNull ClickAction clickAction,
                @NotNull ChestMenu menu,
                @NotNull SlimefunGuideImplementation guide
        ) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    public static class PreviousButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public PreviousButtonClickEvent(
                @NotNull Player who,
                @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                @NotNull ClickAction clickAction,
                @NotNull ChestMenu menu,
                @NotNull SlimefunGuideImplementation guide
        ) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    public static class ItemButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public ItemButtonClickEvent(
                @NotNull Player who,
                @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                @NotNull ClickAction clickAction,
                @NotNull ChestMenu menu,
                @NotNull SlimefunGuideImplementation guide
        ) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    public static class ItemGroupButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public ItemGroupButtonClickEvent(
                @NotNull Player who,
                @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                @NotNull ClickAction clickAction,
                @NotNull ChestMenu menu,
                @NotNull SlimefunGuideImplementation guide
        ) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    public static class RTSButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public RTSButtonClickEvent(
                @NotNull Player who,
                @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                @NotNull ClickAction clickAction,
                @NotNull ChestMenu menu,
                @NotNull SlimefunGuideImplementation guide
        ) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    public static class SearchButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public SearchButtonClickEvent(
                @NotNull Player who,
                @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                @NotNull ClickAction clickAction,
                @NotNull ChestMenu menu,
                @NotNull SlimefunGuideImplementation guide
        ) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    public static class UnknownButtonClickEvent extends GuideEvent {
        private static final HandlerList HANDLERS = new HandlerList();

        public UnknownButtonClickEvent(
                @NotNull Player who,
                @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                @NotNull ClickAction clickAction,
                @NotNull ChestMenu menu,
                @NotNull SlimefunGuideImplementation guide
        ) {
            super(who, clickedItem, clickedSlot, clickAction, menu, guide);
        }

        public static @NotNull HandlerList getHandlerList() {
            return HANDLERS;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return HANDLERS;
        }
    }

    /**
     * @author balugaq
     * @since 1.7
     */
    @Getter
    public static abstract class GuideEvent extends Event implements Cancellable {
        private final @NotNull Player player;
        private final @Nullable ItemStack clickedItem;
        private final @Range(from = 0, to = 53) int clickedSlot;
        private final @NotNull ClickAction clickAction;
        private final @NotNull ChestMenu menu;
        private final @NotNull SlimefunGuideImplementation guide;
        public boolean cancelled = false;

        public GuideEvent(
                @NotNull Player player,
                @Nullable ItemStack clickedItem,
                @Range(from = 0, to = 53) int clickedSlot,
                @NotNull ClickAction clickAction,
                @NotNull ChestMenu menu,
                @NotNull SlimefunGuideImplementation guide
        ) {
            super(false);
            this.player = player;
            this.clickedItem = clickedItem;
            this.clickedSlot = clickedSlot;
            this.clickAction = clickAction;
            this.menu = menu;
            this.guide = guide;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
    }
}
