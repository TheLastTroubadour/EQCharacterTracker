package com.eq.charactertracker.model;

import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.model.quest.ExpansionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerDTO {
    private Long id;
    private String name;
    private String shortName;
    private ServerType type;
    private ExpansionDTO currentExpansion;
}
