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
    // Reuses SpellImport (see ImportService.processSpell) -- disciplines share the same
    // catalog/progress tables as spells, just a different section of the export.
    @JsonDeserialize(using = SpellImportListDeserializer.class)
    private List<SpellImport> disciplines;
}
