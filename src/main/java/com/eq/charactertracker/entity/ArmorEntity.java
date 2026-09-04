package com.eq.charactertracker.entity;

import com.eq.charactertracker.base.BaseAttributes;
import com.eq.charactertracker.base.BaseEntity;
import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.EquipSlotEnum;
import com.eq.charactertracker.entity.quest.ExpansionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Collection;

@Table(name = "armor")
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ArmorEntity extends BaseAttributes implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long extId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private ServerEntity server;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "expansion_id")
    private ExpansionEntity expansion;

    @ElementCollection(targetClass = CharacterClass.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "armor_classes", joinColumns = @JoinColumn(name = "armor_id"))
    @Column(name = "character_class")
    private Collection<CharacterClass> classes;

    @ElementCollection(targetClass = EquipSlotEnum.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "armor_slots", joinColumns = @JoinColumn(name = "armor_id"))
    @Column(name = "equip_slot")
    private Collection<EquipSlotEnum> slots;

    // See NpcEntity.randomLootTier -- items sharing the same tier number are all part of the
    // same shared drop pool on servers with LIVE's loot-randomization ruleset enabled.
    private Integer randomLootTier;

    @Builder
    public ArmorEntity(String name, Integer ac, Integer strength, Integer stamina, Integer agility, Integer dexterity,
                       Integer wisdom, Integer intelligence, Integer charisma, Integer hitPoints, Integer manaPoints,
                       Integer endurancePoints, Integer fireResist, Integer coldResist, Integer magicResist,
                       Integer diseaseResist, Integer poisonResist, Integer attack, Double accuracy, Double avoidance,
                       Double combatEffect, Double stunResist, Integer damageShield, Double dotShielding,
                       Integer enduranceRegen, Integer hitPointsRegen, Integer manaPointsRegen, Double spellShielding,
                       Double shielding, Double strikethrough, Collection<String> focusEffect, Collection<String> clickEffect,
                       Integer heroicStrength, Integer heroicStamina, Integer heroicAgility, Integer heroicDexterity,
                       Integer heroicWisdom, Integer heroicIntelligence, Integer heroicCharisma,
                       Long id, Long extId, ServerEntity server, ExpansionEntity expansion,
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
