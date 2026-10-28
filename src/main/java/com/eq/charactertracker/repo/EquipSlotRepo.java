package com.eq.charactertracker.repo;

import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.EquipSlotEnum;
import com.eq.charactertracker.entity.CharacterEntity;
import com.eq.charactertracker.entity.CharacterEquipSlotEntity;
import com.eq.charactertracker.entity.ServerEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipSlotRepo extends BaseRepo<CharacterEquipSlotEntity, Long> {

    @Query("select c from CharacterEquipSlotEntity c where c.character.characterClass in :classes and c.equipSlot in :slots ORDER BY c.armor.hitPoints desc")
    List<CharacterEquipSlotEntity> findCharacterEquipSlotEntitiesByCharacter_CharacterClassAndEquipSlot(@Param("classes") List<CharacterClass> classes,
                                                                                                        @Param("slots") List<EquipSlotEnum> slots);

    @Query("select c from CharacterEquipSlotEntity c where c.character.characterClass in :classes and c.equipSlot in :slots and c.character.server = :server ORDER BY c.armor.hitPoints desc")
    List<CharacterEquipSlotEntity> findCharacterEquipSlotEntitiesByCharacter_CharacterClassAndEquipSlotAndServer(@Param("classes") List<CharacterClass> classes,
                                                                                                                 @Param("slots") List<EquipSlotEnum> slots,
                                                                                                                 @Param("server") ServerEntity server);

    Optional<CharacterEquipSlotEntity> findCharacterEquipSlotEntityByCharacter_IdAndEquipSlot(Long characterId, EquipSlotEnum equipSlotEnum);

    List<CharacterEquipSlotEntity> findCharacterEquipSlotEntitiesByCharacter_IdAndCharacter_Server(Long characterId, ServerEntity server);

    // namePattern is pre-built in Java ("%needle%", already lower-cased) and bound once -- see
    // NpcRepo.search for why binding the same param both bare and inside CONCAT() in one query
    // broke MySQL's parameter-type inference. server is optional; paired with
    // CharacterRepo.findByCharacterClassInAndServer to compute the "doesn't have it" half of
    // an aug-presence search. Returns the matching slot rows (not distinct characters) so the
    // service can report which specific aug name(s) matched per character.
    //
    // Explicit LEFT JOINs are required here -- aug1/aug2/aug3 are optional (nullable FK)
    // associations, and a plain path expression like `c.aug1.name` in JPQL compiles to an
    // implicit INNER join. With three of those ANDed together into one join tree (one per
    // path), a row only survives at all if aug1 AND aug2 AND aug3 are ALL non-null
    // simultaneously -- silently dropping every slot that only has 1-2 of its 3 aug slots
    // filled, which is the common case. LEFT JOIN keeps a row whose non-matching slots are
    // simply empty, and only the actual WHERE predicate decides whether it's a match.
    @Query("select c from CharacterEquipSlotEntity c " +
            "left join c.aug1 a1 left join c.aug2 a2 left join c.aug3 a3 " +
            "where c.character.characterClass in :classes and (:server is null or c.character.server = :server) " +
            "and (LOWER(a1.name) LIKE :namePattern or LOWER(a2.name) LIKE :namePattern or LOWER(a3.name) LIKE :namePattern)")
    List<CharacterEquipSlotEntity> findEquipSlotsWithAugNameMatching(@Param("namePattern") String namePattern,
                                                                      @Param("classes") List<CharacterClass> classes,
                                                                      @Param("server") ServerEntity server);
}
