package com.eq.charactertracker.entity;

import com.eq.charactertracker.base.BaseAttributes;
import com.eq.charactertracker.constants.FocusEffect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
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
public class ArmorEntity extends BaseAttributes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long extId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private ServerEntity server;

    @Builder
    public ArmorEntity(String name, Integer ac, Integer strength, Integer stamina, Integer agility, Integer dexterity,
                       Integer wisdom, Integer intelligence, Integer charisma, Integer hitPoints, Integer manaPoints,
                       Integer endurancePoints, Integer fireResist, Integer coldResist, Integer magicResist,
                       Integer diseaseResist, Integer poisonResist, Integer attack, Double accuracy, Double avoidance,
                       Double combatEffect, Double stunResist, Integer damageShield, Double dotShielding,
                       Integer enduranceRegen, Integer hitPointsRegen, Integer manaPointsRegen, Double spellShielding,
                       Double shielding, Double strikethrough, Collection<FocusEffect> focusEffect,
                       Integer heroicStrength, Integer heroicStamina, Integer heroicAgility, Integer heroicDexterity,
                       Integer heroicWisdom, Integer heroicIntelligence, Integer heroicCharisma,
                       Long id, Long extId, ServerEntity server) {
        super(name, ac, strength, stamina, agility, dexterity, wisdom, intelligence, charisma, hitPoints, manaPoints,
                endurancePoints, fireResist, coldResist, magicResist, diseaseResist, poisonResist, attack, accuracy,
                avoidance, combatEffect, stunResist, damageShield, dotShielding, enduranceRegen, hitPointsRegen,
                manaPointsRegen, spellShielding, shielding, strikethrough, focusEffect,
                heroicStrength, heroicStamina, heroicAgility, heroicDexterity,
                heroicWisdom, heroicIntelligence, heroicCharisma);
        this.id = id;
        this.extId = extId;
        this.server = server;
    }
}
