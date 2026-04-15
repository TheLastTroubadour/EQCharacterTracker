package com.eq.charactertracker.model.imports;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImportResult {
    private String character;
    private String server;
    private int equippedImported;
    private int inventoryImported;
    private List<String> skipped = new ArrayList<>();

    public void addSkipped(String message) {
        skipped.add(message);
    }
}
