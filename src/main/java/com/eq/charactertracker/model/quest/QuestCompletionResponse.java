package com.eq.charactertracker.model.quest;

import com.eq.charactertracker.constants.QuestType;
import lombok.Data;

@Data
public class QuestCompletionResponse {
    private Long progressId;
    private Long questTemplateId;
    private String questName;
    private QuestType questType;
    private boolean completed;
}
