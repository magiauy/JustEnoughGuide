package com.balugaq.jeg.api.interfaces;

import org.bukkit.entity.Player;

/**
 * This interface defines the methods that a BookmarkRelocation implementation should implement.
 * Used for relocating the buttons of the guide to a different location.
 *
 * @author balugaq
 * @see com.balugaq.jeg.implementation.guide.SurvivalGuideImplementation
 * @see com.balugaq.jeg.implementation.guide.CheatGuideImplementation
 * @since 1.1
 */
public interface BookmarkRelocation {
    int getBackButton(JEGSlimefunGuideImplementation implementation, Player player);

    int getSearchButton(JEGSlimefunGuideImplementation implementation, Player player);

    int getPreviousButton(JEGSlimefunGuideImplementation implementation, Player player);

    int getNextButton(JEGSlimefunGuideImplementation implementation, Player player);

    int getBookMark(JEGSlimefunGuideImplementation implementation, Player player);

    int getItemMark(JEGSlimefunGuideImplementation implementation, Player player);

    int[] getBorder(JEGSlimefunGuideImplementation implementation, Player player);

    int[] getMainContents(JEGSlimefunGuideImplementation implementation, Player player);
}
