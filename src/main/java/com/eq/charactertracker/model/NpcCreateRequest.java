package com.eq.charactertracker.model;

import com.eq.charactertracker.constants.NpcTier;
import lombok.Data;

import java.util.List;

@Data
public class NpcCreateRequest {
    private String name;
    private NpcTier tier;
    private Integer randomLootTier;
    private List<Long> zoneIds;
    private List<Long> armorIds;
}
