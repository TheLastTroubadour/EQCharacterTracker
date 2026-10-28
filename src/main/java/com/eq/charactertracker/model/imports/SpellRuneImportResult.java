package com.eq.charactertracker.model.imports;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SpellRuneImportResult {
    private int updated;
    private List<String> notFound = new ArrayList<>();
}
