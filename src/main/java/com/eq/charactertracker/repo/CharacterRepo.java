package com.eq.charactertracker.repo;

import com.eq.charactertracker.entity.CharacterEntity;
import com.eq.charactertracker.entity.ServerEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterRepo extends BaseRepo<CharacterEntity, Long> {

    CharacterEntity findCharacterEntityByUserIdAndNameAndServer(Long userId, String name, ServerEntity server);
    CharacterEntity findCharacterEntityByNameAndServer(String name, ServerEntity server);

    List<CharacterEntity> findCharacterEntitiesByUserId(Long userId);
    List<CharacterEntity> findCharacterEntitiesByUserIdAndServer(Long userId, ServerEntity server);
}
