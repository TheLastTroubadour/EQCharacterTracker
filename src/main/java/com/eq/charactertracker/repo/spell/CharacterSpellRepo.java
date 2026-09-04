package com.eq.charactertracker.repo.spell;

import com.eq.charactertracker.constants.CharacterClass;
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

    // Filters are optional (:param IS NULL short-circuits the check) except userId, which scopes
    // results to the caller's own characters. Also caps results to the character's server's
    // currentExpansion (when set) so spells from expansions the server hasn't unlocked yet
    // don't show up as "missing" -- a spell with no expansion set is never capped.
    @Query("SELECT cs FROM CharacterSpellEntity cs " +
            "WHERE cs.character.user.id = :userId " +
            "AND cs.scribed = FALSE " +
            "AND (:characterId IS NULL OR cs.character.id = :characterId) " +
            "AND (:serverShortName IS NULL OR cs.character.server.shortName = :serverShortName) " +
            "AND (:characterClass IS NULL OR cs.character.characterClass = :characterClass) " +
            "AND (cs.character.server.currentExpansion IS NULL OR cs.spell.expansion IS NULL " +
            "     OR cs.spell.expansion.releaseOrder <= cs.character.server.currentExpansion.releaseOrder)")
    List<CharacterSpellEntity> findMissingSpells(@Param("userId") Long userId,
                                                  @Param("characterId") Long characterId,
                                                  @Param("serverShortName") String serverShortName,
                                                  @Param("characterClass") CharacterClass characterClass);
}
