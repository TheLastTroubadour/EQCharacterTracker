package com.eq.charactertracker.model.quest;

import com.eq.charactertracker.constants.RequirementType;
import lombok.Data;

@Data
public class RequirementTemplateRequest {
    private RequirementType type;
    private String description;
    private Long externalItemId;
    private String phrase;
}
