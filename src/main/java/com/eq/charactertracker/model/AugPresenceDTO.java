package com.eq.charactertracker.model;

import lombok.Data;

import java.util.List;

@Data
public class AugPresenceDTO {
    private List<AugMatchDTO> hasAug;
    private List<CharacterDTO> missingAug;
}
