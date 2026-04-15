package com.eq.charactertracker.model.imports;

import lombok.Data;

@Data
public class InventoryItemImport {
    private Long externalId;
    private String name;
    private String slot;
    private Integer stack;
}
