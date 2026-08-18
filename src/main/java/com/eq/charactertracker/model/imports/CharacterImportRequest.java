package com.eq.charactertracker.model.imports;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.util.List;

@Data
public class CharacterImportRequest {
    private CharacterImportInfo character;
    private List<EquippedItemImport> equipped;
    private List<InventoryItemImport> inventory;
    @JsonDeserialize(using = SpellImportListDeserializer.class)
    private List<SpellImport> spells;
}
