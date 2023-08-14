package com.eq.charactertracker.model.quest.thf;

import com.eq.charactertracker.model.Character;
import com.eq.charactertracker.model.QuestItem;
import lombok.Data;

@Data
public class Tier5PoAAugmentQuest {

    private Long id;
    private QuestItem questItem;
    private QuestItem windParchment;
    private QuestItem containerOfMist;
    private Character character;

}
