package com.eq.charactertracker.model.spell;

import com.eq.charactertracker.model.CharacterDTO;
import lombok.Data;

@Data
public class CharacterSpellDTO {
    private Long id;
    private CharacterDTO character;
    private SpellDTO spell;
    private boolean scribed;

}
