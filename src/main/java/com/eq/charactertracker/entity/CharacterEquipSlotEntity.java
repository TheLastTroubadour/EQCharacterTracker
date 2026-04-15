package com.eq.charactertracker.entity;

import com.eq.charactertracker.constants.EquipSlotEnum;
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
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "character_armor_by_slot", uniqueConstraints = @UniqueConstraint(columnNames = {"character_id", "equipSlot"}))
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CharacterEquipSlotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EquipSlotEnum equipSlot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private CharacterEntity character;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "armor_id", nullable = false)
    private ArmorEntity armor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aug1_id")
    private AugmentationEntity aug1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aug2_id")
    private AugmentationEntity aug2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aug3_id")
    private AugmentationEntity aug3;
}
