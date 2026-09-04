package com.eq.charactertracker.model.imports;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HunterImportResult {
    private int totalRows;
    private int npcZoneLinksCreated;
    private int skippedNonHunterCategory;
    private int skippedEmptyDescription;
    private int skippedRollupReference;
    private int zonesWithNoExpansionMatch;
    private List<String> unmatchedExpansionNames = new ArrayList<>();
}
