package com.eq.charactertracker.constants;

import lombok.Getter;

@Getter
public enum CharacterClass {

    WARRIOR("Ancient Sword of the Champion", "Ancient Frozen Sword of the Champion"),
    CLERIC("Ancient Shield of the Divine", "Ancient Frozen Aegis of Divinity"),
    PALADIN("Ancient Sword of Truth", "Ancient Frozen Sword of the Valiant"),
    RANGER("Ancient Hunter Blade", "Ancient Frozen Heartwood Blade"),
    SHADOW_KNIGHT("Ancient Doombringer", "Ancient Frozen Sword of the Dark"),
    DRUID("Ancient Roots of the Natureguard", "Ancient Frozen Staff of Brambles"),
    BARD("Ancient Blade of the Singer", "Ancient Blade of Vesagran"),
    ROGUE("Ancient Tool of the Thieves", "Ancient Frozen Razor of Entropy"),
    SHAMAN("Ancient Talisman of Fates", "Ancient Frozen Spiritstaff of the Heyokah"),
    NECROMANCER("Ancient Souleater", "Ancient Frozen Deathwhisper"),
    WIZARD("Ancient Prismatic Staff of Power", "Ancient Frozen Staff of Power"),
    MAGICIAN("Ancient Staff of Elemental Power", "Ancient Frozen Staff of Primal Elements"),
    ENCHANTER("Ancient Entrancers Staff", "Ancient Frozen Staff of Eloquence"),
    BEASTLORD("Ancient Lord's Totem", "Ancient Frozen Spiritcaller Totem"),
    BERSERKER("Ancient Axe of Raw Power", "Ancient Frozen Taelosian Blood Axe"),
    MONK("Ancient Fistwraps of Eternity", "Ancient Frozen Fistwraps");

    private String epic1_5Name;
    private String epic2_0Name;

    CharacterClass(String epic1_5Name, String epic2_0Name) {
        this.epic1_5Name = epic1_5Name;
        this.epic2_0Name = epic2_0Name;
    }

    public static CharacterClass fromWebName(String webName) {
        String normalized = webName.toUpperCase().replace(" ", "");
        for (CharacterClass cc : values()) {
            if (cc.name().replace("_", "").equals(normalized)) {
                return cc;
            }
        }
        throw new IllegalArgumentException("Unknown character class: " + webName);
    }
}
