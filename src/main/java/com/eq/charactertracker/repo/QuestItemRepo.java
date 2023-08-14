package com.eq.charactertracker.repo;

import com.eq.charactertracker.entity.quest.thf.QuestItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestItemRepo extends JpaRepository<QuestItemEntity, Long> {

    Optional<QuestItemEntity> findByItem_ExternalId(Long externalId);
}
