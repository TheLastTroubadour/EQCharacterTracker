package com.eq.charactertracker.repo;

import com.eq.charactertracker.entity.quest.thf.Tier5XegonyKeyQuestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Tier5XegonyKeyQuestRepo extends JpaRepository<Tier5XegonyKeyQuestEntity, Long> {

    Optional<Tier5XegonyKeyQuestEntity> findTier5XegonyKeyQuestEntityByCharacterId(Long id);
}
