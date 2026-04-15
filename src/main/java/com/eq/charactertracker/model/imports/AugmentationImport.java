package com.eq.charactertracker.model.imports;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AugmentationImport {
    private Long externalId;
    private String name;
    private Integer slot;
    private Integer ac;
    private Integer str;
    private Integer sta;
    private Integer agi;
    private Integer dex;
    private Integer wis;
    @JsonProperty("int")
    private Integer intelligence;
    private Integer cha;
    private Integer hp;
    private Integer mana;
    private Integer svFire;
    private Integer svCold;
    private Integer svMagic;
    private Integer svDisease;
    private Integer svPoison;
    private Integer attack;
}
