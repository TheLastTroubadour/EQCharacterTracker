package com.eq.charactertracker.model;

import com.eq.charactertracker.constants.EquipSlotEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CharacterEquipSlotDTO {
    private Long id;
    private EquipSlotEnum equipSlot;
    private CharacterDTO character;
    private ArmorDTO armor;
    private Augmentation aug1;
    private Augmentation aug2;
}
