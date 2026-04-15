package com.eq.charactertracker.model.quest;

import lombok.Data;

import java.util.List;

@Data
public class CharacterQuestProgressResponse {
    private Long progressId;
    private String questName;
    private boolean ordered;
    private boolean completed;
    private List<SubQuestProgressResponse> subQuests;
}
