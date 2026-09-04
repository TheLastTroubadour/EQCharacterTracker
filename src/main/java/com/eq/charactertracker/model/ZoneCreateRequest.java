package com.eq.charactertracker.model;

import lombok.Data;

@Data
public class ZoneCreateRequest {
    private String name;
    private String shortName;
    private Long expansionId;
}
