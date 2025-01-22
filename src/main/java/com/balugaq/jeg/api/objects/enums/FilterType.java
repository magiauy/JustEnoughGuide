package com.balugaq.jeg.api.objects.enums;

import lombok.Getter;

@Getter
public enum FilterType {
    BY_RECIPE_ITEM_NAME("#"),
    BY_RECIPE_TYPE_NAME("$"),
    BY_DISPLAY_ITEM_NAME("%"),
    BY_ADDON_NAME("@"),
    BY_ITEM_NAME("!"),
    BY_MATERIAL_NAME("~");

    private final String flag;
    FilterType(String flag) {
        this.flag = flag;
    }
}
