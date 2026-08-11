package com.eq.charactertracker.model.spell;

import lombok.Data;

@Data
public class CharacterSpellDTO {
    private Long spellId;
    private Long characterSpellId;
    private String name;
    private short rank;
    private short level;
    private Integer manaCost;
    private String description;
    private boolean scribed;
}
