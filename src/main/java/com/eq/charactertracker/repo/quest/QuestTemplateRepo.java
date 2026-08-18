package com.eq.charactertracker.repo.quest;

import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.QuestType;
import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.entity.quest.QuestTemplateEntity;
import com.eq.charactertracker.repo.BaseRepo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestTemplateRepo extends BaseRepo<QuestTemplateEntity, Long> {
    List<QuestTemplateEntity> findByServerType(ServerType serverType);
    List<QuestTemplateEntity> findByServerTypeAndCharacterClass(ServerType serverType, CharacterClass characterClass);
    Optional<QuestTemplateEntity> findByNameAndServerTypeAndCharacterClass(String name, ServerType serverType, CharacterClass characterClass);
    List<QuestTemplateEntity> findByServerTypeAndCharacterClassAndQuestType(ServerType serverType, CharacterClass characterClass, QuestType questType);
    List<QuestTemplateEntity> findByServerTypeAndCharacterClassAndQuestTypeIn(ServerType serverType, CharacterClass characterClass, List<QuestType> questTypes);
    List<QuestTemplateEntity> findByServerTypeAndQuestTypeAndCharacterClassIsNull(ServerType serverType, QuestType questType);
    List<QuestTemplateEntity> findByServerTypeAndCharacterClassIsNull(ServerType serverType);
}
