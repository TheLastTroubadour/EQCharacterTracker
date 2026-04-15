package com.eq.charactertracker.model.quest;

import lombok.Data;

import java.util.List;

@Data
public class SubQuestProgressResponse {
    private Long subQuestTemplateId;
    private String name;
    private int orderIndex;
    private boolean canSkip;
    private boolean completed;
    private List<RequirementProgressResponse> requirements;
}
