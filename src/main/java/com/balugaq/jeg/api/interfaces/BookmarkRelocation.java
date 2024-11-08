package com.balugaq.jeg.api.interfaces;

import org.bukkit.entity.Player;

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
