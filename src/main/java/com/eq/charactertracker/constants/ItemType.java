package com.eq.charactertracker.constants;

public enum ItemType {

    ARMOR,
    WEAPON,
    AUGMENTATION,
    CONTAINER,
    MISC,
    UNKNOWN;

    public static ItemType fromWebName(String webName) {
        if (webName == null || webName.isBlank()) {
            return UNKNOWN;
        }
        try {
            return ItemType.valueOf(webName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
