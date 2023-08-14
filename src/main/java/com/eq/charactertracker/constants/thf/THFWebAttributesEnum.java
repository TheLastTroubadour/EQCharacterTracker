package com.eq.charactertracker.constants.thf;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum THFWebAttributesEnum {

    ARMOR_CLASS("AC:", "ac"),
    HIT_POINTS("HP:", "hitPoints"),
    MANA_POINTS("Mana:", "manaPoints"),
    ENDURANCE_POINTS("End:", "endurancePoints"),
    STRENGTH("STR:", "strength"),
    STAMINA("STA:", "stamina"),
    AGILITY("AGI:", "agility"),
    DEXTERITY("DEX:", "dexterity"),
    WISDOM("WIS:", "wisdom"),
    INTELLIGENCE("INT:", "intelligence"),
    CHARISMA("CHA:", "charisma"),
    FIRE_RESIST("FIRE:", "fireResist"),
    COLD_RESIST("COLD:", "coldResist"),
    MAGIC_RESIST("MAG:", "magicResist"),
    DISEASE_RESIST("DIS:", "diseaseResist"),
    POISON_RESIST("POIS:", "poisonResist"),
    ATTACK("Attack:", "attack"),
    ACCURACY("Accuracy:", "accuracy"),
    AVOIDANCE("Avoidance:", "avoidance"),
    COMBAT_EFFECT("Combat Eff:", "combatEffect"),
    STUN_RESIST("Stun Resist:", "stunResist"),
    DAMAGE_SHIELD("Damage Shield:", "damageShield"),
    DOT_SHIELDING("DoT Shielding:", "dotShielding"),
    ENDURANCE_REGEN("End. Regen:", "enduranceRegen"),
    HIT_POINTS_REGEN("HP Regen:", "hitPointsRegen"),
    MANA_POINTS_REGEN("Mana Regen:", "manaPointsRegen"),
    SHIELDING("Shielding:", "shielding"),
    SPELL_SHIELDING("Spell Shield:", "spellShielding"),
    STRIKETHROUGH("Strikethrough:", "strikethrough")
    ;


    private String htmlName;
    private String fieldName;

    THFWebAttributesEnum(String htmlName, String fieldName) {
        this.htmlName = htmlName;
        this.fieldName = fieldName;
    }

    private static Map<String, THFWebAttributesEnum> mapByHtmlNames = initHtmlNameMap();

    public static THFWebAttributesEnum getTHFWebAttributeByHtmlName(String htmlName){
        return mapByHtmlNames.get(htmlName);
    }

    private static Map<String,THFWebAttributesEnum> initHtmlNameMap() {
        return Arrays.stream(THFWebAttributesEnum.values()).collect(Collectors.toMap(THFWebAttributesEnum::getHtmlName, Function.identity()));
    }


}
