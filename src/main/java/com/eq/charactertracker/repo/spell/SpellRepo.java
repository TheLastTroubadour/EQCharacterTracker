package com.eq.charactertracker.repo.spell;

import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.entity.spell.SpellEntity;
import com.eq.charactertracker.repo.BaseRepo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpellRepo extends BaseRepo<SpellEntity, Long> {
    List<SpellEntity> findByServerType(ServerType serverType);
    List<SpellEntity> findByServerTypeAndCharacterClass(ServerType serverType, CharacterClass characterClass);
    Optional<SpellEntity> findByNameAndRankAndCharacterClassAndServerType(String name, short rank, CharacterClass characterClass, ServerType serverType);
    List<SpellEntity> findByNeedsReviewTrue();
}
