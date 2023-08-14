package com.eq.charactertracker.model.quest.thf;

import com.eq.charactertracker.base.BaseQuest;
import com.eq.charactertracker.model.Character;
import com.eq.charactertracker.model.QuestItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Epic1_5Quest extends BaseQuest {

    private Long id;

    private QuestItem firstStone ;
    private QuestItem secondStone;
    private QuestItem thirdStone;
    private QuestItem fourthStone;

    private QuestItem flawlessUnicornHeart;
    private QuestItem perfectUnicornHeart;
    private QuestItem totalUnicornHeart;
    private QuestItem absurdUnicornHeart;

    private QuestItem eyeOfTheTerrorantula;
    private QuestItem eyeOfTheSpectre;
    private QuestItem eyeOfTheVampire;
    private QuestItem eyeOfTheGhost;

    private QuestItem bloodOfTheDragon;

    private Character character;
}
