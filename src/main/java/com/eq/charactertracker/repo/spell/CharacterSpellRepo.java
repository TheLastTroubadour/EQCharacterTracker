package com.eq.charactertracker.repo.spell;

import com.eq.charactertracker.entity.spell.CharacterSpellEntity;
import com.eq.charactertracker.repo.BaseRepo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CharacterSpellRepo extends BaseRepo<CharacterSpellEntity, Long> {
    List<CharacterSpellEntity> findByCharacter_Id(Long characterId);
    Optional<CharacterSpellEntity> findByCharacter_IdAndSpell_Id(Long characterId, Long spellId);

    @Modifying
    @Query("UPDATE CharacterSpellEntity cs SET cs.scribed = false WHERE cs.character.id = :characterId")
    void resetScribedForCharacter(@Param("characterId") Long characterId);

    @Query("SELECT cs FROM CharacterSpellEntity cs WHERE cs.character.id IN (:characterIds) AND cs.scribed = FALSE")
    List<CharacterSpellEntity> findByCharacterIdList(@Param("characterIds") List<Long> characterIds);
}
