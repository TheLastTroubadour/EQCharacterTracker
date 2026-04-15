package com.eq.charactertracker.constants;

import lombok.Getter;

@Getter
public enum EquipSlotEnum {
    CHARM("slot0"),
    LEFT_EAR("slot1"),
    HEAD("slot2"),
    FACE("slot3"),
    RIGHT_EAR("slot4"),
    NECK("slot5"),
    SHOULDER("slot6"),
    ARMS("slot7"),
    BACK("slot8"),
    LEFT_WRIST("slot9"),
    RIGHT_WRIST("slot10"),
    RANGE("slot11"),
    HANDS("slot12"),
    PRIMARY("slot13"),
    SECONDARY("slot14"),
    LEFT_FINGER("slot15"),
    RIGHT_FINGER("slot16"),
    CHEST("slot17"),
    LEGS("slot18"),
    FEET("slot19"),
    WAIST("slot20"),
    POWER_SOURCE("slot21");

    private String slotNumber;

    EquipSlotEnum(String slotNumber) {
        this.slotNumber = slotNumber;
    }

    public static EquipSlotEnum fromImportSlot(String slot) {
        switch (slot.toLowerCase()) {
            case "charm":       return CHARM;
            case "leftear":     return LEFT_EAR;
            case "head":        return HEAD;
            case "face":        return FACE;
            case "rightear":    return RIGHT_EAR;
            case "neck":        return NECK;
            case "shoulder":    return SHOULDER;
            case "arms":        return ARMS;
            case "back":        return BACK;
            case "leftwrist":   return LEFT_WRIST;
            case "rightwrist":  return RIGHT_WRIST;
            case "ranged":      return RANGE;
            case "hands":       return HANDS;
            case "mainhand":    return PRIMARY;
            case "offhand":     return SECONDARY;
            case "leftfinger":  return LEFT_FINGER;
            case "rightfinger": return RIGHT_FINGER;
            case "chest":       return CHEST;
            case "legs":        return LEGS;
            case "feet":        return FEET;
            case "waist":       return WAIST;
            case "powersource": return POWER_SOURCE;
            default: throw new IllegalArgumentException("Unknown equip slot: " + slot);
        }
    }
}
