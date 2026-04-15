package com.eq.charactertracker.model.imports;

import lombok.Data;

import java.util.List;

@Data
public class CharacterImportRequest {
    private CharacterImportInfo character;
    private List<EquippedItemImport> equipped;
    private List<InventoryItemImport> inventory;
}
