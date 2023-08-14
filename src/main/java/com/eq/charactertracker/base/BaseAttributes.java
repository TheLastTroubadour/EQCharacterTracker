package com.eq.charactertracker.base;

import com.eq.charactertracker.constants.FocusEffect;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.MappedSuperclass;
import java.util.Collection;

@MappedSuperclass
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseAttributes {

    @Column(nullable = false)
    private String name;
    //armor class
    @Column(columnDefinition = "Integer default 0", nullable = false)
    private Integer ac;

    @Column(columnDefinition = "Integer default 0", nullable = false)
    private Integer strength;

    @Column(columnDefinition = "Integer default 0", nullable = false)
    private Integer stamina;

    @Column(columnDefinition = "Integer default 0", nullable = false)
    private Integer agility;

    @Column(columnDefinition = "Integer default 0", nullable = false)
    private Integer dexterity;

    @Column(columnDefinition = "Integer default 0", nullable = false)
    private Integer wisdom;

    @Column(columnDefinition = "Integer default 0", nullable = false)
    private Integer intelligence;

    @Column(columnDefinition = "Integer default 0", nullable = false)
    private Integer charisma;

    @Column(columnDefinition = "Integer default 0", nullable = false)
    private Integer hitPoints;

    @Column(columnDefinition = "Integer default 0", nullable = false)
    private Integer manaPoints;

    @Column(columnDefinition = "Integer default 0", nullable = false)
    private Integer endurancePoints;

    @Column(columnDefinition = "Integer default 0", nullable = false)
    private Integer fireResist;

    @Column(columnDefinition = "Integer default 0", nullable = false)
    private Integer coldResist;

    @Column(columnDefinition = "Integer default 0", nullable = false)
    private Integer magicResist;

    @Column(columnDefinition = "Integer default 0", nullable = false)
    private Integer diseaseResist;

    @Column(columnDefinition = "Integer default 0", nullable = false)
    private Integer poisonResist;

    @Column(columnDefinition = "Integer default 0", nullable = false)
    private Integer attack;

    @Column(columnDefinition = "Double default 0", nullable = false)
    private Double accuracy;

    @Column(columnDefinition = "Double default 0", nullable = false)
    private Double avoidance;

    @Column(columnDefinition = "Double default 0", nullable = false)
    private Double combatEffect;

    @Column(columnDefinition = "Double default 0", nullable = false)
    private Double stunResist;

    @Column(columnDefinition = "Integer default 0", nullable = false)
    private Integer damageShield;

    @Column(columnDefinition = "Double default 0", nullable = false)
    private Double dotShielding;

    @Column(columnDefinition = "Integer default 0", nullable = false)
    private Integer enduranceRegen;

    @Column(columnDefinition = "Integer default 0", nullable = false)
    private Integer hitPointsRegen;

    @Column(columnDefinition = "Integer default 0", nullable = false)
    private Integer manaPointsRegen;

    @Column(columnDefinition = "Double default 0", nullable = false)
    private Double spellShielding;

    @Column(columnDefinition = "Double default 0", nullable = false)
    private Double shielding;

    @Column(columnDefinition = "Double default 0", nullable = false)
    private Double strikethrough;

    @ElementCollection(targetClass = FocusEffect.class)
    private Collection<FocusEffect> focusEffect;
}
