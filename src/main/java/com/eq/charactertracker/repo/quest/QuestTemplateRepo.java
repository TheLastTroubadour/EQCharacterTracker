package com.eq.charactertracker.repo.quest;

import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.entity.quest.QuestTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestTemplateRepo extends JpaRepository<QuestTemplateEntity, Long> {
    List<QuestTemplateEntity> findByServerType(ServerType serverType);
    List<QuestTemplateEntity> findByServerTypeAndCharacterClass(ServerType serverType, CharacterClass characterClass);
    Optional<QuestTemplateEntity> findByNameAndServerTypeAndCharacterClass(String name, ServerType serverType, CharacterClass characterClass);
}
