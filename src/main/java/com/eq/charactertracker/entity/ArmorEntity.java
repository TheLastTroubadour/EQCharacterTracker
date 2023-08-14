package com.eq.charactertracker.entity;

import com.eq.charactertracker.base.BaseAttributes;
import com.eq.charactertracker.constants.FocusEffect;
import com.eq.charactertracker.constants.ServerEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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

    private ServerEnum server;

    @Builder
    public ArmorEntity(String name, Integer ac, Integer strength, Integer stamina, Integer agility, Integer dexterity,
                       Integer wisdom, Integer intelligence, Integer charisma, Integer hitPoints, Integer manaPoints,
                       Integer endurancePoints, Integer fireResist, Integer coldResist, Integer magicResist,
                       Integer diseaseResist, Integer poisonResist, Integer attack, Double accuracy, Double avoidance,
                       Double combatEffect, Double stunResist, Integer damageShield, Double dotShielding,
                       Integer enduranceRegen, Integer hitPointsRegen, Integer manaPointsRegen, Double spellShielding,
                       Double shielding, Double strikethrough, Collection<FocusEffect> focusEffect, Long id, Long extId,
                       ServerEnum server) {
        super(name, ac, strength, stamina, agility, dexterity, wisdom, intelligence, charisma, hitPoints, manaPoints,
                endurancePoints, fireResist, coldResist, magicResist, diseaseResist, poisonResist, attack, accuracy,
                avoidance, combatEffect, stunResist, damageShield, dotShielding, enduranceRegen, hitPointsRegen,
                manaPointsRegen, spellShielding, shielding, strikethrough, focusEffect);
        this.id = id;
        this.extId = extId;
        this.server = server;
    }
}
