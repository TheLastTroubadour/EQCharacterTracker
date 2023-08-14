package com.eq.charactertracker.entity;

import com.eq.charactertracker.constants.EquipSlotEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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


}
