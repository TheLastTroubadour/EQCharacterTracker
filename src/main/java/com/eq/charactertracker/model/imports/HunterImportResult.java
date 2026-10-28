package com.eq.charactertracker.model.imports;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HunterImportResult {
    private int totalRows;
    private int npcZoneLinksCreated;
    // Subset of npcZoneLinksCreated that came from Raids / "Conqueror of" rows -- all of
    // these are created with needsReview = true.
    private int raidNamedCreated;
    private int skippedNonHunterCategory;
    // Raids rows whose achievement name isn't "Conqueror of ..." (tradeskill rollups,
    // explorer/slayer meta-achievements, seasonal events) -- no reliable target data.
    private int skippedNonConquerorRaid;
    private int skippedEmptyDescription;
    private int skippedRollupReference;
    private int zonesWithNoExpansionMatch;
    private List<String> unmatchedExpansionNames = new ArrayList<>();
}
