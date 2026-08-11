package com.eq.charactertracker.repo.spell;

import com.eq.charactertracker.entity.spell.CharacterSpellEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CharacterSpellRepo extends JpaRepository<CharacterSpellEntity, Long> {
    List<CharacterSpellEntity> findByCharacter_Id(Long characterId);
    Optional<CharacterSpellEntity> findByCharacter_IdAndSpell_Id(Long characterId, Long spellId);
}
