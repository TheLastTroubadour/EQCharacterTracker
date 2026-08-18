package com.eq.charactertracker.model.spell;

import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.model.quest.ExpansionDTO;
import lombok.Data;

@Data
public class SpellDTO {
    private Long id;
    private String name;
    private short rank;
    private CharacterClass characterClass;
    private ServerType serverType;
    private short level;
    private Integer manaCost;
    private Integer enduranceCost;
    private String description;
    private ExpansionDTO expansion;
}
