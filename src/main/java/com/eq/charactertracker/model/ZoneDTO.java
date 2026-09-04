package com.eq.charactertracker.model;

import com.eq.charactertracker.model.quest.ExpansionDTO;
import lombok.Data;

@Data
public class ZoneDTO {
    private Long id;
    private String name;
    private String shortName;
    private ExpansionDTO expansion;
}
