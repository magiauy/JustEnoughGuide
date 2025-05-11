package com.balugaq.jeg.api.interfaces;

import org.bukkit.entity.Player;

import java.util.List;

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
    /**
     * Gets the slot number for the back button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the back button.
     */
    @Deprecated
    default int getBackButtonLegacy(JEGSlimefunGuideImplementation implementation, Player player) {
        return 1;
    }

    /**
     * Gets the slot number for the search button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the search button.
     */
    @Deprecated
    default int getSearchButtonLegacy(JEGSlimefunGuideImplementation implementation, Player player) {
        return 7;
    }

    /**
     * Gets the slot number for the previous button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the previous button.
     */
    @Deprecated
    default int getPreviousButtonLegacy(JEGSlimefunGuideImplementation implementation, Player player) {
        return 46;
    }

    /**
     * Gets the slot number for the next button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the next button.
     */
    @Deprecated
    default int getNextButtonLegacy(JEGSlimefunGuideImplementation implementation, Player player) {
        return 52;
    }

    /**
     * Gets the slot number for the bookmark button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the bookmark button.
     */
    @Deprecated
    default int getBookMarkLegacy(JEGSlimefunGuideImplementation implementation, Player player) {
        return 49;
    }

    /**
     * Gets the slot number for the item mark button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the item mark button.
     */
    @Deprecated
    default int getItemMarkLegacy(JEGSlimefunGuideImplementation implementation, Player player) {
        return 48;
    }

    /**
     * Gets the slot numbers for the border in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot numbers for the border.
     */
    @Deprecated
    int[] getBorderLegacy(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot numbers for the main contents in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot numbers for the main contents.
     */
    @Deprecated
    int[] getMainContentsLegacy(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot number for the back button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the back button.
     */
    List<Integer> getBackButton(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot number for the search button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the search button.
     */
    List<Integer> getSearchButton(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot number for the previous button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the previous button.
     */
    List<Integer> getPreviousButton(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot number for the next button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the next button.
     */
    List<Integer> getNextButton(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot number for the bookmark button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the bookmark button.
     */
    List<Integer> getBookMark(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot number for the item mark button in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot number for the item mark button.
     */
    List<Integer> getItemMark(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot numbers for the border in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot numbers for the border.
     */
    List<Integer> getBorder(JEGSlimefunGuideImplementation implementation, Player player);

    /**
     * Gets the slot numbers for the main contents in the guide.
     *
     * @param implementation The JEGSlimefunGuideImplementation.
     * @param player         The player.
     * @return The slot numbers for the main contents.
     */
    List<Integer> getMainContents(JEGSlimefunGuideImplementation implementation, Player player);
}
