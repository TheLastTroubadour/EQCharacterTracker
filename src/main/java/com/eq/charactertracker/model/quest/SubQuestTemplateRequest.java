package com.eq.charactertracker.model.quest;

import lombok.Data;

import java.util.List;

@Data
public class SubQuestTemplateRequest {
    private String name;
    private int order;
    private boolean canSkip;
    private List<RequirementTemplateRequest> requirements;
}
