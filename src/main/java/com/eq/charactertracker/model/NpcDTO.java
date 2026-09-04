package com.eq.charactertracker.model;

import com.eq.charactertracker.constants.NpcTier;
import lombok.Data;

import java.util.Collection;

@Data
public class NpcDTO {
    private Long id;
    private String name;
    private NpcTier tier;
    private Integer randomLootTier;
    private Collection<ZoneDTO> zones;
    private Collection<ArmorDTO> drops;
}
