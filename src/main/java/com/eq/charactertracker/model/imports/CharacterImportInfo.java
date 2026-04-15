package com.eq.charactertracker.model.imports;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CharacterImportInfo {
    private String name;
    private String server;
    @JsonProperty("class")
    private String characterClass;
    private Short level;
    private Short aaSpent;
    private Short aaAvailable;
}
