package com.eq.charactertracker.model.quest;

import com.eq.charactertracker.constants.QuestType;
import lombok.Data;

import java.util.List;

@Data
public class QuestCompletionResponse {
    private Long progressId;
    private Long questTemplateId;
    private String questName;
    private QuestType questType;
    private boolean completed;
    private List<SubQuestProgressResponse> subQuests;
}
