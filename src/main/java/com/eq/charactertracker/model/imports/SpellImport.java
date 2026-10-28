package com.eq.charactertracker.model.imports;

import lombok.Data;

@Data
public class SpellImport {
    private Integer bookSlot;
    private String category;
    private Long externalId;
    private Short level;
    private Integer mana;
    private Integer enduranceCost;
    private String name;
    private String rankName;
    private String subcategory;
}
