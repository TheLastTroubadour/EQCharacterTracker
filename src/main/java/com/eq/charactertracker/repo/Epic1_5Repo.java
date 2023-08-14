package com.eq.charactertracker.repo;

import com.eq.charactertracker.entity.quest.thf.Epic1_5QuestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Epic1_5Repo extends JpaRepository<Epic1_5QuestEntity, Long> {

    Optional<Epic1_5QuestEntity> findEpic1_5QuestEntityByCharacterId(Long characterId);
    List<Epic1_5QuestEntity> findAllByCharacter_User_Id(Long userId);
    List<Epic1_5QuestEntity> findAllByOrderByCompleted();

}
