package com.eq.charactertracker.repo;

import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.entity.ServerEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServerRepo extends BaseRepo<ServerEntity, Long> {
    Optional<ServerEntity> findByShortName(String shortName);
    List<ServerEntity> findByType(ServerType type);
    Optional<ServerEntity> findByNameIgnoreCase(String name);
}
