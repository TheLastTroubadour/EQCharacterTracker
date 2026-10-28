package com.eq.charactertracker.model;

import com.eq.charactertracker.constants.NpcTier;
import lombok.Data;

import java.util.List;

@Data
public class NpcCreateRequest {
    private String name;
    private NpcTier tier;
    private Integer randomLootTier;
    // Nullable -- only applied when supplied. Set false here to clear the review flag on a
    // raid-imported npc once it's been curated.
    private Boolean needsReview;
    // Nullable -- only applied when supplied. Set true here instead of deleting a raid-imported
    // row that turns out to be an event/objective rather than an actual creature.
    private Boolean encounter;
    private List<Long> zoneIds;
    private List<Long> armorIds;
    private List<Long> itemIds;
}
