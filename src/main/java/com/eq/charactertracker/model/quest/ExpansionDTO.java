package com.eq.charactertracker.model.quest;

import com.eq.charactertracker.constants.ServerType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpansionDTO {
    private Long id;
    private String name;
    private String shortName;
    private int releaseOrder;
    private ServerType serverType;
    private LocalDate releaseDate;
}
