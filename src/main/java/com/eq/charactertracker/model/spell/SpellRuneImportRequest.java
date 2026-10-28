package com.eq.charactertracker.model.spell;

import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.ServerType;
import lombok.Data;

import java.util.List;

@Data
public class SpellRuneImportRequest {
    private CharacterClass characterClass;
    private ServerType serverType = ServerType.LIVE;
    private List<RuneGroup> runes;

    @Data
    public static class RuneGroup {
        private String runeName;
        private List<RuneSpellEntry> spells;
    }

    @Data
    public static class RuneSpellEntry {
        // Position within the rune's turn-in order (1, 2, 3, ...) -- see
        // SpellEntity.runeTurnInOrder for why this ordering matters.
        private Integer order;
        private String name;
        private short rank = 3;
    }
}
