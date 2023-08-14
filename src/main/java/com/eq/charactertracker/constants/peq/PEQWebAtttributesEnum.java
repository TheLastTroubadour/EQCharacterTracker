package com.eq.charactertracker.constants.peq;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum PEQWebAtttributesEnum {
    ARMOR_CLASS("AC:", "ac"),
    HIT_POINTS("HP:", "hitPoints"),
    MANA_POINTS("MANA:", "manaPoints"),
    ENDURANCE_POINTS("Endurance:", "endurancePoints"),
    STRENGTH("STR:", "strength"),
    STAMINA("STA:", "stamina"),
    AGILITY("AGI:", "agility"),
    DEXTERITY("DEX:", "dexterity"),
    WISDOM("WIS:", "wisdom"),
    INTELLIGENCE("INT:", "intelligence"),
    CHARISMA("CHA:", "charisma"),
    FIRE_RESIST("Fire:", "fireResist"),
    COLD_RESIST("Cold:", "coldResist"),
    MAGIC_RESIST("Magic:", "magicResist"),
    DISEASE_RESIST("Disease:", "diseaseResist"),
    POISON_RESIST("Poison:", "poisonResist"),
    ATTACK("Attack:", "attack"),
    ACCURACY("Accuracy:", "accuracy"),
    AVOIDANCE("Avoidance:", "avoidance"),
    COMBAT_EFFECT("Combat Effects:", "combatEffect"),
    STUN_RESIST("Stun Resist:", "stunResist"),
    DAMAGE_SHIELD("Damage Shield:", "damageShield"),
    DOT_SHIELDING("Dot Shielding:", "dotShielding"),
    ENDURANCE_REGEN("End. Regen:", "enduranceRegen"),
    HIT_POINTS_REGEN("HP Regen:", "hitPointsRegen"),
    MANA_POINTS_REGEN("Mana Regeneration:", "manaPointsRegen"),
    SHIELDING("Shielding:", "shielding"),
    SPELL_SHIELDING("Spell Shielding:", "spellShielding"),
    STRIKETHROUGH("Strikethrough:", "strikethrough")
    ;

    private String htmlName;
    private String fieldName;

    PEQWebAtttributesEnum(String htmlName, String fieldName) {
        this.htmlName = htmlName;
        this.fieldName = fieldName;
    }

    private static Map<String, PEQWebAtttributesEnum> mapByHtmlNames = initHtmlNameMap();

    public static PEQWebAtttributesEnum getTHFWebAttributeByHtmlName(String htmlName){
        return mapByHtmlNames.get(htmlName);
    }

    private static Map<String,PEQWebAtttributesEnum> initHtmlNameMap() {
        return Arrays.stream(PEQWebAtttributesEnum.values()).collect(Collectors.toMap(PEQWebAtttributesEnum::getHtmlName, Function.identity()));
    }
}
