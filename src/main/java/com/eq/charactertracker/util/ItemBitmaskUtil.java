package com.eq.charactertracker.util;

import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.EquipSlotEnum;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static com.eq.charactertracker.constants.CharacterClass.*;
import static com.eq.charactertracker.constants.EquipSlotEnum.*;

// Bit values match EQEmu's items table `classes`/`slots` columns, confirmed against
// known items (e.g. Rend classes=20 -> PALADIN|SHADOW_KNIGHT, slots=8192 -> PRIMARY).
public class ItemBitmaskUtil {

    private static final Map<Integer, CharacterClass> CLASS_BITS = Map.ofEntries(
            Map.entry(1, WARRIOR), Map.entry(2, CLERIC), Map.entry(4, PALADIN), Map.entry(8, RANGER),
            Map.entry(16, SHADOW_KNIGHT), Map.entry(32, DRUID), Map.entry(64, MONK), Map.entry(128, BARD),
            Map.entry(256, ROGUE), Map.entry(512, SHAMAN), Map.entry(1024, NECROMANCER), Map.entry(2048, WIZARD),
            Map.entry(4096, MAGICIAN), Map.entry(8192, ENCHANTER), Map.entry(16384, BEASTLORD), Map.entry(32768, BERSERKER)
    );

    // Bit 22 (2097152) is the legacy "Ammo" slot, renamed POWER_SOURCE when EQ repurposed it.
    private static final Map<Integer, EquipSlotEnum> SLOT_BITS = Map.ofEntries(
            Map.entry(1, CHARM), Map.entry(2, LEFT_EAR), Map.entry(4, HEAD), Map.entry(8, FACE),
            Map.entry(16, RIGHT_EAR), Map.entry(32, NECK), Map.entry(64, SHOULDER), Map.entry(128, ARMS),
            Map.entry(256, BACK), Map.entry(512, LEFT_WRIST), Map.entry(1024, RIGHT_WRIST), Map.entry(2048, RANGE),
            Map.entry(4096, HANDS), Map.entry(8192, PRIMARY), Map.entry(16384, SECONDARY), Map.entry(32768, LEFT_FINGER),
            Map.entry(65536, RIGHT_FINGER), Map.entry(131072, CHEST), Map.entry(262144, LEGS), Map.entry(524288, FEET),
            Map.entry(1048576, WAIST), Map.entry(2097152, POWER_SOURCE)
    );

    public static Set<CharacterClass> decodeClasses(long bitmask) {
        Set<CharacterClass> result = EnumSet.noneOf(CharacterClass.class);
        CLASS_BITS.forEach((bit, characterClass) -> {
            if ((bitmask & bit) != 0) {
                result.add(characterClass);
            }
        });
        return result;
    }

    public static Set<EquipSlotEnum> decodeSlots(long bitmask) {
        Set<EquipSlotEnum> result = EnumSet.noneOf(EquipSlotEnum.class);
        SLOT_BITS.forEach((bit, slot) -> {
            if ((bitmask & bit) != 0) {
                result.add(slot);
            }
        });
        return result;
    }

    private ItemBitmaskUtil() {
    }
}
