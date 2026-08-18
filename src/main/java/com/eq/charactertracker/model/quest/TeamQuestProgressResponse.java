package com.eq.charactertracker.model.quest;

import com.eq.charactertracker.constants.CharacterClass;
import lombok.Data;

import java.util.List;

@Data
public class TeamQuestProgressResponse {
    private Long characterId;
    private String characterName;
    private CharacterClass characterClass;
    private boolean epicQuestCompleted;
    private List<QuestCompletionResponse> quests;
}
