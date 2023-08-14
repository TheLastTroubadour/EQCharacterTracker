package com.eq.charactertracker.repo;

import com.eq.charactertracker.entity.quest.thf.Tier5PoAAugmentQuestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Tier5PoAAugmentQuestRepo extends JpaRepository<Tier5PoAAugmentQuestEntity, Long> {

    Optional<Tier5PoAAugmentQuestEntity> findTier5PoAAugmentQuestEntityByCharacter_IdAndQuestItem_Item_ExternalId(Long characterId, Long questItemExternalId);
}
