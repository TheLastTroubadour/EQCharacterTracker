package com.eq.charactertracker.repo;

import com.eq.charactertracker.entity.ItemEntity;
import com.eq.charactertracker.entity.ServerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepo extends JpaRepository<ItemEntity, Long> {

    Optional<ItemEntity> findItemEntityByExternalId(Long extId);
    Optional<ItemEntity> findItemEntityByExternalIdAndServer(Long externalId, ServerEntity server);
}
