package com.eq.charactertracker.repo;

import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.EquipSlotEnum;
import com.eq.charactertracker.constants.ServerEnum;
import com.eq.charactertracker.entity.CharacterEquipSlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipSlotRepo extends JpaRepository<CharacterEquipSlotEntity, Long> {

    @Query("select c from CharacterEquipSlotEntity c where c.character.characterClass in :classes and c.equipSlot in :slots ORDER BY c.armor.hitPoints desc")
    List<CharacterEquipSlotEntity> findCharacterEquipSlotEntitiesByCharacter_CharacterClassAndEquipSlot(@Param("classes") List<CharacterClass> classes,
                                                                                                        @Param("slots") List<EquipSlotEnum> slots);

    @Query("select c from CharacterEquipSlotEntity c where c.character.characterClass in :classes and c.equipSlot in :slots and c.character.server = :server ORDER BY c.armor.hitPoints desc")
    List<CharacterEquipSlotEntity> findCharacterEquipSlotEntitiesByCharacter_CharacterClassAndEquipSlotAndServer(@Param("classes") List<CharacterClass> classes,
                                                                                                        @Param("slots") List<EquipSlotEnum> slots,
                                                                                                                 @Param("server") ServerEnum serverEnum);
    Optional<CharacterEquipSlotEntity> findCharacterEquipSlotEntityByCharacter_IdAndEquipSlot(Long characterId, EquipSlotEnum equipSlotEnum);

    List<CharacterEquipSlotEntity> findCharacterEquipSlotEntitiesByCharacter_IdAndCharacter_Server(Long characterId, ServerEnum serverEnum);
}
