package com.eq.charactertracker.model.quest;

import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.QuestType;
import com.eq.charactertracker.constants.ServerType;
import lombok.Data;

import java.util.List;

@Data
public class QuestTemplateDTO {
    private Long id;
    private String name;
    private ServerType serverType;
    private CharacterClass characterClass;
    private boolean ordered;
    private QuestType questType;
    private ExpansionDTO expansion;
    private List<SubQuestTemplateDTO> subQuests;
}
