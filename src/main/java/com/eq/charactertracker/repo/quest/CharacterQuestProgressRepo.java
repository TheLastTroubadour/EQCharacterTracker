package com.eq.charactertracker.repo.quest;

import com.eq.charactertracker.entity.quest.CharacterQuestProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CharacterQuestProgressRepo extends JpaRepository<CharacterQuestProgressEntity, Long> {
    List<CharacterQuestProgressEntity> findByCharacter_Id(Long characterId);
    Optional<CharacterQuestProgressEntity> findByCharacter_IdAndQuestTemplate_Id(Long characterId, Long questTemplateId);
}
