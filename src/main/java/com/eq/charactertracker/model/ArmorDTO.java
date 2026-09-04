package com.eq.charactertracker.model;

import com.eq.charactertracker.base.BaseAttributes;
import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.EquipSlotEnum;
import com.eq.charactertracker.entity.ArmorEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArmorDTO extends BaseAttributes {
    private Long id;
    private Long extId;
    private String server;
    private String expansion;
    private Collection<CharacterClass> classes;
    private Collection<EquipSlotEnum> slots;
    private Integer randomLootTier;

    public ArmorDTO(ArmorEntity armorEntity) {
        this.id = armorEntity.getId();
        this.extId = armorEntity.getExtId();
        this.server = armorEntity.getServer() != null ? armorEntity.getServer().getShortName() : null;
        this.expansion = armorEntity.getExpansion() != null ? armorEntity.getExpansion().getShortName() : null;
        this.classes = armorEntity.getClasses();
        this.slots = armorEntity.getSlots();
        this.randomLootTier = armorEntity.getRandomLootTier();
        this.setAc(armorEntity.getAc());
    }

    @Builder
    public ArmorDTO(String name, Integer ac, Integer strength, Integer stamina, Integer agility, Integer dexterity,
                 Integer wisdom, Integer intelligence, Integer charisma, Integer hitPoints, Integer manaPoints,
                 Integer endurancePoints, Integer fireResist, Integer coldResist, Integer magicResist,
                 Integer diseaseResist, Integer poisonResist, Integer attack, Double accuracy, Double avoidance,
                 Double combatEffect, Double stunResist, Integer damageShield, Double dotShielding,
                 Integer enduranceRegen, Integer hitPointsRegen, Integer manaPointsRegen, Double spellShielding,
                 Double shielding, Double strikethrough, Collection<String> focusEffect, Collection<String> clickEffect,
                 Integer heroicStrength, Integer heroicStamina, Integer heroicAgility, Integer heroicDexterity,
                 Integer heroicWisdom, Integer heroicIntelligence, Integer heroicCharisma,
                 Long id, Long extId, String server, String expansion,
                 Collection<CharacterClass> classes, Collection<EquipSlotEnum> slots, Integer randomLootTier) {
        super(name, ac, strength, stamina, agility, dexterity, wisdom, intelligence, charisma, hitPoints, manaPoints,
                endurancePoints, fireResist, coldResist, magicResist, diseaseResist, poisonResist, attack, accuracy,
                avoidance, combatEffect, stunResist, damageShield, dotShielding, enduranceRegen, hitPointsRegen,
                manaPointsRegen, spellShielding, shielding, strikethrough, focusEffect, clickEffect,
                heroicStrength, heroicStamina, heroicAgility, heroicDexterity,
                heroicWisdom, heroicIntelligence, heroicCharisma);
        this.id = id;
        this.extId = extId;
        this.server = server;
        this.expansion = expansion;
        this.classes = classes;
        this.slots = slots;
        this.randomLootTier = randomLootTier;
    }
}
