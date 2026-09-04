package com.eq.charactertracker.model.imports;

import lombok.Data;

@Data
public class ArmorItemsFileImportResult {
    private int totalRows;
    private int created;
    private int updated;
    private int skippedNoSlot;
}
