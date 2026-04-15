package com.eq.charactertracker.model.quest;

import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.ServerType;
import lombok.Data;

import java.util.List;

@Data
public class QuestTemplateRequest {
    private String name;
    private ServerType serverType;
    private CharacterClass characterClass;
    private boolean ordered;
    private boolean progressionQuest;
    private String expansionShortName;
    private List<SubQuestTemplateRequest> subQuests;
}
