package com.eq.charactertracker.repo;

import com.eq.charactertracker.entity.CharacterInventoryEntity;
import com.eq.charactertracker.entity.ServerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CharacterInventoryRepo extends JpaRepository<CharacterInventoryEntity, Long> {
    Optional<CharacterInventoryEntity> findCharacterInventoryEntityBySlotNameAndCharacter_Id(String slotName, Long characterId);
    Optional<CharacterInventoryEntity> findCharacterInventoryEntityByCharacter_IdAndItem_ExternalIdAndItem_Server(Long characterId, Long externalId, ServerEntity server);
    Optional<CharacterInventoryEntity> findCharacterInventoryEntityByCharacter_IdAndItem_Name(Long characterId, String itemName);
    List<CharacterInventoryEntity> findCharacterInventoryEntitiesByCharacter_Id(Long characterId);
}
