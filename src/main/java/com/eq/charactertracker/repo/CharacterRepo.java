package com.eq.charactertracker.repo;

import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.entity.CharacterEntity;
import com.eq.charactertracker.entity.ServerEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterRepo extends BaseRepo<CharacterEntity, Long> {

    CharacterEntity findCharacterEntityByUserIdAndNameAndServer(Long userId, String name, ServerEntity server);
    CharacterEntity findCharacterEntityByNameAndServer(String name, ServerEntity server);

    List<CharacterEntity> findCharacterEntitiesByUserId(Long userId);
    List<CharacterEntity> findCharacterEntitiesByUserIdAndServer(Long userId, ServerEntity server);

    // server is optional -- see EquipSlotRepo.findCharactersWithAugMatching, which this is
    // paired with to compute the "doesn't have the aug" half of the aug-presence search.
    @Query("select c from CharacterEntity c where c.characterClass in :classes and (:server is null or c.server = :server)")
    List<CharacterEntity> findByCharacterClassInAndServer(@Param("classes") List<CharacterClass> classes,
                                                           @Param("server") ServerEntity server);
}
