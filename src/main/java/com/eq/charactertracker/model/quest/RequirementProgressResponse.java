package com.eq.charactertracker.model.quest;

import com.eq.charactertracker.constants.RequirementType;
import lombok.Data;

@Data
public class RequirementProgressResponse {
    private Long requirementTemplateId;
    private Long requirementProgressId;
    private RequirementType type;
    private String description;
    private Long externalItemId;
    private boolean completed;
}
